package com.test.demo.supervisor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation naïve : elle attribue les fourchettes une par une mais ne
 * décide jamais si un philosophe peut manger. Elle a volontairement les
 * failles décrites dans les tests de ce projet.
 */
public class SupervisorService {
    private final int forkCount;
    private final Map<Integer, Integer> owners = new HashMap<>();

    public SupervisorService() {
        this(5);
    }

    public SupervisorService(int forkCount) {
        if (forkCount <= 0) {
            throw new IllegalArgumentException("forkCount must be positive");
        }
        this.forkCount = forkCount;
        for (int forkId = 0; forkId < forkCount; forkId++) {
            owners.put(forkId, null);
        }
    }

    /** Interrogation séparée de l'attribution : création volontaire d'une course. */
    public synchronized boolean isForkAvailable(int forkId) {
        checkForkId(forkId);
        return owners.get(forkId) == null;
    }

    public synchronized boolean isAvailable(int forkId) {
        return isForkAvailable(forkId);
    }

    /** Demande effective d'une fourchette. */
    public synchronized boolean requestFork(int philosopherId, int forkId) {
        checkForkId(forkId);
        if (owners.get(forkId) != null) {
            return false;
        }
        // Pas de contrôle global : le philosophe peut accumuler une seule
        // fourchette ou demander une seconde sans posséder la première.
        owners.put(forkId, philosopherId);
        return true;
    }

    /** Raccourci pratique : attribution directe par identifiant de philosophe. */
    public synchronized boolean takeFork(int philosopherId, int forkId) {
        return requestFork(philosopherId, forkId);
    }

    public synchronized boolean takeFork(int forkId) {
        return isForkAvailable(forkId);
    }

    public synchronized boolean releaseFork(int philosopherId, int forkId) {
        checkForkId(forkId);
        if (owners.get(forkId) == null) {
            return false;
        }
        // La libération est volontairement simplifiée : aucune validation globale
        // de propriété ou d'état du philosophe n'est effectuée.
        owners.put(forkId, null);
        return true;
    }

    public synchronized void releaseFork(int forkId) {
        checkForkId(forkId);
        owners.put(forkId, null);
    }

    public synchronized Integer getForkOwner(int forkId) {
        checkForkId(forkId);
        return owners.get(forkId);
    }

    public synchronized Integer ownerOf(int forkId) {
        return getForkOwner(forkId);
    }

    public synchronized List<Integer> getAvailableForkIds() {
        List<Integer> available = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : owners.entrySet()) {
            if (entry.getValue() == null) {
                available.add(entry.getKey());
            }
        }
        Collections.sort(available);
        return available;
    }

    public synchronized int getAvailableForkCount() {
        return getAvailableForkIds().size();
    }

    public int getForkCount() {
        return forkCount;
    }

    private void checkForkId(int forkId) {
        if (forkId < 0 || forkId >= forkCount) {
            throw new IllegalArgumentException("Invalid fork id: " + forkId);
        }
    }
}
