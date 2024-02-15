package com.progartisan.module.misc;


import com.progartisan.component.framework.Metadata;
import com.progartisan.component.framework.Service;
import com.progartisan.component.spi.MetadataProvider;
import com.progartisan.module.misc.api.MiscService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Named;

@Named
@RequiredArgsConstructor
@Service(type = Service.Type.Mixed, name = "misc", order = -1)
public class MisServiceImpl implements MiscService {

    private final MetadataProvider metadataProvider;
    private final ResourceLoader resourceLoader;


    @Override
    public String getVersion() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:version.txt");
        return new String(resource.getInputStream().readAllBytes());
    }
    
    @Override
    public Metadata.EntityDef getEntityMetadata(String entityName) {
        return metadataProvider.getEntityDef(entityName);
    }
}
