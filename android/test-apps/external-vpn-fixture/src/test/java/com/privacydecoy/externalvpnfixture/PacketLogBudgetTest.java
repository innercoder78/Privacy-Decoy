package com.privacydecoy.externalvpnfixture;

import org.junit.Test;
import static org.junit.Assert.*;

public final class PacketLogBudgetTest {
    // Independent specification of all seven exact controlled tuples.
    private static final Object[][] SIGNATURES = {
        {4, "tcp", "host-control", 46151},
        {4, "udp", "documentation-v4", 46152},
        {4, "tcp", "host-control", 46153},
        {4, "udp", "documentation-v4", 46154},
        {4, "udp", "synthetic-dns", 53},
        {6, "udp", "documentation-v6", 46152},
        {6, "udp", "documentation-v6", 46154}
    };

    private static boolean emit(PacketLogBudget budget, Object[] tuple) {
        return budget.shouldEmit((Integer) tuple[0], (String) tuple[1],
            (String) tuple[2], (Integer) tuple[3]);
    }

    @Test public void otherFloodCannotStarveControlledEvidence() {
        PacketLogBudget budget = new PacketLogBudget();
        for (int i = 0; i < 1000; i++) budget.shouldEmit(4, "other", "other", 0);
        for (Object[] tuple : SIGNATURES) assertTrue(emit(budget, tuple));
    }

    @Test public void platformDnsFloodCannotStarveControlledEvidence() {
        PacketLogBudget budget = new PacketLogBudget();
        for (int i = 0; i < 1000; i++) budget.shouldEmit(4, "udp", "platform-dns", 53);
        for (Object[] tuple : SIGNATURES) assertTrue(emit(budget, tuple));
    }

    @Test public void backgroundStopsAtExactly64AcrossCategories() {
        PacketLogBudget budget = new PacketLogBudget();
        for (int i = 0; i < 64; i++) assertTrue(budget.shouldEmit(4, "other", "other", 0));
        assertFalse(budget.shouldEmit(4, "udp", "platform-dns", 53));
        assertFalse(budget.shouldEmit(6, "other", "other", 0));
    }

    @Test public void eachSignatureHasExactlyEightIndependentSlots() {
        PacketLogBudget budget = new PacketLogBudget();
        for (Object[] tuple : SIGNATURES) {
            for (int i = 0; i < 8; i++) assertTrue(emit(budget, tuple));
            assertFalse(emit(budget, tuple));
        }
    }

    @Test public void wrongTupleComponentsUseOnlyBackground() {
        for (Object[] tuple : SIGNATURES) {
            Object[][] variants = {tuple.clone(), tuple.clone(), tuple.clone(), tuple.clone()};
            variants[0][0] = 5;
            variants[1][1] = "other";
            variants[2][2] = "other";
            variants[3][3] = 0;
            for (Object[] wrong : variants) {
                PacketLogBudget budget = new PacketLogBudget();
                for (int i = 0; i < 64; i++) assertTrue(emit(budget, wrong));
                assertFalse(emit(budget, wrong));
                for (Object[] exact : SIGNATURES) assertTrue(emit(budget, exact));
            }
        }
    }

    @Test public void knownButNonControlledCombinationsAreBackground() {
        PacketLogBudget budget = new PacketLogBudget();
        for (int i = 0; i < 64; i++) assertTrue(budget.shouldEmit(4, "udp", "host-control", 46152));
        assertFalse(budget.shouldEmit(4, "tcp", "documentation-v4", 46154));
        assertFalse(budget.shouldEmit(6, "udp", "synthetic-dns", 53));
        for (Object[] tuple : SIGNATURES) assertTrue(emit(budget, tuple));
    }

    @Test public void totalNeverExceeds120() {
        PacketLogBudget budget = new PacketLogBudget();
        int emitted = 0;
        for (int i = 0; i < 1000; i++) {
            if (budget.shouldEmit(4, "other", "other", 0)) emitted++;
            if (budget.shouldEmit(4, "udp", "platform-dns", 53)) emitted++;
            for (Object[] tuple : SIGNATURES) if (emit(budget, tuple)) emitted++;
        }
        assertEquals(120, emitted);
    }

    @Test public void newEstablishmentGetsFreshBudget() {
        PacketLogBudget old = new PacketLogBudget();
        for (int i = 0; i < 8; i++) assertTrue(emit(old, SIGNATURES[0]));
        assertFalse(emit(old, SIGNATURES[0]));
        assertTrue(emit(new PacketLogBudget(), SIGNATURES[0]));
    }

    @Test public void apiRequiresOnlySanitizedScalars() throws Exception {
        assertEquals(boolean.class, PacketLogBudget.class.getDeclaredMethod("shouldEmit",
            int.class, String.class, String.class, int.class).getReturnType());
    }
}
