package com.progartisan.module.logging.model;

import org.springframework.data.repository.CrudRepository;

import com.progartisan.component.logging.api.OperateLog;


public interface OperateLogRepository extends CrudRepository<OperateLog, String> {

}
