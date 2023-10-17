package com.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: ApplicationContext
 * Package: com.spring
 * Description:
 *
 * @Author: XKing
 * @Create: 2023/10/7 - 15:44
 * @Version: 1.0
 */
public class ApplicationContext {
    private Class configClass;

    private ConcurrentHashMap<String,Object> singletonObjects=new ConcurrentHashMap<>();//单例池
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap=new ConcurrentHashMap<>();//扫描的bean的定义信息
    private List<BeanPostProcessor> beanPostProcessorList=new ArrayList<>();//BeanPostProcessor链表

    public  ApplicationContext(Class configClass){
        this.configClass=configClass;
        //解析配置类
        //componScan注解-->扫描路径--->扫描-->生成beanDefinition-->beanDefinitionMap
        scan(configClass);
        for(Map.Entry<String,BeanDefinition> entry:beanDefinitionMap.entrySet()){
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if(beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanName,beanDefinition);//单例bean
                singletonObjects.put(beanName,bean);
            }
        }
    }


    private Object createBean(String beanName,BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getClazz();
        try {

            Object instance = clazz.getConstructor().newInstance();

            //依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if(declaredField.isAnnotationPresent(Autowired.class)){
                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);
                }
            }
            //Aware接口
            if(instance instanceof BeanNameAware){
                ((BeanNameAware)instance).setBeanName(beanName);
            }

            //postProcessBeforeInitialization,初始化前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance=beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            }

            //Bean初始化
            if(instance instanceof InitializingBean){
                try {
                    ((InitializingBean)instance).afterPropertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //postProcessAfterInitialization,初始化后
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance=beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            }

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    private void scan(Class configClass) {
        //1.获取ComponentScan要扫描的路径
        ComponentScan componentScanAnnotation =(ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();
        System.out.println(path);
        path=path.replace(".","/");
        //2.获取类加载器 applicationClassLoader
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        //2.1 加载要扫描的包路径
        URL resource = classLoader.getResource(path);
        File file = new File(resource.getFile());
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
                //2.2 D:\JavaCodes\WinnterFramework\target\classes\com\xiaoke\service\UserService.class 路径转化为
                //com.xiaoke.service.UserService
                String loadPath=f.toString();
                if(loadPath.endsWith(".class")){
                    //2.3 是否是class文件
                    loadPath=loadPath.substring(loadPath.indexOf("com"),loadPath.indexOf(".class"));
                    loadPath=loadPath.replace('\\','.');
    //                System.out.println(loadPath);
                    Class<?> aClass = null;
                    try {
                        aClass = classLoader.loadClass(loadPath);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    if(aClass.isAnnotationPresent(Component.class)){
                        //调用BeanPostProcessor
                        if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                            try {
                                //创建BeanPostProcessor对象然后存入List中，
                                // 之后创建bean对象的时候就可以从链表中拿出BeanPostProcessor进行方法调用
                                BeanPostProcessor instance = (BeanPostProcessor) aClass.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }
                            catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        //2.4 扫描的类是否加入了@Component注解,加了注解才是bean
                        //解析类，判断当前Bean是否是单例的--->BeanDefinition
                        //创建BeanDefinition
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setClazz(aClass);
                        //根据component注解获取beanName
                        Component componentAnnotation = aClass.getDeclaredAnnotation(Component.class);
                        String beanName = componentAnnotation.value();
                        //解析scope注解
                        if(aClass.isAnnotationPresent(Scope.class)){
                            //原型bean
                            Scope scopeAnnotation = aClass.getDeclaredAnnotation(Scope.class);
                            beanDefinition.setScope(scopeAnnotation.value());
                        }else {
                            //单例bean
                            beanDefinition.setScope("singleton");
                        }
                        beanDefinitionMap.put(beanName,beanDefinition);
                    }
                }
            }
        }
    }

    public Object getBean(String beanName){
        if(beanDefinitionMap.containsKey(beanName)){
            //说明容器里有要get的bean
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope().equals("singleton")){
                Object o = singletonObjects.get(beanName);
                return o;
            }else {
                //创建新的bean
                Object bean = createBean(beanName,beanDefinition);
                return bean;
            }
        }else {
            //说明容器里没有要get的bean
            throw new NullPointerException();
        }
    }
}
