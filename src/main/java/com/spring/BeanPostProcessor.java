package com.spring;

/**
 * ClassName: BeanPostProcessor
 * Package: com.spring
 * Description:
 *
 * @Author: XKing
 * @Create: 2023/10/17 - 20:40
 * @Version: 1.0
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean,String beanName);

    Object postProcessAfterInitialization(Object bean,String beanName);
}
