package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Represents a data access object for accessing places.
 */
@ManagedBean
public class PlaceDao extends JpaDao<Place, String> {

  private static final String DOCUMENT_ID_PARAMETER = "documentId";

  /**
   * Creates a new data access object for accessing places.
   */
  public PlaceDao() {
    super(Place.class);
  }

  public List<Place> findInDocument(String documentId, int offset, int count) {
    TypedQuery<Place> docQuery = em.createNamedQuery(getInDocumentQueryName(), Place.class);
    docQuery.setParameter(DOCUMENT_ID_PARAMETER, documentId);
    return docQuery.getResultList();
  }

  /**
   * @return the name of the {@link NamedQuery} for searching in a specific Document
   */
  public String getInDocumentQueryName() {
    String className = getPersistentClassName();
    return className + "." + "find" + className + "s" + "InDocument";
  }

}
