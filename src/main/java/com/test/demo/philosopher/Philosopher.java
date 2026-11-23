package com.test.demo.philosopher;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import com.test.demo.monitor.EventType;
import com.test.demo.monitor.MonitorService;
import com.test.demo.monitor.PhilosopherEvent;
import com.test.demo.supervisor.SupervisorService;

public class Philosopher implements Runnable {
    private final int id;
    private final int philosopherCount;
    private final MonitorService monitorService;
    private final SupervisorService supervisorService;
    private final Random random;
    private final AtomicLong logicalClock = new AtomicLong(0);
    private volatile PhilosopherState state = PhilosopherState.THINKING;

    private Integer heldLeftFork;
    private Integer heldRightFork;

    public Philosopher(int id, MonitorService monitorService, Random random) {
        this(id, 5, monitorService, new SupervisorService(5), random);
    }

    public Philosopher(int id, int philosopherCount, MonitorService monitorService,
                      SupervisorService supervisorService, Random random) {
        this.id = id;
        this.philosopherCount = philosopherCount;
        this.monitorService = monitorService;
        this.supervisorService = supervisorService;
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

    public Integer getHeldLeftFork() {
        return heldLeftFork;
    }

    public Integer getHeldRightFork() {
        return heldRightFork;
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

        // Interrogation puis demande séparées (check-then-act), volontairement naïves.
        int leftFork = (id - 1 + philosopherCount) % philosopherCount;
        int rightFork = id % philosopherCount;

        while (!Thread.currentThread().isInterrupted() && heldLeftFork == null) {
            if (supervisorService.isForkAvailable(leftFork)
                    && supervisorService.requestFork(id, leftFork)) {
                heldLeftFork = leftFork;
            } else {
                sleepRandom(20, 50);
            }
        }

        // Une fois la première obtenue, aucune autre vérification globale n'est faite.
        // Le philosophe garde donc sa fourchette pendant qu'il attend la seconde.
        while (!Thread.currentThread().isInterrupted() && heldRightFork == null) {
            if (supervisorService.isForkAvailable(rightFork)
                    && supervisorService.requestFork(id, rightFork)) {
                heldRightFork = rightFork;
            } else {
                sleepRandom(20, 50);
            }
        }
    }

    private void eat() {
        if (heldLeftFork == null || heldRightFork == null) {
            return;
        }

        state = PhilosopherState.EATING;
        emit(EventType.EATING);
        sleepRandom(150, 500);
        emit(EventType.DONE);

        supervisorService.releaseFork(id, heldLeftFork);
        supervisorService.releaseFork(id, heldRightFork);
        heldLeftFork = null;
        heldRightFork = null;
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
