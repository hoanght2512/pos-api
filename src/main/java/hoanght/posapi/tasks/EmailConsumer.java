package hoanght.posapi.tasks;

import hoanght.posapi.dto.common.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailConsumer {
    private final JavaMailSender mailSender;

    @RabbitListener(queues = "${app.prefix}.email-queue")
    public void receiveEmailMessage(EmailMessage emailMessage) {
        log.debug("Received email message: {}", emailMessage);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailMessage.getTo());
            helper.setSubject(emailMessage.getSubject());
            helper.setText(emailMessage.getBody(), true);

            mailSender.send(message);
            log.debug("Email sent successfully to: {}", emailMessage.getTo());
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", emailMessage.getTo(), e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending email to: {}", emailMessage.getTo(), e);
        }
    }
}
