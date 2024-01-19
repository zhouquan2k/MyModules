package com.progartisan.module.user.model.domain;

import com.progartisan.component.common.BizException;
import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Context;
import com.progartisan.component.framework.DO;
import com.progartisan.component.framework.helper.EntityHelper;
import com.progartisan.module.user.api.User;
import com.progartisan.module.user.api.UserService.UpdatePasswordParams;
import com.progartisan.module.user.model.domain.UserPO.UserRole;

import java.util.Date;
import java.util.Set;

public class UserDO implements DO<UserPO> {

	private final UserPO state;
	private static PassEncoder passEncoder = Context.getBean("passEncoder", PassEncoder.class);
	private static EntityHelper<UserPO> entityHelper = new EntityHelper<UserPO>(UserPO.class).init();

	private String getDefaultPassword() {
		return Context.getProperty("app.user.initialPassword");
	}
	public UserDO(UserPO state) {
		this.state = state;
		this.state.setPassword(passEncoder.encode(getDefaultPassword()));
	}

	public UserDO(UserPO state, boolean fromDb) {
		this.state = state;
	}
    @Override
    public UserPO get() {
        return this.state.clone();
    }

    // 方式1:手动set/get
	// 方式2:自行根据Meta用反射实现: 已在EntityHelper实现，可以作为DO的static成员，对于大部分字段都可update的情况,但是关系映射
	// 仍需手工赋值
    @Override
    public void update(Object obj) {
		entityHelper.update(this.state, obj);
		var user = (User) obj;
		if (user.getRoles() != null) {
			this.state.setRoles(Util.toSet(user.getRoles().stream().map(ur -> {
				return UserRole.builder().userId(user.getUserId()).roleId(ur.getRoleId()).orgId(ur.getOrgId()).build();
			})));
		}
    }

	public void updateMyProfile(User user) {
		this.state.setUsername(user.getUsername());
		this.state.setPhone(user.getPhone());
	}

	public void updateMyPassword(UpdatePasswordParams params) throws BizException {
		Util.checkBiz(passEncoder.matches(params.oldPass, this.state.getPassword()), "401", "原密码不正确");
		this.state.setPassword(passEncoder.encode(params.newPass));
	}

    public void resetPassword() {
		state.setPassword(passEncoder.encode(getDefaultPassword()));
    }

	// only update roles (in input param) for specified orgId
	public void assignRoles(String orgId, Set<UserRole> roles) {
		this.state.getRoles().removeIf(role -> Util.isEmpty(orgId) ? Util.isEmpty(role.getOrgId()) : Util.equals(orgId, role.getOrgId()));
		this.state.getRoles().addAll(Util.mapToList(roles.stream(), role -> {
			if (role.getCreateTime() == null)
				role.setCreateTime(new Date());
			return role;
		}));
	}

}
