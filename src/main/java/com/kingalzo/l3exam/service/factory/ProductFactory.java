package com.kingalzo.l3exam.service.factory;

import com.kingalzo.l3exam.model.Product;

public class ProductFactory {
    public static Product createProduct(String type) {
        return switch (type.toLowerCase()) {
            case "book" -> new Product("Book", "Book", 25.0);
            case "electronic" -> new Product("Electronic device", "Electronic", 180.0);
            case "toy" -> new Product("Toy", "Toy", 15.0);
            default -> new Product("Generic item", "Generic", 10.0);
        };
    }
}
