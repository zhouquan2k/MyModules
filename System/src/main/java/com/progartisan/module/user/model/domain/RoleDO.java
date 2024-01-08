package com.progartisan.module.user.model.domain;

import java.util.List;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.DO;
import com.progartisan.component.framework.helper.EntityHelper;

public class RoleDO implements DO<RolePO> {

	private static EntityHelper<RolePO> entityHelper = new EntityHelper<RolePO>(RolePO.class).init();

    private final RolePO state;

    public RoleDO(RolePO role) {
        this.state = role;
		this.state.setEnabled(true);
		Util.check(role.getRoleType() != null);
    }

    public RoleDO(RolePO role, boolean fromDB) {
        this.state = role;
    }

    public void assignPermissions(List<String> permissions) {
        this.state.setPermissions(permissions);
    }

    @Override
    public RolePO get() {
        return this.state.clone();
    }

    @Override
    public void update(Object obj) {
		entityHelper.update(this.state, obj);
    }
}
