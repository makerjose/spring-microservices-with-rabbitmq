package com.josemaker.product_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 30, nullable = false)
    private String type;

}
