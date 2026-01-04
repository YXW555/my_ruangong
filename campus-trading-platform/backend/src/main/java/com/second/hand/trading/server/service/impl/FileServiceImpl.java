package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {

    @Value("${userFilePath}")
    private String userFilePath;

    public boolean uploadFile(MultipartFile multipartFile, String fileName) throws IOException {
        File fileDir = new File(userFilePath);
        if (!fileDir.exists()) {
            // 如果目录不存在，则创建它。如果创建失败，则抛出异常。
            if (!fileDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + fileDir.getAbsolutePath());
            }
        }

        File file = new File(fileDir.getAbsolutePath(), fileName);

        try {
            // transferTo 会自动创建文件，我们只需要确保父目录存在即可
            multipartFile.transferTo(file);
            return true;
        } catch (IOException e) {
            // 打印更详细的错误日志，并重新抛出异常
            System.err.println("Error saving uploaded file to: " + file.getAbsolutePath());
            e.printStackTrace();
            throw e;
        }
    }
}
