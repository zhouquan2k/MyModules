package com.progartisan.module.user.model;

import java.util.List;

import javax.inject.Named;

import com.progartisan.component.framework.Command;
import com.progartisan.component.framework.EnumCode;
import com.progartisan.component.framework.EnumDescription;
import com.progartisan.component.framework.Repository;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.component.framework.helper.CrudServiceImpl;
import com.progartisan.module.user.api.Role;
import com.progartisan.module.user.api.RoleService;
import com.progartisan.module.user.infra.ConvertRole;
import com.progartisan.module.user.model.domain.RoleDO;
import com.progartisan.module.user.model.domain.RolePO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum RolePermissions implements EnumDescription, EnumCode {
    RoleRead("read", "角色信息读"), RoleWrite("write", "角色信息写"), RoleAssignPermission("assign", "角色分配权限");

    private String code;
    private String desc;

}

@Service(value = "角色管理", type = Type.Mixed, name = "role", permissions = RolePermissions.class, order = 2)
@Named
public class RoleServiceImpl extends CrudServiceImpl<Role, RolePO, RoleDO> implements RoleService {

	public RoleServiceImpl(Repository<RoleDO> repository, ConvertRole convert) {
		super(repository, convert);
        // TODO Auto-generated constructor stub
    }

    @Override
	@Command
    public void assignRolePermissions(String roleId, List<String> permissionCodes) {
        RoleDO role = this.repository.get(roleId).orElseThrow();
        role.assignPermissions(permissionCodes);
        this.repository.save(role);
    }
}
