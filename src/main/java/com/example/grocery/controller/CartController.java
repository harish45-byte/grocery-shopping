package com.example.grocery.controller;

import com.example.grocery.model.CartItem;
import com.example.grocery.model.Product;
import com.example.grocery.repository.CartItemRepository;
import com.example.grocery.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartController(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    // Returns the cart contents plus a running total, keyed by the caller's cartOwnerId.
    @GetMapping("/{cartOwnerId}")
    public Map<String, Object> viewCart(@PathVariable String cartOwnerId) {
        List<CartItem> items = cartItemRepository.findByCartOwnerId(cartOwnerId);
        double total = items.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
        return Map.of(
                "items", items,
                "itemCount", items.stream().mapToInt(CartItem::getQuantity).sum(),
                "total", total
        );
    }

    public record AddItemRequest(Long productId, Integer quantity) {}

    @PostMapping("/{cartOwnerId}/items")
    public ResponseEntity<?> addItem(@PathVariable String cartOwnerId, @RequestBody AddItemRequest request) {
        Product product = productRepository.findById(request.productId()).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Product not found"));
        }
        int qty = request.quantity() == null || request.quantity() < 1 ? 1 : request.quantity();

        CartItem item = cartItemRepository.findByCartOwnerIdAndProductId(cartOwnerId, product.getId())
                .orElseGet(() -> new CartItem(cartOwnerId, product, 0));
        item.setQuantity(item.getQuantity() + qty);
        cartItemRepository.save(item);

        return ResponseEntity.ok(viewCart(cartOwnerId));
    }

    public record UpdateQuantityRequest(Integer quantity) {}

    @PutMapping("/{cartOwnerId}/items/{itemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable String cartOwnerId, @PathVariable Long itemId,
                                             @RequestBody UpdateQuantityRequest request) {
        CartItem item = cartItemRepository.findById(itemId).orElse(null);
        if (item == null || !item.getCartOwnerId().equals(cartOwnerId)) {
            return ResponseEntity.notFound().build();
        }
        if (request.quantity() == null || request.quantity() < 1) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(request.quantity());
            cartItemRepository.save(item);
        }
        return ResponseEntity.ok(viewCart(cartOwnerId));
    }

    @DeleteMapping("/{cartOwnerId}/items/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable String cartOwnerId, @PathVariable Long itemId) {
        CartItem item = cartItemRepository.findById(itemId).orElse(null);
        if (item != null && item.getCartOwnerId().equals(cartOwnerId)) {
            cartItemRepository.delete(item);
        }
        return ResponseEntity.ok(viewCart(cartOwnerId));
    }

    @DeleteMapping("/{cartOwnerId}")
    public ResponseEntity<?> clearCart(@PathVariable String cartOwnerId) {
        cartItemRepository.deleteByCartOwnerId(cartOwnerId);
        return ResponseEntity.ok(viewCart(cartOwnerId));
    }
}
