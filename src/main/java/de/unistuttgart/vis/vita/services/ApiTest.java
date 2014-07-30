package de.unistuttgart.vis.vita.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("get")
public class ApiTest {

	@GET
	@Path("version")
	public Api getApiAsJSON(){
		Api api = new Api();
		api.setApi("1.0-SNAPSHOT");
		return api;
		
	}
}
