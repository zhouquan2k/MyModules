package com.progartisan.module.file.model;

import com.progartisan.module.file.api.File;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface FileManagerSpi {

    File upload(MultipartFile file, String path);

    void download(String id, HttpServletResponse response);

    void delete(String id);
}
