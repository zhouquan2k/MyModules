package com.progartisan.module.logging.model;

import com.progartisan.component.framework.Command;
import com.progartisan.component.framework.Command.LogType;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.component.logging.api.OperateLog;
import com.progartisan.component.logging.api.OperateLogService;
import com.progartisan.module.logging.infra.OperateLogMapper;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@Service(type = Type.Mixed, value = "操作日志", name = "log", order = 104)
@RequiredArgsConstructor

public class OperateLogServiceImpl implements OperateLogService {

    private final OperateLogRepository repository;
    private final OperateLogMapper mapper;

    @Override
	@Command(log = LogType.No)
    public void persist(OperateLog log) {
        repository.save(log);
    }

    @Override
    public List<OperateLog> queryOperateLogs(Map<String, Object> example) {
        return mapper.queryByExample(example);
    }

    @Override
    public List<OperateLog> queryOperateLogs() {
        return mapper.queryByExample(Map.of());
    }

}
