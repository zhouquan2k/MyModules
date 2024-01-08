package com.progartisan.module.user.infra;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.progartisan.component.framework.helper.ConvertBase;
import com.progartisan.module.user.api.Role;
import com.progartisan.module.user.model.domain.RolePO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConvertRole extends ConvertBase<Role, RolePO> {

}

