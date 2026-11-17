package com.test.demo.monitor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class MonitorServiceTest {

    @Test
    void shouldKeepEventsInLogicalOrder() {
        MonitorService monitor = new MonitorService();

        monitor.receive(new PhilosopherEvent(1, EventType.HUNGRY, 1L));
        monitor.receive(new PhilosopherEvent(2, EventType.EATING, 2L));
        monitor.receive(new PhilosopherEvent(1, EventType.DONE, 3L));

        List<PhilosopherEvent> history = monitor.getHistory();
        assertEquals(3, history.size());
        assertEquals(1L, history.get(0).logicalClock());
        assertEquals(2L, history.get(1).logicalClock());
        assertEquals(3L, history.get(2).logicalClock());
        assertFalse(history.isEmpty());
    }
}
