package com.julian.mail_service.kafka;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.julian.mail_service.dto.EmailRequest;
import com.julian.mail_service.service.EmailSenderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailKafkaConsumer {

    private final EmailSenderService emailSenderService;

    @KafkaListener(
            topics = "mail.send",
            groupId = "mail-service"
    )
    public void consume(EmailSendEvent event) {

        log.info(
                "Email recibido desde Kafka. subject={}",
                event.subject()
        );

        EmailRequest request = new EmailRequest(
                event.to(),
                event.cc(),
                event.bcc(),
                event.replyTo(),
                event.subject(),
                event.body(),
                event.html()
        );

        MultipartFile[] attachments =
                createAttachments(event.attachments());

        emailSenderService.send(request, attachments);
    }

    private MultipartFile[] createAttachments(
            List<EmailAttachmentEvent> attachmentEvents) {

        if (attachmentEvents == null || attachmentEvents.isEmpty()) {
            return new MultipartFile[0];
        }

        return attachmentEvents.stream()
                .map(attachment ->
                        new KafkaMultipartFile(
                                attachment.filename(),
                                attachment.contentType(),
                                attachment.content()
                        )
                )
                .toArray(MultipartFile[]::new);
    }

    private static final class KafkaMultipartFile
            implements MultipartFile {

        private final String filename;
        private final String contentType;
        private final byte[] content;

        private KafkaMultipartFile(
                String filename,
                String contentType,
                byte[] content) {

            this.filename = filename;
            this.contentType = contentType;
            this.content = content;
        }

        @Override
        public String getName() {
            return filename;
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content == null ? 0 : content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(Path destination) throws IOException {
            Files.write(destination, content);
        }

        @Override
        public void transferTo(File dest)
                throws IOException, IllegalStateException {

            Files.write(dest.toPath(), content);
        }
    }
}