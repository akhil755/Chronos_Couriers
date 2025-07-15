package com.chronos_couriers_service;

import com.chronos_couriers_model.LogEntry;
import com.chronos_couriers_model.Package;
import com.chronos_couriers_model.Rider;
import com.chronos_couriers_model.RiderLogEntry;
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

    public void registerRider(Rider rider) {
        riders.put(rider.getId(), rider);
        audit.recordRiderStatus(new RiderLogEntry(rider.getId(),
                null,
                Rider.Status.AVAILABLE,
                System.currentTimeMillis()));
        assignPackageToRider();
    }

    public void updateRiderStatus(String riderId, Rider.Status status) {
        Rider rider = riders.get(riderId);
        if (rider == null) throw new IllegalArgumentException("Rider not found");
        Rider.Status oldStatus = rider.getStatus();
        rider.setStatus(status);

        if (oldStatus == Rider.Status.BUSY && status == Rider.Status.OFFLINE) {
            String pkgId = findPackageAssignedToRider(riderId);
            if (pkgId != null) {
                Package pkg = packages.get(pkgId);

                if (pkg != null && pkg.getStatus() == Package.Status.ASSIGNED) {
                    if (status == Rider.Status.AVAILABLE) {
                        System.out.println("Rider " + riderId + " can not be set to available while package " + pkgId + " is still assigned");
                        System.out.println("Updated rider status : " + getRiderStatus(riderId));
                        return;
                    }
                    if (status == Rider.Status.OFFLINE) {
                        pkg.setStatus(Package.Status.PENDING);
                        pkg.setPickupTime(0);
                        queue.offer(pkg);
                        assignments.remove(pkgId);

                        audit.record(new LogEntry(pkgId,
                                riderId,
                                Package.Status.ASSIGNED,
                                Package.Status.PENDING,
                                System.currentTimeMillis()
                        ));
                        System.out.println("package " + pkgId + " returned to queue as rider status changed to offline");
                    }
                }
            }
        }

        rider.setStatus(status);
        audit.recordRiderStatus(new RiderLogEntry(riderId,
                oldStatus,
                status,
                System.currentTimeMillis()));
        System.out.println("Rider status updated: " + riderId + " → " + status);

        if(status==Rider.Status.AVAILABLE){
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
    private String findPackageAssignedToRider(String riderId){
        for (Map.Entry<String, String> entry : assignments.entrySet()){
            if(riderId.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }

    private Rider findRider(Package pkg){
        for (Rider rider : riders.values()){
            if (rider.getStatus() == Rider.Status.AVAILABLE){
                if (!pkg.isFragile() || rider.isFragileHandling()) {
                    return rider;
                }
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

    public void completeDelivery(String packageId){
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
        //packages.remove(packageId);
        assignments.remove(packageId);
        pkg.setDeliveryTime(System.currentTimeMillis());

        if(riderId != null){
            Rider rider = riders.get(riderId);
            rider.setStatus(Rider.Status.AVAILABLE);
        }
        assignPackageToRider();

    }
    public String getStatus(String packageId){
        Package pkg = packages.get(packageId);
        if(pkg==null) return "Package is not available";
        switch (pkg.getStatus()){
            case PENDING -> {
                return "package is pending";
            }
            case ASSIGNED-> {
                String riderId = assignments.get(packageId);
                return "package assigned to rider: " + (riderId !=null ? riderId : "unknown");
            }
            case DELIVERED -> {
                return "package delivered " + pkg.getDeliveryTime();
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
                return "Rider "+riderId+ " is available " +(riderId)+" " +rider.getReliabilityRating()+ " "+rider.isFragileHandling();
            }
            case BUSY -> {
                return "rider "+riderId+ " is busy";
            }
            case OFFLINE -> {
                return "Rider "+riderId+" is Offline";
            }
            default -> {
                return "unknown status";
            }
        }
    }
}
