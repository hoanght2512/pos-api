package hoanght.posapi.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${app.rabbitmq.email-queue-name}")
    private String emailQueueName;

    @Value("${app.rabbitmq.email-exchange-name}")
    private String emailExchangeName;

    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueueName, true);
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(emailExchangeName, true, false);
    }

    @Bean
    public Binding binding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(emailRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
