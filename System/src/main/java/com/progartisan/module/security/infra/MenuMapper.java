package com.progartisan.module.security.infra;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.progartisan.module.security.model.domain.MenuPO;

@Mapper
public interface MenuMapper {

    @Select("select * from t_menu where tree_path like '${treePath}%' order by tree_path")
    List<MenuPO> getSubTree(String treePath);

    @Select("select max(menu_id)+1 from t_menu")
    String generateNewId();
}
