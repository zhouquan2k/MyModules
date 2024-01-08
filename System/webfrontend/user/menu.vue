<template>
    <div>
        <crud ref="crud" name="MenuPO" desc="菜单" :apis="apis" :actions="actions" @action="defaultActionProc" />
    </div>
</template>
  
<script>
import menu_api from './menu_api.js';
import Crud from '@/components/Crud';
import { defaultCrudActions, defaultActionProc } from '@/utils/utils';

export default {
    components: {
        Crud,
    },
    data() {
        return {
            apis: menu_api,
            user: {},
            actions: [
                {
                    desc: "新增",
                    method: 'onAddSubMenu',
                },
                defaultCrudActions[0],
                defaultCrudActions[1]
            ],
            assignRolesVisible: false,
            roles: [], //selected roles
            allRoles: [],
        };
    },
    methods: {
        defaultActionProc,
        onAddSubMenu(menu) {
            this.$refs.crud.showAddDialog(menu);
        }
    }
};
</script>
<style scoped>
.el-checkbox {
    display: block;
    line-height: 30px;
}
</style>