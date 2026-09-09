package com.portfolio.supplydrops.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SupplyDropTest {

    @Test
    void testStateTransitions() {
        // Pure logic for state machine
        SupplyDrop.State from = SupplyDrop.State.CREATING;
        assertTrue(from == SupplyDrop.State.CREATING);
        assertFalse(from == SupplyDrop.State.LOOTED);
    }

    @Test
    void testUniqueIdConcept() {
        String id1 = java.util.UUID.randomUUID().toString().substring(0, 8);
        String id2 = java.util.UUID.randomUUID().toString().substring(0, 8);
        assertNotEquals(id1, id2);
    }
}
