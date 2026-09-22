package com.example.grocery.controller;

import com.example.grocery.model.CartItem;
import com.example.grocery.model.Order;
import com.example.grocery.model.OrderItem;
import com.example.grocery.repository.CartItemRepository;
import com.example.grocery.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    public OrderController(OrderRepository orderRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public record CheckoutRequest(String cartOwnerId, String customerName, String deliveryAddress) {}

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByCartOwnerId(request.cartOwnerId());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Your cart is empty"));
        }

        Order order = new Order();
        order.setCartOwnerId(request.cartOwnerId());
        order.setCustomerName(request.customerName());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setPlacedAt(LocalDateTime.now());

        double total = 0.0;
        for (CartItem ci : cartItems) {
            double lineTotal = ci.getProduct().getPrice() * ci.getQuantity();
            total += lineTotal;
            order.getItems().add(new OrderItem(order, ci.getProduct().getName(), ci.getProduct().getPrice(), ci.getQuantity()));
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByCartOwnerId(request.cartOwnerId());

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{cartOwnerId}")
    public List<Order> orderHistory(@PathVariable String cartOwnerId) {
        return orderRepository.findByCartOwnerIdOrderByPlacedAtDesc(cartOwnerId);
    }
}
