package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }


    //  GET /api/v1/sensors/{sensorId}/readings
  
    @GET
    public Response getReadings() {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 404);
            error.put("message", "Sensor '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        List<SensorReading> history = DataStore.getReadings()
            .getOrDefault(sensorId, Collections.emptyList());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("sensorId", sensorId);
        response.put("sensorType", sensor.getType());
        response.put("currentValue", sensor.getCurrentValue());
        response.put("totalReadings", history.size());
        response.put("data", history);
        return Response.ok(response).build();
    }


    //  POST /api/v1/sensors/{sensorId}/readings
    //

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 404);
            error.put("message", "Sensor '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // State constraint check — mapped to 403 Forbidden
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Cannot record reading: Sensor '" + sensorId +
                "' is in MAINTENANCE mode. It is physically disconnected."
            );
        }
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Cannot record reading: Sensor '" + sensorId +
                "' is OFFLINE. Reconnect the sensor before posting readings."
            );
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        DataStore.getReadings().get(sensorId).add(reading);

        double previousValue = sensor.getCurrentValue();
        sensor.setCurrentValue(reading.getValue());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Reading recorded. Sensor currentValue updated.");
        response.put("sensorId", sensorId);
        response.put("previousValue", previousValue);
        response.put("newValue", reading.getValue());
        response.put("data", reading);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}