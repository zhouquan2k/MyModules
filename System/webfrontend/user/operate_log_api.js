import CrudApi from '@/utils/api_base';
import { request } from '@/utils/utils'

const operateLogApi = new CrudApi('/api/operatelog')

operateLogApi.testException = async function () {
    return await request({
        url: `/api/misc/exception-test`,
        method: 'post',
    });
}

export default operateLogApi;
export { operateLogApi };

