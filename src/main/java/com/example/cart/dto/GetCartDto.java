package com.example.cart.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetCartDto {
    private int customerId;
    private String customerEmail;
    private List<CartItemDto> items;
    private double totalPrice;
}
