package org.yiwannuofulasi.plugin.excel.resolve;

import com.alibaba.excel.write.handler.RowWriteHandler;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行写处理程序适配器
 * @author cheng-qiang
 * @date 2022年11月17日15:52
 */
public class RowWriteHandlerAdapter implements RowWriteHandler {

    protected Map<Integer, List<ExcelError>> excelErrorMap = new HashMap<>();

    public void setExcelErrorMap(Map<Integer, List<ExcelError>> excelErrorMap) {
        this.excelErrorMap = excelErrorMap;
    }

    /**
     * 设置单元格批注
     * @param sheet sheet
     * @param rowIndex 行索引
     * @param colIndex 列索引
     * @param value 批注
     */
    protected void setCellCommon(Sheet sheet, int rowIndex, int colIndex, String value) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return;
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        if (value == null) {
            cell.removeCellComment();
            return;
        }
        Drawing drawingPatriarch = sheet.createDrawingPatriarch();
        XSSFClientAnchor clientAnchor = new XSSFClientAnchor(0, 0, 0, 0, colIndex, rowIndex, 10, 10);
        Row row1 = sheet.getRow(clientAnchor.getRow1());
        if (row1 != null) {
            Cell cell1 = row1.getCell(clientAnchor.getCol1());
            if (cell1 != null) {
                cell1.removeCellComment();
            }
        }
        Comment comment = drawingPatriarch.createCellComment(clientAnchor);
        comment.setString(new XSSFRichTextString(value));
        comment.setAuthor("国资监管平台");
        cell.setCellComment(comment);
        cell.setCellStyle(cellStyle);
    }

}
