package com.example.cart.controller;

import com.example.cart.dto.AddProdToCartDto;
import com.example.cart.dto.GetCartDto;
import com.example.cart.dto.UpdateQuantityDto;
import com.example.cart.events.CartCheckoutEvent;
import com.example.cart.events.CartCheckoutProducer;
import com.example.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartCheckoutProducer cartCheckoutProducer;


    @GetMapping("/{customerId}")
    public GetCartDto getCart(@PathVariable int customerId) {
        return cartService.getCartByCustomerId(customerId);
    }

    @PostMapping("/add")
    public boolean addProductToCart(@RequestBody AddProdToCartDto dto,@RequestHeader("X-Customer-Email")String customerEmail) {
        cartService.addProductToCart(dto,customerEmail);
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
        GetCartDto cart = cartService.getCartByCustomerId(customerId);
        CartCheckoutEvent event = convertToCheckoutEvent(cart);
        cartCheckoutProducer.sendCheckoutEvent(event);
        cartService.clearCart(customerId);
    }

    private CartCheckoutEvent convertToCheckoutEvent(GetCartDto cart) {
        return new CartCheckoutEvent(
                cart.getCustomerId(),
                cart.getCustomerEmail(),
                cart.getItems().stream().map(p -> new CartCheckoutEvent.CartItemDto(
                        p.getProductImageUrl(),
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
