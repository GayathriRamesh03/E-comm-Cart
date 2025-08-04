package com.example.cart.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddProdToCartDto {
    private int customerId;
    private int productId;
    private String productName;
    private int quantity;
    private double productPrice;
    private int sellerId;
}
