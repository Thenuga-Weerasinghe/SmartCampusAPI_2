package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sensor Resource — handles all /api/v1/sensors endpoints
 *
 * GET  /sensors              → list all (optionally filtered by ?type=)
 * GET  /sensors/{id}         → get one sensor
 * POST /sensors              → register new sensor (validates roomId)
 * [locator] /sensors/{id}/readings → delegates to SensorReadingResource
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // ═══════════════════════════════════════════════════════════
    //  GET /api/v1/sensors
    //  GET /api/v1/sensors?type=CO2  (filtered)
    //
    //  @QueryParam reads the ?type= part of the URL.
    //  If not provided, 'type' is null and all sensors are returned.
    // ═══════════════════════════════════════════════════════════
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(DataStore.getSensors().values());

        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("count", sensorList.size());
        if (type != null && !type.isEmpty()) {
            response.put("appliedFilter", "type=" + type);
        }
        response.put("data", sensorList);
        return Response.ok(response).build();
    }

    // ═══════════════════════════════════════════════════════════
    //  GET /api/v1/sensors/{sensorId}
    // ═══════════════════════════════════════════════════════════
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensors().get(sensorId);

        if (sensor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 404);
            error.put("message", "Sensor '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", sensor);
        return Response.ok(response).build();
    }

    // ═══════════════════════════════════════════════════════════
    //  POST /api/v1/sensors
    //
    //  @Consumes(APPLICATION_JSON) — JAX-RS automatically returns
    //  415 Unsupported Media Type if the client sends anything
    //  other than Content-Type: application/json.
    //  Our method code is never even reached in that case.
    // ═══════════════════════════════════════════════════════════
    @POST
    public Response createSensor(Sensor sensor) {

        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 400);
            error.put("message", "Sensor 'id' field is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        // DEPENDENCY VALIDATION: roomId must point to a real room
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            throw new LinkedResourceNotFoundException(
                "Sensor registration failed: 'roomId' field is required.");
        }
        if (!DataStore.getRooms().containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Sensor registration failed: Room '" + sensor.getRoomId() +
                "' does not exist. Create the room first via POST /api/v1/rooms.");
        }

        if (DataStore.getSensors().containsKey(sensor.getId())) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 409);
            error.put("message", "Sensor with ID '" + sensor.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        DataStore.getSensors().put(sensor.getId(), sensor);
        DataStore.getReadings().put(sensor.getId(), new ArrayList<>());
        DataStore.getRooms().get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Sensor '" + sensor.getId() + "' registered and linked to room '" + sensor.getRoomId() + "'.");
        response.put("data", sensor);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    // ═══════════════════════════════════════════════════════════
    //  SUB-RESOURCE LOCATOR
    //
    //  No HTTP verb annotation (@GET, @POST) — this is intentional.
    //  When JAX-RS sees /sensors/TEMP-001/readings, it calls this
    //  method, gets the SensorReadingResource instance back, and
    //  then routes the actual request (GET or POST) to that class.
    //
    //  This is the "Sub-Resource Locator" pattern:
    //  SensorResource LOCATES the right sub-resource handler.
    //  SensorReadingResource handles the actual logic.
    // ═══════════════════════════════════════════════════════════
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}