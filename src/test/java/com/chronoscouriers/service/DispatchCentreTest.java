package com.chronoscouriers.service;

import com.chronoscouriers.model.Package;
import com.chronoscouriers.model.Rider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DispatchCentreTest {

    private DispatchCentre dispatch;

    @BeforeEach
    void setUp() {
        dispatch = new DispatchCentre();
    }

    @Test
    void testPlaceOrderAndAssign() {
        Package pkg = new Package("PKG1", Package.Type.EXPRESS, System.currentTimeMillis() + 10000, System.currentTimeMillis(), false);
        Rider rider = new Rider("R1", 4.5, true);

        dispatch.registerRider(rider);
        dispatch.updateRiderStatus("R1", Rider.Status.AVAILABLE);
        dispatch.placeOrder(pkg);

        String status = dispatch.getStatus("PKG1");
        assertTrue(status.contains("assigned to rider: R1"));
    }

    @Test
    void testFragilePackageAssignedToCorrectRider() {
        Package fragilePkg = new Package("PKG2", Package.Type.STANDARD, System.currentTimeMillis() + 10000, System.currentTimeMillis(), true);
        Rider capableRider = new Rider("R2", 4.9, true);
        Rider notCapableRider = new Rider("R3", 4.5, false);

        dispatch.registerRider(notCapableRider);
        dispatch.updateRiderStatus("R3", Rider.Status.AVAILABLE);

        dispatch.registerRider(capableRider);
        dispatch.updateRiderStatus("R2", Rider.Status.AVAILABLE);

        dispatch.placeOrder(fragilePkg);

        String status = dispatch.getStatus("PKG2");
        assertTrue(status.contains("R2"));
    }

    @Test
    void testOfflineRiderReturnsPackageToQueue() {
        Package pkg = new Package("PKG3", Package.Type.STANDARD, System.currentTimeMillis() + 10000, System.currentTimeMillis(), false);
        Rider rider = new Rider("R4", 4.0, true);

        dispatch.registerRider(rider);
        dispatch.updateRiderStatus("R4", Rider.Status.AVAILABLE);
        dispatch.placeOrder(pkg);

        dispatch.updateRiderStatus("R4", Rider.Status.OFFLINE);
        String status = dispatch.getStatus("PKG3");

        assertEquals("package is pending", status);
    }

    @Test
    void testDeliveryCompletesAndRiderBecomesAvailable() {
        Package pkg = new Package("PKG4", Package.Type.EXPRESS, System.currentTimeMillis() + 10000, System.currentTimeMillis(), false);
        Rider rider = new Rider("R5", 4.8, true);

        dispatch.registerRider(rider);
        dispatch.updateRiderStatus("R5", Rider.Status.AVAILABLE);
        dispatch.placeOrder(pkg);
        dispatch.completeDelivery("PKG4");

        String status = dispatch.getStatus("PKG4");
        assertTrue(status.contains("delivered"));
        assertEquals("Rider R5 is available R5 4.8 true", dispatch.getRiderStatus("R5"));
    }

    @Test
    void testMissedExpressDeliveries() throws InterruptedException {
        long pastDeadline = System.currentTimeMillis() - 5000;
        Package pkg = new Package("PKG5", Package.Type.EXPRESS, pastDeadline, System.currentTimeMillis(), false);
        Rider rider = new Rider("R6", 4.0, true);

        dispatch.registerRider(rider);
        dispatch.updateRiderStatus("R6", Rider.Status.AVAILABLE);
        dispatch.placeOrder(pkg);

        Thread.sleep(10); // simulate delay
        dispatch.completeDelivery("PKG5");

        List<String> missed = dispatch.getMissedExpressDeliveries();
        assertTrue(missed.stream().anyMatch(s -> s.contains("PKG5")));
    }

    @Test
    void testDeliveriesByRiderInLast24Hours() {
        Package pkg = new Package("PKG6", Package.Type.STANDARD, System.currentTimeMillis() + 100000, System.currentTimeMillis(), false);
        Rider rider = new Rider("R7", 4.6, true);

        dispatch.registerRider(rider);
        dispatch.updateRiderStatus("R7", Rider.Status.AVAILABLE);
        dispatch.placeOrder(pkg);
        dispatch.completeDelivery("PKG6");

        List<String> history = dispatch.getDeliveriesByRiderInLast24Hours("R7");
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("PKG6"));
    }
    @Test
    void testGetStatusInvalidPackage() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dispatch.getStatus("non existent");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("package not found"));
    }

    @Test
    void testGetRiderStatusInvalidRider() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> dispatch.getRiderStatus("non existent"));
        assertTrue(exception.getMessage().contains("Rider not found"));
    }
    @Test
    void testGetRiderStatusThrowsForInvalidId() {
        DispatchCentre dispatch = new DispatchCentre();
        Exception ex = assertThrows(IllegalArgumentException.class, () ->{ dispatch.getRiderStatus("ghost123");});
        assertTrue(ex.getMessage().contains("Rider not found"));
    }

}
