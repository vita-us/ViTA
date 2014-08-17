package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Place;

public class PlacePersistenceTest extends AbstractPersistenceTest {
  
  // test data
  private static final int TEST_PLACE_RANKING_VALUE = 3;
  private static final String TEST_PLACE_NAME = "Rivendell";

  /**
   * Checks whether one Place can be persisted correctly.
   */
  @Test
  public void testPersistOnePlace() {
    // first set up a place
    Place testPlace = createTestPlace();

    // persist this place
    em.persist(testPlace);
    startNewTransaction();

    // read places from the database
    List<Place> places = readPlacesFromDb();
    assertEquals(1, places.size());
    Place readPlace = places.get(0);
    
    checkData(readPlace);
  }

  /**
   * Creates a new Place, sets attributes to test values and returns it.
   * 
   * @return test place
   */
  private Place createTestPlace() {
    Place testPlace = new Place();
    testPlace.setDisplayName(TEST_PLACE_NAME);
    testPlace.setRankingValue(TEST_PLACE_RANKING_VALUE);
    return testPlace;
  }

  /**
   * Reads Places from database and returns them.
   * 
   * @return list of places
   */
  private List<Place> readPlacesFromDb() {
    TypedQuery<Place> query = em.createQuery("from Place", Place.class);
    return query.getResultList();
  }
  
  /**
   * Checks whether the given place is not <code>null</code> and includes the correct test data.
   * 
   * @param readPlace - the place which should be checked
   */
  private void checkData(Place readPlace) {
    assertNotNull(readPlace);
    assertEquals(TEST_PLACE_NAME, readPlace.getDisplayName());
    assertEquals(TEST_PLACE_RANKING_VALUE, readPlace.getRankingValue());
  }
  
  /**
   * Checks whether all Named Queries of Place are working correctly.
   */
  @Test
  public void testNamedQueries() {
    Place testPlace = createTestPlace();
    
    em.persist(testPlace);
    startNewTransaction();
    
    // check Named Query finding all places
    TypedQuery<Place> allQ = em.createNamedQuery("Place.findAllPlaces", Place.class);
    List<Place> allPlaces = allQ.getResultList();
    
    assertTrue(allPlaces.size() > 0);
    Place readPlace = allPlaces.get(0);
    checkData(readPlace);
    
    int id = readPlace.getId();
    
    // check Named Query finding place by id
    TypedQuery<Place> idQ = em.createNamedQuery("Place.findPlaceById", Place.class);
    idQ.setParameter("placeId", id);
    Place idPlace = idQ.getSingleResult();
    
    checkData(idPlace);
    
    // check Named Query finding places by name
    TypedQuery<Place> nameQ = em.createNamedQuery("Place.findPlaceByName", Place.class);
    nameQ.setParameter("placeName", TEST_PLACE_NAME);
    List<Place> namePlaces = nameQ.getResultList();
    
    assertTrue(namePlaces.size() > 0);
    Place namePlace = namePlaces.get(0);
    checkData(namePlace);
  }
  
  @Test
  public void testOcurrencesAreSorted() {
    Document doc = new Document();
    Chapter chapter = new Chapter(doc);
    TextPosition pos1 = new TextPosition(chapter, 10);
    TextPosition pos2 = new TextPosition(chapter, 20);
    TextPosition pos3 = new TextPosition(chapter, 30);
    TextPosition pos4 = new TextPosition(chapter, 40);
    TextSpan span1 = new TextSpan(pos1, pos4);
    TextSpan span2 = new TextSpan(pos2, pos4);
    TextSpan span3 = new TextSpan(pos3, pos4);
    
    Place p = new Place();
    // Add the occurrences in an order that is neither the correct one, nor the reverse
    p.getOccurrences().add(span1);
    p.getOccurrences().add(span3);
    p.getOccurrences().add(span2);

    em.persist(doc);
    em.persist(chapter);
    em.persist(span1);
    em.persist(span3);
    em.persist(span2);
    em.persist(p);
    startNewTransaction();
    
    Place dbPlace = em.createNamedQuery("Place.findAllPlaces", Place.class).getSingleResult();
    assertThat(dbPlace.getOccurrences(),
        IsIterableContainingInOrder.contains(span1, span2, span3));
  }

}
