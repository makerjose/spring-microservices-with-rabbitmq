package com.josemaker.order_service.services;

import com.josemaker.order_service.entities.OrderEntity;
import com.josemaker.order_service.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    // save to DB
    public void createOrder(OrderEntity orderEntity){
        orderRepository.save(orderEntity);
    }
}
