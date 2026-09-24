package com.julian.mail_service.service.impl;

import com.julian.mail_service.service.StorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioStorageServiceImpl implements StorageService {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioStorageServiceImpl(
            MinioClient minioClient,
            @Value("${minio.bucket}") String bucket) {

        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    @Override
    public String upload(
            InputStream content,
            String fileName,
            String contentType,
            long size) {

        String storageKey = UUID.randomUUID() + "-" + fileName;

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(storageKey)
                            .stream(content, size, -1)
                            .contentType(contentType)
                            .build()
            );

            return storageKey;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error uploading file to MinIO", e);
        }
    }

    @Override
    public InputStream download(String storageKey) {

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(storageKey)
                            .build()
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error downloading file from MinIO", e);
        }
    }

    @Override
    public void delete(String storageKey) {

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(storageKey)
                            .build()
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error deleting file from MinIO", e);
        }
    }
}