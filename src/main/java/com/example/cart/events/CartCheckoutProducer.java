package com.example.cart.events;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartCheckoutProducer {

    @Autowired
    private KafkaTemplate<Integer, CartCheckoutEvent> kafkaTemplate;

    private static final String TOPIC = "cart-checkout";

    public void sendCheckoutEvent(CartCheckoutEvent event) {
        kafkaTemplate.send(TOPIC, event.getCustomerId(), event);
    }
}
