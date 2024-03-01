package com.progartisan.module.user.model.domain;

import com.progartisan.component.common.Util;
import com.progartisan.component.data.BaseEntity;
import com.progartisan.component.data.DictionaryItem;
import com.progartisan.component.meta.Meta;
import com.progartisan.component.meta.Meta.Category;
import com.progartisan.component.meta.Meta.Type;
import com.progartisan.component.meta.MetaEntity;
import com.progartisan.module.user.api.Role.RoleType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.progartisan.component.meta.Meta.BooleanEx.False;
import static com.progartisan.component.meta.Meta.BooleanEx.True;

@Data
@EqualsAndHashCode(callSuper = false)
@MetaEntity(tableName = "t_role")
public class RolePO extends BaseEntity<RolePO> implements DictionaryItem {
    private static final long serialVersionUID = 1L;

    @MetaEntity(tableName = "t_role_permission")
    @Data
    @RequiredArgsConstructor
    public static class RolePermission {
        @Meta(Type.ID)
        String rolePermissionId = null;

		@Meta(value = Type.RefID, nullable = False)
        private final String roleId;

		@Meta(value = Type.String, nullable = False)
        private final String permission;

		@Meta(value = Type.Integer, label = "顺序", hidden = True)
        Integer index;
    }

	@Meta(value = Type.ID, label = "编号", hidden = False, listable = True)
    private String roleId;

    @Meta(category = Category.DisplayName, label = "角色名称", updatable = True) // 显示名称
    private String roleName;

    /*
    @Meta(value = Type.String, label = "角色标识", updatable = true)
    private String roleCode;
    */

	@Meta(value = Type.Enum, label = "是否启用", updatable = True)
    private Boolean enabled;

	@Meta(value = Type.RefID, label = "所属组织")
	private String orgId;

	@Meta(value = Type.Enum, label = "类型", listable = True, updatable = True, searchable = True, nullable = False) //
	private RoleType roleType;

    @Meta(value = Type.Enum, label = "工作流角色", listable = True, updatable = False)
    private Boolean workflowGroup;

	@Meta(value = Type.ToMany, label = "权限")
    private List<RolePermission> _permissions;

    public List<String> getPermissions() {
        return _permissions == null ? null : Util.mapToList(_permissions.stream(), perm -> perm.getPermission());
    }

    public void setPermissions(List<String> permissions) {
        this._permissions = Util.mapToList(permissions.stream(), perm -> new RolePermission(this.roleId, perm));
    }

    // DictionaryItem
    @Override
    public String getType() {
        return "Role";
    }

    @Override
    public Object getValue() {
        return roleId;
    }

    @Override
    public String getLabel() {
        return roleName;
    }
}
