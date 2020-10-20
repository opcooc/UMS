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

package top.dcenter.ums.security.core.permission.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import top.dcenter.ums.security.core.api.permission.service.UriAuthorizeService;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.Instant;

/**
 * uri 权限控制 hasPermission 表达式处理器, <br>
 * 默认启用: <br>
 *     1. 默认启用 <code>httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)"); </code> 方式. <br>
 *     2. 如果开启注解方式(<code> &#64;UriAuthorize 或 &#64;EnableGlobalMethodSecurity(prePostEnabled = true) </code>): 则通过注解 <code>
 *         &#64;PerAuthority("hasPermission('/users/**', '/users/**:list')") </code> 方式生效,
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/3 14:41
 */
@Slf4j
public class UriAuthoritiesPermissionEvaluator  implements PermissionEvaluator {

    private final UriAuthorizeService uriAuthorizeService;

    public UriAuthoritiesPermissionEvaluator(UriAuthorizeService uriAuthorizeService) {
        this.uriAuthorizeService = uriAuthorizeService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        if (targetDomainObject == null || permission == null)
        {
            return false;
        }

        // 如果是 String 类型, 通过注解调用
        if (targetDomainObject instanceof String)
        {
            String uri = ((String) targetDomainObject);
            return hasPermission(authentication, null, uri, permission);
        }
        // 如果是 HttpServletRequest 类型, 通过 spring security 内置权限处理器调用.
        else if (targetDomainObject instanceof HttpServletRequest)
        {
            HttpServletRequest request = (HttpServletRequest) targetDomainObject;

            boolean hasPermission = uriAuthorizeService.hasPermission(authentication, request);

            // 日志参数
            Object principal = authentication.getPrincipal();
            String sid = request.getSession(true).getId();
            String ip = request.getRemoteAddr();
            String uri = request.getRequestURI();
            long now = Instant.now().toEpochMilli();
            String referer = request.getHeader("referer");
            String userAgent = request.getHeader("User-Agent");
            final String method = request.getMethod();

            if (hasPermission)
            {
                // 有访问权限
                log.info("URI权限控制-放行: sid={}, user={}, ip={}, uri={}, method={}, time={}, referer={}, agent={}",
                         sid, principal, ip, uri, method, now, referer, userAgent);

                return true;
            }

            // 没有访问权限
            log.warn("URI权限控制-禁止: sid={}, user={}, ip={}, uri={}, method={}, time={}, referer={}, agent={}",
                     sid, principal, ip, uri, method, now, referer, userAgent);
            return  false;
        }

        log.warn("URI权限控制-传参类型错误: targetDomainObject={}, permission={}", targetDomainObject, permission);
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        if (permission instanceof String)
        {
            String uriAuthority = ((String) permission);
            boolean hasPermission = uriAuthorizeService.hasPermission(authentication, targetType, uriAuthority);

            Object principal = authentication.getPrincipal();
            long now = Instant.now().toEpochMilli();

            // 有访问权限
            if (hasPermission)
            {
                log.info("URI权限控制-放行: user={}, uri={},time={}, permission={}",
                         principal, targetType, now, permission);
                return true;
            }

            // 没有访问权限
            log.warn("URI权限控制-禁止: user={}, uri={}, time={}, permission={}",
                     principal, targetType, now, permission);
        }

        return false;

    }


}