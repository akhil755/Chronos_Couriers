package com.chronos_couriers_model;

public record RiderLogEntry(String riderId,
                            Rider.Status from,
                            Rider.Status to,
                            long timestamp) {

}
