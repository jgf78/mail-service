package com.julian.mail_service.service;

import java.io.InputStream;

public interface StorageService {

    String upload(
            InputStream content,
            String fileName,
            String contentType,
            long size);

    InputStream download(String storageKey);

    void delete(String storageKey);
}