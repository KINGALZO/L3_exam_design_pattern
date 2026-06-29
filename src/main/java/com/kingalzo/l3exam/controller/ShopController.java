package com.kingalzo.l3exam.controller;

import com.kingalzo.l3exam.model.Order;
import com.kingalzo.l3exam.model.Product;
import com.kingalzo.l3exam.service.OrderService;
import com.kingalzo.l3exam.service.PaymentContext;
import com.kingalzo.l3exam.service.PaymentStrategy;
import com.kingalzo.l3exam.service.ProductService;
import com.kingalzo.l3exam.service.decorator.DiscountedProduct;
import com.kingalzo.l3exam.service.factory.ProductFactory;
import com.kingalzo.l3exam.service.observer.EmailNotificationObserver;
import com.kingalzo.l3exam.service.observer.OrderSubject;
import com.kingalzo.l3exam.service.strategy.CreditCardPaymentStrategy;
import com.kingalzo.l3exam.service.strategy.PayPalPaymentStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShopController {
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderSubject orderSubject;

    public ShopController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
        this.orderSubject = new OrderSubject();
        this.orderSubject.registerObserver(new EmailNotificationObserver("student@example.com"));
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/orders")
    public Order createOrder(@RequestParam Long productId, @RequestParam int quantity) {
        Order order = orderService.createOrder(productId, quantity);
        orderSubject.notifyObservers("New order created for productId=" + productId + " qty=" + quantity);
        return order;
    }

    @PostMapping("/payments")
    public String pay(@RequestParam double amount, @RequestParam String method) {
        PaymentStrategy strategy = switch (method.toLowerCase()) {
            case "creditcard", "card" -> new CreditCardPaymentStrategy();
            case "paypal" -> new PayPalPaymentStrategy();
            default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
        };
        return new PaymentContext(strategy).executePayment(amount);
    }

    @GetMapping("/factory/{type}")
    public Product createProductWithFactory(@PathVariable String type) {
        return ProductFactory.createProduct(type);
    }

    @GetMapping("/discount/{productId}")
    public String getDiscountedProduct(@PathVariable Long productId) {
        Product product = productService.getProduct(productId);
        if (product == null) {
            return "Product not found";
        }
        DiscountedProduct decorated = new DiscountedProduct(product, 0.15);
        return decorated.getDescription() + " price=" + decorated.getDiscountedPrice();
    }

    @GetMapping("/orders")
    public List<Order> listOrders() {
        return orderService.getOrders();
    }
}
