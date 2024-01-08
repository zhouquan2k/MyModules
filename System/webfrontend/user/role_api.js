import CrudApi from '@/utils/api_base';
import { request } from '@/utils/utils'

const role_api = new CrudApi('/api/roles');
role_api.getAllFunctions = async function () {
    return await request({
        url: `/api/security/functions`,
        method: 'get',
    });
};

role_api.assignRolePermissions = async function (roleId, permissions) {
    return await request({
        url: `${this.baseUrl}/${roleId}/assign`,
        data: permissions,
        method: 'post',
    });
}

role_api.queryRolesByOrgId = async function (orgId) {
    return await request({
        url: `${this.baseUrl}/by-org/${orgId}`,
        method: 'get',
    });
}

export default role_api;