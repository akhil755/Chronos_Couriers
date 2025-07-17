package com.chronoscouriers.model;

public record LogEntry
    (String packageId,
    String riderId,
    Package.Status from,
    Package.Status to,
    long timeStamp){

}
