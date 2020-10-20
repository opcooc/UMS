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

package top.dcenter.ums.security.social.provider.gitee.adapter;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.social.oauth2.OAuth2Version;

/**
 * 针对 Gitee，处理 gitee 服务商回调时返回的 JSON 进行解析。
 * Strategy enumeration where each value carries an interceptor defining how an access token is carried on API requests.
 * @author Craig Walls
 */
public enum GiteeTokenStrategy {

	/**
	 * Indicates that the access token should be carried in the Authorization header as an OAuth2 Bearer token.
	 */
	AUTHORIZATION_HEADER {
		@Override
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2RequestInterceptor(accessToken, oauth2Version);
		}
	},
	/**
	 * Indicates that the access token should be carried as a query parameter named "access_token".
	 */
	ACCESS_TOKEN_PARAMETER {
		@Override
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2TokenParameterRequestInterceptor(accessToken);
		}
	},
	/**
	 * Indicates that the access token should be carried as a query parameter named "oauth_token".
	 */
	OAUTH_TOKEN_PARAMETER {
		@Override
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2TokenParameterRequestInterceptor(accessToken, "oauth_token");
		}
	};

	abstract ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version);

}