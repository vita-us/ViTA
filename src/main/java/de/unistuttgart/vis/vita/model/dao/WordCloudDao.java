package de.unistuttgart.vis.vita.model.dao;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

/**
 * Represents a data access object for accessing WordClouds.
 */
@Stateless
public class WordCloudDao extends JpaDao<WordCloud, String> {

  /**
   * Creates a new data access object for accessing WordClouds.
   */
  public WordCloudDao() {
    super(WordCloud.class);
  }

  /**
   * Finds the global WordCloud for the Document with the given id.
   * 
   * @param docId - the id of the document for which the global WordCloud should be found
   * @return the global WordCloud for the given Document
   */
  public WordCloud findByDocument(String docId) {
    TypedQuery<WordCloud> docQuery = em.createNamedQuery("WordCloud.getGlobal", WordCloud.class);
    docQuery.setParameter("docId", docId);
    return docQuery.getSingleResult();
  }
  
  /**
   * Finds the entity-specific WordCloud for the entity with the given id.
   * 
   * @param entityId - the id of the entity for which the WordCloud should be found
   * @return the WordCloud for the given entity
   */
  public WordCloud findByEntity(String entityId) {
    TypedQuery<WordCloud> entityQuery = em.createNamedQuery("WordCloud.getForEntity", WordCloud.class);
    entityQuery.setParameter("entityId", entityId);
    return entityQuery.getSingleResult();
  }

}
