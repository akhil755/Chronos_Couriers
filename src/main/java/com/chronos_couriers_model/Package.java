package com.chronos_couriers_model;

public class Package {
    private final String id;
    private final Type type;
    private final long dueDate;
    private final long orderedTime;
    private Status status;
    private long pickupTime;
    private long deliveryTime;
    private final boolean fragile;

    public enum Type{EXPRESS, STANDARD}
    public enum Status{PENDING,ASSIGNED,DELIVERED}

    public Package(String id, Type type, long dueDate, long orderedTime, boolean fragile){
        this.id=id;
        this.type=type;
        this.dueDate=dueDate;
        this.orderedTime=orderedTime;
        this.fragile = fragile;
        this.status=status.PENDING;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public long getDueDate() {
        return dueDate;
    }

    public long getOrderedTime() {
        return orderedTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(long pickupTime) {
        this.pickupTime = pickupTime;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public boolean isFragile() {
        return fragile;
    }
}
