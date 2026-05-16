package com.example.bank.Controller;

import com.example.bank.Entity.Product;
import com.example.bank.Service.ProductService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService; // 注入的是 Service

    @PostMapping
    public ResponseEntity<String> addProduct(@Valid @RequestBody Product product) {
        // Controller 只負責呼叫 Service 並處理回傳狀態
        productService.addProduct(product);
        return ResponseEntity.ok("新增成功");
    }

    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }
}
