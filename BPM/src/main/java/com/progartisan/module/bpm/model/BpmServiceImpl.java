package com.progartisan.module.bpm.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Named;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Context;
import com.progartisan.component.framework.Service;
import com.progartisan.module.bpm.api.BpmProcessInstance;
import com.progartisan.module.bpm.api.BpmProcessInstanceCreateReq;
import com.progartisan.module.bpm.api.BpmService;
import com.progartisan.module.bpm.api.BpmTask;

import lombok.RequiredArgsConstructor;

@Named
@RequiredArgsConstructor
@Service
public class BpmServiceImpl implements BpmService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final ConvertTask convertTask;
	private final ConvertProcessInstance convertInstance;

    private Optional<ProcessDefinition> getProcessDefinition(String key) {
        return Optional.ofNullable(repositoryService.createProcessDefinitionQuery().processDefinitionKey(key)
                .latestVersion().singleResult());
    }

    @Override
    public String createProcessInstance(BpmProcessInstanceCreateReq req) {

        // 获得流程定义
        ProcessDefinition definition = getProcessDefinition(req.getProcessDefinitionKey()).orElseThrow();
        Util.check(!definition.isSuspended());

        Authentication.setAuthenticatedUserId(Context.getUserId());
        // 创建流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceById(definition.getId(), req.getBusinessKey(),
                req.getVariables());

        // set a meaningful name from request
        runtimeService.setProcessInstanceName(instance.getId(), req.getInstanceName());

        return instance.getId();
    }

    @Override
    public void completeTask(String taskId, Map<String, Object> variables) {
        var task = getBpmTask(taskId);
        if (Util.isEmpty(task.getAssignee())) {
            taskService.claim(taskId, Context.getUserId());
        }
        taskService.complete(taskId, variables);
        // Context.publishEvent(new UserTaskCompletedEvent(task, variables));
    }

    @Override
    public List<BpmTask> queryMyTasks() {
        // 获取用户作为候选人的任务
		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(Context.getUserId())
				.includeProcessVariables().orderByTaskCreateTime()
                .asc().list();
        //获取用户所属候选组的任务
        // var groups = List.of("warehouse");
        /*
        List<Group> groups = identityService.createGroupQuery().groupMember(Context.getUserId()).list();
        List<Task> groupTasks = taskService.createTaskQuery()
                .taskCandidateGroupIn(Util.mapToList(groups.stream(), Group::getId)).list();
        */
        return convertTask.tasksToBpmTasks(tasks);
    }

    @Override
    public void claimTask(String taskId, String userId) {
        this.taskService.claim(taskId, userId);
    }

    private BpmTask getBpmTask(String taskId) {
        TaskInfo task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmTask bpmTask = null;
        if (task == null) {
            task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            Util.check(task != null);
            bpmTask = convertTask.taskToBpmTask(task);
            bpmTask.setPendingTask(false);
        } else {
            bpmTask = convertTask.taskToBpmTask(task);
            bpmTask.setPendingTask(true);
            if (bpmTask.getProcessVariables().isEmpty()) {
                bpmTask.setProcessVariables(runtimeService.getVariables(task.getProcessInstanceId()));
            }
        }
        return bpmTask;
    }

    @Override
    public BpmTask getTask(String taskId) {
        BpmTask task = getBpmTask(taskId);
        // TODO security checks

        return task;
    }

	@Override
	public List<BpmTask> getCurrentTasks(String processInstanceId) {
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
		return convertTask.tasksToBpmTasks(tasks);
	}

	@Override
	public BpmProcessInstance getInstanceByBusinessKey(String businessKey) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(businessKey).singleResult();
		if (processInstance == null)
			return null;
		var tasks = getCurrentTasks(processInstance.getProcessInstanceId());
		var bpmInstance = convertInstance.instanceToBpmInstance(processInstance);
		bpmInstance.setTasks(tasks);
		return bpmInstance;
	}

}
