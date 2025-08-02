package com.example.ecommerce.dto;

import com.example.ecommerce.model.Product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotNull(message = "Price is required")
  @Min(value = 0, message = "Price can't be negative")
  private Double price;

  @NotNull
  @Min(value = 0, message = "Stock can't be negative")
  private Integer stock;

  public Product toEntity() {
    return Product.builder()
        .name(this.name)
        .description(this.description)
        .price(this.price)
        .stock(this.stock)
        .build();
  }
}
