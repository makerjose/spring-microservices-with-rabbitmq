package com.josemaker.product_service.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {

    private final String orderProcessedExchange;
    private final String orderProcessedQueue;

    public RabbitMQProducerConfig(
            @Value("${rabbitmq.exchanges.order-processed}") String orderProcessedExchange,
            @Value("${rabbitmq.queues.order-processed}") String orderProcessedQueue) {

        this.orderProcessedExchange = orderProcessedExchange;
        this.orderProcessedQueue = orderProcessedQueue;
    }

    // Define Fanout Exchanges
    @Bean
    public FanoutExchange orderProcessedFanoutExchange() {
        return new FanoutExchange(orderProcessedExchange);
    }

    // Define orderProcessedQueue
    @Bean
    public Queue orderProcessedQueue() {
        return new Queue(orderProcessedQueue, true);
    }

    // Bind queue to fanout exchange
    @Bean
    public Binding orderProcessedBinding(Queue orderProcessedQueue, FanoutExchange orderProcessedFanoutExchange) {
        return BindingBuilder.bind(orderProcessedQueue).to(orderProcessedFanoutExchange);
    }
}
