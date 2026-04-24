package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Discovery Endpoint — GET /api/v1/
 *
 * Returns API metadata and HATEOAS navigation links.

 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {

        Map<String, Object> response = new HashMap<>();
        response.put("api", "Smart Campus Sensor and Room Management API");
        response.put("version", "1.0.0");
        response.put("status", "operational");
        response.put("description", "RESTful API for managing campus rooms and IoT sensors");
        response.put("contact", "admin@smartcampus.westminster.ac.uk");
        response.put("builtWith", "Java JAX-RS (Jersey 2.41) + Apache Tomcat 9");

        // Available resource collections
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms",   "http://localhost:8080/api/v1/rooms");
        resources.put("sensors", "http://localhost:8080/api/v1/sensors");
        response.put("resources", resources);

        // HATEOAS links — hypermedia navigation
        Map<String, String> links = new HashMap<>();
        links.put("self",    "http://localhost:8080/api/v1/");
        links.put("rooms",   "http://localhost:8080/api/v1/rooms");
        links.put("sensors", "http://localhost:8080/api/v1/sensors");
        response.put("_links", links);

        return Response.ok(response).build();
    }
}