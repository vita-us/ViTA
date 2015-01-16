package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PlaceTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Place;

public class PlacePersistenceTest extends AbstractPersistenceTest {

  private PlaceTestData testData;

  @Override
  public void setUp() {
    super.setUp();

    this.testData = new PlaceTestData();
  }

  /**
   * Checks whether one Place can be persisted correctly.
   */
  @Test
  public void testPersistOnePlace() {
    // first set up a place
    Place testPlace = testData.createTestPlace();

    // persist this place
    em.persist(testPlace);
    startNewTransaction();

    // read places from the database
    List<Place> places = readPlacesFromDb();
    assertEquals(1, places.size());
    Place readPlace = places.get(0);

    testData.checkData(readPlace);
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
   * Checks whether all Named Queries of Place are working correctly.
   */
  @Test
  public void testNamedQueries() {
    Place testPlace = testData.createTestPlace(1);
    Place docTestPlace = testData.createTestPlace(2);

    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPlaces().add(docTestPlace);

    String documentId = testDoc.getId();

    em.persist(testPlace);
    em.persist(docTestPlace);
    em.persist(testDoc);
    startNewTransaction();

    // check Named Query finding all places
    TypedQuery<Place> allQ = em.createNamedQuery("Place.findAllPlaces", Place.class);
    List<Place> allPlaces = allQ.getResultList();

    assertTrue(allPlaces.size() > 0);
    Place readPlace = allPlaces.get(0);
    testData.checkData(readPlace, 1);

    String id = readPlace.getId();

    // check NamedQuery finding all places in a document
    TypedQuery<Place> query = em.createNamedQuery("Place.findPlacesInDocument", Place.class);
    query.setParameter("documentId", documentId);

    List<Place> docPlaces = query.getResultList();

    assertEquals(1, docPlaces.size());
    testData.checkData(docTestPlace, 2);

    // check Named Query finding place by id
    TypedQuery<Place> idQ = em.createNamedQuery("Place.findPlaceById", Place.class);
    idQ.setParameter("placeId", id);
    Place idPlace = idQ.getSingleResult();

    testData.checkData(idPlace, 1);

    // check Named Query finding places by name
    TypedQuery<Place> nameQ = em.createNamedQuery("Place.findPlaceByName", Place.class);
    nameQ.setParameter("placeName", PlaceTestData.TEST_PLACE_1_NAME);
    List<Place> namePlaces = nameQ.getResultList();

    assertTrue(namePlaces.size() > 0);
    Place namePlace = namePlaces.get(0);
    testData.checkData(namePlace, 1);
  }

  @Test
  public void testOcurrencesAreSorted() {
    Document doc = new Document();
    Chapter chapter = new Chapter();
    TextPosition pos1 =
        TextPosition.fromGlobalOffset(chapter, 10, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition pos2 =
        TextPosition.fromGlobalOffset(chapter, 20, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition pos3 =
        TextPosition.fromGlobalOffset(chapter, 30, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition pos4 =
        TextPosition.fromGlobalOffset(chapter, 40, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);

    Range sentenceRange1 = new Range(pos1, pos4);
    Range sentenceRange2 = new Range(pos3, pos4);
    Sentence sentence1 = new Sentence(sentenceRange1, chapter, 0);
    Sentence sentence2 = new Sentence(sentenceRange2, chapter, 1);

    Range range1 = new Range(pos1, pos4);
    Range range2 = new Range(pos2, pos4);
    Range range3 = new Range(pos3, pos4);

    Occurrence occurrence1 = new Occurrence(sentence1, range1);
    Occurrence occurrence2 = new Occurrence(sentence1, range2);
    Occurrence occurrence3 = new Occurrence(sentence2, range3);

    Range span1 = new Range(pos1, pos4);
    Range span2 = new Range(pos2, pos4);
    Range span3 = new Range(pos3, pos4);

    Place p = new Place();
    // Add the occurrences in an order that is neither the correct one, nor the reverse
    p.getOccurrences().add(occurrence1);
    p.getOccurrences().add(occurrence2);
    p.getOccurrences().add(occurrence3);

    em.persist(doc);
    em.persist(chapter);
    em.persist(span1);
    em.persist(span3);
    em.persist(span2);
    em.persist(p);
    startNewTransaction();

    Place dbPlace = em.createNamedQuery("Place.findAllPlaces", Place.class).getSingleResult();
    assertThat(dbPlace.getOccurrences(),
        IsIterableContainingInOrder.contains(occurrence1, occurrence2, occurrence3));
  }

}
