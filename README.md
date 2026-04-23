# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W — Client-Server Architectures  
**Student:** Don Thenuga Dinayana Weerasinghe  
**Student ID:** 20233120 / w2153542
**University:** University of Westminster / IIT  
**Technology Stack:** Java 11, JAX-RS (Jersey 2.41), Apache Tomcat 9, Jackson JSON

---

## API Overview

A RESTful web service for the University's Smart Campus initiative, managing campus Rooms and IoT Sensors with full historical reading logs. Built with Java JAX-RS (Jersey 2.41) deployed on Apache Tomcat 9. All data is stored in-memory using `ConcurrentHashMap` — no database.

**Base URL:** `http://localhost:8080/api/v1/`

### Endpoint Summary

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/` | API discovery + HATEOAS links |
| GET | `/api/v1/rooms` | List all rooms |
| POST | `/api/v1/rooms` | Create a new room |
| GET | `/api/v1/rooms/{id}` | Get one room |
| DELETE | `/api/v1/rooms/{id}` | Delete room (blocked if sensors present) |
| GET | `/api/v1/sensors` | List all sensors (optional `?type=` filter) |
| POST | `/api/v1/sensors` | Register sensor (validates roomId) |
| GET | `/api/v1/sensors/{id}` | Get one sensor |
| GET | `/api/v1/sensors/{id}/readings` | Get reading history |
| POST | `/api/v1/sensors/{id}/readings` | Record new reading |

---

## Project Structure

```
SmartCampusAPI/
├── pom.xml
└── src/main/
    ├── java/com/smartcampus/
    │   ├── SmartCampusApplication.java
    │   ├── model/       (Room, Sensor, SensorReading)
    │   ├── store/       (DataStore)
    │   ├── resource/    (DiscoveryResource, RoomResource, SensorResource, SensorReadingResource)
    │   ├── exception/   (3 custom exceptions + 4 mappers)
    │   └── filter/      (LoggingFilter)
    └── webapp/WEB-INF/
        └── web.xml
```

---

## How to Build and Run

### Prerequisites
- Java JDK 11+
- Apache Maven 3.6+
- Apache Tomcat 9.x

### Option A — Run in NetBeans (Recommended)
1. Open project in NetBeans
2. Register Tomcat 9 in Services panel
3. Set project Run → Context Path to `/`
4. Right-click project → Run
5. API available at: `http://localhost:8080/api/v1/`

### Option B — Build WAR and Deploy Manually
```bash
# Build the WAR file
mvn clean package

# Copy WAR to Tomcat's webapps folder
cp target/SmartCampusAPI-1.0.war /path/to/tomcat9/webapps/ROOT.war

# Start Tomcat
/path/to/tomcat9/bin/startup.sh    # Mac/Linux
/path/to/tomcat9/bin/startup.bat   # Windows

# API runs at: http://localhost:8080/api/v1/
```

### Pre-Loaded Data
The application starts with:
- 4 rooms: LIB-301, LAB-101, HALL-A, CAFE-1
- 5 sensors: TEMP-001 (Active), CO2-001 (Active), OCC-001 (Maintenance), TEMP-002 (Active), CO2-002 (Offline)

---

## Sample curl Commands

```bash
# 1. API Discovery
curl -X GET http://localhost:8080/api/v1/

# 2. List all rooms
curl -X GET http://localhost:8080/api/v1/rooms

# 3. Create a room
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"SCI-101","name":"Science Lab","capacity":25}'

# 4. Filter sensors by type
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"

# 5. Register new sensor (with valid roomId)
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"LIGHT-001","type":"Light","status":"ACTIVE","currentValue":500.0,"roomId":"LAB-101"}'

# 6. Post a sensor reading (updates currentValue automatically)
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":26.3}'

# 7. Get reading history
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings

# 8. Attempt delete room with sensors → 409 Conflict
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301

# 9. Invalid roomId → 422 Unprocessable Entity
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"X-001","type":"CO2","roomId":"GHOST-999"}'

# 10. Post to maintenance sensor → 403 Forbidden
curl -X POST http://localhost:8080/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":10.0}'
```

## Report: Answers to Coursework Questions

### Part 1, Q1 — JAX-RS Resource Class Lifecycle

In JAX-RS, a new object of the resource class is created for each request.
So data inside that class does not stay after the request ends.
To keep data, I used static variables (like lists or maps) so all requests can share the same data.
