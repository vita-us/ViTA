package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Represents a data access object for accessing places.
 */
@ManagedBean
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "Place.findAllPlaces",
              query = "SELECT pl "
                    + "FROM Place pl"),
      
  @NamedQuery(name = "Place.findPlacesInDocument",
              query = "SELECT pl "
                    + "FROM Place pl, Document d "
                    + "WHERE d.id = :documentId "
                    + "AND pl MEMBER OF d.content.places "
                    + "ORDER BY pl.rankingValue"),
      
  @NamedQuery(name = "Place.findPlaceById",
              query = "SELECT pl "
                    + "FROM Place pl "
                    + "WHERE pl.id = :placeId"),
  
  @NamedQuery(name = "Place.findPlaceByName",
              query = "SELECT pl "
                    + "FROM Place pl "
                    + "WHERE pl.displayName = :placeName")
})
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
