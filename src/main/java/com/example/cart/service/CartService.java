package com.example.cart.service;

import com.example.cart.dto.AddProdToCartDto;
import com.example.cart.dto.GetCartDto;
import com.example.cart.dto.UpdateQuantityDto;

public interface CartService {

    GetCartDto getCartByCustomerId(int customerId);

    GetCartDto addProductToCart(AddProdToCartDto addProdToCartDto);

    GetCartDto updateQuantity(UpdateQuantityDto updateQuantityDto);

    void removeProductFromCart(int customerId, int productId);

    void clearCart(int customerId);

    void checkoutCart(int customerId);

}
