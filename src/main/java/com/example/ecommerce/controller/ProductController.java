package com.example.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.PagedResponse;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.dto.ProductPatchDto;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  @Autowired
  private ProductService productService;

  @GetMapping
  public PagedResponse<Product> getAllProducts(Pageable pageable) {
    Page<Product> page = productService.findAll(pageable);
    return new PagedResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    Product product = productService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    return ResponseEntity.ok(product);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDto productDto) {
    Product product = productDto.toEntity();
    return ResponseEntity.ok(productService.save(product));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Product> updateProduct(
      @PathVariable Long id,
      @Valid @RequestBody ProductPatchDto patchDto) {

    Product product = productService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));

    if (patchDto.getName() != null) {
      if (patchDto.getName().isBlank()) {
        throw new BadRequestException("Name is required");
      }
      product.setName(patchDto.getName());
    }
    if (patchDto.getDescription() != null) {
      if (patchDto.getDescription().isBlank()) {
        throw new BadRequestException("Description can't be blank");
      }
      product.setDescription(patchDto.getDescription());
    }
    if (patchDto.getPrice() != null) {
      if (patchDto.getPrice() < 0) {
        throw new BadRequestException("Price can't be negative");
      }
      product.setPrice(patchDto.getPrice());
    }
    if (patchDto.getStock() != null) {
      if (patchDto.getStock() < 0) {
        throw new BadRequestException("Stock can't be negative");
      }
      product.setStock(patchDto.getStock());
    }

    Product updated = productService.save(product);

    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    return productService.findById(id)
        .map(product -> {
          productService.deleteById(id);
          return ResponseEntity.noContent().<Void>build();
        })
        .orElse(ResponseEntity.notFound().build());
  }
}
