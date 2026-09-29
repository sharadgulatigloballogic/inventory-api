package com.example.defectapi.order;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderControllerTest {

    @Test
    void findByIdReturnsOrderForKnownId() {
        OrderController controller = new OrderController(new OrderService());

        assertEquals(1001L, controller.findById(1001L).getId());
    }

    @Test
    void findByIdReturns404ForUnknownId() {
        OrderController controller = new OrderController(new OrderService());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> controller.findById(9999L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
