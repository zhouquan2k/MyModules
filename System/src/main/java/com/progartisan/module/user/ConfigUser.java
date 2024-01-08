package com.progartisan.module.user;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.progartisan.component.data.impl.RepositoryImpl;
import com.progartisan.component.framework.Repository;
import com.progartisan.module.user.api.Role;
import com.progartisan.module.user.api.User;
import com.progartisan.module.user.infra.ConvertRole;
import com.progartisan.module.user.infra.ConvertUser;
import com.progartisan.module.user.model.domain.PassEncoder;
import com.progartisan.module.user.model.domain.RoleDO;
import com.progartisan.module.user.model.domain.RolePO;
import com.progartisan.module.user.model.domain.UserDO;
import com.progartisan.module.user.model.domain.UserPO;

@Configuration
public class ConfigUser {

    @Bean
	public Repository<UserDO> myUserRepository(UserRepository springRepo) {
		return new RepositoryImpl<User, UserPO, UserDO>(User.class, UserPO.class, UserDO.class, springRepo);
    }

    @Bean
	public Repository<RoleDO> myRoleRepository(RoleRepository springRepo) {
		return new RepositoryImpl<Role, RolePO, RoleDO>(Role.class, RolePO.class, RoleDO.class, springRepo);
    }

    @Bean
    ConvertUser convertUser() {
        return Mappers.getMapper(ConvertUser.class);
    }

    @Bean
    ConvertRole convertRole() {
        return Mappers.getMapper(ConvertRole.class);
    }

    @Bean
	PassEncoder passEncoder(PasswordEncoder passEncoder) {
		return new PassEncoder() {

			@Override
			public String encode(String raw) {
				return passEncoder.encode(raw);
			}

			@Override
			public boolean matches(String raw, String encoded) {
				return passEncoder.matches(raw, encoded);
			}

		};
    }

}

//spring data需要为每个repository创建单独的类，但我们只需要相同的类和单独的实例。
interface UserRepository extends CrudRepository<UserPO, String> {
}

interface RoleRepository extends CrudRepository<RolePO, String> {
}
