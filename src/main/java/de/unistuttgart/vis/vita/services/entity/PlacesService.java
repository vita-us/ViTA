package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.dao.PlaceDao;
import de.unistuttgart.vis.vita.services.responses.PlacesResponse;

/**
 * Provides a method to GET all places mentioned in the document this service refers to.
 */
@ManagedBean
public class PlacesService {
  
  private String documentId;

  @Inject
  private PlaceDao placeDao;
  
  @Inject
  private PlaceService placeService;

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
    return new PlacesResponse(placeDao.findInDocument(documentId, offset, count));
  }

  /**
   * Returns the Service to access the Place with the given id.
   * 
   * @param id - the id of the Place to be accessed
   * @return the PlaceService to access the Place with the given id
   */
  @Path("{placeId}")
  public PlaceService getPlace(@PathParam("placeId") String id) {
    return placeService.setPlaceId(id);
  }
  
}
