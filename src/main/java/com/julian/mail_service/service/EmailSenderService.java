package com.julian.mail_service.service;

import com.julian.mail_service.dto.EmailRequest;

public interface EmailSenderService {

    void send(EmailRequest request);

}
