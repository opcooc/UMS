package top.dcenter.security.core.consts;

/**
 * social 常量
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/6 21:29
 */
public class SecurityConstants {

    // =================== 认证授权相关 ===================

    /**
     * 当请求需要身份认证时，默认跳转的url
     */
    public static final String DEFAULT_UNAUTHENTICATION_URL = "/authentication/require";
    /**
     * 默认的用户名密码登录请求处理url
     */
    public static final String DEFAULT_LOGIN_PROCESSING_URL_FORM = "/authentication/form";
    /**
     * 默认的手机验证码登录请求处理url
     */
    public static final String DEFAULT_LOGIN_PROCESSING_URL_MOBILE = "/authentication/mobile";
    /**
     * 默认的 social OAuth2 注册请求处理url
     */
    public static final String DEFAULT_LOGIN_PROCESSING_URL_SOCIAL = "/authentication/social";
    /**
     * 默认登录页面
     */
    public static final String DEFAULT_LOGIN_PAGE_URL = "/security/login.html";
    /**
     * session失效默认的跳转地址
     */
    public static final String DEFAULT_SESSION_INVALID_URL = "/session/invalid";

    /**
     * The default name for remember me parameter name and remember me cookie name
     */
    public static final String DEFAULT_REMEMBER_ME_NAME = "remember-me";

    // =================== error相关 ===================

    /**
     * 服务器内部错误信息
     */
    public static final String INTERNAL_SERVER_ERROR_MSG = "服务器开小差，请重试";



    // =================== 验证码相关 ===================
    /**
     * 需要验证码校验的 authUrls 之间的分隔符
     */
    public static final String URI_SEPARATOR = ",";
    /**
     * 默认的处理验证码的url前缀
     */
    public static final String DEFAULT_VALIDATE_CODE_URL_PREFIX = "/code";

    /**
     * 图片验证码的 SESSION KEY
     */
    public static final String SESSION_KEY_IMAGE = "SESSION_KEY_IMAGE_CODE";
    /**
     * 提交图片验证码请求时，请求中带点图片验证码变量名，默认 imageCode
     */
    public static final String DEFAULT_REQUEST_PARAM_IMAGE_CODE_NAME = "imageCode";

    /**
     * 短信验证码的 SESSION KEY
     */
    public static final String SESSION_KEY_SMS = "SESSION_KEY_SMS_CODE";
    /**
     * 提交短信验证码请求时，请求中带的短信验证码变量名，默认 smsCode
     */
    public static final String DEFAULT_REQUEST_PARAM_SMS_CODE_NAME = "smsCode";
    /**
     * 提交短信验证码请求时，请求中带的手机号变量名，默认 mobile
     */
    public static final String DEFAULT_REQUEST_PARAM_MOBILE_NAME = "mobile";

    /**
     * 轨迹验证码验证码的 SESSION KEY
     */
    public static final String SESSION_KEY_TRACK = "SESSION_KEY_TRACK_CODE";
    /**
     * 提交轨迹验证码请求时，请求中带的轨迹验证码变量名，默认 trackCode
     */
    public static final String DEFAULT_REQUEST_PARAM_TRACK_CODE_NAME = "trackCode";
    /**
     * 滑块验证码的 SESSION KEY
     */
    public static final String SESSION_KEY_SLIDER = "SESSION_KEY_SLIDER_CODE";
    /**
     * 提交滑块验证码请求时，请求中带的滑块验证码变量名，默认 sliderCode
     */
    public static final String DEFAULT_REQUEST_PARAM_SLIDER_CODE_NAME = "sliderCode";
    /**
     * 从图片中选取内容的验证码的 SESSION KEY
     */
    public static final String SESSION_KEY_SELECTION = "SESSION_KEY_SELECTION_CODE";
    /**
     * 从图片中选取内容的验证码请求时，请求中带的从图片中选取内容的验证码变量名，默认 selectionCode
     */
    public static final String DEFAULT_REQUEST_PARAM_SELECTION_CODE_NAME = "selectionCode";
    /**
     * 自定义验证码的 SESSION KEY
     */
    public static final String SESSION_KEY_CUSTOMIZE = "SESSION_KEY_CUSTOMIZE_CODE";
    /**
     * 提交自定义验证码请求时，请求中带的自定义验证码变量名，默认 customizeCode
     */
    public static final String DEFAULT_REQUEST_PARAM_CUSTOMIZE_CODE_NAME = "customizeCode";



    // =============== url 相关 ===============
    /**
     * request GET Method
     */
    public static final String GET_METHOD = "GET";
    /**
     * request POST Method
     */
    public static final String POST_METHOD = "POST";
    /**
     * url 参数标识符
     */
    public static final String URL_PARAMETER_IDENTIFIER = "?";
    /**
     * url 路径之间分隔符
     */
    public static final String URL_SEPARATOR = "/";
    /**
     * url 请求上带的参数之间分隔符
     */
    public static final String URL_PARAMETER_SEPARATOR = "&";
    /**
     * key value 键值对分隔符
     */
    public static final String KEY_VALUE_SEPARATOR = "=";
    /**
     * url 请求上带的参数 code
     */
    public static final String URL_PARAMETER_CODE = "code";
    /**
     * url 请求上带的参数 state
     */
    public static final String URL_PARAMETER_STATE = "state";



    // =================== Callback 相关 ===================
    /**
     * 对真实回调地址设置成KV键值对形式时的 key , 例如：回调地址(/auth/callback) -> 加密前设置key(path=/auth/callback), key = path
     */
    public static final String CALLBACK_URL_KEY_IN_STATE = "path";

    /**
     * UUID 分隔符
     */
    public static final String UUID_SEPARATOR = "-";
    /**
     * RFC 6819 安全检查(https://oauth.net/advisories/2014-1-covert-redirect/)时, 使用的正则表达式
     */
    public static final String RFC_6819_CHECK_REGEX = "^(([a-zA-z]+://)?[^/]+)+/.*$";





    // =================== 数据库相关 ===================
    /**
     * 查询表返回的结果集 ResultSet 的 COUNT(1) 索引
     */
    public static final int QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX = 1;

    /**
     * 查询数据库名称
     */
    public static final String QUERY_DATABASE_NAME_SQL = "select database();";



}
