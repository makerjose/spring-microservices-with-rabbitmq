package com.josemaker.order_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josemaker.order_service.entities.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final String orderCreatedExchange;
    private final ObjectMapper objectMapper;

    // Constructor to initialize RabbitTemplate, exchange name, and ObjectMapper
    public RabbitMQProducerService(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchanges.order-created}") String orderCreatedExchange,
            ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderCreatedExchange = orderCreatedExchange;
        this.objectMapper = objectMapper;
    }

    // Sends an OrderCreated event to the RabbitMQ exchange
    // Converts the OrderEntity object to a JSON byte array before sending
    // Adds necessary headers, including content type as "application/json"
    // Uses a fanout exchange (empty routing key) to broadcast the event to all bound queues
    public void sendOrderCreatedEvent(OrderEntity orderEntity) {
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("content_type", "application/json");

            byte[] messageBody = objectMapper.writeValueAsBytes(orderEntity);

            rabbitTemplate.convertAndSend(orderCreatedExchange, "", messageBody, message -> {
                message.getMessageProperties().getHeaders().putAll(headers);
                return message;
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize and send OrderCreatedEvent", e);
        }
    }
}
