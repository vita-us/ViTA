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
                    + "WHERE pl.displayName = :placeName"),

  @NamedQuery(name = "Place.findSpecialPlacesInDocument",
              query = "SELECT DISTINCT pl "
                    + "FROM Place pl, Document d  "
                    + "INNER JOIN pl.occurrences ts "
                    + "WHERE d.id = :documentId "
                    + "AND pl MEMBER OF d.content.places "
                    + "GROUP BY pl.id "
                    + "HAVING (MAX(ts.end.offset) - MIN(ts.start.offset)) "
                    + "BETWEEN :minRange AND :maxRange "
                    + "AND COUNT(ts) > 3 " // lower threshold: at least 4 occurrences
                    + "ORDER BY pl.rankingValue")
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

  /**
   * Reads a given number of special places occurring in the current Document from the database and
   * returns them. Special means that these places do NOT occur widespread over the whole Document.
   *
   * @param count - the amount of places to be returned
   * @return list of places occurring in the current Document
   */
  public List<Place> readSpecialPlacesFromDatabase(String documentId, int count) {
    TypedQuery<Place> placeQuery = em.createNamedQuery("Place.findSpecialPlacesInDocument",
        Place.class);
    placeQuery.setParameter("documentId", documentId);
    placeQuery.setMaxResults(count);
    return placeQuery.getResultList();
  }

}
