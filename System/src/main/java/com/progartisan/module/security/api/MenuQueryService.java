package com.progartisan.module.security.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/menus")
public interface MenuQueryService {
    @GetMapping
    List<Menu> getMenus();
}
