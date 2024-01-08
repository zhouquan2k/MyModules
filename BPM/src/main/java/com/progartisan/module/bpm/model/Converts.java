package com.progartisan.module.bpm.model;

import java.util.List;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.progartisan.module.bpm.api.BpmProcessInstance;
import com.progartisan.module.bpm.api.BpmTask;

@Mapper
interface ConvertTask {

    @Mapping(target = "pendingTask", ignore = true)
    @Mapping(target = "businessKey", ignore = true)
	@Mapping(target = "nodeDefId", source = "taskDefinitionKey")
    BpmTask taskToBpmTask(TaskInfo src);

    List<BpmTask> tasksToBpmTasks(List<Task> source);
}


@Mapper
interface ConvertProcessInstance {
	@Mapping(target = "instanceId", source = "processInstanceId")
	@Mapping(target = "tasks", ignore = true)
	@Mapping(target = "activities", ignore = true)
	@Mapping(target = "status", ignore = true)
	BpmProcessInstance instanceToBpmInstance(ProcessInstance instance);
}
