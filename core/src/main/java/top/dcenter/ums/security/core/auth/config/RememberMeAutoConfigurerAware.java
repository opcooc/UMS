/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

/**
 * RememberMe 相关配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/28 14:06
 */
@Configuration
@AutoConfigureAfter({SecurityRememberMeAutoConfiguration.class, PropertiesAutoConfiguration.class})
@Slf4j
public class RememberMeAutoConfigurerAware implements HttpSecurityAware, InitializingBean {

    private final ClientProperties clientProperties;
    private final PersistentTokenRepository persistentTokenRepository;
    private final UmsUserDetailsService umsUserDetailsService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RememberMeAutoConfigurerAware(ClientProperties clientProperties,
                                         UmsUserDetailsService umsUserDetailsService,
                                         @Autowired(required = false) PersistentTokenRepository persistentTokenRepository) {
        this.clientProperties = clientProperties;
        this.persistentTokenRepository = persistentTokenRepository;
        this.umsUserDetailsService = umsUserDetailsService;
    }

    @Override
    public void configure(WebSecurity web) {
        // dto nothing
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // dto nothing
    }

    @Override
    public void postConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {

        final ClientProperties.RememberMeProperties rememberMe = clientProperties.getRememberMe();
        if (rememberMe.getEnable())
        // 开启 REMEMBER_ME 功能
        {
            http.rememberMe()
                .rememberMeParameter(rememberMe.getRememberMeParameter())
                .rememberMeCookieName(rememberMe.getRememberMeCookieName())
                .tokenRepository(persistentTokenRepository)
                .tokenValiditySeconds(Integer.parseInt(String.valueOf(rememberMe.getRememberMeTimeout().getSeconds())))
                .userDetailsService(umsUserDetailsService)
                .useSecureCookie(rememberMe.getUseSecureCookie());
        }
        else {
            http.rememberMe().disable();
        }

    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (persistentTokenRepository != null)
        {
            // 如果 JdbcTokenRepositoryImpl 所需的表 persistent_logins 未创建则创建它
            JdbcTokenRepositoryImpl jdbcTokenRepository = (JdbcTokenRepositoryImpl) persistentTokenRepository;
            DataSource dataSource = jdbcTokenRepository.getDataSource();
            if (dataSource == null)
            {
                log.error("错误: 不能获取 dataSource 错误");
                throw new Exception("不能获取 dataSource 错误");
            }
            try (Connection connection = dataSource.getConnection())
            {
                if (connection == null)
                {
                    log.error("错误: 初始化 Remember-me 的 persistent_logins 用户表时发生错误" );
                    throw new Exception("初始化 Remember-me 的 persistent_logins 用户表时发生错误");
                }

                String database;

                try (final PreparedStatement preparedStatement = connection.prepareStatement(SecurityConstants.QUERY_DATABASE_NAME_SQL);
                     ResultSet resultSet = preparedStatement.executeQuery())
                {
                    resultSet.next();
                    database = resultSet.getString(SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                }

                if (StringUtils.hasText(database))
                {

                    try (final Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(clientProperties.getQueryRememberMeTableExistSql(database)))
                    {
                        resultSet.next();
                        int tableCount = resultSet.getInt(SecurityConstants.QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX);
                        if (tableCount < 1)
                        {
                            try (final PreparedStatement preparedStatement =
                                         connection.prepareStatement(JdbcTokenRepositoryImpl.CREATE_TABLE_SQL)) {
                                preparedStatement.executeUpdate();
                                log.info("persistent_logins 表创建成功，SQL：{}", JdbcTokenRepositoryImpl.CREATE_TABLE_SQL);
                                if (!connection.getAutoCommit())
                                {
                                    connection.commit();
                                }
                            }
                        }
                    }
                }
                else
                {
                    log.error("错误: 初始化 Remember-me 的 persistent_logins 用户表时发生错误");
                    throw new Exception("初始化 Remember-me 的 persistent_logins 用户表时发生错误");
                }
            }
        }
    }
}