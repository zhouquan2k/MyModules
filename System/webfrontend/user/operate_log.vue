<template>
    <div>
        <crud ref="crud" name="OperateLog" desc="操作日志" :apis="apis" :actions="[]" :searchVisible="true">
        </crud>
    </div>
</template>
<script>
import operateLogApi from "@user/operate_log_api.js";
import Crud from '@/components/Crud';
export default {
    components: {
        Crud,
    },
    data() {
        return {
            apis: operateLogApi,
        }
    }
}
</script>