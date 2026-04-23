package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DataStore — the in-memory "database" for the Smart Campus API.
 *
 * WHY static fields?
 * JAX-RS (by default) creates a NEW instance of every resource class
 * for EACH incoming request. Instance fields would be lost after every
 * request. Static fields belong to the CLASS, not instances, so they
 * live for the entire lifetime of the Tomcat application — giving us
 * persistent storage without a real database.
 *
 * WHY ConcurrentHashMap?
 * Tomcat handles multiple simultaneous requests using multiple threads.
 * A plain HashMap is NOT thread-safe — concurrent writes can corrupt
 * its internal structure. ConcurrentHashMap uses internal locking to
 * safely handle concurrent access without blocking reads.
 */
public class DataStore {

    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    // Static initializer — runs ONCE when Tomcat loads this class.
    // Pre-populates the "database" with sample campus data.
    static {
        // --- Rooms ---
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Science Lab", 30);
        Room r3 = new Room("HALL-A", "Main Lecture Hall", 200);
        Room r4 = new Room("CAFE-1", "Campus Cafeteria", 120);

        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);
        rooms.put(r4.getId(), r4);

        // --- Sensors ---
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        r1.getSensorIds().add(s1.getId());
        sensors.put(s1.getId(), s1);
        readings.put(s1.getId(), new ArrayList<>());

        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 450.0, "LAB-101");
        r2.getSensorIds().add(s2.getId());
        sensors.put(s2.getId(), s2);
        readings.put(s2.getId(), new ArrayList<>());

        Sensor s3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "LIB-301");
        r1.getSensorIds().add(s3.getId());
        sensors.put(s3.getId(), s3);
        readings.put(s3.getId(), new ArrayList<>());

        Sensor s4 = new Sensor("TEMP-002", "Temperature", "ACTIVE", 19.0, "HALL-A");
        r3.getSensorIds().add(s4.getId());
        sensors.put(s4.getId(), s4);
        readings.put(s4.getId(), new ArrayList<>());

        Sensor s5 = new Sensor("CO2-002", "CO2", "OFFLINE", 0.0, "CAFE-1");
        r4.getSensorIds().add(s5.getId());
        sensors.put(s5.getId(), s5);
        readings.put(s5.getId(), new ArrayList<>());
    }

    public static Map<String, Room> getRooms() { return rooms; }
    public static Map<String, Sensor> getSensors() { return sensors; }
    public static Map<String, List<SensorReading>> getReadings() { return readings; }
}