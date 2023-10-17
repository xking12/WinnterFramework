package com.spring;

/**
 * ClassName: BeanDefinition
 * Package: com.spring
 * Description:
 *
 * @Author: XKing
 * @Create: 2023/10/14 - 15:38
 * @Version: 1.0
 */
public class BeanDefinition {
    private Class clazz;

    private String scope;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
