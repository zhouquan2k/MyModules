package com.progartisan.module.security.model.domain;

import com.progartisan.component.data.TreeEntity;
import com.progartisan.component.meta.Meta;
import com.progartisan.component.meta.Meta.Type;
import com.progartisan.component.meta.MetaEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@MetaEntity(tableName = "t_menu", defaultUpdatable = true)
@Deprecated
public class MenuPO extends TreeEntity<MenuPO> {
    private static final long serialVersionUID = 1L;

	@Meta(value = Type.ID, label = "编号")
    private String menuId;

    @Meta(value = Type.String, label = "菜单名")
    private String menuName;

    @Meta(value = Type.String, label = "图标")
    private String icon;

    @Meta(value = Type.Integer, label = "排序")
	private Integer order;

    // used only by non leaf menu
	/*
	@Meta(value = Type.String, label = "路径")
	private String path;
	*/

    // used only by leaf menu
    @Meta(value = Type.Dictionary, label = "功能", refData = "AllFunctions")
    private String function;
}

