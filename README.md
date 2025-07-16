# Chronos Couriers Dispatch System

🚀 **Overview**

Chronos Couriers is an intelligent, in-memory, single-threaded package dispatching system built using Java. The system simulates real-time rider and package interactions, automating delivery assignments based on package priority, deadlines, rider availability, and fragile handling capabilities.

📦 Features Implemented

# Core Functionalities

1.Add new delivery orders (EXPRESS or STANDARD)

2.Rider registration with reliability and fragile handling

3.Dynamic package assignment based on:

4.Priority: EXPRESS > STANDARD

5.Deadline: Sooner first

6.Order time: Earlier first

7.Package reassignment when riders go OFFLINE

8.Fragile package support (assigned only to capable riders)

9.Package lifecycle tracking: PENDING, ASSIGNED, DELIVERED

10.Audit logs for both package and rider status transitions

11.Real-time reassignment logic on rider status change

12.Delivery history query for rider (last 24 hours)

13.Missed deadline reporting for EXPRESS packages

# Design Decisions

**Architecture**

**Clean package structure:**

model – Package, Rider, LogEntry, RiderLogEntry classes

service – DispatchCentre (core logic)

util – AuditLogger, priority comparator

**Priority Logic**

PriorityQueue with custom comparator:

EXPRESS before STANDARD

Earlier deadline first

Earlier order time as tiebreaker

Rider & Package States

Rider states: AVAILABLE, BUSY, OFFLINE

Package states: PENDING, ASSIGNED, DELIVERED

Rider reassignment happens automatically when status changes

**Logging & Audit**

All transitions logged using AuditLogger

Rider log entries and status logs are separated for clarity

Queries support last 24h deliveries, missed deadlines

**Constraints Met**

1) In-memory only (no DB or filesystem)

2) Single-threaded (no concurrency code)

3) No frameworks (plain Java, CLI-based)

4) No external APIs

5) Time tracking via System.currentTimeMillis()

# How to Run Tests
**mvn test** :
All unit tests will run. Coverage includes:

Package placement and delivery

Rider availability and fragile handling

Missed express delivery reporting

Rider delivery history (last 24h)

# How to Build & Run

**Prerequisites:**
Java 17+

**Build:**
mvn clean install

**Run:**
java -jar target/Chronos_Couriers-1.0-SNAPSHOT.jar

you will see "Chronos Couriers application is started with CLI enabled"

# Sample Commands

**1. Register a rider with fragile handling capability :**
registerrider R1 4.5 true

**2. Place an EXPRESS package that is NOT fragile :**
placeorder P1 EXPRESS 1899999999999 false

 **3. Set rider status to AVAILABLE to trigger assignment :**
updateriderstatus R1 AVAILABLE

**4. Check the package status :**
status P1

**5. Complete the delivery :**
completedelivery P1

**6. View the full package transition history :**
packagehistory P1

**7. View all delivery events performed by the rider :**
riderhistory R1

**8. View rider's availability transitions :**
riderstatushistory R1

**9. Check if the delivery missed its deadline :**
missedexpress

**10. View rider's deliveries in the last 24 hours :**
riderdeliveries R1

**11. Check the current rider status :**
riderstatus R1
