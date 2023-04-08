package org.yiwannuofulasi.plugin.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwannuofulasi.plugin.excel.resolve.ExcelError;
import org.yiwannuofulasi.plugin.excel.resolve.ExcelValidator;
import org.yiwannuofulasi.plugin.excel.utils.EasyExcelUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author cheng-qiang
 * @date 2022年11月17日11:11
 */
public class JdbcEventListener<T> extends AnalysisEventListener<T> {

    protected Map<Integer, List<ExcelError>> excelErrorMap = new HashMap<>();

    public Map<Integer, List<ExcelError>> getExcelErrorMap() {
        return excelErrorMap;
    }

    Logger logger = LoggerFactory.getLogger(JdbcEventListener.class);

    /**
     * Excel总条数阈值
     */
    private static final Integer MAX_SIZE = 10000;
    /**
     * 校验工具
     */
    private final ExcelValidator<T> excelValidator;
    /**
     * 如果校验通过消费解析得到的excel数据
     */
    private final Consumer<Collection<T>> batchConsumer;
    /**
     * 解析数据的临时存储容器
     */
    private final List<T> list = new ArrayList<>();

    public List<T> getListExcels() {
        return list;
    }

    /**
     * Instantiates a new Jdbc event listener.
     *
     * @param excelValidator Excel校验工具
     * @param batchConsumer  Excel解析结果批量消费工具，可实现为写入数据库等消费操作
     */
    public JdbcEventListener(ExcelValidator<T> excelValidator, Consumer<Collection<T>> batchConsumer) {
        this.excelValidator = excelValidator;
        this.batchConsumer = batchConsumer;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Set<Integer> integers = headMap.keySet();
        StringBuilder head = new StringBuilder();
        for (Integer integer : integers) {
            head.append(headMap.get(integer).getStringValue()).append(",");
        }
        logger.info("解析头部标题：{}", head.substring(0,head.lastIndexOf(",")));
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        list.clear();
        throw exception;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        // 如果没有超过阈值就把解析的excel字段加入集合
        if (list.size() >= MAX_SIZE) {
            logger.error("单次上传条数不得超过：{}", MAX_SIZE);
        }
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!CollectionUtils.isEmpty(this.list)) {
            List<String> validated = this.excelValidator.validate(this.list);
            if (CollectionUtils.isEmpty(validated)) {
                this.batchConsumer.accept(this.list);
            } else {
                T object = list.get(0);
                for (String validate : validated) {
                    String[] split = validate.split("_");
                    Integer cellIndex = EasyExcelUtils.getCellIndex(object, split[0]);
                    if (null!=cellIndex){
                        setExcelErrorMaps(Integer.parseInt(split[2]),cellIndex,split[1]+split[2]+split[3]);
                    }
                }
            }
        }
    }

    /**
     * 设置批注集合
     *
     * @param rowsNum   行数
     * @param cellIndex 单元格索引
     * @param msg       错误信息
     */
    protected void setExcelErrorMaps(int rowsNum, int cellIndex, String msg) {
        if (excelErrorMap.containsKey(rowsNum)) {
            List<ExcelError> excelErrors = excelErrorMap.get(rowsNum);
            excelErrors.add(new ExcelError(rowsNum, cellIndex, msg));
            excelErrorMap.put(rowsNum, excelErrors);
        } else {
            List<ExcelError> excelErrors = new ArrayList<>();
            excelErrors.add(new ExcelError(rowsNum, cellIndex, msg));
            excelErrorMap.put(rowsNum, excelErrors);
        }
    }
}
