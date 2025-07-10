package com.chronos_couriers_model;

public class Rider {
    public enum Status{AVAILABLE, BUSY, OFFLINE}
    private final String id;
    private Status status;
    private double reliabilityRating;
    private boolean fragileHandling;

    public Rider(String id,double reliabilityRating, boolean fragileHandling){
        this.id= id;
        this.reliabilityRating=reliabilityRating;
        this.fragileHandling=fragileHandling;
        this.status=Status.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getReliabilityRating() {
        return reliabilityRating;
    }

    public void setReliabilityRating(double reliabilityRating) {
        this.reliabilityRating = reliabilityRating;
    }

    public boolean isFragileHandling() {
        return fragileHandling;
    }

    public void setFragileHandling(boolean fragileHandling) {
        this.fragileHandling = fragileHandling;
    }
}
