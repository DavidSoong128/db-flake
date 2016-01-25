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
@Target({ ElementType.TYPE ,ElementType.FIELD})
public @interface Table {
    
    String value() default "";
}
