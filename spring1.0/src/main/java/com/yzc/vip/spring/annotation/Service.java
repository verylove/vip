package com.yzc.vip.spring.annotation;

import java.lang.annotation.*;

/**
 * @Author: 20160301301
 * @Comment:
 * @Date: 2018/4/19 20:45
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
