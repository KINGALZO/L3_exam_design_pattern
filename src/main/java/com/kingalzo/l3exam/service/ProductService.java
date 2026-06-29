package com.kingalzo.l3exam.service;

import com.kingalzo.l3exam.model.Product;
import com.kingalzo.l3exam.repository.ProductRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void initProducts() {
        repository.save(new Product("Book", "Book", 25.0));
        repository.save(new Product("Electronic device", "Electronic", 180.0));
        repository.save(new Product("Toy", "Toy", 15.0));
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProduct(Long id) {
        return repository.findById(id).orElse(null);
    }
}
