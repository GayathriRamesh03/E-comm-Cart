package com.example.cart.service.impl;

import com.example.cart.dto.AddProdToCartDto;
import com.example.cart.dto.GetCartDto;
import com.example.cart.dto.UpdateQuantityDto;
import com.example.cart.entity.Cart;
import com.example.cart.entity.CartItem;
import com.example.cart.exceptions.CartNotFoundException;
import com.example.cart.repository.CartRepository;
import com.example.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

//    @Override
//    public GetCartDto getCartByCustomerId(int customerId) {
//        Cart cart = cartRepository.findByCustomerId(customerId).orElseGet(() -> new Cart(null, customerId, new ArrayList<>(), 0));
//        return mapToGetCartDto(cart);
//    }

    @Override
    public GetCartDto getCartByCustomerId(int customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for customerId: " + customerId));

        return mapToGetCartDto(cart);
    }

    @Override
    public GetCartDto addProductToCart(AddProdToCartDto dto) {
        Cart cart = cartRepository.findByCustomerId(dto.getCustomerId()).orElseGet(() ->
                new Cart(null, dto.getCustomerId(), dto.getCustomerEmail(), new ArrayList<>(), 0)
        );

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId() == dto.getProductId())
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            item.setTotalPrice(item.getProductPrice() * item.getQuantity());
        } else {
            CartItem newItem = new CartItem(
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
        Cart cart = cartRepository.findByCustomerId(dto.getCustomerId()).orElseThrow(() ->
                new RuntimeException("Cart not found")
        );

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
        Cart cart = cartRepository.findByCustomerId(customerId).orElseThrow(() ->
                new RuntimeException("Cart not found")
        );

        cart.getItems().removeIf(item -> item.getProductId() == productId);
        cart.setTotalPrice(calculateTotalPrice(cart.getItems()));
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(int customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId).orElseThrow(() ->
                new RuntimeException("Cart not found")
        );
        cart.setItems(new ArrayList<>());
        cart.setTotalPrice(0);
        cartRepository.save(cart);
    }

    @Override
    public void checkoutCart(int customerId) {
        // Placeholder for Kafka publish logic, payment service, etc.
        clearCart(customerId); // After order is placed
    }

    private double calculateTotalPrice(List<CartItem> items) {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    private GetCartDto mapToGetCartDto(Cart cart) {
        GetCartDto dto = new GetCartDto();
        dto.setCustomerId(cart.getCustomerId());
        dto.setCustomerEmail(cart.getCustomerEmail());
        dto.setItems(cart.getItems());
        dto.setTotalPrice(cart.getTotalPrice());
        return dto;
    }
}
