package com.julian.mail_service.service;

import org.springframework.web.multipart.MultipartFile;

import com.julian.mail_service.dto.EmailRequest;

public interface EmailSenderService {

    public void send(
            EmailRequest request,
            MultipartFile[] attachments);

}
