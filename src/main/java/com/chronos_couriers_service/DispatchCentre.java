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
    public void updateRiderStatus(){

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
    public void processNewPackages(){

    }
    public void deliveryCompletion(){

    }
    public void getStatus(){

    }
}
