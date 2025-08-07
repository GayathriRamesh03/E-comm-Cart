package com.example.cart.service.impl;

import com.example.cart.dto.AddProdToCartDto;
import com.example.cart.dto.CartItemDto;
import com.example.cart.dto.GetCartDto;
import com.example.cart.dto.UpdateQuantityDto;
import com.example.cart.entity.Cart;
import com.example.cart.entity.CartItem;
import com.example.cart.exceptions.CartNotFoundException;
import com.example.cart.repository.CartRepository;
import com.example.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    //Todo : Handle Cart not existing condition (null), make use of Optional
    @Override
    public GetCartDto getCartByCustomerId(int customerId) {
        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);
        if (optionalCart.isEmpty()) {
            throw new CartNotFoundException("Cart not found for customerId: " + customerId);
        }
        Cart cart = optionalCart.get();
        return mapToGetCartDto(cart);
    }

    @Override
    public GetCartDto addProductToCart(AddProdToCartDto dto, String customerEmail) {
        Optional<Cart> optionalCart = cartRepository.findByCustomerId(dto.getCustomerId());

        Cart cart;
        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
        } else {
            cart = new Cart(null, dto.getCustomerId(), customerEmail, new ArrayList<>(), 0);
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId() == dto.getProductId())
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            item.setTotalPrice(item.getProductPrice() * item.getQuantity());
        } else {
            CartItem newItem = new CartItem(
                    dto.getProductImageUrl(),
                    dto.getSellerId(),
                    dto.getProductId(),
                    dto.getProductName(),
                    dto.getQuantity(),
                    dto.getProductPrice(),
                    dto.getProductPrice() * dto.getQuantity()
            );
            cart.getItems().add(newItem);
        }

        cart.setTotalPrice(calculateTotalPrice(cart.getItems()));
        cartRepository.save(cart);
        return mapToGetCartDto(cart);
    }

    @Override
    public GetCartDto updateQuantity(UpdateQuantityDto dto) {
        Optional<Cart> optionalCart = cartRepository.findByCustomerId(dto.getCustomerId());

        if (!optionalCart.isPresent()) {
            throw new CartNotFoundException("Cart not found");
        }

        Cart cart = optionalCart.get();
        for (CartItem item : cart.getItems()) {
            if (item.getProductId() == dto.getProductId()) {
                item.setQuantity(dto.getQuantity());
                item.setTotalPrice(item.getProductPrice() * dto.getQuantity());
                break;
            }
        }

        cart.setTotalPrice(calculateTotalPrice(cart.getItems()));
        cartRepository.save(cart);
        return mapToGetCartDto(cart);
    }

    @Override
    public void removeProductFromCart(int customerId, int productId) {
        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);

        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();

            cart.getItems().removeIf(item -> item.getProductId() == productId);
            cart.setTotalPrice(calculateTotalPrice(cart.getItems()));
            cartRepository.save(cart);
        } else {
            throw new CartNotFoundException("Cart not found for customerId: " + customerId);
        }
    }

    @Override
    public void clearCart(int customerId) {
        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);

        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.setItems(new ArrayList<>());
            cart.setTotalPrice(0);
            cartRepository.save(cart);
        } else {
            throw new CartNotFoundException("Cart not found");
        }
    }

    @Override
    public void checkoutCart(int customerId) {
        clearCart(customerId);
    }

    private double calculateTotalPrice(List<CartItem> items) {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    private GetCartDto mapToGetCartDto(Cart cart) {
        GetCartDto dto = new GetCartDto();
        dto.setCustomerId(cart.getCustomerId());
        dto.setCustomerEmail(cart.getCustomerEmail());

        List<CartItemDto> itemDtos = cart.getItems().stream().map(item -> new CartItemDto(
                item.getProductImageUrl(),
                item.getSellerId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductPrice(),
                item.getQuantity(),
                item.getTotalPrice()
        )).collect(Collectors.toList());

        dto.setItems(itemDtos);
        dto.setTotalPrice(cart.getTotalPrice());
        return dto;
    }

}
