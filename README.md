# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W — Client-Server Architectures  
**Student:** [YOUR FULL NAME]  
**Student ID:** [YOUR STUDENT ID]  
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
