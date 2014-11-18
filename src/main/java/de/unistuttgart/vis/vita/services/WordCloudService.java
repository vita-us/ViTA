package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

@ManagedBean
public class WordCloudService {
  
  private String documentId;

  @Inject
  private EntityManager em;

  public WordCloudService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public WordCloud getWordCloudContent(@QueryParam("wordCount") int wordCount,
                                        @QueryParam("entityId") String entityId) {
    // TODO implement WordCloudService
    return new WordCloud();
  }

}
