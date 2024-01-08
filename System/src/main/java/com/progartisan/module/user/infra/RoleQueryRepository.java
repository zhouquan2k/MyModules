package com.progartisan.module.user.infra;

import org.springframework.data.repository.CrudRepository;

import com.progartisan.module.user.model.domain.RolePO;

public interface RoleQueryRepository extends CrudRepository<RolePO, Long> {

}