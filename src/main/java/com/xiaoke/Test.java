package com.xiaoke;

import com.spring.ApplicationContext;
import com.xiaoke.service.UserService;

/**
 * ClassName: Test
 * Package: com.xiaoke
 * Description:
 *
 * @Author: XKing
 * @Create: 2023/10/7 - 15:45
 *
 * @Version: 1.0
 */
public class Test {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);

        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();


    }
}
