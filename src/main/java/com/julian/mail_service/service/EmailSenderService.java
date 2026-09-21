package com.julian.mail_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.julian.mail_service.dto.EmailDetailResponse;
import com.julian.mail_service.dto.EmailRequest;

public interface EmailSenderService {

    public Long send(
            EmailRequest request,
            MultipartFile[] attachments);
    
    List<EmailDetailResponse> findAll();

    Optional<EmailDetailResponse> findById(Long id);

}
