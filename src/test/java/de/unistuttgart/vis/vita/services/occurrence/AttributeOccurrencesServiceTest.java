package de.unistuttgart.vis.vita.services.occurrence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.AttributeTestData;
import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.TextSpanTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.occurrence.AbsoluteTextPosition;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

public class AttributeOccurrencesServiceTest extends OccurrencesServiceTest {
  
  private static final int ABSOLUTE_START_OFFSET = TextSpanTestData.TEST_TEXT_SPAN_START;
  private static final int ABSOLUTE_END_OFFSET = TextSpanTestData.TEST_TEXT_SPAN_END;

  private String documentId;
  private String entityId;
  private String chapterId;
  private String attributeId;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    // first set up test data
    TextSpanTestData testData = new TextSpanTestData();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    Chapter testChapter = new ChapterTestData().createTestChapter();

    // Set range
    TextPosition rangeStartPos = new TextPosition(null, 0);
    TextPosition rangeEndPos = new TextPosition(null, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);

    TextSpan chapterRangeSpan = new TextSpan(rangeStartPos, rangeEndPos);
    testChapter.setRange(chapterRangeSpan);

    TextSpan attributeTextSpan = testData.createTestTextSpan(testChapter);
    Person testEntity = new PersonTestData().createTestPerson(1);
    Attribute testAttribute = new AttributeTestData().createTestAttribute(1);
    testEntity.getAttributes().add(testAttribute);
    testAttribute.getOccurrences().add(attributeTextSpan);

    // id for query
    documentId = testDoc.getId();
    chapterId = testChapter.getId();
    entityId = testEntity.getId();
    attributeId = testAttribute.getId();

    // persist it
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    em.getTransaction().begin();
    em.persist(testDoc);
    em.persist(chapterRangeSpan);
    em.persist(testChapter);
    em.persist(attributeTextSpan);
    em.persist(testEntity);
    em.persist(testAttribute);
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(AttributeOccurrencesService.class);
  }
  
  /**
   * @return the path to the service under test
   */
  @Override
  protected String getPath() {
    return "/documents/" + documentId + "/entities/" + entityId + "/attributes/" + attributeId 
        + "/occurrences";
  }
  
  /**
   * Checks whether the exact occurrences for an attribute of an entity can be caught via GET by not
   * passing a "step" parameter
   */
  @Test
  public void testGetExactOccurrences() {
    OccurrencesResponse actualResponse = target(getPath()).queryParam("rangeStart", DEFAULT_RANGE_START)
                                                        .queryParam("rangeEnd", DEFAULT_RANGE_END)
                                                        .request().get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    checkOccurrences(actualResponse.getOccurrences(), true);
  }

  /**
   * Checks whether stepwise occurrences for attribute of an entity can be caught via GET.
   */
  @Test
  public void testGetOccurencesInSteps() {
    OccurrencesResponse actualResponse = target(getPath()).queryParam("steps", DEFAULT_STEP_AMOUNT)
                                                      .queryParam("rangeStart", DEFAULT_RANGE_START)
                                                      .queryParam("rangeEnd", DEFAULT_RANGE_END)
                                                      .request().get(OccurrencesResponse.class);

    // check response and amount of occurrences
    assertNotNull(actualResponse);
    checkOccurrences(actualResponse.getOccurrences(), false);
  }

  private void checkOccurrences(List<Occurrence> occurrences, boolean exact) {
    assertEquals(1, occurrences.size());
    Occurrence receivedOccurence = occurrences.get(0);

    AbsoluteTextPosition absoluteStart = receivedOccurence.getStart();
    AbsoluteTextPosition absoluteEnd = receivedOccurence.getEnd();
    
    // check ids of the chapters
    assertEquals(chapterId, absoluteStart.getChapter());
    assertEquals(chapterId, absoluteEnd.getChapter());
    
    // check values
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
