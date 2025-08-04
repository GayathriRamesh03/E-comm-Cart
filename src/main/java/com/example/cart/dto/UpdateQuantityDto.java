package com.example.cart.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateQuantityDto {
    private int customerId;
    private int productId;
    private int quantity;
}
