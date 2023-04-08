package org.yiwannuofulasi.plugin.excel.annotation;

/**
 * @author cheng-qiang
 * @date 2022年11月09日15:27
 */
public interface ExcelDynamicSelect {
    /**
     * 获取动态生成的下拉框可选数据
     * @return 动态生成的下拉框可选数据
     */
    String getSource();
}
