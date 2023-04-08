package org.yiwannuofulasi.plugin.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 数据读取监听器
 * @author cheng-qiang
 * @date 2022年08月16日10:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnalysisEventCustomListener extends AnalysisEventListener<Map<Integer, String>> {

    private List<List<String>> dataList;

    public AnalysisEventCustomListener(){
        this.dataList = new ArrayList<>();
    }

    @Override
    public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
        LinkedList<String> linkedList = new LinkedList<>();
        integerStringMap.forEach((k,v) -> linkedList.add(v));
        this.dataList.add(linkedList);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
