package com.progartisan.module.user.model.domain;

import com.progartisan.component.data.BaseEntity;
import com.progartisan.component.data.DictionaryItem;
import com.progartisan.component.framework.AuthInfo;
import com.progartisan.component.meta.Meta;
import com.progartisan.component.meta.Meta.Category;
import com.progartisan.component.meta.Meta.Type;
import com.progartisan.component.meta.MetaEntity;
import com.progartisan.module.user.api.User.UserStatus;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.progartisan.component.meta.Meta.BooleanEx.False;
import static com.progartisan.component.meta.Meta.BooleanEx.True;

@Data
@EqualsAndHashCode(callSuper = false)
@MetaEntity(tableName = "t_user")
public class UserPO extends BaseEntity<UserPO> implements UserDetails, AuthInfo, DictionaryItem {

    private static final long serialVersionUID = 1L;

    @Meta(value = Type.ID, label = "编号", searchable = True)
    private String userId;

	@Meta(value = Type.String, label = "登录名", searchable = True, nullable = False, unique = {
            "login_name"}, updatable = True)
    private String loginName;

	@Meta(category = Category.PersonName, label = "用户名", nullable = False)
    private String username;

    @Meta(value = Type.String, label = "用户编码", updatable = True, searchable = True)
	private String userCode;

    // 不允许直接update，只能assighRoles，因为多org的原因
    @Meta(value = Type.ToMany, label = "角色", refData = "roleId,roleName", nullable = False, searchable = True, updatable = False)
    // UserRole.roleName
	private Set<UserRole> roles;

    @Meta(value = Type.Enum, label = "状态", updatable = True, defaultValue = "Active")
	private UserStatus status;

	@Meta(label = "密码", category = Category.Password)
    private String password;

    @Meta(category = Category.Phone, updatable = True)
    private String phone;

    @Meta(value = Type.String, label = "备注", updatable = True)
    private String remark;

	/* TODO
	@Meta(value = Type.JSON, label = "信息", updatable = True)
	private String content;
	*/

    @Meta(value = Type.None)
    private Set<String> permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status != UserStatus.Disabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status != UserStatus.Disabled;
    }

    // for DictionaryItem
    @Override
    public String getType() {
        return "User";
    }

    @Override
    public Object getValue() {
        return this.userId;
    }

    @Override
    public String getLabel() {
        return this.username;
    }

    @MetaEntity(tableName = "t_user_role")
    @Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UserRole {

        @Meta(value = Type.ID)
		private String id;

		@Meta(value = Type.RefID, nullable = False)
		private String userId;

		@Meta(value = Type.RefID, nullable = False, searchable = True)
		private String roleId;

		@Meta(value = Type.RefID, label = "所属组织", searchable = True)
		private String orgId;

		@Meta(value = Type.Integer, hidden = True)
        private Integer index;

		@Meta(value = Type.None, searchable = True)
		private RolePO role;
		
		@Meta(value = Type.Date)
		private Date createTime;

    }
}
