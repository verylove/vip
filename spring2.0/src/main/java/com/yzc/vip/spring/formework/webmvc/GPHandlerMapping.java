package com.yzc.vip.spring.formework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by Tom on 2018/4/22.
 */
//解析url路径到具体的控制器的方法
public class GPHandlerMapping {
    private Object controller;
    private Method method;
    private Pattern pattern;

    public GPHandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
