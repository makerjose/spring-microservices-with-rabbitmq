package com.josemaker.order_service.controllers;

import com.josemaker.order_service.dtos.OrderRequestDto;
import com.josemaker.order_service.entities.OrderEntity;
import com.josemaker.order_service.services.EmailValidator;
import com.josemaker.order_service.services.RabbitMQProducerService;
import com.josemaker.order_service.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@Tag(name = "Order Controller", description = "APIs for managing orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    @Autowired
    private EmailValidator emailValidator;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/createOrder")
    @Operation(summary = "Create an order", description = "Creates order and sends RabbitMQ message to Product Service for processing")
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto request) {
        try {
            if (request == null) {
                request.setMessage("Bad Request! Please provide a valid email address.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(request);
            }

            // Validate email
            String validationMessage = emailValidator.validate(request.getCustomerEmail());
            if (!validationMessage.equals("valid")) {
                request.setMessage(validationMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(request);
            }
            // Proceed with order creation if email is valid
            OrderEntity orderEntity = new OrderEntity();

            // Set data to entity
            orderEntity.setProductId(request.getProductId());
            orderEntity.setCustomerName(request.getCustomerName());
            orderEntity.setCustomerEmail(request.getCustomerEmail());
            orderEntity.setQuantity(request.getQuantity());
            orderEntity.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            // Save to DB
            orderService.createOrder(orderEntity);

            // Send RabbitMQ message after successful save
            rabbitMQProducerService.sendOrderCreatedEvent(orderEntity);

            // Populate response DTO
            request.setOrderDate(orderEntity.getOrderDate());
            request.setMessage("Order created successfully");

            logger.info("Order created successfully: {}", orderEntity);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            request.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(request);
        }
    }

    @GetMapping("/getAllOrders")
    @Operation(summary = "Get all orders", description = "Fetches all orders from the database.")
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        try {
            List<OrderEntity> orders = orderService.getAllOrders();
            logger.info("Fetched all orders successfully: {}", orders);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
