
import { request } from '@/utils/utils'

const baseUrl = '/api/security';
export function login(params) {
    return request({
        url: `${baseUrl}/login`,
        method: 'post',
        data: params
    })
}

export function logout() {
    return request({
        url: `${baseUrl}/logout`,
        method: 'post',
        noAutoLogin: true, //no auto login
        noPopup: true, // no global error popup
    })
}

export function getInfo() {
    return request({
        url: `${baseUrl}/info`,
        method: 'get',
    });
}

export function getAllMetadata() {
    return request({
        url: `/api/public/metadata`,
        method: 'get'
    });
}
