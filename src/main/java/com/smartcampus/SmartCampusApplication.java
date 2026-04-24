package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // Jersey's package scanning (configured in web.xml) automatically
    // discovers and registers all @Path and @Provider annotated classes.
   
}