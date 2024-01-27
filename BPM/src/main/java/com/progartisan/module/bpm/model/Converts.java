package com.progartisan.module.bpm.model;

import com.progartisan.module.bpm.api.BpmProcessInstance;
import com.progartisan.module.bpm.api.BpmTask;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
interface ConvertTask {

    @Mapping(target = "pendingTask", ignore = true)
    @Mapping(target = "businessKey", ignore = true)
	@Mapping(target = "nodeDefId", source = "taskDefinitionKey")
    BpmTask taskToBpmTask(TaskInfo src);

	List<BpmTask> tasksToBpmTasks(List<Task> source);

	List<BpmTask> historicTasksToBpmTasks(List<HistoricTaskInstance> source);

}


@Mapper
interface ConvertProcessInstance {
	@Mapping(target = "instanceId", source = "processInstanceId")
	@Mapping(target = "tasks", ignore = true)
	@Mapping(target = "activities", ignore = true)
	@Mapping(target = "status", ignore = true)
	BpmProcessInstance instanceToBpmInstance(ProcessInstance instance);

	List<BpmProcessInstance> instancesToBpmInstances(List<HistoricProcessInstance> instances);

	@Mapping(target = "instanceId", source = "id")
	@Mapping(target = "tasks", ignore = true)
	@Mapping(target = "activities", ignore = true)
	@Mapping(target = "status", ignore = true)
	BpmProcessInstance instanceToBpmInstance(HistoricProcessInstance instance);
}
