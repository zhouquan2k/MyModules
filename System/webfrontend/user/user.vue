<template>
  <div>
    <el-row>
      <el-col :span="orgId ? 4 : 0">
        <el-tree v-if="orgId" style="padding:5px;" :data="treeData" :props="treeProps" default-expand-all
          highlight-current :expand-on-click-node="false" :current-node-key="currentRole" node-key="id"
          @current-change="roleSelected"></el-tree>
      </el-col>
      <el-col :span="orgId ? 20 : 24">
        <crud ref="crud" name="UserPO" desc="用户" :apis="apis" :actions="actions" @action="defaultActionProc"
          :searchParam="{ orgId: orgId }" :searches="searches" :toManySelectData="toManySelectData" :searchVisible="true">
          <template #buttons>
            <!--el-button @click="onTest">test</el-button-->
          </template>
          <template #columns>
            <el-table-column label="角色">
              <template slot-scope="scope">
                <el-tag v-for="(item, index) in getUniqueRoleNames(scope.row.roles)" :key="`tag-roles-${index}`">{{
                  item }}
                </el-tag>
              </template>
            </el-table-column>
          </template>
        </crud>
      </el-col>
    </el-row>
    <el-dialog :title="`分配角色 - ${user.username} （${user.loginName}）`" :visible.sync="assignRolesVisible" width="500px"
      append-to-body>
      <el-form label-width="80px">
        <el-form-item>
          <el-checkbox-group v-model="roles">
            <el-checkbox v-for="role in allRoles" :label="role.roleId" :key="role.roleId">{{ role.roleName
            }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onAssignRoles">确 定</el-button>
        <el-button @click="assignRolesVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import user_api from './user_api.js';
import role_api from './role_api.js';
import Crud from '@/components/Crud';
import { defaultCrudActions, defaultActionProc } from '@/utils/utils';

export default {
  name: 'User',
  components: {
    Crud,
  },
  props: {
    orgId: {},
  },
  data() {
    return {
      apis: user_api,
      user: {},
      treeData: [{
        id: null,
        label: '所有',
        children: []
      }],
      treeProps: {
        children: 'children',
        label: 'label'
      },
      currentRole: '0',
      actions: [
        defaultCrudActions[0],
        {
          desc: "分配角色",
          method: 'openAssignRolesDlg',
          permission: ''
        },
        {
          desc: '重置密码',
          method: 'onResetPassword',
        }, defaultCrudActions[1]
      ],
      searches: {},
      assignRolesVisible: false,
      roles: [], //selected roles
      allRoles: [],
      toManySelectData: {},
    };
  },
  methods: {
    defaultActionProc,
    async onTest() {
      await user_api.test();
    },
    getUniqueRoleNames(roles) {
      return Array.from(new Map(roles.map(role => [role.roleName, role.roleName])).values());
    },
    roleSelected(role) {
      this.searches = { roleId: role.id };
      setTimeout(() => this.$refs.crud.onSearch(), 100);
    },
    async openAssignRolesDlg(user) {
      this.user = await user_api.get(user.userId);
      this.roles = this.user.roles.map(role => role.roleId); //filter(role => role.orgId == this.orgId || !this.orgId)
      this.assignRolesVisible = true;
    },
    async onAssignRoles() {
      await user_api.assignRoles(this.user.userId, this.roles.map(role => ({ roleId: role, orgId: this.orgId })));
      this.assignRolesVisible = false;
      this.$refs.crud.getList();
      this.$message.success('分配角色成功.')
    },
    async onResetPassword(user) {
      try {
        await this.$confirm('此操作将重置该用户密码到默认密码, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
      }
      catch (error) {
        return;
      }
      await user_api.resetPassword(user.userId);
      this.$message({
        type: 'success',
        message: '密码重置成功!'
      });
    },
  },
  async created() {
    if (this.orgId) {
      this.allRoles = await role_api.queryRolesByOrgId(this.orgId);
    }
    else {
      this.allRoles = await role_api.list({ roleType: 'Global' });
    }
    this.toManySelectData.roles = this.allRoles.map(role => ({ value: { ...role, key: role.roleId, orgId: this.orgId ? this.orgId : role.orgId }, label: role.roleName, key: role.roleId }));
    this.treeData[0].children.push(...this.allRoles.map(role => ({ id: role.roleId, label: role.roleName })));
  }
};
</script>
<style scoped>
.el-checkbox {
  display: block;
  line-height: 30px;
}
</style>