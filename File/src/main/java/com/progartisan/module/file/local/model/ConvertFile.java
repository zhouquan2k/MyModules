package com.progartisan.module.file.local.model;

import javax.inject.Named;

import org.mapstruct.Mapper;

import com.progartisan.component.framework.helper.ConvertBase;
import com.progartisan.module.file.api.File;

@Mapper
@Named
public interface ConvertFile extends ConvertBase<File, FilePO> {

}
