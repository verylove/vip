package com.yzc.vip.spring.formework.context;

import com.yzc.vip.spring.demo.action.MyAction;
import com.yzc.vip.spring.formework.annotation.GPAutowired;
import com.yzc.vip.spring.formework.annotation.GPController;
import com.yzc.vip.spring.formework.annotation.GPService;
import com.yzc.vip.spring.formework.beans.BeanDefinition;
import com.yzc.vip.spring.formework.beans.BeanPostProcessor;
import com.yzc.vip.spring.formework.beans.BeanWrapper;
import com.yzc.vip.spring.formework.context.support.BeanDefinitionReader;
import com.yzc.vip.spring.formework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tom on 2018/4/21.
 */
public class GPApplicationContext implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;

    //beanDefinitionMap用来保存配置信息
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();  //类：类名首字母小写 接口：权限命名首字母小写

    //用来保证注册式单例的容器
    private Map<String, Object> beanCacheMap = new HashMap<String, Object>(); //主键为com.xxx.xxx

    //用来存储所有的被代理过的对象
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>(); //key为类名首字母小写


    public GPApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        refresh();
    }


    public void refresh() {
        //定位 通过配置文件找到扫描的包
        this.reader = new BeanDefinitionReader(configLocations);

        //加载 扫描目标包下的所有类，存放到容器中
        List<String> beanDefinitions = reader.loadBeanDefinitions();

        //注册 对每一个类，创建其对应的beanDefinition存放到容器中
        doRegisty(beanDefinitions);

        //依赖注入（lazy-init = false），要是执行依赖注入
        //在这里自动调用getBean方法
        doAutowrited();


        MyAction myAction = (MyAction) this.getBean("myAction");
        myAction.query(null, null, "任性的Tom老师");
    }


    //开始执行自动化的依赖注入
    private void doAutowrited() {

        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();

            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }

        }


        for (Map.Entry<String, BeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()) {

            populateBean(beanWrapperEntry.getKey(), beanWrapperEntry.getValue().getWrappedInstance());

        }


    }


    public void populateBean(String beanName, Object instance) {

        Class clazz = instance.getClass();

        //不是所有牛奶都叫特仑苏
        if (!(clazz.isAnnotationPresent(GPController.class) ||
                clazz.isAnnotationPresent(GPService.class))) {
            return;
        }


        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }

            GPAutowired autowired = field.getAnnotation(GPAutowired.class);

            String autowiredBeanName = autowired.value().trim();

            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {

                System.out.println("=======================" + instance + "," + autowiredBeanName + "," + this.beanWrapperMap.get(autowiredBeanName));
                field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }


    }


    //真正的将BeanDefinitions注册到beanDefinitionMap中

    private void doRegisty(List<String> beanDefinitions) {

        //beanName有三种情况:
        //1、默认是类名首字母小写
        //2、自定义名字
        //3、接口注入
        try {
            for (String className : beanDefinitions) {

                Class<?> beanClass = Class.forName(className);

                //如果是一个接口，是不能实例化的
                //用它实现类来实例化
                if (beanClass.isInterface()) {
                    continue;
                }

                BeanDefinition beanDefinition = reader.registerBean(className);
                if (beanDefinition != null) {
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                }

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候，可以自定义名字
                    this.beanDefinitionMap.put(i.getName(), beanDefinition);
                }
                //到这里为止，容器初始化完毕
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    @Override
    public Object getBean(String beanName) {

        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        String className = beanDefinition.getBeanClassName();

        try {

            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            Object instance = instantionBean(beanDefinition);
            if (null == instance) {
                return null;
            }

            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName, beanWrapper);

            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);

//            populateBean(beanName,instance);

            //通过这样一调用，相当于给我们自己留有了可操作的空间
            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    //传一个BeanDefinition，就返回一个实例Bean
    private Object instantionBean(BeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {

            //因为根据Class才能确定一个类是否有实例
            //要保证单例，必须加锁
            if (this.beanCacheMap.containsKey(className)) {
                instance = this.beanCacheMap.get(className);
            } else {
                    Class<?> clazz = Class.forName(className);
                    //dobule check
                    if (!this.beanCacheMap.containsKey(className)) {
                        synchronized (beanCacheMap) {
                            if (!this.beanCacheMap.containsKey(className)) {
                                instance = clazz.newInstance();
                                this.beanCacheMap.put(className, instance);
                            }
                        }
                    }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
