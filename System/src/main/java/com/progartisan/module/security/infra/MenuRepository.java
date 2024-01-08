package com.progartisan.module.security.infra;

import org.springframework.data.repository.CrudRepository;

import com.progartisan.module.security.model.domain.MenuPO;

// factory需要用到
public interface MenuRepository extends CrudRepository<MenuPO, String> {

    default boolean isNew(MenuPO menu) {
        return menu.getVersion() == null;
    }
}
