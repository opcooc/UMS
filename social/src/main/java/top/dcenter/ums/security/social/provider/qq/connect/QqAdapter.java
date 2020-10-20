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

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import top.dcenter.ums.security.social.provider.qq.api.Qq;
import top.dcenter.ums.security.social.provider.qq.api.QqUserInfo;

/**
 * QQ 服务适配器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/8 21:57
 */
@Slf4j
public class QqAdapter implements ApiAdapter<Qq> {


    private String providerId;

    public QqAdapter() {
    }

    public QqAdapter(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public boolean test(Qq api) {
        return true;
    }

    @Override
    public void setConnectionValues(Qq api, ConnectionValues values) throws ApiException {
        try
        {
            QqUserInfo userInfo = api.getUserInfo();
            values.setDisplayName(userInfo.getNickname());
            values.setImageUrl(userInfo.getFigureurl_qq_1());
            values.setProfileUrl(null);
            values.setProviderUserId(userInfo.getOpenId());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new ApiException(providerId, e.getMessage(), e);
        }
    }

    @Override
    public UserProfile fetchUserProfile(Qq api) {
        // qq no homepage
        return null;
    }

    @Override
    public void updateStatus(Qq api, String message) {
        // dto nothing
    }
}