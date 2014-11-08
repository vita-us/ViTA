package de.unistuttgart.vis.vita.services.occurrence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.TextSpanTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.occurrence.AbsoluteTextPosition;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Performs test on the EntityOccurrencesService.
 */
public class EntityOccurrencesServiceTest extends ServiceTest {

  private static final int ABSOLUTE_START_OFFSET = TextSpanTestData.TEST_TEXT_SPAN_START;
  private static final int ABSOLUTE_END_OFFSET = TextSpanTestData.TEST_TEXT_SPAN_END;
  
  private String docId;
  private String chapterId;
  private String personId;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    // first set up test data
    TextSpanTestData testData = new TextSpanTestData();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    Chapter testChapter = new ChapterTestData().createTestChapter();
    
    // Set range of the chapter
    TextPosition rangeStartPos = new TextPosition(null, 0);
    TextPosition rangeEndPos = new TextPosition(null, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextSpan chapterRangeSpan = new TextSpan(rangeStartPos, rangeEndPos);
    testChapter.setRange(chapterRangeSpan);
    
    TextSpan personTextSpan = testData.createTestTextSpan(testChapter);
    Person testPerson = new PersonTestData().createTestPerson(1);
    testPerson.getOccurrences().add(personTextSpan);
    
    // save ids for query
    docId = testDoc.getId();
    chapterId = testChapter.getId();
    personId = testPerson.getId();
    
    // persist it
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    em.getTransaction().begin();
    em.persist(testDoc);
    em.persist(chapterRangeSpan);
    em.persist(testChapter);
    em.persist(personTextSpan);
    em.persist(testPerson);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(EntityOccurrencesService.class);
  }

  /**
   * Checks whether occurrences for an entity can be caught using the REST interface.
   */
  @Test
  public void testGetOccurrences() {
    String path = "/documents/" + docId + "/entities/" + personId + "/occurrences";
    
    OccurrencesResponse actualResponse = target(path).queryParam("steps", 100)
                                                      .queryParam("rangeStart", 0.0)
                                                      .queryParam("rangeEnd", 1.0)
                                                      .request().get(OccurrencesResponse.class);
    // check response and amount of occurrences
    assertNotNull(actualResponse);
    checkOccurrences(actualResponse.getOccurrences());
  }


  /**
   * Check whether the occurrences list contains the expected data.
   * 
   * @param occurrences - the list of occurrences to be checked
   */
  private void checkOccurrences(List<Occurrence> occurrences) {
    assertEquals(1, occurrences.size());
    
    Occurrence receivedOccurence = occurrences.get(0);
    
    // check start position, should be lower or equals
    AbsoluteTextPosition absoluteStart = receivedOccurence.getStart();
    assertEquals(chapterId, absoluteStart.getChapter());
    assertTrue(ABSOLUTE_START_OFFSET >= absoluteStart.getOffset());
    
    // check start position, should be greater or equals
    AbsoluteTextPosition absoluteEnd = receivedOccurence.getEnd();
    assertEquals(chapterId, absoluteEnd.getChapter());
    assertTrue(ABSOLUTE_END_OFFSET <= absoluteEnd.getOffset());
    
    // check the length, should be greater or equals
    assertTrue(TextSpanTestData.TEST_TEXT_SPAN_LENGTH <= receivedOccurence.getLength());
  }

}
