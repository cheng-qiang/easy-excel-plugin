package org.yiwannuofulasi.plugin.excel.resolve;

import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Set;

/**
 * 将参数校验失败的Excel添加批注后导出
 * @author cheng-qiang
 * @date 2022年11月17日16:01
 */
public class CommentWriteHandler extends RowWriteHandlerAdapter{

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (Boolean.FALSE.equals(isHead)){
            Sheet sheet = writeSheetHolder.getSheet();
            Set<Integer> integers = excelErrorMap.keySet();
            for (Integer integer : integers) {
                List<ExcelError> excelErrors = excelErrorMap.get(integer);
                for (int i = 0; i < excelErrors.size(); i++) {
                    ExcelError excelError = JSON.parseObject(JSON.toJSONString(excelErrors.get(i)), ExcelError.class);
                    setCellCommon(sheet, excelError.getRow()-1, excelError.getColumn(), excelError.getErrorMsg());
                }
            }
        }
    }
}
