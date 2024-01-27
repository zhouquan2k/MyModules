package com.progartisan.module.user.infra;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.progartisan.component.data.BaseMapper;
import com.progartisan.module.user.model.domain.RolePO;

@Mapper
public interface RoleMapper extends BaseMapper<RolePO> {

	@Select("select * from t_role where org_id = #{orgId} or role_type = 'GroupPublic'")
    List<RolePO> queryByOrgId(@Param("orgId") String orgId);

    @Override
    @Select("select * from t_role")
    List<RolePO> queryAll();

	@Select("select * from t_role where role_type=#{roleType}")
	List<RolePO> queryByType(String roleType);
}