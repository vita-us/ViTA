package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.dao.PlaceDao;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.responses.PlacesResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a method to GET all places mentioned in the document this service refers to.
 */
@ManagedBean
public class PlacesService extends BaseService {

  private String documentId;

  private PlaceDao placeDao;
  private DocumentDao documentDao;

  private final Logger LOGGER = Logger.getLogger(PlacesService.class.getName());
  
  @Inject
  private PlaceService placeService;

  @Override public void postConstruct() {
    super.postConstruct();
    placeDao = getDaoFactory().getPlaceDao();
    documentDao = getDaoFactory().getDocumentDao();
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

    if (!documentDao.isAnalysisFinished(documentId)) {
      LOGGER.log(Level.FINEST, "List of Places requested, but analysis not finished yet.");
      // send HTTP 409 Conflict instead of empty response to avoid wrong caching
      throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
    }

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
