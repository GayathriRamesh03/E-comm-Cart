package com.example.cart.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private int sellerId;
    private int productId;
    private String productName;
    private double productPrice;
    private int quantity;
    private double totalPrice;
}
