package com.test.demo.monitor;

public record PhilosopherEvent(int philosopherId, EventType type, long logicalClock) {
}
