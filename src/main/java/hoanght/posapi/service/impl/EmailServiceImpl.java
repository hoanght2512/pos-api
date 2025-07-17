package hoanght.posapi.service.impl;

import hoanght.posapi.config.RabbitMQConfig;
import hoanght.posapi.dto.common.EmailMessage;
import hoanght.posapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final RabbitTemplate rabbitTemplate;
    private final TemplateEngine templateEngine;
    private final RabbitMQConfig rabbitMQConfig;

    @Value("${app.frontend.name}")
    private String frontendName;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendEmailResetPassword(String email, String username, String token) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("resetUrl", String.format("%s/reset-password?token=%s", frontendUrl, token));
        context.setVariable("appName", frontendName);
        context.setVariable("expirationTime", "15 phút");
        String htmlContent = templateEngine.process("email/reset-password", context);
        EmailMessage payload = EmailMessage.builder().to(email).subject(String.format("[%s] Yêu cầu đặt lại mật khẩu", frontendName)).body(htmlContent).build();
        rabbitTemplate.convertAndSend(rabbitMQConfig.getEmailExchangeName(), rabbitMQConfig.getEmailRoutingKey(), payload);
    }
}
