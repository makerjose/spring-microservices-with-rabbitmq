package com.josemaker.email_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josemaker.email_service.dtos.OrderProcessedDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListenerService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListenerService.class);

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public RabbitMQListenerService(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${rabbitmq.queues.order-processed}")
    public void consumeOrderProcessedEvent(byte[] messageBody) {
        try {
            // Deserialize message
            OrderProcessedDto orderProcessedDto = objectMapper.readValue(messageBody, OrderProcessedDto.class);
            logger.info("Received Order Processed Event: {}", orderProcessedDto);

            // compose email
            String subject = "Order Processed: " + orderProcessedDto.getProductName();
            String body = String.format(
                    "Hello %s,\n\nYour order for %d unit(s) of %s has been processed successfully.\n" +
                            "Total Price: $%.2f\nProcessed Date: %s\n\nThank you for shopping with us!",
                    orderProcessedDto.getCustomerName(),
                    orderProcessedDto.getQuantity(),
                    orderProcessedDto.getProductName(),
                    orderProcessedDto.getTotalPrice(),
                    orderProcessedDto.getProcessedDate()
            );

            // send email
            emailService.sendEmail(orderProcessedDto.getCustomerEmail(), subject, body);

        } catch (Exception e) {
            logger.error("Error processing order processed event: {}", e.getMessage(), e);
        }
    }
}
