package com.julian.mail_service.repository;

import com.julian.mail_service.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
