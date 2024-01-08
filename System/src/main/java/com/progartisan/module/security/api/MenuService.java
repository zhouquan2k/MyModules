package com.progartisan.module.security.api;


import org.springframework.web.bind.annotation.RequestMapping;

import com.progartisan.component.framework.helper.CrudService;

@RequestMapping("/api/menus")
public interface MenuService extends CrudService<Menu> {
}
