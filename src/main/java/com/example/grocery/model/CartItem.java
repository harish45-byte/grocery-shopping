package com.example.grocery.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifies which shopper's cart this row belongs to. No login system in
    // this simple app, so the browser generates and stores a random cart id.
    @Column(nullable = false)
    private String cartOwnerId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    public CartItem() {
    }

    public CartItem(String cartOwnerId, Product product, Integer quantity) {
        this.cartOwnerId = cartOwnerId;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCartOwnerId() {
        return cartOwnerId;
    }

    public void setCartOwnerId(String cartOwnerId) {
        this.cartOwnerId = cartOwnerId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
