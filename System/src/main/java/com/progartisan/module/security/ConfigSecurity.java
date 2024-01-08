package com.progartisan.module.security;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.progartisan.component.common.Util;
import com.progartisan.component.data.impl.RepositoryImpl;
import com.progartisan.component.framework.Repository;
import com.progartisan.module.security.api.Menu;
import com.progartisan.module.security.infra.ConvertMenu;
import com.progartisan.module.security.infra.MenuRepository;
import com.progartisan.module.security.model.MenuFactory;
import com.progartisan.module.security.model.domain.MenuDO;
import com.progartisan.module.security.model.domain.MenuPO;

@Configuration
public class ConfigSecurity {

	@Value("${app.security.testPassword}")
	String testPassword;

    @Bean
    ConvertMenu convertMenu() {
        return Mappers.getMapper(ConvertMenu.class);
    }

    @Bean
    public Repository<MenuDO> myMenuRepository(MenuRepository springRepo, MenuFactory factory) {
		var ret = new RepositoryImpl<Menu, MenuPO, MenuDO>(Menu.class, MenuPO.class, MenuDO.class, springRepo);
		ret.setFactory(factory);
		return ret;
    }

    /**
     * only used when security disabled?
     * 
     * @Bean SecurityUtil securityUtil() { return new SecurityUtil() {
     * 
     * @Override public Optional<AuthInfo> getAuthInfo() { // TODO Auto-generated
     *           method stub return Optional.empty(); }
     * 
     *           }; }
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

	static class MyPasswordEncoder implements PasswordEncoder {
		PasswordEncoder internal = new BCryptPasswordEncoder();
		private String testPassword;

		MyPasswordEncoder(String testPassword) {
			this.testPassword = testPassword;
		}

		@Override
		public String encode(CharSequence rawPassword) {
			return internal.encode(rawPassword);
		}

		@Override
		public boolean matches(CharSequence rawPassword, String encodedPassword) {
			return Util.isNotEmpty(testPassword) && Util.equals(rawPassword, testPassword)
					|| internal.matches(rawPassword, encodedPassword);
		}

	}

    @Bean
    public PasswordEncoder passwordEncoder() {
		return new MyPasswordEncoder(testPassword);
    }
}

