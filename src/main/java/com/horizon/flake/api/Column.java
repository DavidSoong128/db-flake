package com.horizon.flake.api;

import java.lang.annotation.*;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 17:05
 * @see
 * @since : 1.0.0
 */

/**
 * 表结构映射
 * name 列名
 * type 列类型
 * autoOper 当列为int类型并且update该条数据时，是否允许列以自己为基准进行增减，
 *          如update table set sku=sku+10 (正数为+)
 *          或者update table set sku=sku-10 (负数为-)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.LOCAL_VARIABLE})
public @interface Column {

    /**
     * 列名
     * @return
     */
    String name();
    
    /**
     * 列类型
     * @return
     */
    String type();
    
    /**
     * autoOper 当列为int类型并且update该条数据时，是否允许列以自己为基准进行增减，
     * 如update table set sku=sku+10 (正数为+)
     * 或者update table set sku=sku-10 (负数为-)
     * @return false 默认不允许自增减 / true 允许自增减
     */
    boolean autoOper() default false;
    
}
