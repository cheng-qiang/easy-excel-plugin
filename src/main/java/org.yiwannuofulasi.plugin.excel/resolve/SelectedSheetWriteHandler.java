package org.yiwannuofulasi.plugin.excel.resolve;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.yiwannuofulasi.plugin.excel.utils.EasyExcelUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cheng-qiang
 * @date 2022年11月09日15:30
 */
@Data
@AllArgsConstructor
public class SelectedSheetWriteHandler implements SheetWriteHandler {

    private final Map<Integer, ExcelSelectedResolve> selectedMap;

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        selectedMap.forEach((k, v) -> {
            if (StrUtil.isNotEmpty(v.getParent())) {
                final Map<String, List<String>> data = ((JSONObject) v.getSource()).toJavaObject(new TypeReference<HashMap<String, List<String>>>() {
                });
                EasyExcelUtils.addCascadeValidationToSheet(writeWorkbookHolder, writeSheetHolder, data, v.getParentColumnIndex(), k, v.getFirstRow(), v.getLastRow());
            } else {
                String classPath = v.getClassPath();
                if (StrUtil.isEmpty(classPath)){
                    EasyExcelUtils.selectStillness(sheet, helper, k, v);
                }else {
                    EasyExcelUtils.selectDictionary(writeWorkbookHolder, sheet, helper, k, v, classPath);
                }
            }
        });
    }
}
