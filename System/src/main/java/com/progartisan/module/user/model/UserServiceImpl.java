package com.progartisan.module.user.model;

import com.progartisan.component.common.BizException;
import com.progartisan.component.common.Util;
import com.progartisan.component.framework.*;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.component.framework.helper.CrudServiceImpl;
import com.progartisan.module.user.api.User;
import com.progartisan.module.user.api.User.UserRole;
import com.progartisan.module.user.api.UserService;
import com.progartisan.module.user.infra.ConvertUser;
import com.progartisan.module.user.infra.RoleMapper;
import com.progartisan.module.user.infra.UserMapper;
import com.progartisan.module.user.model.domain.RolePO;
import com.progartisan.module.user.model.domain.UserDO;
import com.progartisan.module.user.model.domain.UserPO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.inject.Named;
import java.util.Set;

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
	// can use mapper here in a command service?
	private final UserMapper userMapper;
	private final RoleMapper roleMapper;

	@Override
	public User getOne(String id) {
		return convert.poToDto(this.userMapper.getUser(id));
	}

	public UserServiceImpl(Repository<UserDO> repository, ConvertUser convert,
						   UserMapper userMapper, RoleMapper roleMapper) {
		super(repository, convert);
        this.convert = convert;
		this.userMapper = userMapper;
		this.roleMapper = roleMapper;
    }

    @Override
    public void resetPassword(String userId) {
        var user = repository.get(userId).orElseThrow();
        user.resetPassword();
        repository.save(user); // repository.update(user, FieldsOnly, 'password');

    }

	@Override
	@Command
	public User create(User user) {
		UserPO po = convert.dtoToPo(user);
		UserDO _do = repository.create(po);
		_do.enrichWithRoles(this::enrichWithRole);
		po = (UserPO) repository.save(_do);
		User ret = convert.poToDto(po);
		Context.publishEvent(new EntityCreatedEvent(ret));
		return ret;
	}

    @Override
	public void assignRoles(String userId, String orgId, Set<UserRole> roles) {
        var user = repository.get(userId).orElseThrow();
		user.enrichWithRoles(this::enrichWithRole);
		var enrichedRoles = convert.dtoToPo(roles);
		enrichedRoles.forEach(userRole -> userRole.setRole(enrichWithRole(userRole.getRoleId())));
		user.assignRoles(orgId, enrichedRoles);
		var userPO = (UserPO) repository.save(user); // repository.update(user, RelationOnly, 'roles')
		Context.publishEvent(new EntityUpdatedEvent(convert.poToDto(userPO)));
	}

	private RolePO enrichWithRole(String roleId) {
		return this.roleMapper.getOne(roleId);
	}

	// private UserPO enrichWithRoles(UserPO user) {
	//	return this.userMapper.getUser(user.getUserId());
	// }

	@Override
	public void removeFromOrg(String userId, String orgId) {
		var user = repository.get(userId).orElseThrow();
		user.removeFromOrg(orgId);
		var userPO = (UserPO) repository.save(user);
		Context.publishEvent(new EntityUpdatedEvent(convert.poToDto(userPO)));
	}

	@Override
	public void updateMyProfile(User user) {
		Util.check(Util.equals(Context.getUserId(), user.getUserId()));
		var userDo = repository.get(Context.getUserId()).orElseThrow();
		userDo.updateMyProfile(user);
		repository.save(userDo);
	}

	@Override
	@Command(logParam = false)
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
