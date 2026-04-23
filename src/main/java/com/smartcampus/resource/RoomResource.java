package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Room Resource — handles all /api/v1/rooms endpoints
 *
 * GET  /rooms          → list all rooms
 * POST /rooms          → create a room
 * GET  /rooms/{id}     → get one room
 * DELETE /rooms/{id}   → delete a room (blocks if sensors exist)
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // ═══════════════════════════════════════════════════════════
    //  GET /api/v1/rooms
    // ═══════════════════════════════════════════════════════════
    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(DataStore.getRooms().values());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("count", roomList.size());
        response.put("data", roomList);

        return Response.ok(response).build();
    }

    // ═══════════════════════════════════════════════════════════
    //  POST /api/v1/rooms
    //  Body: {"id":"ENG-202","name":"Engineering Lab","capacity":40}
    // ═══════════════════════════════════════════════════════════
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 400);
            error.put("message", "Room 'id' field is required and cannot be empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (DataStore.getRooms().containsKey(room.getId())) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 409);
            error.put("message", "A room with ID '" + room.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.getRooms().put(room.getId(), room);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Room '" + room.getId() + "' created successfully.");
        response.put("data", room);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    // ═══════════════════════════════════════════════════════════
    //  GET /api/v1/rooms/{roomId}
    // ═══════════════════════════════════════════════════════════
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);

        if (room == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 404);
            error.put("message", "Room with ID '" + roomId + "' was not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", room);
        return Response.ok(response).build();
    }

    // ═══════════════════════════════════════════════════════════
    //  DELETE /api/v1/rooms/{roomId}
    //
    //  BUSINESS RULE: Cannot delete a room that has sensors.
    //  Reason: Deleting would create "orphan sensors" — sensors
    //  pointing to a non-existent room, breaking data integrity.
    //
    //  IDEMPOTENCY: Calling DELETE twice on the same room
    //  results in the same end state (room doesn't exist).
    //  First call: 200 OK. Second call: 404 Not Found.
    //  Both leave the system in the same state — room is gone.
    // ═══════════════════════════════════════════════════════════
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);

        if (room == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("code", 404);
            error.put("message", "Room '" + roomId + "' not found. It may have already been deleted.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Room '" + roomId + "' cannot be deleted. It still has " +
                room.getSensorIds().size() + " sensor(s) assigned: " +
                room.getSensorIds() + ". Decommission all sensors first."
            );
        }

        DataStore.getRooms().remove(roomId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Room '" + roomId + "' has been successfully decommissioned.");
        return Response.ok(response).build();
    }
}