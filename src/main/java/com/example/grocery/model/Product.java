package com.example.grocery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String category;

    @PositiveOrZero
    private Double price;

    private String unit; // e.g. "kg", "dozen", "litre", "pack"

    @PositiveOrZero
    private Integer stock;

    private String imageEmoji; // simple emoji used as a lightweight product icon

    public Product() {
    }

    public Product(String name, String category, Double price, String unit, Integer stock, String imageEmoji) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.unit = unit;
        this.stock = stock;
        this.imageEmoji = imageEmoji;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImageEmoji() {
        return imageEmoji;
    }

    public void setImageEmoji(String imageEmoji) {
        this.imageEmoji = imageEmoji;
    }
}
