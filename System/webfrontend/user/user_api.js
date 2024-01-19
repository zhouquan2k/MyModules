import CrudApi from '@/utils/api_base';
import { request } from '@/utils/utils'

const user_api = new CrudApi('/api/users')
user_api.assignRoles = async function (userId, orgId, roles) {
    return await request({
        url: `${this.baseUrl}/${userId}/assign`,
        data: roles,
        method: 'post',
        params: { orgId }
    });
}

user_api.resetPassword = async function (userId) {
    return await request({
        url: `${this.baseUrl}/${userId}/password-reset`,
        method: 'put',
    });
}

user_api.getMyProfile = async function () {
    return await request({
        url: `${this.baseUrl}/my`,
        method: 'get',
    });
}

user_api.updateMyProfile = async function (user) {
    return await request({
        url: `${this.baseUrl}/my`,
        method: 'put',
        data: user,
    });
}

user_api.updateMyPwd = async function (oldPass, newPass) {
    return await request({
        url: `${this.baseUrl}/my/password`,
        method: 'put',
        data: { oldPass, newPass },
    });
}

user_api.test = async function () {
    return await request({
        url: `/api/security/exception`,
        method: 'get'
    })
}

export default user_api;