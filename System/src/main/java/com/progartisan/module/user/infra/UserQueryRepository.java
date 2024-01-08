package com.progartisan.module.user.infra;

import org.springframework.data.repository.CrudRepository;

import com.progartisan.module.user.model.domain.UserPO;

@Deprecated
//  QueryByExampleExecutor<UserPO>, 当前版本不支持
public interface UserQueryRepository extends CrudRepository<UserPO, String> {

}
