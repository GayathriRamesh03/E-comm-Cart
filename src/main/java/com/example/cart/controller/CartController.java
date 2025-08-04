package com.example.cart.controller;

import com.example.cart.dto.AddProdToCartDto;
import com.example.cart.dto.GetCartDto;
import com.example.cart.dto.UpdateQuantityDto;
import com.example.cart.events.CartCheckoutEvent;
import com.example.cart.events.CartCheckoutProducer;
import com.example.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartCheckoutProducer kafkaProducerService;


    @GetMapping("/{customerId}")
    public GetCartDto getCart(@PathVariable int customerId) {
        return cartService.getCartByCustomerId(customerId);
    }

    @PostMapping("/add")
    public boolean addProductToCart(@RequestBody AddProdToCartDto dto) {
        cartService.addProductToCart(dto);
        return true;
    }

    @PutMapping("/update")
    public GetCartDto updateQuantity(@RequestBody UpdateQuantityDto dto) {
        return cartService.updateQuantity(dto);
    }

    @DeleteMapping("/{customerId}/remove/{productId}")
    public void removeProductFromCart(@PathVariable int customerId, @PathVariable int productId) {
        cartService.removeProductFromCart(customerId, productId);
    }


    @PostMapping("/{customerId}/checkout")
    public void checkout(@PathVariable int customerId) {
        GetCartDto cart = cartService.getCartByCustomerId(customerId); // 1. Get cart
        CartCheckoutEvent event = convertToCheckoutEvent(cart); // 2. Convert to event
        kafkaProducerService.sendCheckoutEvent(event); // 3. Send to Kafka
        cartService.clearCart(customerId); // 4. Clear cart
    }

    private CartCheckoutEvent convertToCheckoutEvent(GetCartDto cart) {
        return new CartCheckoutEvent(
                cart.getCustomerId(),
                cart.getCustomerEmail(),
                cart.getItems().stream().map(p -> new CartCheckoutEvent.CartItemDto(
                        p.getProductId(),
                        p.getProductName(),
                        p.getQuantity(),
                        p.getProductPrice(),
                        p.getSellerId(),
                        p.getTotalPrice()
                )).toList(),
                cart.getTotalPrice()
        );
    }


    @DeleteMapping("/{customerId}/clear")
    public void clearCart(@PathVariable int customerId) {
        cartService.clearCart(customerId);
    }
}
