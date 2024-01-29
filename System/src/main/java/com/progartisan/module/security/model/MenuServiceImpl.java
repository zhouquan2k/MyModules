package com.progartisan.module.security.model;

import com.progartisan.component.framework.EnumCode;
import com.progartisan.component.framework.EnumDescription;
import com.progartisan.component.framework.Repository;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.component.framework.helper.ConvertBase;
import com.progartisan.component.framework.helper.CrudServiceImpl;
import com.progartisan.module.security.api.Menu;
import com.progartisan.module.security.api.MenuService;
import com.progartisan.module.security.model.domain.MenuDO;
import com.progartisan.module.security.model.domain.MenuPO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum MenuPermissions implements EnumDescription, EnumCode {
    Read("read", "菜单信息读"), Write("write", "菜单信息写");

    private String code;
    private String desc;
}

@Service(value = "菜单管理", type = Type.Command, name = "menu", order = -1)
@Deprecated
public class MenuServiceImpl extends CrudServiceImpl<Menu, MenuPO, MenuDO> implements MenuService {

	public MenuServiceImpl(Repository<MenuDO> repository, ConvertBase<Menu, MenuPO> convert) {
		super(repository, convert);
    }

    @Override
    public void delete(String id) {
        MenuDO menu = this.repository.get(id).orElseThrow();
        // to support remove tree children
        this.repository.remove(menu);
    }

}
