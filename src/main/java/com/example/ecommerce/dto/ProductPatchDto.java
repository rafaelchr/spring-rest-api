package com.example.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPatchDto {
  private String name;
  private String description;
  private Double price;
  private Integer stock;
}
