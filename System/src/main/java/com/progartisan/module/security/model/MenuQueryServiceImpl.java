package com.progartisan.module.security.model;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Named;

import com.progartisan.component.common.Util;
import com.progartisan.component.data.TreeEntity;
import com.progartisan.component.framework.Context;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.spi.MetadataProvider;
import com.progartisan.module.security.api.Menu;
import com.progartisan.module.security.api.MenuQueryService;
import com.progartisan.module.security.infra.ConvertMenu;
import com.progartisan.module.security.infra.MenuMapper;
import com.progartisan.module.security.model.domain.MenuPO;

import lombok.RequiredArgsConstructor;

@Service(type = Service.Type.Query)
@RequiredArgsConstructor
@Named
@Deprecated
public class MenuQueryServiceImpl implements MenuQueryService {

    private final MenuMapper menuMapper;
    private final MenuFactory factory;
    private final ConvertMenu convert;
    private final MetadataProvider metadataProvider;

    @Override
    public List<Menu> getMenus() {
		var menus = Util.toList(menuMapper.getSubTree("").stream().filter(menu -> {
			return Util.isEmpty(menu.getFunction()) || Context.hasPermission(menu.getFunction());
		}));
        var metadata = this.metadataProvider.getMetadata(null);
        var functionMap = Util.toMap(metadata.getFunctions().stream(), func -> func.getName());
        var rootMenu = new MenuPO();

        factory.buildMenuTree(rootMenu, menus);
        var ret = convert.poToDto(rootMenu.getChildren());
        ret.forEach(node -> {
            traverseTree(node, menu -> {
                var function = functionMap.get(menu.getFunction());
                if (function != null) {
					// menu.setFeComponent(function.getFeComponent());
                }
            });
        });
        return ret;
    }

    // TODO move to common
    <T extends TreeEntity<T>> void traverseTree(T root, Consumer<T> consumer) {
        consumer.accept(root);
        if (root.getChildren() != null)
            root.getChildren().forEach(node -> traverseTree(node, consumer));
    }
}
