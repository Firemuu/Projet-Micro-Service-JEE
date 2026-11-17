package com.test.demo.philosopher;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import com.test.demo.monitor.EventType;
import com.test.demo.monitor.MonitorService;
import com.test.demo.monitor.PhilosopherEvent;

public class Philosopher implements Runnable {
    private final int id;
    private final MonitorService monitorService;
    private final Random random;
    private final AtomicLong logicalClock = new AtomicLong(0);
    private volatile PhilosopherState state = PhilosopherState.THINKING;

    public Philosopher(int id, MonitorService monitorService, Random random) {
        this.id = id;
        this.monitorService = monitorService;
        this.random = random;
    }

    public int getId() {
        return id;
    }

    public PhilosopherState getState() {
        return state;
    }

    public long getLogicalClock() {
        return logicalClock.get();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            think();
            hungry();
            eat();
        }
    }

    private void think() {
        state = PhilosopherState.THINKING;
        sleepRandom(200, 600);
    }

    private void hungry() {
        state = PhilosopherState.HUNGRY;
        emit(EventType.HUNGRY);
    }

    private void eat() {
        state = PhilosopherState.EATING;
        emit(EventType.EATING);
        sleepRandom(150, 500);
        emit(EventType.DONE);
        state = PhilosopherState.THINKING;
    }

    private void emit(EventType type) {
        long nextClock = logicalClock.incrementAndGet();
        monitorService.receive(new PhilosopherEvent(id, type, nextClock));
    }

    private void sleepRandom(int minMs, int maxMs) {
        try {
            Thread.sleep(random.nextInt(minMs, maxMs + 1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
