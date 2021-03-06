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

package top.dcenter.ums.security.core.api.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeCacheType;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.util.IpUtil;

import javax.servlet.http.HttpServletRequest;

import static java.util.Objects.requireNonNull;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;

/**
 * 验证码处理逻辑的默认实现抽象类
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/6 10:14
 * @author YongWu zheng
 */
@Slf4j
public abstract class AbstractValidateCodeProcessor implements ValidateCodeProcessor {

    protected final ValidateCodeGeneratorHolder validateCodeGeneratorHolder;

    protected final StringRedisTemplate stringRedisTemplate;

    protected final ValidateCodeCacheType validateCodeCacheType;

    protected final Class<? extends ValidateCode> validateCodeClass;

    /**
     * 验证码处理逻辑的默认实现抽象类.<br><br>
     *
     * @param validateCodeGeneratorHolder   validateCodeGeneratorHolder
     * @param validateCodeCacheType         验证码缓存类型
     * @param validateCodeClass             验证码 class
     * @param stringRedisTemplate           stringRedisTemplate, 缓存类型不为 redis 时可以为 null
     */
    public AbstractValidateCodeProcessor(@NonNull ValidateCodeGeneratorHolder validateCodeGeneratorHolder,
                                         @NonNull ValidateCodeCacheType validateCodeCacheType,
                                         @NonNull Class<? extends ValidateCode> validateCodeClass,
                                         @Nullable StringRedisTemplate stringRedisTemplate) {
        this.validateCodeGeneratorHolder = validateCodeGeneratorHolder;
        this.validateCodeCacheType = validateCodeCacheType;
        this.validateCodeClass = validateCodeClass;
        this.stringRedisTemplate = stringRedisTemplate;
        if (ValidateCodeCacheType.REDIS.equals(validateCodeCacheType)) {
            requireNonNull(stringRedisTemplate, "stringRedisTemplate cannot be null");
        }
    }

    @Override
    public final boolean produce(ServletWebRequest request) throws ValidateCodeException {

        ValidateCode validateCode;
        HttpServletRequest req = request.getRequest();
        String ip = IpUtil.getRealIp(req);
        String sid = request.getSessionId();
        String uri = req.getRequestURI();
        try
        {
            validateCode = generate(request);
            boolean validateStatus = sent(request, validateCode);
            if (!validateStatus)
            {
                log.warn("发送验证码失败: ip={}, sid={}, uri={}, validateCode={}",
                         ip, sid, uri, validateCode.toString());
                return false;
            }
            save(request, validateCode);
        }
        catch (Exception e)
        {
            this.validateCodeCacheType.removeCache(request, getValidateCodeType(), this.stringRedisTemplate);

            if (e instanceof ValidateCodeException)
            {
                ValidateCodeException exception = (ValidateCodeException) e;
                String msg = String.format("生成验证码失败: error=%s, ip=%s, uid=%s, sid=%s, uri=%s, data=%s",
                                              exception.getMessage(), ip, exception.getUid(), sid, uri, exception.getData());
                log.warn(msg, exception);
                throw exception;
            }
            else
            {
                String msg = String.format("生成验证码失败: error=%s, ip=%s, sid=%s, uri=%s",
                                              e.getMessage(), ip, sid, uri);
                log.warn(msg, e);
                throw new ValidateCodeException(GET_VALIDATE_CODE_FAILURE, e, ip, uri);
            }
        }
        return true;
    }

    @Override
    public final ValidateCode generate(ServletWebRequest request) {
        try
        {
            ValidateCodeGenerator<?> validateCodeGenerator = getValidateCodeGenerator(getValidateCodeType());
            final ValidateCode validateCode = (ValidateCode) validateCodeGenerator.generate(request.getRequest());
            if (validateCode == null) {
                throw new ValidateCodeException(GET_VALIDATE_CODE_FAILURE, IpUtil.getRealIp(request.getRequest()), request.getRequest().getRequestURI());
            }
            return validateCode;
        }
        catch (ValidateCodeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ValidateCodeException(GET_VALIDATE_CODE_FAILURE, e, IpUtil.getRealIp(request.getRequest()), request.getRequest().getRequestURI());
        }
    }

    @Override
    public boolean save(ServletWebRequest request, ValidateCode validateCode) {

        ValidateCodeType validateCodeType = getValidateCodeType();
        try
        {
            return this.validateCodeCacheType.save(request, validateCode, validateCodeType, stringRedisTemplate);
        }
        catch (Exception e)
        {
            String msg = String.format("验证码保存到Session失败: error=%s, ip=%s, code=%s",
                                       e.getMessage(),
                                       IpUtil.getRealIp(request.getRequest()),
                                       validateCode);
            log.error(msg, e);
            return false;
        }

    }



    /**
     * 发送验证码，由子类实现,
     * 发送失败，必须清除 sessionKey 的缓存，示例代码: <br><br>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;sessionStrategy.removeAttribute(request, sessionKey); </p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;sessionKey 获取方式：</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * 如果不清楚是哪种类型 sessionKey，用如下方式：</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * ValidateCodeType validateCodeType = getValidateCodeType();</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * String sessionKey = validateCodeType.getKeyPrefix();</p>
     *
     * @param request      ServletWebRequest
     * @param validateCode 验证码对象
     * @return 是否发送成功的状态
     */
    @Override
    public abstract boolean sent(ServletWebRequest request, ValidateCode validateCode);


    /**
     * 校验验证码
     * @param request   {@link ServletWebRequest}
     * @throws ValidateCodeException 验证码异常
     */
    @Override
    public void validate(ServletWebRequest request) throws ValidateCodeException {

        ValidateCodeType validateCodeType = getValidateCodeType();
        ValidateCodeGenerator<?> validateCodeGenerator = getValidateCodeGenerator(validateCodeType);
        defaultValidate(request, validateCodeGenerator.getRequestParamValidateCodeName(),
                        this.validateCodeClass, this.validateCodeCacheType,this.stringRedisTemplate);

    }



    /**
     * 获取验证码类型
     * @return {@link ValidateCodeType}
     */
    @Override
    public abstract ValidateCodeType getValidateCodeType();

    /**
     * 获取验证码生成器
     * @param type 验证码类型
     * @return 验证码生成器
     */
    protected ValidateCodeGenerator<?> getValidateCodeGenerator(ValidateCodeType type) throws ValidateCodeException {
        try
        {
            ValidateCodeGenerator<?> validateCodeGenerator = validateCodeGeneratorHolder.findValidateCodeGenerator(type);
            if (validateCodeGenerator != null)
            {
                return validateCodeGenerator;
            }
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, null, type.name());
        }
        catch (Exception e)
        {
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, e, null, type.name());
        }
    }

}