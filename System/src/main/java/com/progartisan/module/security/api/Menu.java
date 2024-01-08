package com.progartisan.module.security.api;

import com.progartisan.component.data.TreeEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Menu extends TreeEntity<Menu> {
    private static final long serialVersionUID = 1L;

    private String menuId;

    private String menuName;

    private String icon;

	private Integer order;

    private String function;

    private String path;

    private String feComponent;
}
