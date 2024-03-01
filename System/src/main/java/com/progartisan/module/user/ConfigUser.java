package com.progartisan.module.user;

import com.progartisan.component.data.impl.DictionaryProvider;
import com.progartisan.component.data.impl.RepositoryImpl;
import com.progartisan.component.framework.Repository;
import com.progartisan.module.user.api.Role;
import com.progartisan.module.user.api.User;
import com.progartisan.module.user.infra.ConvertRole;
import com.progartisan.module.user.infra.ConvertUser;
import com.progartisan.module.user.infra.RoleMapper;
import com.progartisan.module.user.infra.UserMapper;
import com.progartisan.module.user.model.domain.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Bean
    public DictionaryProvider<UserPO> userDictioanyProvider(UserMapper mapper) {
        return new DictionaryProvider<>(mapper);
    }

    @Bean
    public DictionaryProvider<RolePO> roleDictioanyProvider(RoleMapper mapper) {
        return new DictionaryProvider<>(mapper);
    }

}

//spring data需要为每个repository创建单独的类，但我们只需要相同的类和单独的实例。
interface UserRepository extends CrudRepository<UserPO, String> {
}

interface RoleRepository extends CrudRepository<RolePO, String> {
}
