package com.josemaker.product_service.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {

    private final String productCreatedExchange;
    private final String orderProcessedExchange;
    private final String productCreatedQueue;
    private final String orderProcessedQueue;

    public RabbitMQProducerConfig(
            @Value("${rabbitmq.exchanges.product-created}") String productCreatedExchange,
            @Value("${rabbitmq.exchanges.order-processed}") String orderProcessedExchange,
            @Value("${rabbitmq.queues.product-created}") String productCreatedQueue,
            @Value("${rabbitmq.queues.order-processed}") String orderProcessedQueue) {

        this.productCreatedExchange = productCreatedExchange;
        this.orderProcessedExchange = orderProcessedExchange;
        this.productCreatedQueue = productCreatedQueue;
        this.orderProcessedQueue = orderProcessedQueue;
    }

    // Define Fanout Exchanges
    @Bean
    public FanoutExchange productCreatedFanoutExchange() {
        return new FanoutExchange(productCreatedExchange);
    }

    @Bean
    public FanoutExchange orderProcessedFanoutExchange() {
        return new FanoutExchange(orderProcessedExchange);
    }

    // Define Queues
    @Bean
    public Queue productCreatedQueue() {
        return new Queue(productCreatedQueue);
    }

    @Bean
    public Queue orderProcessedQueue() {
        return new Queue(orderProcessedQueue);
    }

    // Bind Queues to Exchanges
    @Bean
    public Binding productCreatedBinding(Queue productCreatedQueue, FanoutExchange productCreatedFanoutExchange) {
        return BindingBuilder.bind(productCreatedQueue).to(productCreatedFanoutExchange);
    }

    @Bean
    public Binding orderProcessedBinding(Queue orderProcessedQueue, FanoutExchange orderProcessedFanoutExchange) {
        return BindingBuilder.bind(orderProcessedQueue).to(orderProcessedFanoutExchange);
    }
}
