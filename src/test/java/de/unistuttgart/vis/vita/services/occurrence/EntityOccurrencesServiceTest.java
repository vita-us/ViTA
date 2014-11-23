package de.unistuttgart.vis.vita.services.occurrence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.TextSpanTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.occurrence.AbsoluteTextPosition;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Performs test on the EntityOccurrencesService.
 */
public class EntityOccurrencesServiceTest extends OccurrencesServiceTest {

  private static final int ABSOLUTE_START_OFFSET = TextSpanTestData.TEST_TEXT_SPAN_START;
  private static final int ABSOLUTE_END_OFFSET = TextSpanTestData.TEST_TEXT_SPAN_END;
  
  private String docId;
  private String chapterId;
  private String personId;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    // first set up test data
    TextSpanTestData testData = new TextSpanTestData();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    Chapter testChapter = new ChapterTestData().createTestChapter();

    // Set range of the chapter
    TextPosition rangeStartPos = TextPosition.fromGlobalOffset(testChapter, 0);
    TextPosition rangeEndPos = TextPosition.fromGlobalOffset(testChapter, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextSpan chapterRangeSpan = new TextSpan(rangeStartPos, rangeEndPos);
    testChapter.setRange(chapterRangeSpan);

    TextSpan personTextSpan = testData.createTestTextSpan(testChapter);
    Person testPerson = new PersonTestData().createTestPerson(1);
    testPerson.getOccurrences().add(personTextSpan);

    // save ids for query
    docId = testDoc.getId();
    chapterId = testChapter.getId();
    personId = testPerson.getId();

    DocumentPart testPart = new DocumentPart();
    testPart.getChapters().add(testChapter);
    testDoc.getContent().getParts().add(testPart);
    
    // persist it
    EntityManager em = getModel().getEntityManager();
    em.getTransaction().begin();
    em.persist(chapterRangeSpan);
    em.persist(testChapter);
    em.persist(personTextSpan);
    em.persist(testPerson);
    em.persist(testPart);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(EntityOccurrencesService.class);
  }
  
  @Override
  protected String getPath() {
    return "/documents/" + docId + "/entities/" + personId + "/occurrences";
  }

  @Override
  public void testGetExactOccurrences() {
    // make request without steps
    OccurrencesResponse actualResponse = target(getPath()).queryParam("rangeStart", DEFAULT_RANGE_START)
                                                          .queryParam("rangeEnd", DEFAULT_RANGE_END)
                                                          .request().get(OccurrencesResponse.class);
    
    // check response and amount of occurrences
    assertNotNull(actualResponse);
    checkOccurrences(actualResponse.getOccurrences(), true);
  }

  /**
   * Checks whether occurrences for an entity can be caught using the REST interface.
   */
  @Override
  public void testGetStepwiseOccurences() {
    OccurrencesResponse actualResponse = target(getPath()).queryParam("steps", DEFAULT_STEP_AMOUNT)
        .queryParam("rangeStart", DEFAULT_RANGE_START)
        .queryParam("rangeEnd", DEFAULT_RANGE_END)
        .request().get(OccurrencesResponse.class);

    // check response and amount of occurrences
    assertNotNull(actualResponse);
    checkOccurrences(actualResponse.getOccurrences(), false);
  }

  /**
   * Check whether the occurrences list contains the expected data.
   *
   * @param occurrences - the list of occurrences to be checked
   */
  private void checkOccurrences(List<Occurrence> occurrences, boolean exact) {
    assertEquals(1, occurrences.size());

    Occurrence receivedOccurence = occurrences.get(0);
  
    AbsoluteTextPosition absoluteStart = receivedOccurence.getStart();
    AbsoluteTextPosition absoluteEnd = receivedOccurence.getEnd();
    
    assertEquals(chapterId, absoluteStart.getChapter());
    assertEquals(chapterId, absoluteEnd.getChapter());
    
    if (exact) {
      assertEquals(ABSOLUTE_START_OFFSET, absoluteStart.getOffset());
      assertEquals(ABSOLUTE_END_OFFSET, absoluteEnd.getOffset());
      assertEquals(TextSpanTestData.TEST_TEXT_SPAN_LENGTH, receivedOccurence.getLength());
    } else {
      assertTrue(ABSOLUTE_START_OFFSET >= absoluteStart.getOffset());
      assertTrue(ABSOLUTE_END_OFFSET <= absoluteEnd.getOffset());
      assertTrue(TextSpanTestData.TEST_TEXT_SPAN_LENGTH <= receivedOccurence.getLength()); 
    }
  }

}
