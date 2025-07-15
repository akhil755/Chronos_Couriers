package com.chronos_couriers_model;

public record LogEntry
    (String packageId,
    String riderId,
    Package.Status from,
    Package.Status to,
    long timeStamp){

}
