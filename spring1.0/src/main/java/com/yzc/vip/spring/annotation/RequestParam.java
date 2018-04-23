package com.yzc.vip.spring.annotation;

import java.lang.annotation.*;

/**
 * @Author: 20160301301
 * @Comment:
 * @Date: 2018/4/19 20:43
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
