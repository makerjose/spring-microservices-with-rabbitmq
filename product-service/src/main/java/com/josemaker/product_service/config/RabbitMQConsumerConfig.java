package com.josemaker.product_service.config;

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
    private final String orderCreatedQueue;
    private final String orderCreatedExchange;

    public RabbitMQConsumerConfig(
            @Value("${spring.rabbitmq.host}") String rabbitMQHost,
            @Value("${spring.rabbitmq.port}") int rabbitMQPort,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${rabbitmq.queues.order-created}") String orderCreatedQueue,
            @Value("${rabbitmq.exchanges.order-created}") String orderCreatedExchange) {
        this.rabbitMQHost = rabbitMQHost;
        this.rabbitMQPort = rabbitMQPort;
        this.username = username;
        this.password = password;
        this.orderCreatedQueue = orderCreatedQueue;
        this.orderCreatedExchange = orderCreatedExchange;
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
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueue, true); // durable queue
    }

    // bind exchange to queue
    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(new FanoutExchange(orderCreatedExchange, true, false)); // Use existing exchange
    }
}
