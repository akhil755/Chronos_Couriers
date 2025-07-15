package com.chronos_couriers_service;


import com.chronos_couriers_model.LogEntry;
import com.chronos_couriers_model.Package;
import com.chronos_couriers_model.Rider;
import com.chronos_couriers_util.AuditLogger;
import com.chronos_couriers_util.CheckPackagePriority;

import java.util.*;

public class DispatchCentre {
    private final Map<String, Package> packages = new LinkedHashMap<>();
    private final Map<String, Rider> riders = new LinkedHashMap<>();
    private final Map<String, String> assignments = new LinkedHashMap<>();
    private final PriorityQueue<Package> queue = new PriorityQueue<>(new CheckPackagePriority());
    private static final AuditLogger audit = AuditLogger.getInstance();

    public void placeOrder(Package pkg) {
        packages.put(pkg.getId(), pkg);
        audit.record(new LogEntry(pkg.getId(),
                null,
                null,
                Package.Status.PENDING,
                System.currentTimeMillis()
        ));
        queue.offer(pkg);
        assignPackageToRider();

    }
    public void registerRider(Rider rider){
        riders.put(rider.getId(), rider);
        assignPackageToRider();
    }
    public void updateRiderStatus(String riderId, Rider.Status status){
        Rider rider = riders.get(riderId);
        if(rider == null) throw new IllegalArgumentException("Rider not found");
        rider.setStatus(status);

        if (status==Rider.Status.AVAILABLE){
            assignPackageToRider();
        }
    }
    public void assignPackageToRider(){
        List<Package> assigned = new ArrayList<>();
        Iterator<Package> iterator =queue.iterator();

        while (iterator.hasNext()){
            Package pkg = iterator.next();
            Rider rider = findRider(pkg);
            if (rider != null){
                assign(pkg,rider);
                assigned.add(pkg);
            }else {
                noRider(pkg);
            }
        }

        for(Package pkg : assigned){
            queue.remove(pkg);
        }

    }
    private Rider findRider(Package pkg){
        for (Rider rider : riders.values()){
            if (rider.getStatus() == Rider.Status.AVAILABLE){
                return rider;
            }
        }
        return null;
    }
    private void assign(Package pkg, Rider rider){
        pkg.setStatus(Package.Status.ASSIGNED);
        pkg.setPickupTime(System.currentTimeMillis());
        rider.setStatus(Rider.Status.BUSY);
        assignments.put(pkg.getId(), rider.getId());

        audit.record(new LogEntry(pkg.getId(),
                rider.getId(),
                Package.Status.PENDING,
                Package.Status.ASSIGNED,
                System.currentTimeMillis()));
    }
    private void noRider(Package pkg){
        pkg.setStatus(Package.Status.PENDING);
        assignments.put(pkg.getId(), null);
    }

    public void deliveryCompletion(String packageId){
        Package pkg = packages.get(packageId);
        String riderId = assignments.get(packageId);
        if(pkg==null) throw new IllegalArgumentException("Package not found");

        pkg.setStatus(Package.Status.DELIVERED);
        audit.record(new LogEntry(pkg.getId(),
                riderId,
                Package.Status.ASSIGNED,
                Package.Status.DELIVERED,
                System.currentTimeMillis()
        ));
        packages.remove(packageId);
        assignments.remove(packageId);
        pkg.setDeliveryTime(System.currentTimeMillis());

        if(riderId != null){
            Rider rider = riders.get(riderId);
            if (packages.isEmpty() & assignments.isEmpty()){
            rider.setStatus(Rider.Status.AVAILABLE);
            }else {
                rider.setStatus(Rider.Status.BUSY);
            }
        }
        assignPackageToRider();

    }
    public String getStatus(String packageId){
        Package pkg = packages.get(packageId);
        if(!assignments.containsKey(packageId)) return "Package is not available";
        switch (pkg.getStatus()){
            case PENDING -> {
                return "package is pending";
            }
            case ASSIGNED-> {
                return "package assigned to rider: " + assignments.get(packageId);
            }
            case DELIVERED -> {
                return "package delivered";
            }
            default -> {
                return "Unknown Status";
            }

        }

    }
    public String getRiderStatus(String riderId){
        Rider rider = riders.get(riderId);
        if(!assignments.containsKey(riderId) & rider==null) return "Rider is not available";
        switch (rider.getStatus()){
            case AVAILABLE -> {
                return "Rider available " +(riderId)+" " +rider.getReliabilityRating();
            }
            case BUSY -> {
                return "Rider is busy";
            }
            case OFFLINE -> {
                return "Rider is Offline";
            }
            default -> {
                return "unknown status";
            }
        }
    }
}
