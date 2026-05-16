package com.example.bank.Service;

import com.example.bank.Entity.Product;
import com.example.bank.Exception.BusinessException;
import com.example.bank.Repository.ProductRepository;
import com.example.bank.util.XssSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    /**
     * 處理新增商品的業務邏輯
     */
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(Product product) {
        // 1. 業務邏輯處理：防止 XSS，將 HTML 標籤轉義
        String safeName = HtmlUtils.htmlEscape(product.getProductName());

        // 2. 呼叫 Repository 執行資料庫 Procedure
        productRepository.insertProduct(
                product.getProductId(),
                safeName,
                product.getPrice(),
                product.getStock()
        );

    }
    public List<Product> getAvailableProducts() {
        // 取得庫存 > 0 的產品
        return productRepository.findAllAvailableProducts();
    }
}
