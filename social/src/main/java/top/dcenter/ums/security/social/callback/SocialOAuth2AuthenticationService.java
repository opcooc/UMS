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

package top.dcenter.ums.security.social.callback;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.social.api.callback.BaseOAuth2ConnectionFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static top.dcenter.ums.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_IDENTIFIER;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * 对 {@link OAuth2AuthenticationService} 的扩展. <br><br>
 * 通过覆写 buildReturnToUrl(request)方法, 使 social 支持通过统一的回调地址路由到多个回调地址。
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/21 15:54
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class SocialOAuth2AuthenticationService<S> extends OAuth2AuthenticationService<S> {

    public SocialOAuth2AuthenticationService(OAuth2ConnectionFactory<S> connectionFactory) {
        super(connectionFactory);
    }

    /**
     * 使 social 支持通过统一的回调地址路由到多个回调地址。<br><br>
     * 如要自定义此逻辑，实现 {@link BaseOAuth2ConnectionFactory#buildReturnToUrl(HttpServletRequest, Set)} 即可。
     * @param request   request
     * @return  返回统一的回调地址
     */
    @Override
    protected String buildReturnToUrl(HttpServletRequest request) {
        OAuth2ConnectionFactory<S> connectionFactory = getConnectionFactory();
        Set<String> returnToUrlParameters = getReturnToUrlParameters();

        if (connectionFactory instanceof BaseOAuth2ConnectionFactory)
        {
            // 获取自定义逻辑的回调地址
            //noinspection rawtypes
            String returnToUrl = ((BaseOAuth2ConnectionFactory) connectionFactory).buildReturnToUrl(request, returnToUrlParameters);
            if (!StringUtils.isEmpty(returnToUrl))
            {
                // 返回用户自定义实现
                return returnToUrl;
            }
        }
        // 如果自定义实现返回null，则使用默认实现
        StringBuffer sb = request.getRequestURL();
        // 去掉 providerId，改成统一的回调地址
        sb.setLength(sb.lastIndexOf(URL_SEPARATOR));
        // url 添加参数
        sb.append(URL_PARAMETER_IDENTIFIER);
        for (String name : returnToUrlParameters) {
            // Assume for simplicity that there is only one value
            String value = request.getParameter(name);

            if (value == null) {
                continue;
            }
            sb.append(name).append(KEY_VALUE_SEPARATOR).append(value).append(URL_PARAMETER_SEPARATOR);
        }
        // strip trailing ? or &
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}