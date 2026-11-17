package com.test.demo.monitor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MonitorService {
    private final List<PhilosopherEvent> history = new CopyOnWriteArrayList<>();

    public void receive(PhilosopherEvent event) {
        history.add(event);
    }

    public List<PhilosopherEvent> getHistory() {
        return history.stream()
            .sorted(Comparator.comparingLong(PhilosopherEvent::logicalClock))
            .toList();
    }

    public List<PhilosopherEvent> getHistory(int philosopherId) {
        return history.stream()
            .filter(event -> event.philosopherId() == philosopherId)
            .sorted(Comparator.comparingLong(PhilosopherEvent::logicalClock))
            .toList();
    }

    public List<String> getDashboardRows() {
        List<String> rows = new ArrayList<>();
        for (PhilosopherEvent event : getHistory()) {
            rows.add("Philosophe " + event.philosopherId() + " -> " + event.type() + " @ horloge " + event.logicalClock());
        }
        return rows;
    }
}
