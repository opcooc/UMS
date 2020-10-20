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

package top.dcenter.ums.security.social.provider.qq.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.io.IOException;

/**
 * 请求返回信息绑定服务实现
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/8 20:13
 */
@Getter
@Setter
@Slf4j
public class QqImpl extends AbstractOAuth2ApiBinding implements Qq {

    /**
     * 获取 Qq openid 链接
     */
    public static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%S";
    /**
     * 获取 Qq userInfo 链接
     */
    public static final String URL_GET_USER_INFO = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";
    private String appId;
    private String openId;

    private final ObjectMapper objectMapper;

    public QqImpl(String accessToken, String appId, ObjectMapper objectMapper) {
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.appId = appId;

        String url = String.format(URL_GET_OPENID, accessToken);
        // callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} )
        String callback = getRestTemplate().getForObject(url, String.class);
        if (log.isDebugEnabled())
        {
            log.debug("qq openid: {}", callback);
        }
        this.openId = StringUtils.substringBetween(callback, "\"openid\":\"", "\"}");

        this.objectMapper = objectMapper;
    }

    @Override
    public QqUserInfo getUserInfo() throws IOException {
        String url = String.format(URL_GET_USER_INFO, appId, openId);
        String callback = getRestTemplate().getForObject(url, String.class);
        if (log.isDebugEnabled())
        {
            log.debug("qq userInfo = {}", callback);
        }
        QqUserInfo qqUserInfo = objectMapper.readValue(callback, QqUserInfo.class);
        qqUserInfo.setOpenId(openId);
        return qqUserInfo;
    }

}