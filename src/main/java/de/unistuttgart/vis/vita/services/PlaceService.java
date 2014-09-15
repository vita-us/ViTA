package de.unistuttgart.vis.vita.services;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Provides methods to GET a place with the current id.
 */
public class PlaceService {
  
  private String placeId;
  
  private EntityManager em;
  
  @Inject
  public PlaceService (Model model) {
    em = model.getEntityManager();
  }

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
    return readPlaceFromDatabase();
  }
  
  private Place readPlaceFromDatabase() {
    TypedQuery<Place> query = em.createNamedQuery("Place.findPlaceById", Place.class);
    query.setParameter("placeId", placeId);
    return query.getSingleResult();
  }

}
