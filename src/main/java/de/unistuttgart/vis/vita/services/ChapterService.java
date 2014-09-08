package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Provides method returning chapters requested using GET.
 */
@ManagedBean
public class ChapterService {
  private String chapterId;

  private EntityManager em;
  
  @Inject
  public ChapterService(Model model) {
    em = model.getEntityManager();
  }
  
  /**
   * Sets the id of the chapter this resource should represent
   * @param id the id
   */
  public ChapterService setId(String id) {
    this.chapterId = id;
    return this;
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
