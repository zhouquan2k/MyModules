package com.progartisan.module.user.model;

import java.util.Set;

import javax.inject.Named;

import com.progartisan.component.common.BizException;
import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Context;
import com.progartisan.component.framework.EnumCode;
import com.progartisan.component.framework.EnumDescription;
import com.progartisan.component.framework.Repository;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.component.framework.helper.CrudServiceImpl;
import com.progartisan.module.user.api.User;
import com.progartisan.module.user.api.User.UserRole;
import com.progartisan.module.user.api.UserService;
import com.progartisan.module.user.infra.ConvertUser;
import com.progartisan.module.user.model.domain.UserDO;
import com.progartisan.module.user.model.domain.UserPO;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 支持多层次表达，code以.分隔层级：物理上平面，逻辑上支持层次（spring security/shiro）
@Getter
@AllArgsConstructor
enum UserPermissions implements EnumDescription, EnumCode {
    UserRead("read", "用户信息读"), UserWrite("write", "用户信息写"), UserImportExport("importexport", "用户信息导入导出"),
    UserPasswordReset("pass-reset", "用户密码重置");

    private String code;
    private String desc;
}

@Service(value = "用户管理", type = Type.Command, name = "user", permissions = UserPermissions.class, order = 1)
@Named
class UserServiceImpl extends CrudServiceImpl<User, UserPO, UserDO> implements UserService {

    private final ConvertUser convert;

	public UserServiceImpl(Repository<UserDO> repository, ConvertUser convert) {
		super(repository, convert);
        this.convert = convert;
    }

    @Override
    public void resetPassword(String userId) {
        var user = repository.get(userId).orElseThrow();
        user.resetPassword();
        repository.save(user); // repository.update(user, FieldsOnly, 'password');

    }

    @Override
    public void assignRoles(String userId, Set<UserRole> roles) {
        var user = repository.get(userId).orElseThrow();
        user.assignRoles(convert.dtoToPo(roles));
        repository.save(user); // repository.update(user, RelationOnly, 'roles')
    }

	@Override
	public void updateMyProfile(User user) {
		Util.check(Util.equals(Context.getUserId(), user.getUserId()));
		var userDo = repository.get(Context.getUserId()).orElseThrow();
		userDo.updateMyProfile(user);
		repository.save(userDo);
	}

	@Override
	public void updateMyPassword(UpdatePasswordParams params) throws BizException {
		var userDo = repository.get(Context.getUserId()).orElseThrow();
		userDo.updateMyPassword(params);
		repository.save(userDo);
	}

	/*
	 * @Override public void update(String id, User dto) { UserDO _do =
	 * repository.get(id).orElseThrow();
	 * 
	 * // convert.dtoToPo _do.update(dto); repository.save(_do); }
	 */
}
