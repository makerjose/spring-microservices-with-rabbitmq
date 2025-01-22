package com.josemaker.product_service.controllers;

import com.josemaker.product_service.avro.ProductCreatedEvent;
import com.josemaker.product_service.dtos.ProductDto;
import com.josemaker.product_service.entities.ProductEntity;
import com.josemaker.product_service.repositories.ProductRepository;
import com.josemaker.product_service.services.ProductService;
import com.josemaker.product_service.services.RabbitMQProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/createProduct")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto request) {
        try {
            if (request == null) {
                request.setMessage("Bad Request!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(request);
            }

            ProductEntity productEntity = new ProductEntity();
            productEntity.setName(request.getName());
            productEntity.setType(request.getType());
            productEntity.setTotalPrice(request.getTotalPrice());
            productEntity.setQuantity(request.getQuantity());

            productService.createProduct(productEntity);

//            ProductCreatedEvent productCreated = mapEntityToAvro(productEntity);
//            rabbitMQProducerService.sendProductCreatedEvent(productCreated);

            logger.info("Success, product created: {}, Type: {}, Price: {}, Quantity: {}",
                    productEntity.getName(), productEntity.getType(), productEntity.getTotalPrice(), productEntity.getQuantity());

            request.setMessage("Success, product created");
            return ResponseEntity.ok(request);

        } catch (Exception e) {
            logger.error("Error creating product: ", e);
            request.setMessage("Error!: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(request);
        }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        try {
            List<ProductEntity> products = productService.getAllProducts();
            logger.info("Successfully fetched all products: {}", products);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.warn("Error fetching products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    private ProductCreatedEvent mapEntityToAvro(ProductEntity entity) {
//        return ProductCreatedEvent.newBuilder()
//                .setProductId(entity.getProductId())
//                .setName(entity.getName())
//                .setTotalPrice(entity.getTotalPrice())
//                .setQuantity(entity.getQuantity())
//                .setType(entity.getType())
//                .build();
//    }

}

