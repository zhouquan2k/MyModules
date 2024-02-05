package com.progartisan.module.security;

import com.progartisan.component.common.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ConfigSecurity {

	@Value("${app.security.testPassword}")
	String testPassword;

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

