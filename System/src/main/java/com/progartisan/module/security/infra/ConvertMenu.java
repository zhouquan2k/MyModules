package com.progartisan.module.security.infra;

import javax.inject.Named;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.progartisan.component.framework.helper.ConvertBase;
import com.progartisan.module.security.api.Menu;
import com.progartisan.module.security.model.domain.MenuPO;

@Named
@Mapper
public interface ConvertMenu extends ConvertBase<Menu, MenuPO> {

    @Override
    @Mapping(target = "feComponent", ignore = true)
    Menu poToDto(MenuPO source);
}
