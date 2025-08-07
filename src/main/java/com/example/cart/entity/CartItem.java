package com.example.cart.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItem {

    private String productImageUrl;
    private int sellerId;
    private int productId;
    private String productName;
    private int quantity;
    private double productPrice;
    private double totalPrice;

}
