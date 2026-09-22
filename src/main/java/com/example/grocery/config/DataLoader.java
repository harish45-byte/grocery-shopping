package com.example.grocery.config;

import com.example.grocery.model.Product;
import com.example.grocery.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.saveAll(java.util.List.of(
                new Product("Bananas", "Fruits", 40.0, "dozen", 60, "\uD83C\uDF4C"),
                new Product("Apples", "Fruits", 180.0, "kg", 45, "\uD83C\uDF4E"),
                new Product("Oranges", "Fruits", 90.0, "kg", 50, "\uD83C\uDF4A"),
                new Product("Mangoes", "Fruits", 120.0, "kg", 30, "\uD83E\uDD6D"),
                new Product("Grapes", "Fruits", 95.0, "kg", 25, "\uD83C\uDF47"),
                new Product("Tomatoes", "Vegetables", 35.0, "kg", 80, "\uD83C\uDF45"),
                new Product("Potatoes", "Vegetables", 28.0, "kg", 100, "\uD83E\uDD54"),
                new Product("Onions", "Vegetables", 32.0, "kg", 90, "\uD83E\uDDC5"),
                new Product("Carrots", "Vegetables", 45.0, "kg", 55, "\uD83E\uDD55"),
                new Product("Spinach", "Vegetables", 25.0, "bunch", 40, "\uD83E\uDD6C"),
                new Product("Whole Milk", "Dairy", 62.0, "litre", 70, "\uD83E\uDD5B"),
                new Product("Greek Yogurt", "Dairy", 55.0, "tub", 35, "\uD83E\uDD63"),
                new Product("Cheddar Cheese", "Dairy", 220.0, "pack", 20, "\uD83E\uDDC0"),
                new Product("Butter", "Dairy", 240.0, "pack", 25, "\uD83E\uDDC8"),
                new Product("Free-range Eggs", "Dairy", 85.0, "dozen", 60, "\uD83E\uDD5A"),
                new Product("Whole Wheat Bread", "Bakery", 45.0, "loaf", 40, "\uD83C\uDF5E"),
                new Product("Croissants", "Bakery", 30.0, "piece", 30, "\uD83E\uDD50"),
                new Product("Basmati Rice", "Pantry", 110.0, "kg", 75, "\uD83C\uDF5A"),
                new Product("Rolled Oats", "Pantry", 150.0, "pack", 40, "\uD83E\uDD63"),
                new Product("Extra Virgin Olive Oil", "Pantry", 480.0, "litre", 20, "\uD83E\uDED2"),
                new Product("Almonds", "Pantry", 620.0, "kg", 22, "\uD83C\uDF30"),
                new Product("Orange Juice", "Beverages", 95.0, "litre", 35, "\uD83E\uDDC3"),
                new Product("Filter Coffee", "Beverages", 260.0, "pack", 28, "\u2615"),
                new Product("Green Tea", "Beverages", 180.0, "pack", 32, "\uD83C\uDF75"),
                new Product("Dark Chocolate", "Snacks", 140.0, "bar", 45, "\uD83C\uDF6B"),
                new Product("Mixed Nuts", "Snacks", 350.0, "pack", 26, "\uD83E\uDD5C")
        ));
    }
}
