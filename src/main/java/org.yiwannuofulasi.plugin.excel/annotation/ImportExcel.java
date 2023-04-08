package org.yiwannuofulasi.plugin.excel.annotation;

import java.lang.annotation.*;

/**
 * @author cheng-qiang
 * @date 2022/11/9 15:24
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ImportExcel {
    /**
     * 字典表映射类路径
     */
    String classPath() default "";
}
