package com.chronos_couriers_util;



import com.chronos_couriers_model.LogEntry;
import com.chronos_couriers_model.Package;

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

    public void record(LogEntry logEntry){
        byPackage
                .computeIfAbsent(logEntry.packageId(), id-> new ArrayList<>())
                .add(logEntry);
        if (logEntry.riderId() != null){
            byRider.computeIfAbsent(logEntry.riderId(), id-> new ArrayList<>())
                    .add(logEntry);
        }
    }

    public List<LogEntry> getPackageHistory(String packageId){
        return  byPackage.getOrDefault(packageId, Collections.emptyList());
    }

    public List<LogEntry> getRiderHistory(String riderId){
        return byRider.getOrDefault(riderId, Collections.emptyList());
    }


    public List<String> getMissedExpressPackages(long expressPackages){
        List<String> missed = new ArrayList<>();
        byPackage.values().forEach(list-> {LogEntry last = list.get(list.size()-1);
        if (last.to() == Package.Status.DELIVERED){
            long deadLine = list.get(0).timeStamp();
        }
        });
        return missed;
    }

    public void recordRiderStatus(LogEntry logEntry) {
            byRider.computeIfAbsent(logEntry.riderId(), id -> new ArrayList<>())
                    .add(logEntry);
        }

        public List<LogEntry> getRiderStatusHistory (String riderId){
            return byRider.getOrDefault(riderId, Collections.emptyList());
        }
}
