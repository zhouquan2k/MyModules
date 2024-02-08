package com.progartisan.module.misc;


import com.progartisan.component.framework.Metadata;
import com.progartisan.component.framework.Service;
import com.progartisan.component.spi.MetadataProvider;
import com.progartisan.module.misc.api.MiscService;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;

@Named
@RequiredArgsConstructor
@Service(type = Service.Type.Mixed, name = "misc", order = -1)
public class MisServiceImpl implements MiscService {

    private final MetadataProvider metadataProvider;

    @Override
    public Metadata.EntityDef getEntityMetadata(String entityName) {
        return metadataProvider.getEntityDef(entityName);
    }
}
