package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.dao.PlaceDao;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Provides methods to GET a place with the current id.
 */
@ManagedBean
public class PlaceService {
  
  private String placeId;

  @EJB(name = "placeDao")
  private PlaceDao placeDao;

  /**
   * Sets the id of the Place this resource should represent.
   * 
   * @param id the id
   */
  public PlaceService setPlaceId(String id) {
    this.placeId = id;
    return this;
  }
  
  /**
   * Reads the requested place from the database and returns it in JSON using the REST.
   * 
   * @return the place with the current id in JSON
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Place getPlace() {
    Place readPlace = null;
    
    try {
      readPlace = placeDao.findById(placeId);
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }
    
    return readPlace;
  }

}
