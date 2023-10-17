package com.spring;

/**
 * ClassName: InitializingBean
 * Package: com.spring
 * Description:
 *
 * @Author: XKing
 * @Create: 2023/10/16 - 11:03
 * @Version: 1.0
 */
public interface InitializingBean {

    void afterPropertiesSet() throws Exception;

}
