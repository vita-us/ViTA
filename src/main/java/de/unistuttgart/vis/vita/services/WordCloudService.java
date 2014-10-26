package de.unistuttgart.vis.vita.services;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;

public class WordCloudService {
  private String wordCloudId;
  private String documentId;
  
  private EntityManager em;
  
  @Context
  private ResourceContext resourceContext;
 
  
  @Inject
  public WordCloudService(Model model) {
	  em = model.getEntityManager();
  }
  
  public WordCloudService setWordCloudId(String wordCloudId) {
	  this.wordCloudId = wordCloudId;
	  return this;
  }
  
  public WordCloudService setDocumentId(String documentId) {
	  this.documentId = documentId;
	  return this;
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public WordCloudService getWordCloudContent() {
	return resourceContext.getResource(WordCloudService.class).setDocumentId(documentId).setWordCloudId(wordCloudId);
  }
  
  
}
