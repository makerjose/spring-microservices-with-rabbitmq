package com.josemaker.order_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josemaker.order_service.avro.OrderCreatedEvent;
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

    public RabbitMQProducerService(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchanges.order-created}") String orderCreatedExchange,
            ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderCreatedExchange = orderCreatedExchange;
        this.objectMapper = objectMapper;
    }

    public void sendOrderCreatedEvent(OrderEntity orderEntity) {
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("content_type", "application/json");

            byte[] messageBody = objectMapper.writeValueAsBytes(orderEntity);

            rabbitTemplate.convertAndSend(orderCreatedExchange, "", messageBody, message -> {
                message.getMessageProperties().getHeaders().putAll(headers);
                System.out.println("ORDER CREATED EVENT SUCCESS!");
                return message;
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize and send OrderCreatedEvent", e);
        }
    }
}
