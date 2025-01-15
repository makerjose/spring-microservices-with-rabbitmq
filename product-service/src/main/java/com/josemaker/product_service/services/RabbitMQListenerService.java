package com.josemaker.product_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josemaker.product_service.avro.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListenerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListenerService.class);

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public RabbitMQListenerService(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.queues.order-processed}")
    public void consumeOrderEvent(byte[] messageBody) {
        try {
            // Deserialize message
            OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(messageBody, OrderCreatedEvent.class);
            logger.info("Received Order Event: {}", orderCreatedEvent);

            // Process order and update inventory
            productService.updateProductQuantity(orderCreatedEvent);
        } catch (Exception e) {
            logger.error("Error processing order event: {}", e.getMessage(), e);
        }
    }
}
