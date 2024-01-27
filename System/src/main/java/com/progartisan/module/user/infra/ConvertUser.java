package com.progartisan.module.user.infra;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.progartisan.component.framework.helper.ConvertBase;
import com.progartisan.module.user.api.User;
import com.progartisan.module.user.api.User.UserRole;
import com.progartisan.module.user.model.domain.UserPO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConvertUser extends ConvertBase<User, UserPO> {

    com.progartisan.module.user.model.domain.UserPO.UserRole dtoToPo(UserRole dto);

    Set<com.progartisan.module.user.model.domain.UserPO.UserRole> dtoToPo(Set<UserRole> dto);

	@Mapping(target = "roleName", source = "role.roleName")
	UserRole poToDto(com.progartisan.module.user.model.domain.UserPO.UserRole po);
}