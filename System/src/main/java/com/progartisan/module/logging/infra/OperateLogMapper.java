package com.progartisan.module.logging.infra;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.progartisan.component.data.BaseMapper;
import com.progartisan.component.logging.api.OperateLog;

@Mapper
public interface OperateLogMapper extends BaseMapper<OperateLog> {

    static final String selectByExample = "select * from t_operate_log a\n"
            + "         ${where} order by a.log_id desc";

    @Override
    @Select("select * from t_operate_log")
    @Results(id = "Example")
    List<OperateLog> queryAll();

    default List<OperateLog> queryByExample(OperateLog example) {
        return this.queryByExample(example, selectByExample);
    }
}
