package com.example.cart.controller;

import com.example.cart.events.CartCheckoutEvent;
import com.example.cart.events.CartCheckoutProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart/test")
@RequiredArgsConstructor
public class CartTestController {

    private final CartCheckoutProducer cartCheckoutProducer;

    @PostMapping("/checkout")
    public ResponseEntity<String> testCheckout() {
        CartCheckoutEvent.CartItemDto item = new CartCheckoutEvent.CartItemDto(
                1, // productId
                "Test Product",
                2, // quantity
                250, // productPrice
                5, // sellerId
                500 // totalPrice
        );

        CartCheckoutEvent event = new CartCheckoutEvent(
                1, // customerId
                List.of(item),
                500
        );

        cartCheckoutProducer.sendCheckoutEvent(event);
        return ResponseEntity.ok("Cart checkout event sent");
    }
}