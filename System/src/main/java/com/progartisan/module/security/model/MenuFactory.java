package com.progartisan.module.security.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.inject.Named;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.DOFactory;
import com.progartisan.module.security.api.Menu;
import com.progartisan.module.security.infra.ConvertMenu;
import com.progartisan.module.security.infra.MenuMapper;
import com.progartisan.module.security.infra.MenuRepository;
import com.progartisan.module.security.model.domain.MenuDO;
import com.progartisan.module.security.model.domain.MenuPO;

import lombok.RequiredArgsConstructor;

@Named
@RequiredArgsConstructor
@Deprecated
public class MenuFactory implements DOFactory<Menu, MenuPO, MenuDO> {
    private final MenuMapper menuMapper;
    private final ConvertMenu convert;
    private final MenuRepository menuRepository;

    private String generateId() {
        return menuMapper.generateNewId();
    }

    @Override
    public MenuDO create(Menu menu) {
        MenuPO menuPo = convert.dtoToPo(menu);
        var parentPath = "";
        if (Util.isNotEmpty(menu.getParentId())) {
            MenuPO parent = menuRepository.findById(menu.getParentId()).orElseThrow();
            parentPath = parent.getTreePath();
        }
        var id = generateId();
        menuPo.setMenuId(id);
        menuPo.setTreePath(String.format("%s%s-", parentPath, id));
        return new MenuDO(menuPo);
    }

    @Override
    public MenuDO loadFromPO(MenuPO menu) {
        List<MenuPO> nodes = menuMapper.getSubTree(menu.getTreePath());
        buildMenuTree(menu, nodes);
        return new MenuDO(menu);
    }

    @Override
    public Class<?> getPOClass() {
        return MenuPO.class;
    }

    void buildMenuTree(MenuPO root, List<MenuPO> nodes) {
        Map<String, MenuPO> nodeMap = new HashMap<>();
        nodeMap.put(root.getParentId(), root);
        for (MenuPO node : nodes) {
            nodeMap.put(node.getMenuId(), node);
            MenuPO parent = nodeMap.get(node.getParentId());
            Util.check(parent != null, "parent not found? %s", node.getMenuId());
            if (parent.getChildren() == null)
                parent.setChildren(new Vector<MenuPO>());
            parent.getChildren().add(node);
        }
    }
}
