import CrudApi from '@/utils/api_base';
import { request } from '@/utils/utils'

const menu_api = new CrudApi('/api/menus')
export default menu_api;