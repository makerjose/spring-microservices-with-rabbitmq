package com.josemaker.product_service.controllers;

import com.josemaker.product_service.dtos.ProductDto;
import com.josemaker.product_service.entities.ProductEntity;
import com.josemaker.product_service.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {

    @Autowired
    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/createProduct")
    @Operation(summary = "Create a new product", description = "Creates a new product and saves it to the database.")
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

            logger.info("Product created: {}", productEntity.getName());
            request.setMessage("Success, product created");
            return ResponseEntity.ok(request);

        } catch (Exception e) {
            logger.error("Error creating product: ", e);
            request.setMessage("Error!: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(request);
        }
    }

    @GetMapping("/getAllProducts")
    @Operation(summary = "Get all products", description = "Fetches all products from the database.")
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        try {
            List<ProductEntity> products = productService.getAllProducts();
            logger.info("Fetched products: {}", products);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.warn("Error fetching products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
