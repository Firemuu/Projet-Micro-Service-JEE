package com.test.demo.supervisor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SupervisorServiceTest {

    @Test
    void shouldAssignOnlyOneOwnerPerFork() {
        SupervisorService supervisor = new SupervisorService(5);

        assertTrue(supervisor.requestFork(1, 0));
        assertFalse(supervisor.requestFork(2, 0));
        assertEquals(1, supervisor.getForkOwner(0));
    }

    @Test
    void flawSupervisorDoesNotCheckTwoForksBeforeEating() {
        SupervisorService supervisor = new SupervisorService(5);

        // Le superviseur accepte une première fourchette seule.
        assertTrue(supervisor.requestFork(1, 0));

        // Il accepte aussi une seconde fourchette, mais ne décide pas si le
        // philosophe peut manger; ici le test documente seulement ses opérations.
        assertTrue(supervisor.requestFork(1, 1));

        assertEquals(1, supervisor.getForkOwner(0));
        assertEquals(1, supervisor.getForkOwner(1));
    }

    @Test
    void flawNaiveProtocolCanCreateFiveForksHeldWithNoSecondForkAvailable() {
        SupervisorService supervisor = new SupervisorService(5);

        // Chaque philosophe prend sa fourchette « gauche » puis attend sa droite.
        for (int id = 1; id <= 5; id++) {
            int left = (id - 1 + 5) % 5;
            assertTrue(supervisor.requestFork(id, left));
        }

        // Toutes les fourchettes sont occupées par une seule, donc aucun
        // philosophe ne peut obtenir sa seconde fourchette : interblocage.
        assertEquals(0, supervisor.getAvailableForkCount());
        for (int id = 1; id <= 5; id++) {
            int right = id % 5;
            assertFalse(supervisor.isForkAvailable(right));
        }
    }

    @Test
    void flawReleaseDoesNotVerifyTheOwner() {
        SupervisorService supervisor = new SupervisorService(5);
        assertTrue(supervisor.requestFork(1, 3));

        // Le philosophe 2 n'est pas propriétaire, mais la méthode naïve accepte
        // la libération de la fourchette 3.
        assertTrue(supervisor.releaseFork(2, 3));
        assertNull(supervisor.getForkOwner(3));
    }
}
