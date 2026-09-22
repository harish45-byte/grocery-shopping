package com.example.grocery.repository;

import com.example.grocery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartOwnerId(String cartOwnerId);
    Optional<CartItem> findByCartOwnerIdAndProductId(String cartOwnerId, Long productId);
    void deleteByCartOwnerId(String cartOwnerId);
}
