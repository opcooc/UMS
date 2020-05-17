package top.dcenter.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_IMAGE_CODE_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_MOBILE_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_SMS_CODE_NAME;

/**
 * 校验码属性
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:52
 */
@Getter
@Setter
@ConfigurationProperties("security.code")
public class ValidateCodeProperties {

    private ImageCodeProperties image = new ImageCodeProperties();
    private SmsCodeProperties sms = new SmsCodeProperties();


    /**
     * 图片验证码属性
     * @author zhailiang
     * @medifiedBy  zyw
     * @version V1.0  Created by 2020/5/4 16:04
     */
    @Getter
    @Setter
    public class SmsCodeProperties {

        public SmsCodeProperties() {
            List<String> list = new ArrayList<>();
            this.authUrls = list;
        }

        /**
         * 设置记住我功能的 session 的缓存时长，默认 7 * 24 * 3600
         */
        private int rememberMeSeconds = 7 * 24 * 3600;

        /**
         * 校验码的验证码长度，默认 6 位
         */
        private int length = 6;
        /**
         * 校验码的有效时间，默认 120秒
         */
        private int expire = 120;
        /**
         * 提交短信校验码请求时，请求中带的短信校验码变量名，默认 smsCode
         */
        private String requestParamSmsCodeName = DEFAULT_REQUEST_PARAM_SMS_CODE_NAME;
        /**
         * 提交短信校验码请求时，请求中带的手机号变量名，默认 mobile
         */
        private String requestParamMobileName = DEFAULT_REQUEST_PARAM_MOBILE_NAME;

        /**
         * 设置需要短信校验码认证的 uri，多个 uri 用 “，”号分开支持通配符，如：/hello,/user/*；默认为 /authentication/form
         */
        private List<String> authUrls;

    }

    /**
     * 图片验证码属性
     * @author zhailiang
     * @medifiedBy  zyw
     * @version V1.0  Created by 2020/5/4 16:04
     */
    @Getter
    @Setter
    public class ImageCodeProperties {

        public ImageCodeProperties() {
            List<String> list = new ArrayList<>();
            list.add(DEFAULT_LOGIN_PROCESSING_URL_FORM);
            this.authUrls = list;
        }
        /**
         * 图片校验码的宽度，默认 270； 宽度如果小于 height * 45 / 10, 则 width = height * 45 / 10
         */
        private int width = 270;
        /**
         * 图片校验码的高度，默认 60
         */
        private int height = 60;
        /**
         * 校验码的验证码长度，默认 4位
         */
        private int length = 4;
        /**
         * 校验码的有效时间，默认 1200秒
         */
        private int expire = 1200;


        /**
         * 图片校验码的宽度的字面量，默认 width
         */
        private String requestParaWidthName = "width";
        /**
         * 图片校验码的高度的字面量，默认 height
         */
        private String requestParaHeightName = "height";


        /**
         * 提交图片校验码请求时，请求中带点图片校验码变量名，默认 imageCode
         */
        private String requestParamImageCodeName = DEFAULT_REQUEST_PARAM_IMAGE_CODE_NAME;
        /**
         * 设置需要图片校验码认证的 uri，多个 uri 用 “，”号分开支持通配符，如：/hello,/user/*；默认为 /authentication/form
         */
        private List<String> authUrls;

    }
}