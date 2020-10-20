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

package top.dcenter.ums.security.core.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 切面类型：对方法运行时间的统计
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/2 18:26
 */
@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
@Aspect
@Component
@Slf4j
public class TimeAspect {
    @Around("execution(* top.dcenter.ums.security.core.oauth.repository.jdbc.Auth2JdbcUsersConnectionRepository.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        long start = Instant.now().toEpochMilli();
        Object obj = pjp.proceed();
        log.info("{}.{} 耗时：{} {}", pjp.getSignature().getDeclaringType().getName(), pjp.getSignature().getName(),
                 (Instant.now().toEpochMilli() - start), "毫秒");
        return obj;
    }
    @Around("execution(* top.dcenter.ums.security.core.oauth.repository.jdbc.Auth2JdbcUsersConnectionTokenRepository.*(..))")
    public Object handlerControllerMethod1(ProceedingJoinPoint pjp) throws Throwable {
        long start = Instant.now().toEpochMilli();
        Object obj = pjp.proceed();
        log.info("{}.{} 耗时：{} {}", pjp.getSignature().getDeclaringType().getName(), pjp.getSignature().getName(),
                 (Instant.now().toEpochMilli() - start), "毫秒");
        return obj;
    }
}