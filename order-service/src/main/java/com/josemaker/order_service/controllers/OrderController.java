package com.josemaker.order_service.controllers;

import com.josemaker.order_service.avro.OrderCreatedEvent;
import com.josemaker.order_service.dtos.OrderRequestDto;
import com.josemaker.order_service.entities.OrderEntity;
import com.josemaker.order_service.services.RabbitMQProducerService;
import com.josemaker.order_service.services.OrderService;
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
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/createOrder")
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto request) {
        try {
            if (request == null) {
                request.setMessage("Bad Request!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(request);
            }

            OrderEntity orderEntity = new OrderEntity();

            // Set data to entity
            orderEntity.setProductId(request.getProductId());
            orderEntity.setCustomerName(request.getCustomerName());
            orderEntity.setCustomerEmail(request.getCustomerEmail());
            orderEntity.setQuantity(request.getQuantity());
            orderEntity.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            // Save to DB
            orderService.createOrder(orderEntity);

            // Map entity to Avro class for RabbitMQ message
            OrderCreatedEvent orderCreated = mapEntityToAvro(orderEntity);

            // Send RabbitMQ message after successful save
            rabbitMQProducerService.sendOrderCreatedEvent(orderCreated);

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

    private OrderCreatedEvent mapEntityToAvro(OrderEntity entity) {
        return OrderCreatedEvent.newBuilder()
                .setOrderId(entity.getOrderId())
                .setProductId(entity.getProductId())
                .setCustomerName(entity.getCustomerName())
                .setCustomerEmail(entity.getCustomerEmail())
                .setQuantity(entity.getQuantity())
                .setOrderDate(entity.getOrderDate())
                .build();
    }
}
