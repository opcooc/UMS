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

package demo.permission.service.impl;

import demo.entity.UriResourcesDTO;
import demo.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.api.permission.service.UpdateAndCacheAuthoritiesService;
import top.dcenter.ums.security.core.util.ConvertUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * request 的 uri 访问权限控制服务.<br>
 * 注意: 角色的 uri(资源) 权限更新与缓存<br>
 * 1. 基于 角色 的权限控制: 简单的实现 {@link UpdateAndCacheAuthoritiesService#updateAuthoritiesOfAllRoles()} 的接口, 实现所有角色 uri(资源) 的权限
 * Map(role, map(uri, Set(permission))) 的更新与缓存本机内存.
 * 2. 基于 SCOPE 的权限控制: 情况复杂一点, 但 SCOPE 类型比较少, 也还可以像 1 的方式实现缓存本机内存与更新.
 * 3. 基于 多租户 的权限控制: 情况比较复杂, 租户很少的情况下, 也还可以全部缓存在本机内存, 通常情况下全部缓存本机内存不现实, 只能借助于类似 redis 等的内存缓存.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Service
@Slf4j
public class UriAuthorizeServiceImpl extends AbstractUriAuthorizeService implements UpdateAndCacheAuthoritiesService, InitializingBean {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 所有角色 uri(资源) 权限 Map(role, map(uri, Set(permission)))
     */
    private volatile Map<String, Map<String, Set<String>>> rolesAuthoritiesMap;

    private final Object lock = new Object();

    @Override
    public void updateAuthoritiesOfAllRoles() {

        synchronized (lock) {
            // 更新并缓存所有角色 uri(资源) 权限 Map<role, Map<uri, Set<permission>>>
            this.rolesAuthoritiesMap = updateRolesAuthorities();
        }

    }

    @Override
    public void afterPropertiesSet() {
        // 更新并缓存所有角色 uri(资源) 权限 Map<role, Map<uri, Set<permission>>>
        updateAuthoritiesOfAllRoles();

    }

    /**
     * 获取角色的 uri 的权限 map.<br>
     *     返回值为: Map(role, Map(uri, UriResourcesDTO))
     * @return Map(String, Map(String, String)) 的 key 为必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), value 为 UriResourcesDTO map
     * (key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为 UriResourcesDTO).
     */
    @Override
    @NonNull
    public Map<String, Map<String, Set<String>>> getRolesAuthorities() {

        if (this.rolesAuthoritiesMap != null) {
            return this.rolesAuthoritiesMap;
        }
        else {
            return updateRolesAuthorities();
        }

    }

    /**
     * @param status   返回状态
     * @param response response
     */
    @Override
    public void handlerError(int status, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        try (PrintWriter writer = response.getWriter())
        {
            writer.write("{\"msg\":\"demo: 您没有访问权限或未登录\"}");
            writer.flush();
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 更新并缓存所有角色 uri(资源) 权限 Map<role, Map<uri, Set<permission>>>.<br>
     * @return 所有角色 uri(资源) 权限 Map<role, Map<uri, Set<permission>>>
     */
    @NonNull
    private Map<String, Map<String, Set<String>>> updateRolesAuthorities() {

        // 从数据源获取所有角色的权限
        final Map<String, Map<String, UriResourcesDTO>> rolesAuthoritiesMap = sysRoleService.getRolesAuthorities();
        final Map<String, Map<String, Set<String>>> rolesAuthorities = new HashMap<>(rolesAuthoritiesMap.size());
        rolesAuthoritiesMap.forEach((key, value) -> rolesAuthorities.compute(key, (k, v) ->
        {
            if (v == null) {
                v = new HashMap<>(value.size());
            }
            final Map<String, Set<String>> finalV = v;
            value.forEach((uri, dto) ->
                                  finalV.put(uri,
                                             ConvertUtil.string2Set(dto.getPermission(),
                                                                    PERMISSION_DELIMITER)));
            v = finalV;
            return v;
        }));

        this.rolesAuthoritiesMap = rolesAuthorities;

        return rolesAuthorities;
    }

}