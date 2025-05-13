package com.fazlyev.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FirebaseFileStorageService implements FileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FirebaseFileStorageService.class);
    private final Storage storage;
    private final String bucketName;

    @Autowired
    public FirebaseFileStorageService(Storage storage) {
        this.storage = storage;
        this.bucketName = StorageClient.getInstance().bucket().getName();;
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String fullPath = folder + "/" + fileName;

            BlobId blobId = BlobId.of(bucketName, fullPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            // Используем стандартный URL Firebase
            return "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" +
                    URLEncoder.encode(fullPath, "UTF-8") + "?alt=media";
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new IOException("File upload failed", e);
        }
    }


    @Override
    public List<String> uploadFiles(MultipartFile[] files, String folder) throws IOException {
        return Arrays.stream(files)
                .map(file -> {
                    try {
                        return uploadFile(file, folder);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    public String generateDownloadUrl(String filePath) {
        BlobId blobId = BlobId.of(bucketName, filePath);
        Blob blob = storage.get(blobId);
        return blob.signUrl(7, TimeUnit.DAYS).toString();  // Токен на 7 дней
    }

    @Override
    public byte[] downloadFile(String filePath) throws IOException {
        try {
            BlobId blobId = BlobId.of(bucketName, filePath);
            Blob blob = storage.get(blobId);
            if (blob == null) {
                throw new IOException("File not found: " + filePath);
            }
            return blob.getContent();
        } catch (Exception e) {
            log.error("Failed to download file: " + filePath, e);
            throw new IOException("File download failed", e);
        }
    }

}