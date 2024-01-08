package com.progartisan.module.security.model.domain;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.DO;
import com.progartisan.module.security.api.Menu;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Deprecated
public class MenuDO implements DO<MenuPO> {

    private final MenuPO state;
    // private final ConvertMenu convert;

    @Override
    public MenuPO get() {
        return this.state.clone();
    }

    @Override
    public void update(Object obj) {
        Util.check(obj instanceof Menu);
        Menu menu = (Menu) obj;
        // this.convert.dtoToPo((Menu) obj, this.state);
        this.state.setFunction(menu.getFunction());
        this.state.setMenuName(menu.getMenuName());
        this.state.setIcon(menu.getIcon());
        this.state.setOrder(menu.getOrder());
	}

}
