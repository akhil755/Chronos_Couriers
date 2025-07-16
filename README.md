# Chronos Couriers Dispatch System

## Overview

Chronos Couriers is an intelligent, in-memory, single-threaded package dispatching system built using Java. The system simulates real-time rider and package interactions, automating delivery assignments based on package priority, deadlines, rider availability, and fragile handling capabilities.

---

## Features & Use Cases Implemented

### Core Functionalities

1. Add new delivery orders (**EXPRESS** or **STANDARD**)
2. Rider registration with reliability and fragile handling
3. Dynamic package assignment based on:
   - Priority: **EXPRESS > STANDARD**
   - Deadline: Sooner first
   - Order time: Earlier first
4. Package reassignment when riders go OFFLINE
5. Fragile package support (assigned only to capable riders)
6. Package lifecycle tracking: **PENDING → ASSIGNED → DELIVERED**
7. Audit logs for both package and rider status transitions
8. Real-time reassignment logic on rider status change
9. Delivery history query for rider (last 24 hours)
10. Missed deadline reporting for EXPRESS packages

---

## Design Decisions & Architecture

### Clean Package Structure

- `model` – `Package`, `Rider`, `LogEntry`, `RiderLogEntry`
- `service` – `DispatchCentre` (core business logic)
- `util` – `AuditLogger`, `CheckPackagePriority`

### Priority Logic

Implemented using a custom `PriorityQueue` comparator:

- EXPRESS packages come before STANDARD
- Earlier deadlines have higher priority
- Earlier order time acts as a final tiebreaker

### Rider & Package States

- Rider States: `AVAILABLE`, `BUSY`, `OFFLINE`
- Package States: `PENDING`, `ASSIGNED`, `DELIVERED`
- Reassignment happens automatically on rider status change

### Rider Assignment Logic
   **A Rider must be:**

- AVAILABLE 

- Able to handle fragile items if the package is fragile

- First matching eligible rider is assigned

### Rider Status Transitions
-Riders can transition between AVAILABLE, BUSY, and OFFLINE
- If a rider goes OFFLINE while delivering, the package is:
- - Returned to queue
- - Logged in the audit system

### Fragile Handling
- Fragile packages can only be assigned to riders with fragileHandling = true
- If no such rider is available, package stays in PENDING

### Logging & Auditing

- All transitions are recorded using `AuditLogger`
- Separate logs for:
  - Package status transitions
  - Rider status transitions
- Supports queries for:
  - Missed EXPRESS deliveries
  - Rider deliveries in last 24 hours

### Missed Deadline Reporting
- EXPRESS packages are tracked for deadline violations
- If deliveryTime > deadline, the package is listed as “missed delivery”

### 24-Hour Rider Delivery History
- Based on audit logs, all deliveries completed by a rider in the last 24 hours are retrievable using timestamps

---

## Constraints Met

- Fully in-memory (no database or file persistence)
- Single-threaded (no concurrency code)
- Plain Java (no frameworks)
- No external APIs or libraries
- Time tracking via `System.currentTimeMillis()`

---

## How to Run Tests

### Run all unit tests:

```
mvn test
```

### Coverage includes:

- Package placement and delivery
- Rider availability and fragile handling
- Reassignment and delivery flow
- Missed EXPRESS delivery reporting
- Rider delivery history (last 24h)

---

## How to Build & Run

### Prerequisites:

- Java 17+
- Maven 3.8+

### Build the project:

```bash
mvn clean install
```

### Run the application:

```bash
java -jar target/Chronos_Couriers-1.0-SNAPSHOT.jar
```

You will see:

```
Chronos Couriers application is started with CLI enabled
```

---

## Sample CLI Commands

### Rider & Package Management

```
registerrider R1 4.5 true
placeorder P1 EXPRESS 1899999999999 false
updateriderstatus R1 AVAILABLE
```

### Status & History

```
status P1
completedelivery P1
packagehistory P1
riderhistory R1
riderstatushistory R1
riderstatus R1
```

### Reports

```
missedexpress
riderdeliveries R1
```

### Exit

```
exit
```


