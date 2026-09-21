package com.julian.mail_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailKafkaConsumer {

    @KafkaListener(
            topics = "mail.send",
            groupId = "mail-service"
    )
    public void consume(EmailSendEvent event) {

        log.info(
                "Email recibido desde Kafka. emailId={}, subject={}",
                event.emailId(),
                event.subject()
        );
    }
}
