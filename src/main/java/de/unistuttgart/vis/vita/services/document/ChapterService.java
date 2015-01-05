package de.unistuttgart.vis.vita.services.document;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.dao.ChapterDao;
import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Provides method returning chapters requested using GET.
 */
@ManagedBean
public class ChapterService {
  
  private String chapterId;
  private String documentId;

  @EJB(name = "chapterDao")
  private ChapterDao chapterDao;

  @Inject
  private Model model;

  private static final Logger LOGGER = Logger.getLogger(ChapterService.class.getName());

  /**
   * Sets the id of the chapter this resource should represent
   * @param id the id
   */
  public ChapterService setId(String id) {
    this.chapterId = id;
    return this;
  }

  /**
   * Sets the id of the document the chapter lies in
   *
   * @param id the document id
   */
  public ChapterService setDocumentId(String id) {
    this.documentId = id;
    return this;
  }

  /**
   * Returns the requested chapter
   * @return the chapter
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Chapter getChapter() {
    Chapter readChapter = null;
    
    try {
      readChapter = chapterDao.findById(chapterId);
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }

    try {
      model.getTextRepository().populateChapterText(readChapter, documentId);
    } catch (IOException e) {
      LOGGER.log(Level.FINE, "Unable to populate chapter text of " + readChapter.getId()
          + " in document " + documentId + ". The document analysis is probably not finished yet",
          e);
    }

    return readChapter;
  }

}
