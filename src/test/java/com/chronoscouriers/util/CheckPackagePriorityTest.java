package com.chronoscouriers.util;

import com.chronoscouriers.model.Package;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckPackagePriorityTest {

    @Test
    void testExpressPriorityOverStandard() {
        Package std = new Package("PKG1", Package.Type.STANDARD, 10000, 1000, false);
        Package exp = new Package("PKG2", Package.Type.EXPRESS, 10000, 1000, false);

        PriorityQueue<Package> pq = new PriorityQueue<>(new CheckPackagePriority());
        pq.add(std);
        pq.add(exp);

        assertEquals("PKG2", pq.poll().getId());  // EXPRESS should come first
    }

    @Test
    void testDeadlineComparison() {
        Package pkg1 = new Package("PKG1", Package.Type.EXPRESS, 5000, 1000, false);
        Package pkg2 = new Package("PKG2", Package.Type.EXPRESS, 4000, 1000, false);

        PriorityQueue<Package> pq = new PriorityQueue<>(new CheckPackagePriority());
        pq.add(pkg1);
        pq.add(pkg2);

        assertEquals("PKG2", pq.poll().getId());  // lower deadline wins
    }

    @Test
    void testOrderTimeTiebreaker() {
        Package pkg1 = new Package("PKG1", Package.Type.EXPRESS, 5000, 900, false);
        Package pkg2 = new Package("PKG2", Package.Type.EXPRESS, 5000, 1000, false);

        PriorityQueue<Package> pq = new PriorityQueue<>(new CheckPackagePriority());
        pq.add(pkg1);
        pq.add(pkg2);

        assertEquals("PKG1", pq.poll().getId());  // earlier orderTime wins
    }
}
