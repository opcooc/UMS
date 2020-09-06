package top.dcenter.security.social.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.HttpSecurityAware;
import top.dcenter.security.social.properties.SocialProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * social 第三方授权登录注册相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 22:22
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social", name = "social-sign-in-is-open", havingValue = "true")
@AutoConfigureAfter({SocialAuthenticationSignUpConfig.class})
@Slf4j
public class SocialAuthenticationSignUpConfigurerAware implements HttpSecurityAware {

    private final SocialAuthenticationSignUpConfig socialAuthenticationSignUpConfig;
    private final SocialProperties socialProperties;

    public SocialAuthenticationSignUpConfigurerAware(SocialProperties socialProperties, SocialAuthenticationSignUpConfig socialAuthenticationSignUpConfig) {
        this.socialProperties = socialProperties;
        this.socialAuthenticationSignUpConfig = socialAuthenticationSignUpConfig;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 短信验证码登录配置
        if (this.socialAuthenticationSignUpConfig != null)
        {
            http.apply(this.socialAuthenticationSignUpConfig);
        }
    }

    @Override
    public Map<String, Map<String, Set<String>>> getAuthorizeRequestMap() {
        final Map<String, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(this.socialProperties.getSignUpUrl(), null);
        permitAllMap.put(this.socialProperties.getFailureUrl(), null);
        permitAllMap.put(this.socialProperties.getSignInUrl(), null);

        Map<String, Map<String, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.permitAll, permitAllMap);

        return resultMap;

    }
}


