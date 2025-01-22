package com.josemaker.product_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josemaker.product_service.dtos.OrderProcessedDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final String orderProcessedExchange;
    private final ObjectMapper objectMapper;

    public RabbitMQProducerService(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchanges.order-processed}") String orderProcessedExchange,
            ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderProcessedExchange = orderProcessedExchange;
        this.objectMapper = objectMapper;
    }

    public void sendOrderProcessedEvent(OrderProcessedDto orderProcessedDto) {
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("content_type", "application/json");

            byte[] messageBody = objectMapper.writeValueAsBytes(orderProcessedDto);

            rabbitTemplate.convertAndSend(orderProcessedExchange, "", messageBody, message -> {
                message.getMessageProperties().getHeaders().putAll(headers);
                return message;
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize and send OrderProcessedEvent", e);
        }
    }
}
