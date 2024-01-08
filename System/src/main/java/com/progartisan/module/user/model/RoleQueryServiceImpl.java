package com.progartisan.module.user.model;

import java.util.List;

import javax.inject.Named;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.component.framework.helper.QueryServiceImpl;
import com.progartisan.module.user.api.Role;
import com.progartisan.module.user.api.RoleQueryService;
import com.progartisan.module.user.infra.ConvertRole;
import com.progartisan.module.user.infra.RoleMapper;
import com.progartisan.module.user.model.domain.RolePO;

@Service(type = Type.Query)
@Named
class RoleQueryServiceImpl extends QueryServiceImpl<Role, RolePO> implements RoleQueryService {

    private RoleMapper roleMapper;

    public RoleQueryServiceImpl(ConvertRole convert, RoleMapper roleMapper) {
        super(convert, roleMapper);
        this.roleMapper = roleMapper;
    }

    @Override
	public List<Role> getRoles(String roleType) {
		if (Util.isNotEmpty(roleType)) {
			return convert.poToDto(roleMapper.queryByType(roleType));
		}
        return convert.poToDto(roleMapper.queryAll());
    }

    @Override
    public List<Role> queryByOrgId(String orgId) {
        return convert.poToDto(roleMapper.queryByOrgId(orgId));
    }
}
