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

package top.dcenter.ums.security.social.provider.qq.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import top.dcenter.ums.security.core.util.ConvertUtil;
import top.dcenter.ums.security.social.provider.qq.api.Qq;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static top.dcenter.ums.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

/**
 * QqOauth2Template
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/10 22:43
 */
@Slf4j
public class QqOauth2Template extends OAuth2Template {

    private final ObjectMapper objectMapper;

    public QqOauth2Template(String clientId,
                            String clientSecret,
                            String authorizeUrl,
                            String accessTokenUrl,
                            ObjectMapper objectMapper) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
        // 设置 UseParameters 时 带上 clientId 与 clientSecret
        setUseParametersForClientAuthentication(true);
        this.objectMapper = objectMapper;
    }

    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        // 添加处理 html 格式的消息体转换器
        messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    @Override
    protected QqAccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        Map<String, Object> responseMap = extractAccessGrantMap(getRestTemplate().postForObject(accessTokenUrl, parameters
                , String.class));
        Long expiresIn = null;
        if (!StringUtils.isEmpty(responseMap.get(Qq.EXPIRES_IN)))
        {
            expiresIn = Long.valueOf((String) responseMap.get(Qq.EXPIRES_IN));
        }

        return new QqAccessGrant((String) responseMap.get(Qq.ACCESS_TOKEN),
                                 (String) responseMap.get(Qq.SCOPE),
                                 (String) responseMap.get(Qq.REFRESH_TOKEN), expiresIn);
    }

    /**
     * 根据 responseResult 返回 Map.
     * @param responseResult 格式化字符串, 例如: name=tom,age=18
     * @return  当 responseResult 无效或内部出现异常直接返回空的 {@link Map} 实列
     */
    private Map<String, Object> extractAccessGrantMap(String responseResult) {
        return ConvertUtil.string2JsonMap(responseResult, URL_PARAMETER_SEPARATOR, KEY_VALUE_SEPARATOR);
    }
}