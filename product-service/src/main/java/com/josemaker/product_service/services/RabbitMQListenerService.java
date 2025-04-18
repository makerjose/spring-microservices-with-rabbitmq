package com.josemaker.product_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josemaker.product_service.dtos.OrderCreatedDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListenerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListenerService.class);

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    // Constructor, initialize productService and Object Mapper
    public RabbitMQListenerService(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    // Deserialize the message into orderCreatedDto object
    // Calls the productService to update the inventory based on the order details
    @RabbitListener(queues = "${rabbitmq.queues.order-created}")
    public void consumeOrderEvent(byte[] messageBody) {
        try {
            // Deserialize message
            OrderCreatedDto orderCreatedDto = objectMapper.readValue(messageBody, OrderCreatedDto.class);
            logger.info("Received Order Event: {}", orderCreatedDto);

            // Process order and update inventory
            productService.updateProductQuantity(orderCreatedDto);
        } catch (Exception e) {
            logger.error("Error processing order event: {}", e.getMessage(), e);
        }
    }
}
