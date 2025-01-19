package com.josemaker.product_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josemaker.product_service.avro.OrderProcessedEvent;
import com.josemaker.product_service.avro.ProductCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final String productCreatedExchange;
    private final String orderProcessedExchange;
    private final ObjectMapper objectMapper;

    public RabbitMQProducerService(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchanges.product-created}") String productCreatedExchange,
            @Value("${rabbitmq.exchanges.order-processed}") String orderProcessedExchange,
            ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.productCreatedExchange = productCreatedExchange;
        this.orderProcessedExchange = orderProcessedExchange;
        this.objectMapper = objectMapper;
    }

    public void sendProductCreatedEvent(ProductCreatedEvent productCreatedEvent) {
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("content_type", "application/json");

            byte[] messageBody = objectMapper.writeValueAsBytes(productCreatedEvent);

            rabbitTemplate.convertAndSend(productCreatedExchange, "", messageBody, message -> {
                message.getMessageProperties().getHeaders().putAll(headers);
                return message;
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize and send ProductCreatedEvent", e);
        }
    }

    public void sendOrderProcessedEvent(OrderProcessedEvent orderProcessedEvent) {
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("content_type", "application/json");

            byte[] messageBody = objectMapper.writeValueAsBytes(orderProcessedEvent);

            rabbitTemplate.convertAndSend(orderProcessedExchange, "", messageBody, message -> {
                message.getMessageProperties().getHeaders().putAll(headers);
                return message;
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize and send OrderProcessedEvent", e);
        }
    }
}
