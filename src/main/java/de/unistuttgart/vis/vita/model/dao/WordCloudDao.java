package de.unistuttgart.vis.vita.model.dao;

import javax.annotation.ManagedBean;
import javax.persistence.*;

import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

/**
 * Represents a data access object for accessing WordClouds.
 */
@ManagedBean
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "WordCloud.getGlobal",
    query = "SELECT doc.content.globalWordCloud "
    + "FROM Document doc "
    + "WHERE doc.id = :documentId"),
  @NamedQuery(name = "WordCloud.getForEntity",
    query = "SELECT ent.wordCloud "
    + "FROM Entity ent "
    + "WHERE ent.id = :entityId")
})
public class WordCloudDao extends JpaDao<WordCloud, String> {

  private static final String ENTITY_ID_PARAMETER = "entityId";
  private static final String DOCUMENT_ID_PARAMETER = "documentId";

  public WordCloudDao(EntityManager em) {
    super(WordCloud.class, em);
  }

  /**
   * Finds the global WordCloud for the Document with the given id.
   * 
   * @param docId - the id of the document for which the global WordCloud should be found
   * @return the global WordCloud for the given Document
   */
  public WordCloud findByDocument(String docId) {
    TypedQuery<WordCloud> docQuery = em.createNamedQuery("WordCloud.getGlobal", WordCloud.class);
    docQuery.setParameter(DOCUMENT_ID_PARAMETER, docId);
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
    entityQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    return entityQuery.getSingleResult();
  }

}
