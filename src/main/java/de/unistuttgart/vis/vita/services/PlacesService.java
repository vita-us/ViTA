package de.unistuttgart.vis.vita.services;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.services.responses.PlacesResponse;

/**
 * Provides a method to GET all places mentioned in the document this service refers to.
 */
@ManagedBean
public class PlacesService {
  
  private String documentId;
  
  @Context
  private ResourceContext resourceContext;
  
  private EntityManager em;
  
  /**
   * Creates a new instance of PlacesService
   * 
   * @param model - the model to be used in this service
   */
  @Inject
  public PlacesService(Model model) {
    em = model.getEntityManager();
  }
  
  /**
   * Sets the id of the document for which this service should provide the mentioned places.
   * 
   * @param docId - the id of the document
   * @return the places service
   */
  public PlacesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }
  
  /**
   * Returns a PlacesResponse including a list of Places with a given maximum length, starting at
   * an also given offset.
   * 
   * @param offset - the first Place to be returned
   * @param count - the maximum amount of Places to be returned
   * @return PlacesResponse including a list of Places
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public PlacesResponse getPlaces(@QueryParam("offset") int offset,
                                  @QueryParam("count") int count) {
    List<Place> places = readPlacesFromDatabase(offset, count);
    
    return new PlacesResponse(places);
  }
  
  private List<Place> readPlacesFromDatabase(int offset, int count) {
    TypedQuery<Place> query = em.createNamedQuery("Place.findPlacesInDocument", Place.class);
    query.setParameter("documentId", documentId);
    
    query.setFirstResult(offset);
    query.setMaxResults(count);
    
    return query.getResultList();
  }

  /**
   * Returns the Service to access the Place with the given id.
   * 
   * @param id - the id of the Place to be accessed
   * @return the PlaceService to access the Place with the given id
   */
  @Path("{placeId}")
  public PlaceService getPlace(@PathParam("placeId") String id) {
    return resourceContext.getResource(PlaceService.class).setPlaceId(id);
  }
  
}
