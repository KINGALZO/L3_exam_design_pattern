package com.kingalzo.l3exam.service.decorator;

import com.kingalzo.l3exam.model.Product;

public class DiscountedProduct {
    private final Product product;
    private final double discountRate;

    public DiscountedProduct(Product product, double discountRate) {
        this.product = product;
        this.discountRate = discountRate;
    }

    public double getDiscountedPrice() {
        return product.getPrice() * (1 - discountRate);
    }

    public String getDescription() {
        return product.getName() + " with " + (discountRate * 100) + "% discount";
    }
}
