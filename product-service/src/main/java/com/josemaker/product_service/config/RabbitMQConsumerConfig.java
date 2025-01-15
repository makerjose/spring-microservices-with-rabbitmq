package com.josemaker.product_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
    private final String queueName;
    private final String exchangeName;

    public RabbitMQConsumerConfig(
            @Value("${spring.rabbitmq.host}") String rabbitMQHost,
            @Value("${spring.rabbitmq.port}") int rabbitMQPort,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${rabbitmq.queues.order-processed}") String queueName,
            @Value("${rabbitmq.exchanges.product-created}") String exchangeName) {
        this.rabbitMQHost = rabbitMQHost;
        this.rabbitMQPort = rabbitMQPort;
        this.username = username;
        this.password = password;
        this.queueName = queueName;
        this.exchangeName = exchangeName;
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
    public Queue queue() {
        return new Queue(queueName, true); // durable queue
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("product.created.#");
    }
}
