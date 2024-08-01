package com.progartisan.module.bpm.model;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Context;
import com.progartisan.component.framework.Service;
import com.progartisan.module.bpm.api.BpmProcessInstance;
import com.progartisan.module.bpm.api.BpmProcessInstanceCreateReq;
import com.progartisan.module.bpm.api.BpmService;
import com.progartisan.module.bpm.api.BpmTask;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        return convertTask.tasksToBpmTasks(tasks);
    }

    @Override
    public List<BpmProcessInstance> queryMyHistoryInstances() {
        // 查询参与者为特定用户的历史流程实例
        var userId = Context.getUserId();
        HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId);
        List<HistoricTaskInstance> taskInstances = taskQuery.list();
        // 获取这些任务实例的流程实例 ID
        Set<String> processInstanceIds = taskInstances.stream()
                .map(HistoricTaskInstance::getProcessInstanceId)
                .collect(Collectors.toSet());


        // 查询这些流程实例
        if (!processInstanceIds.isEmpty()) {
            var instances = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceIds(processInstanceIds)
                    .orderByProcessInstanceStartTime().desc()
                    .listPage(0, 10);

            /* involvedUser 会导致一些无法解释的作为participant的情形
            List<HistoricProcessInstance> instances = historyService.createHistoricProcessInstanceQuery()
                    .involvedUser(Context.getUserId())
                    .orderByProcessInstanceStartTime().desc()
                    .listPage(0, 10);
             */
            return convertInstance.instancesToBpmInstances(instances);
        }
        return List.of();
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
