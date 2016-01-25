package com.horizon.flake.api;

import java.lang.annotation.*;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 11:27
 * @see
 * @since : 1.0.0
 */
/**
 * 路由规则ID(标识一条数据的唯一ID，新增修改删除动作时此ID必须相同)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Route {
    
}
