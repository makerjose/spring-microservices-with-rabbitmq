package com.josemaker.product_service.services;

import com.josemaker.product_service.avro.OrderCreatedEvent;
import com.josemaker.product_service.avro.OrderProcessedEvent;
import com.josemaker.product_service.dtos.OrderCreatedDto;
import com.josemaker.product_service.dtos.OrderProcessedDto;
import com.josemaker.product_service.entities.ProductEntity;
import com.josemaker.product_service.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    @Transactional
    public void createProduct(ProductEntity productEntity) {
        productRepository.save(productEntity);
    }

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public void updateProductQuantity(OrderCreatedEvent orderCreatedEvent) {
        try {
            Long productId = orderCreatedEvent.getProductId();
            Integer orderQuantity = orderCreatedEvent.getQuantity();

            ProductEntity productEntity = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product with ID " + productId + " not found"));

            if (productEntity.getQuantity() < orderQuantity) {
                throw new RuntimeException("Insufficient stock for Product ID: " + productId);
            }

            productEntity.setQuantity(productEntity.getQuantity() - orderQuantity);
            productRepository.save(productEntity);

            logger.info("Product quantity updated successfully for Product ID: {}", productId);

            OrderProcessedEvent orderProcessedEvent = new OrderProcessedEvent();
            orderProcessedEvent.setMessage("Order processed successfully");
            orderProcessedEvent.setCustomerName(orderCreatedEvent.getCustomerName());
            orderProcessedEvent.setCustomerEmail(orderCreatedEvent.getCustomerEmail());
            orderProcessedEvent.setQuantity(orderCreatedEvent.getQuantity());
            orderProcessedEvent.setProcessedDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            orderProcessedEvent.setProductName(productEntity.getName());
            orderProcessedEvent.setTotalPrice(productEntity.getTotalPrice());

            rabbitMQProducerService.sendOrderProcessedEvent(orderProcessedEvent);
            logger.info("Order processed event sent for Product ID: {}", productId);

        } catch (Exception e) {
            logger.error("Error updating product quantity: ", e);
            throw new RuntimeException("Failed to update product quantity: " + e.getMessage());
        }
    }
}
