package com.progartisan.module.bpm.model;

import org.flowable.engine.IdentityService;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBpm {

    @Bean
	public ConvertTask convertTask() {
		return Mappers.getMapper(ConvertTask.class);
    }

	@Bean
	public ConvertProcessInstance convertProcessInstance() {
		return Mappers.getMapper(ConvertProcessInstance.class);
	}

	@Bean
	public UserSync userSync(IdentityService identityService) {
		return new UserSync(identityService);
	}

}
