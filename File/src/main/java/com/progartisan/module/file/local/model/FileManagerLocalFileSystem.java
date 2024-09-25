package com.progartisan.module.file.local.model;

import com.progartisan.component.common.Util;
import com.progartisan.module.file.api.File;
import com.progartisan.module.file.model.FileManagerSpi;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RequiredArgsConstructor
@Component
public class FileManagerLocalFileSystem implements FileManagerSpi {

    @Value("${app.file.local.basePath}")
    private String fileRootPath;
    private final FileRepository repository;
    private final ConvertFile convert = Mappers.getMapper(ConvertFile.class);;

    private Path resolve(FilePO file) {
        Path uploadPath = Paths.get(fileRootPath + "/" + file.getPath());
        return uploadPath.resolve(String.format("f%s_%s", file.getFileId(), file.getFileName()));

    }

    @Override
    public File upload(MultipartFile file, String path) {
        try {
            Path uploadPath = Paths.get(fileRootPath);

            if (Util.isNotEmpty(path)) {
                // 创建子目录
                uploadPath = uploadPath.resolve(path);
            }

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            FilePO filePO = new FilePO();
            filePO.setFileName(file.getOriginalFilename());
            filePO.setPath(path);
            filePO.setSize(file.getSize());
            filePO.setMimeType(file.getContentType());
            filePO = repository.save(filePO);
            var filePath = resolve(filePO);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return convert.poToDto(filePO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void download(String id, HttpServletResponse response) {

        FilePO file = repository.findById(id).orElseThrow();

        Path filePath = resolve(file);
        // Java 7+的try-with-resources语句，它可以确保关闭流
        try (BufferedInputStream inStream = new BufferedInputStream(Files.newInputStream(filePath));
                BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream())) {

            // TODO 设置contentType放到service？
            if (file.getMimeType().startsWith("image/")) {
                response.setContentType(file.getMimeType());
            } else if (file.getMimeType().startsWith("application/pdf")) {
                response.setContentType(file.getMimeType());
            } else {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
            }

            byte[] buffer = new byte[10240]; // 缓冲区大小可根据需要调整
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
                outStream.flush(); // 刷新输出流，确保字节已经发送
            }
        } catch (IOException e) {
            // 处理异常，可以选择记录日志或者发送错误信息到客户端
            // response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException("Error sending file", e);
        }

    }

    @Override
    public void delete(String id) {
        FilePO file = repository.findById(id).orElseThrow();
        Path filePath = resolve(file);
        try {
            Files.delete(filePath);
            repository.delete(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
