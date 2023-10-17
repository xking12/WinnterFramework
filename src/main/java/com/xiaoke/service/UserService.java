package com.xiaoke.service;

import com.spring.*;

/**
 * ClassName: UserService
 * Package: com.xiaoke.service
 * Description:
 *
 * @Author: XKing
 * @Create: 2023/10/7 - 16:11
 * @Version: 1.0
 */
@Component("userService")
@Scope("prototype")
public class UserService implements InitializingBean,BeanNameAware {
    @Autowired
    private OrderService orderService;

    private String beanName;

    public void test(){
        System.out.println(orderService);
        System.out.println(beanName);
    }

    @Override
    public void setBeanName(String name) {
        beanName=name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化");
    }
}
