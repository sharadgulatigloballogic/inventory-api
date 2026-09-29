package com.example.defectapi.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderControllerTest {

    @Test
    void findByIdReturnsOrderForKnownId() {
        OrderController controller = new OrderController(new OrderService());

        assertEquals(1001L, controller.findById(1001L).getId());
    }

    @Test
    void findByIdThrowsNotFoundForUnknownId() {
        OrderController controller = new OrderController(new OrderService());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
                () -> controller.findById(9999L));

        assertEquals("Order not found: 9999", exception.getMessage());
    }
}
