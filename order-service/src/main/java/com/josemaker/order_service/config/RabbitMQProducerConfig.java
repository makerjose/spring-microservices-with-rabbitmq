package com.josemaker.order_service.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {

    private final String orderCreatedExchange;
    private final String orderCreatedQueue;

    public RabbitMQProducerConfig(
            @Value("${rabbitmq.exchanges.order-created}") String orderCreatedExchange,
            @Value("${rabbitmq.queues.order-created}") String orderCreatedQueue) {

        this.orderCreatedExchange = orderCreatedExchange;
        this.orderCreatedQueue = orderCreatedQueue;
    }

    // Define Fanout Exchanges
    @Bean
    public FanoutExchange orderCreatedFanoutExchange() {
        return new FanoutExchange(orderCreatedExchange);
    }

    // Define Queues
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueue, true);
    }

    // Bind Queues to Exchanges
    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, FanoutExchange orderCreatedFanoutExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderCreatedFanoutExchange);
    }
}
