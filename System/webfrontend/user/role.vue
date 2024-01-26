<template>
  <div>
    <crud ref="crud" name="RolePO" desc="角色" :apis="apis" :actions="actions" @action="defaultActionProc" />
    <el-dialog :title="`分配功能权限 - ${role.roleName}`" :visible.sync="assignPermissionsVisible" width="500px" append-to-body>
      <el-form label-width="80px">
        <el-form-item>
          <!--el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选</el-checkbox-->
          <el-tree class="tree-border" :data="functions" show-checkbox ref="tree_functions" node-key="name"
            :check-strictly="true" empty-text="加载中，请稍后" :props="defaultTreeProps"></el-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onAssignPermissions">确 定</el-button>
        <el-button @click="assignPermissionsVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import role_api from './role_api.js';
import Crud from '@/components/Crud';
import { defaultCrudActions, defaultActionProc } from '@/utils/utils';

export default {
  components: {
    Crud,
  },
  data() {
    return {
      apis: role_api,
      role: {},
      assignPermissionsVisible: false,
      defaultTreeProps: {
        label: "label",
        children: "permissions"
      },
      functions: [],
      actions: [
        defaultCrudActions[0],
        {
          desc: "分配权限",
          method: 'openAssignPermissionsDlg',
        },
        {
          desc: '删除',
          // available: row => !row.fixed ,
          method: 'showDeleteConfirm'
        }
      ]
    };
  },
  methods: {
    defaultActionProc,
    async openAssignPermissionsDlg(role) {
      this.role = await role_api.get(role.roleId);
      this.assignPermissionsVisible = true;
      if (!this.functions || this.functions.length == 0) {
        const functions = await role_api.getAllFunctions();
        functions.forEach(func => {
          for (var perm of func.permissions) {
            perm.name = `${func.name}.${perm.name}`;
          }
        });
        this.functions = functions;
      }
      this.$refs.tree_functions.setCheckedKeys(this.role.permissions);
    },
    async onAssignPermissions() {
      var permissions = this.$refs.tree_functions.getCheckedKeys();
      await role_api.assignRolePermissions(this.role.roleId, permissions);
      this.assignPermissionsVisible = false;
      this.$refs.crud.getList();
      this.$message.success('分配权限成功.')
    }
  }
};
</script>
