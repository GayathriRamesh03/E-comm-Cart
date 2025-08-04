package com.example.cart.dto;

import com.example.cart.entity.CartItem;
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
    private List<CartItem> items;
    private double totalPrice;
}
