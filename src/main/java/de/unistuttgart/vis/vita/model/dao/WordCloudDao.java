package de.unistuttgart.vis.vita.model.dao;

import javax.persistence.*;

import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

/**
 * Represents a data access object for accessing WordClouds.
 */
@MappedSuperclass
@NamedQueries({
    @NamedQuery(name = "WordCloud.getGlobal",
        query = "SELECT doc.content.globalWordCloud "
                + "FROM Document doc "
                + "WHERE doc.id = :documentId"),
    @NamedQuery(name = "WordCloud.getForEntity",
        query = "SELECT ent.wordCloud "
                + "FROM Entity ent "
                + "WHERE ent.id = :entityId"),
    @NamedQuery(name = "WordCloud.removeEntityIdOfItems",
        query = "UPDATE WordCloudItem AS wordCloudItem "
                + "SET wordCloudItem.entityId = null "
                + "WHERE wordCloudItem.entityId = :entityId")

})
public class WordCloudDao extends JpaDao<WordCloud, String> {

  private static final String ENTITY_ID_PARAMETER = "entityId";
  private static final String DOCUMENT_ID_PARAMETER = "documentId";

  /**
   * Creates a new data access object for accessing WordClouds.
   *
   * @param em - the EntityManager to be used to access the WordClouds
   */
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
    TypedQuery<WordCloud> entityQuery =
        em.createNamedQuery("WordCloud.getForEntity", WordCloud.class);
    entityQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    return entityQuery.getSingleResult();
  }

  /**
   * Removes the entityId of all items in this word cloud that have it set to the given value.
   *
   * @param entityId the entityId which will be set to null
   */
  public void removeEntityIdOfItems(String entityId){
    Query entityQuery =
        em.createNamedQuery("WordCloud.removeEntityIdOfItems");
    entityQuery.setParameter("entityId", entityId).executeUpdate();
  }

}
