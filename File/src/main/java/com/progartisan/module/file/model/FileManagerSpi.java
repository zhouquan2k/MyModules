package com.progartisan.module.file.model;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.progartisan.module.file.api.File;

public interface FileManagerSpi {

    File upload(MultipartFile file, String path);

    void download(String id, HttpServletResponse response);
}
