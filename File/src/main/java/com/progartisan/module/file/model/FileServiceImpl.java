package com.progartisan.module.file.model;

import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Service;
import com.progartisan.component.framework.Service.Type;
import com.progartisan.module.file.api.File;
import com.progartisan.module.file.api.FileService;

import lombok.RequiredArgsConstructor;

@Named
@Service(value = "文件上传", type = Type.Command, name = "file")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    
    private final FileManagerSpi fileManager; 

    @Override
    public File uploadFile(MultipartFile file, String path) {
        
        Util.check(!file.isEmpty(), "Cannot upload empty file");

        return fileManager.upload(file, path);
    }

    @Override
    public void downloadFile(String id, HttpServletResponse response) {

        // 设置 header 和 contentType
        fileManager.download(id, response);
    }
}
