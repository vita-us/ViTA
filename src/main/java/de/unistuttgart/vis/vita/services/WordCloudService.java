package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.dao.WordCloudDao;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
public class WordCloudService extends BaseService {

  private String documentId;

  @Inject
  private EntityManager em;

  private DocumentDao documentDao;
  private WordCloudDao wordCloudDao;

  private final Logger LOGGER = Logger.getLogger(WordCloudService.class.getName());

  @Override
  public void postConstruct() {
    super.postConstruct();
    documentDao = getDaoFactory().getDocumentDao();
    wordCloudDao = getDaoFactory().getWordCloudDao();
  }

  public WordCloudService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public WordCloud getWordCloudContent(@QueryParam("wordCount") int wordCount,
                                        @QueryParam("entityId") String entityId) {
    WordCloud wordCloud = null;

    try {
      if (StringUtils.isEmpty(entityId)) {
        wordCloud = wordCloudDao.findByDocument(documentId);
      } else {
        wordCloud = wordCloudDao.findByEntity(entityId);
      }
    } catch (NoResultException nre) {
      LOGGER.log(Level.FINEST, "No word cloud found!");
    }

    if (!documentDao.isAnalysisFinished(documentId) && wordCloud == null) {
      LOGGER.log(Level.INFO, "Word cloud requested, but analysis is still running.");
      throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
    }

    return wordCloud;
  }
}
