package com.progartisan.module.security.model;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.progartisan.component.framework.Metadata;
import com.progartisan.component.framework.spi.MetadataProvider;
import com.progartisan.module.security.api.Menu;
import com.progartisan.module.security.infra.ConvertMenu;
import com.progartisan.module.security.infra.MenuMapper;
import com.progartisan.module.security.model.domain.MenuPO;

@RunWith(MockitoJUnitRunner.class)
public class MenuQueryServiceTest {

    @Mock
    MenuMapper menuMapper;

    @Mock
    MetadataProvider metadataProvider;

    @InjectMocks
    MenuFactory factory;

    @Mock
    ConvertMenu convert;

    //@InjectMocks
    private MenuQueryServiceImpl service;

    @Before
    public void setup() {
        service = new MenuQueryServiceImpl(menuMapper, factory, convert, metadataProvider);
    }


    @Test
    public void testGetMenusEmpty() {
        when(metadataProvider.getMetadata(null)).thenReturn(new Metadata());
        assertEquals(List.of(), service.getMenus());
    }

    @Test
    public void testGetMenus() {
        var menu = new MenuPO();
        menu.setMenuId("1");
        var menuDto = new Menu();
        when(menuMapper.getSubTree("")).thenReturn(List.of(menu));
        when(metadataProvider.getMetadata(null)).thenReturn(new Metadata());
        when(convert.poToDto(List.of(menu))).thenReturn(List.of(menuDto));
        assertEquals(List.of(menuDto), service.getMenus());
    }
}
