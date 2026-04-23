package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Formal JAX-RS Application declaration.
 *
 * With Tomcat deployment, the URL root (/api/v1/*) is defined
 * in web.xml's servlet-mapping. This class satisfies the coursework
 * requirement to subclass Application.
 *
 * @ApplicationPath is present to satisfy the LO requirement but
 * the effective routing is controlled by web.xml's servlet-mapping.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // Jersey's package scanning (configured in web.xml) automatically
    // discovers and registers all @Path and @Provider annotated classes.
    // This class serves as the formal JAX-RS application declaration.
}