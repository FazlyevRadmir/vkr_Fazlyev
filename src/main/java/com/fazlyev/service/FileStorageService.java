package com.fazlyev.service;

import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public interface FileStorageService {
    String uploadFile(MultipartFile file, String folder) throws IOException;
    List<String> uploadFiles(MultipartFile[] files, String folder) throws IOException;
    String generateDownloadUrl(String fileId);
    byte[] downloadFile(String filePath) throws IOException;
}