package com.example.grocery.repository;

import com.example.grocery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCartOwnerIdOrderByPlacedAtDesc(String cartOwnerId);
}
