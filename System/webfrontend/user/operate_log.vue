<template>
    <div>
        <crud ref="crud" name="OperateLog" desc="操作日志" :apis="apis" :actions="[]" :searchVisible="true">
        </crud>
        <el-button v-if="hasPermission('***')" type="text" @click="apis.testException()">test</el-button>
    </div>
</template>
<script>
import operateLogApi from "@user/operate_log_api.js";
import Crud from '@/components/Crud';
import { hasPermission } from '@/utils/utils';
export default {
    components: {
        Crud,
    },
    data() {
        return {
            apis: operateLogApi,
        }
    },
    methods: {
        hasPermission,
    }
}
</script>