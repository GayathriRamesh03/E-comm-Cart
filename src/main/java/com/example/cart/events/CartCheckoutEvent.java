package com.example.cart.events;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartCheckoutEvent {

    private int customerId;
    private String customerEmail;
    private List<CartItemDto> items;
    private double totalPrice;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDto {
        private String productImageUrl;
        private int productId;
        private String productName;
        private int quantity;
        private double productPrice;
        private int sellerId;
        private double totalPrice;
    }
}
