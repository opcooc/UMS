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

import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.util.ConvertUtil;
import top.dcenter.ums.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.ums.security.social.api.callback.RedirectUrlHelperService;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static top.dcenter.ums.security.core.consts.SecurityConstants.CALLBACK_URL_KEY_IN_STATE;
import static top.dcenter.ums.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.UUID_INTERCEPT_NUMBER;

/**
 * 解析 state，返回真实的回调地址，支持通过统一的回调地址路由到多个回调地址的解析助手。<br><br>
 *     注意：如果修改回调地址解密逻辑，同时要修改 {@link BaseOAuth2ConnectionFactory#buildReturnToUrl(HttpServletRequest, Set)} 与
 *     {@link BaseOAuth2ConnectionFactory#generateState(String)}的加密逻辑。<br><br>
 *     自定义此逻辑: 实现 {@link RedirectUrlHelperService} 并注入 IOC 容器即可替换
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/25 20:54
 */
public class RedirectUrlHelperServiceImpl implements RedirectUrlHelperService {
    /**
     * 解析 state，返回真实的回调地址
     * @param state state 参数值
     * @return 返回真实回调地址 redirectUrl, 如果传入 state 为格式不正确，返回 null
     */
    @Override
    public String decodeRedirectUrl(@NonNull String state) {
        // 解密 state
        if (state.length() <= UUID_INTERCEPT_NUMBER)
        {
            return null;
        }
        byte[] router = Base64.getDecoder().decode(state.substring(UUID_INTERCEPT_NUMBER).getBytes(UTF_8));
        // 提取 redirectUrl
        Map<String, Object> routerMap = ConvertUtil.string2JsonMap(new String(router, UTF_8),
                                                                   URL_PARAMETER_SEPARATOR,
                                                                   KEY_VALUE_SEPARATOR);
        return (String) routerMap.get(CALLBACK_URL_KEY_IN_STATE);
    }
}