package com.josemaker.email_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {

    private final String emailSentExchange;
    private final String emailSentQueue;

    // Constructor injecting exchange and queue names from application properties
    public RabbitMQProducerConfig(
            @Value("${rabbitmq.exchanges.email-sent}") String emailSentExchange,
            @Value("${rabbitmq.queues.email-sent}") String emailSentQueue) {

        this.emailSentExchange = emailSentExchange;
        this.emailSentQueue = emailSentQueue;
    }

    // return configured fanout exchange bean
    @Bean
    public FanoutExchange emailSentFanoutExchange() {
        return new FanoutExchange(emailSentExchange);
    }

    // Creates a queue to hold email sent notifications.
    @Bean
    public Queue emailSentQueue() {
        return new Queue(emailSentQueue);
    }

    // Establishes binding between the email sent queue and exchange.
    // Allows messages published to the exchange to be routed to this queue.
    @Bean
    public Binding emailSentBinding(Queue emailSentQueue, FanoutExchange emailSentFanoutExchange) {
        return BindingBuilder.bind(emailSentQueue).to(emailSentFanoutExchange);
    }
}