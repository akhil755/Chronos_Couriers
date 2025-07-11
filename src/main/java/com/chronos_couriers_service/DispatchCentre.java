package com.chronos_couriers_service;

import com.chronos_couriers_model.Package;
import com.chronos_couriers_model.Rider;
import com.chronos_couriers_util.CheckPackagePriority;

import java.util.*;

public class DispatchCentre {
    private final Map<String, Package> packages = new LinkedHashMap<>();
    private final Map<String, Rider> riders = new LinkedHashMap<>();
    private final Map<String, String> assignments = new LinkedHashMap<>();
    private final PriorityQueue<Package> queue = new PriorityQueue<>(new CheckPackagePriority());

    public void placeOrder(Package pkg) {
        packages.put(pkg.getId(), pkg);
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

    }

    public void deliveryCompletion(String packageId){
        Package pkg = packages.get(packageId);
        if(pkg==null) throw new IllegalArgumentException("Package not found");

        pkg.setStatus(Package.Status.DELIVERED);
        pkg.setDeliveryTime(System.currentTimeMillis());

        String riderId = assignments.get(packageId);
        if(riderId != null){
            Rider rider = riders.get(riderId);
            rider.setStatus(Rider.Status.AVAILABLE);
        }
        assignPackageToRider();
    }
    public String getStatus(String packageId){
        Package pkg = packages.get(packageId);
        if(!assignments.containsKey(packageId)) return "Package not assigned";
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
}
