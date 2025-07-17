package com.chronoscouriers.model;

public record RiderLogEntry(String riderId,
                            Rider.Status from,
                            Rider.Status to,
                            long timestamp) {

}
