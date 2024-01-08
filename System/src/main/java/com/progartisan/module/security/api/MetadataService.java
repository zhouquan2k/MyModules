package com.progartisan.module.security.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.progartisan.component.framework.Metadata;

@RequestMapping("/api/public/metadata")
public interface MetadataService {

    @GetMapping
    Metadata getMetadata();
}
