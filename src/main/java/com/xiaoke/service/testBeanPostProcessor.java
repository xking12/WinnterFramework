package com.xiaoke.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

/**
 * ClassName: testBeanPostProcessor
 * Package: com.xiaoke.service
 * Description:
 *
 * @Author: XKing
 * @Create: 2023/10/17 - 20:42
 * @Version: 1.0
 */
@Component
public class testBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(beanName.equals("userService")){
            System.out.println("初始化前~");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后~");
        return bean;
    }
}
