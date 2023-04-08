package org.yiwannuofulasi.plugin.excel.resolve;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yiwannuofulasi.plugin.excel.annotation.ExcelDynamicSelect;
import org.yiwannuofulasi.plugin.excel.annotation.ExcelSelected;
import org.yiwannuofulasi.plugin.excel.utils.SpringContextUtils;

/**
 * @author cheng-qiang
 * @date 2022年11月09日15:27
 */
@Data
@Slf4j
public class ExcelSelectedResolve {

    /**
     * 下拉内容
     */
    private Object source;

    private String classPath;

    private int rowIndex;

    private String self;

    private String parent;

    private int parentColumnIndex;

    /**
     * 设置下拉框的起始行，默认为第二行
     */
    private int firstRow;

    /**
     * 设置下拉框的结束行，默认为最后一行
     */
    private int lastRow;

    /**
     * 解析选定来源
     * @author cheng-qiang
     * @date 2022/11/9 15:29
     * @param excelSelected excelSelected
     * @return java.lang.String[]
     */
    public Object resolveSelectedSource(ExcelSelected excelSelected) {
        if (excelSelected == null) {
            return null;
        }
        // 情形1：静态下拉框
        final String source = excelSelected.source();
        if (StrUtil.isNotEmpty(source)) {
            return convert(excelSelected.parent(), excelSelected.source());
        }
        // 情形2：动态下拉框
        Class<? extends ExcelDynamicSelect>[] classes = excelSelected.sourceClass();
        if (classes.length > 0) {
            final ExcelDynamicSelect excelDynamicSelect = SpringContextUtils.getBean(classes[0]);
            String dynamicSelectSource = excelDynamicSelect.getSource();
            if (StrUtil.isNotEmpty(dynamicSelectSource)) {
                return convert(excelSelected.parent(), dynamicSelectSource);
            }
        }
        return null;
    }

    /**
     * 主要是根据是否关联父列, 判断ExcelDynamicSelect处理器的返回结果
     * 如果注解的parent不为空, 则source应该返回一个k,v (父,子列表)对象
     * 如果注解的parent为空, 则source应该返回一个字符串array
     */
    private Object convert(String parent, String source) {
        if (StrUtil.isEmpty(parent)) {
            return JSONArray.parseArray(source);
        } else {
            return JSONObject.parseObject(source);
        }
    }
}
