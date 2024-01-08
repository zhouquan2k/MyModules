import Default from '@/default';

export const rootPath = "/system";
const Routes = [{
    name: '_System',
    path: rootPath,
    component: Default,
    meta: { title: '系统管理' },
    isFolder: true,
    children: [
        {
            name: '用户',
            path: 'user',
            meta: { title: '用户' },
            permission: 'user',
            component: (resolve) => require(['@user/user'], resolve)
        },
        {
            name: '角色',
            path: 'role',
            meta: { title: '角色' },
            permission: 'role',
            component: (resolve) => require(['@user/role'], resolve)
        },
        {
            name: '操作日志',
            path: 'operatelog',
            meta: { title: '操作日志' },
            permission: 'log',
            component: (resolve) => require(['@user/operate_log'], resolve)
        },
        {
            name: '工作台',
            path: 'desktop',
            hidden: true,
            component: (resolve) => require(['@user/desktop'], resolve)
        }
    ]
}];

export function getRoutes() {
    return Routes;
}