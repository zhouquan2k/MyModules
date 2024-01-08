package com.progartisan.module.file.local.model;

import com.progartisan.component.data.BaseEntity;
import com.progartisan.component.meta.Meta;
import com.progartisan.component.meta.Meta.Type;
import com.progartisan.component.meta.MetaEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@MetaEntity(tableName = "t_file")
@EqualsAndHashCode(callSuper = false)
@Data
public class FilePO extends BaseEntity<FilePO> {
    private static final long serialVersionUID = 1L;
    @Meta(Type.ID)
    String fileId;
    @Meta(Type.String)
    String fileName;
    @Meta(Type.String)
    String path;
    @Meta(Type.Integer)
    Long size;
    @Meta(Type.String)
    String mimeType;
    @Meta(Type.String)
    String accessUrl;
}
