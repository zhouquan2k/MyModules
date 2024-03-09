package com.progartisan.module.bpm.model;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Context;
import com.progartisan.module.bpm.api.BpmEvent;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class ConfigBpm {

	private final RuntimeService runtimeService;

	@PostConstruct
	public void init() {
		runtimeService.addEventListener(new FlowableEventListener() {
			@Override
			public void onEvent(FlowableEvent event) {
				BpmEvent bpmEvent = null;
				if (Util.equals(event.getType(), FlowableEngineEventType.PROCESS_COMPLETED)) {
					FlowableEngineEvent engineEvent = (FlowableEngineEvent) event;
					ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
							.processInstanceId(engineEvent.getProcessInstanceId())
							.singleResult();
					String businessKey = processInstance.getBusinessKey();
					bpmEvent = BpmEvent.builder().eventType(BpmEvent.EventType.Completed).processInstanceId(engineEvent.getProcessInstanceId())
							.businessKey(businessKey).build();
				}
				if (bpmEvent != null) {
					Context.publishEvent(bpmEvent);
				}
			}

			@Override
			public boolean isFailOnException() {
				return false;
			}

			@Override
			public boolean isFireOnTransactionLifecycleEvent() {
				return false;
			}

			@Override
			public String getOnTransaction() {
				return null;
			}
		});
	}

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
