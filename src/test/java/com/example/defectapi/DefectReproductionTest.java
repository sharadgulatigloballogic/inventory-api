package com.example.defectapi;

import com.example.defectapi.inventory.InventoryService;
import com.example.defectapi.order.CreateOrderRequest;
import com.example.defectapi.order.Order;
import com.example.defectapi.order.OrderService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefectReproductionTest {

    @Test
    void lookupScenarioShouldRemainStable() {
        // DEF-101 reproduction; see logs/DEF-101-missing-order.log for expected response semantics.
        OrderService service = new OrderService();

        assertDoesNotThrow(() -> service.findById(9999L));
    }

    @Test
    void createScenarioShouldRemainStable() {
        // DEF-102 reproduction; the omitted input and expected value are defined in the attachment.
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(42L);
        request.setCustomerName("Ava Patel");
        request.setEmail("ava.patel@example.com");
        request.setProductCode("BK-100");
        request.setProductName("Spring in Action");
        request.setQuantity(2);
        request.setUnitPrice(new BigDecimal("49.99"));
        request.setShippingAddress("100 King Street, Toronto, ON");

        assertEquals(new BigDecimal("99.98"), new OrderService().create(request).getUnitPrice());
    }

    @Test
    void createWithOmittedDiscountPercentDefaultsToZero() {
        // VDLC-2345: omitted discountPercent must default to 0 (no discount) instead of throwing.
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(42L);
        request.setCustomerName("test");
        request.setEmail("test@example.com");
        request.setProductCode("BK-100");
        request.setProductName("Spring in Action");
        request.setQuantity(2);
        request.setUnitPrice(new BigDecimal("49.99"));
        request.setShippingAddress("100 Acme Street, Toronto, ON");
        request.setStatus("NEW");

        Order order = assertDoesNotThrow(() -> new OrderService().create(request));

        assertEquals(new BigDecimal("99.98"), order.getUnitPrice());
        assertEquals(BigDecimal.ZERO, order.getDiscountPercent());
    }

    @Test
    void createWithSuppliedDiscountPercentAppliesDiscount() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(42L);
        request.setCustomerName("test");
        request.setEmail("test@example.com");
        request.setProductCode("BK-100");
        request.setProductName("Spring in Action");
        request.setQuantity(2);
        request.setUnitPrice(new BigDecimal("49.99"));
        request.setDiscountPercent(new BigDecimal("10.00"));
        request.setShippingAddress("100 Acme Street, Toronto, ON");
        request.setStatus("NEW");

        Order order = new OrderService().create(request);

        assertEquals(new BigDecimal("89.98"), order.getUnitPrice());
        assertEquals(new BigDecimal("10.00"), order.getDiscountPercent());
    }

    @Test
    void reservationScenarioShouldRemainStable() {
        // DEF-103 reproduction; see logs/DEF-103-inventory-comparison.log for the boundary contract.
        InventoryService service = new InventoryService();

        assertEquals(23, service.reserve("BK-100", 2).getAvailableQuantity());
    }
}
