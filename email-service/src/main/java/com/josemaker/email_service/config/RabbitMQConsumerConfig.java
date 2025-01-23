package com.josemaker.email_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerConfig {

    private final String rabbitMQHost;
    private final int rabbitMQPort;
    private final String username;
    private final String password;
    private final String orderProcessedQueue;
    private final String orderProcessedExchange;

    public RabbitMQConsumerConfig(
            @Value("${spring.rabbitmq.host}") String rabbitMQHost,
            @Value("${spring.rabbitmq.port}") int rabbitMQPort,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${rabbitmq.queues.order-processed}") String orderProcessedQueue,
            @Value("${rabbitmq.exchanges.order-processed}") String orderProcessedExchange) {
        this.rabbitMQHost = rabbitMQHost;
        this.rabbitMQPort = rabbitMQPort;
        this.username = username;
        this.password = password;
        this.orderProcessedQueue = orderProcessedQueue;
        this.orderProcessedExchange = orderProcessedExchange;
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMQHost, rabbitMQPort);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public org.springframework.amqp.core.Queue orderProcessedQueue() {
        return new org.springframework.amqp.core.Queue(orderProcessedQueue, true);
    }

    @Bean
    public Binding orderProcessedBinding(Queue orderProcessedQueue) {
        return BindingBuilder.bind(orderProcessedQueue)
                .to(new FanoutExchange(orderProcessedExchange, true, false));
    }
}
