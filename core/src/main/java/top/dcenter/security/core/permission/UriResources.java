package top.dcenter.security.core.permission;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UriResources {

    /**
     * id
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 前端 uri 代表的类型, 如菜单或按钮
     */
    private String type;
    /**
     * uri
     */
    private String url;
    /**
     * 权限, 多个权限用逗号分隔
     */
    private String permission;
    /**
     * 父级 id,
     */
    private Long parentId;
    /**
     * 菜单或按钮的顺序
     */
    private Integer sort;
    /**
     * ?
     */
    private Boolean external;
    /**
     * 是否支持
     */
    private Boolean available;
    /**
     * 图标
     */
    private String icon;

}