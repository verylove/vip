package com.yzc.vip.spring.annotation;

import java.lang.annotation.*;

/**
 * Creator      : 20160301301
 * Created Date : 2018/4/19
 * Comment      : 20:40
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
