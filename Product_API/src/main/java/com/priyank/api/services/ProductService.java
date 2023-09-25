package com.priyank.api.services;



import com.priyank.api.payload.*;
import com.priyank.api.exceptions.*;
import com.priyank.api.entites.*;
import com.priyank.api.respositery.*;
import com.priyank.api.security.AuthenticationFacade;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade; 

    @Autowired
    private ModelMapper modelMapper; 

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        return (product != null) ? modelMapper.map(product, ProductDTO.class) : null;
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        if (authenticationFacade.isAdminUser()) {
            Product product = modelMapper.map(productDTO, Product.class);
            Product createdProduct = productRepository.save(product);
            return modelMapper.map(createdProduct, ProductDTO.class);
        } else {
            throw new CustomException("Only admin users are allowed to create products.");
        }
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            modelMapper.map(productDTO, existingProduct);
            Product updatedProduct = productRepository.save(existingProduct);
            return modelMapper.map(updatedProduct, ProductDTO.class);
        } else {
            throw new CustomException("Product not found with id " + id);
        }
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
