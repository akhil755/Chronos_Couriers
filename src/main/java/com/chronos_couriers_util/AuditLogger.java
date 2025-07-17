package com.chronos_couriers_util;



import com.chronos_couriers_model.LogEntry;
import com.chronos_couriers_model.Package;
import com.chronos_couriers_model.RiderLogEntry;

import java.util.*;

public class AuditLogger {
    private static final AuditLogger INSTANCE = new AuditLogger();
    private AuditLogger(){
    }
    public static AuditLogger getInstance(){
        return INSTANCE;
    }
    private final Map<String, List<LogEntry>> byPackage = new HashMap<>();
    private final Map<String, List<LogEntry>> byRider = new HashMap<>();
    private final Map<String, List<RiderLogEntry>> riderStatusLog = new HashMap<>();

    public void record(LogEntry logEntry){
        byPackage
                .computeIfAbsent(logEntry.packageId(), id-> new ArrayList<>())
                .add(logEntry);
        if (logEntry.riderId() != null){
            byRider.computeIfAbsent(logEntry.riderId(), id-> new ArrayList<>())
                    .add(logEntry);
        }
    }
    public void recordRiderStatus(RiderLogEntry logEntry) {
        riderStatusLog.computeIfAbsent(logEntry.riderId(), id -> new ArrayList<>())
                .add(logEntry);
    }

    public List<LogEntry> getPackageHistory(String packageId){
        return  byPackage.getOrDefault(packageId, Collections.emptyList());
    }

    public List<LogEntry> getRiderHistory(String riderId){
            return byRider.getOrDefault(riderId, Collections.emptyList());
    }
    public List<RiderLogEntry> getRiderStatusHistory (String riderId){
        return riderStatusLog.getOrDefault(riderId, Collections.emptyList());
    }
}
