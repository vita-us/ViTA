package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Provides method returning chapters requested using GET.
 */
public class ChapterService {
  
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  
  private String chapterId;
  
  private EntityManager em;

  /**
   * Creates a new service for the chapter with the given id.
   * 
   * @param uriInfo
   * @param request
   * @param cId
   */
  public ChapterService(UriInfo uriInfo, Request request, String cId) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.chapterId = cId;
    
    this.em = Model.createUnitTestModelWithoutDrop().getEntityManager();
  }
  
  /**
   * Returns the requested chapter
   * @return
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Chapter getChapter() {
    return readChapterFromDatabase();
  }

  /**
   * Reads current chapter from database and returns it.
   * 
   * @return current chapter
   */
  private Chapter readChapterFromDatabase() {
    TypedQuery<Chapter> query = em.createNamedQuery("Chapter.findChapterById", Chapter.class);
    query.setParameter("chapterId", chapterId);
    return query.getSingleResult();
  }

}
