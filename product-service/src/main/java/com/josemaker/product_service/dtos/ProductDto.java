package com.josemaker.product_service.dtos;

import lombok.Data;

@Data
public class ProductDto {
    private String message;
    private String name;
    private Double totalPrice;
    private Integer quantity;
    private String type;
}
