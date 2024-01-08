package com.progartisan.module.security.model;

import javax.inject.Named;

import com.progartisan.component.framework.Metadata;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.component.framework.spi.MetadataProvider;
import com.progartisan.module.security.api.MetadataService;

import lombok.RequiredArgsConstructor;

@Service(type=Type.Mixed)
@RequiredArgsConstructor
@Named
public class MetadataServiceImpl implements MetadataService {

    private final MetadataProvider metadataProvider;
    @Override
    public Metadata getMetadata() {
        return metadataProvider.getMetadata(null);
    }

}
