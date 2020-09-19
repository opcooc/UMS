package top.dcenter.ums.security.core.api.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 用户名密码、手机短信登录与注册服务：<br><br>
 *     1. 用于用户名密码登录与手机短信登录逻辑。<br><br>
 *     2. 用于用户名密码注册与手机短信注册逻辑。<br><br>
 * @author zyw
 * @version V1.0  Created by 2020/5/16 11:48
 */
public abstract class AbstractUserDetailsService implements UserDetailsService, RegisterUserDetailsService, InitializingBean {
    /**
     * 用于密码加解密
     */
    protected PasswordEncoder passwordEncoder;
    protected ApplicationContext applicationContext;

    public AbstractUserDetailsService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void afterPropertiesSet() {
        // 解决 Autowired 循环引用的问题。
        passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

    }
}
