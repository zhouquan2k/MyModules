package com.progartisan.module.user.infra;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import com.progartisan.component.data.BaseMapper;
import com.progartisan.component.data.QueryProvider;
import com.progartisan.module.user.model.domain.UserPO;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
    List<UserPO> getUsers();

    UserPO getUserByLoginName(String username);

	UserPO getUser(String userId);

	/*
	 * static final String selectByExample = "select *\n" +
	 * "        from t_user a\n" +
	 * "        left join t_user_role roles on a.user_id = roles.user_id" +
	 * "        left join t_role r on r.role_id = roles.role_id" +
	 * "        ${where}" + "        order by a.created_time desc";
	 */
	static final String selectByExample = "with user as (\n" //
			+ "select distinct a.user_id\n" //
			+ "from t_user a\n" //
			+ "left join t_user_role roles on a.user_id = roles.user_id  \n"
			+ "left join t_role role on role.role_id = roles.role_id \n" //
			+ "${where}\n" //
			+ ")\n" //
			+ "select *\n" //
			+ "from t_user a\n" + "inner join user u on a.user_id = u.user_id \n"
			+ "left join t_user_role roles on a.user_id = roles.user_id  \n"
			+ "left join t_role role on role.role_id = roles.role_id\n" //
			+ "${where}\n" //
			+ "order by a.created_time desc ";

	// 当需要跨多表字段查询时，使用Map替代Object，并将类型信息已第三个参数传入
    default List<UserPO> queryByExample(Map<String, Object> example) {
		return this.queryByExampleEx(example, selectByExample, "UserPO");
    }
    
    @SelectProvider(type = QueryProvider.class, method = "queryByExample")
    @ResultMap("Example")
	List<UserPO> queryByExampleEx(@Param("example") Map<String, Object> example, @Param("select") String select,
			@Param("name") String name);

	List<UserPO> queryUsersByOrg(String orgId);
}
