package com.horizon.flake.api;

import java.lang.annotation.*;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 11:27
 * @see
 * @since : 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Condition {

    /**
     * 列名 
     * @return
     */
    String column();
    
    /**
     * 连接符号   目前只能为（=等于|>大于|<小于|>=大于等于|<=小于等于|!=等于）
     * @return
     */
    Operator symbol() default Operator.custom;
    
}
