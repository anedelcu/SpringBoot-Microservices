package com.dailycodebuffer.ProductService.service;

import com.dailycodebuffer.ProductService.entity.Product;
import com.dailycodebuffer.ProductService.exception.ProductServiceCustomException;
import com.dailycodebuffer.ProductService.model.ProductRequest;
import com.dailycodebuffer.ProductService.model.ProductResponse;
import com.dailycodebuffer.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product..");

        Product product =
                Product.builder()
                        .productName(productRequest.getName())
                        .price(productRequest.getPrice())
                        .quantity(productRequest.getQuantity())
                        .build();
        productRepository.save(product);

        log.info("Product Created!");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {

        log.info("Get the product for productId: {}", productId);
        Product product
                = productRepository.findById(productId)
                .orElseThrow(
                        ()-> new ProductServiceCustomException("Product with given id not found.", "PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();
        copyProperties(product,productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce quantity {} for id: {}", quantity, productId);

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() -> new ProductServiceCustomException(
                                "Product with given Id not found",
                                "PRODUCT_NOT_FOUND"
                        ));

        if(product.getQuantity() < quantity) {
            throw new ProductServiceCustomException(
                    "Product does not have sufficient Quantity",
                    "INSUFICIENT_QUANTITY"
            );
        }
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            log.info("Product Quantity Updated Successfully");
    }
}
