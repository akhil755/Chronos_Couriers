package com.chronoscouriers.util;

import com.chronoscouriers.model.LogEntry;
import com.chronoscouriers.model.Package;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
public class AuditLoggerTest {
    @Test
    public void recordAndRetrieve() {
        AuditLogger auditLogger = AuditLogger.getInstance();
        auditLogger.reset();
        LogEntry logEntry = new LogEntry("PKG1", "RDR1", null,
                Package.Status.PENDING,
                System.currentTimeMillis());
        auditLogger.record(logEntry);
        assertEquals(1, auditLogger.getPackageHistory("PKG1").size());
    }
}