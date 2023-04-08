package org.yiwannuofulasi.plugin.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.yiwannuofulasi.plugin.excel.annotation.ImportExcel;
import org.yiwannuofulasi.plugin.excel.utils.EasyExcelUtils;
import org.yiwannuofulasi.plugin.excel.utils.RedisService;
import org.yiwannuofulasi.plugin.excel.utils.SpringContextUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author 程强
 * @date 2023/2/13
 * @Description 名称编号转换器
 */
public class NameCodeConverter implements Converter<String> {

    private RedisService redisService;

    public NameCodeConverter(){
        this.redisService = SpringContextUtils.getBean(RedisService.class);
    }

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToJavaData(ReadConverterContext<?> context) throws Exception {
        MapType mapType = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Map.class);
        Object map = redisService.get(EasyExcelUtils.DICTIONARY_TABLE);
        String mapInfo = JSON.toJSON(map).toString();
        Map<String, Map<String, String>> dictionaryTableDataMap = objectMapper.readValue(mapInfo, mapType);
        String name = context.getReadCellData().getStringValue();
        Field field = context.getContentProperty().getField();
        ImportExcel annotation = field.getAnnotation(ImportExcel.class);
        if (annotation!=null){
            String classPathKey = annotation.classPath();
            Map<String, String> tableMap = dictionaryTableDataMap.get(classPathKey);
            if (tableMap!=null){
                return tableMap.get(name);
            }
        }
        return null;
    }
}
