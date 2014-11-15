package de.unistuttgart.vis.vita.services.occurrence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.occurrence.AbsoluteTextPosition;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

public class AttributeOccurrencesServiceTest extends ServiceTest {
  private static final int ABSOLUTE_START_OFFSET = TextSpanTestData.TEST_TEXT_SPAN_START;
  private static final int ABSOLUTE_END_OFFSET = TextSpanTestData.TEST_TEXT_SPAN_END;

  private String documentId;
  private String entityId;
  private String chapterId;
  private String attributeId;

  @Override
  @Before
  public void setUp() throws Exception {
    // first set up test data
    super.setUp();
    TextSpanTestData testData = new TextSpanTestData();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    Chapter testChapter = new ChapterTestData().createTestChapter();

    // Set range
    TextPosition rangeStartPos =
        TextPosition.fromGlobalOffset(testChapter, 0);
    TextPosition rangeEndPos =
        TextPosition.fromGlobalOffset(testChapter, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);

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
    EntityManager em = getModel().getEntityManager();
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
   * Checks whether occurrences for attribute of an entity can be caught using
   * the REST interface.
   */
  @Test
  public void testGetOccurences() {


    String path = "/documents/" + documentId + "/entities/" + entityId + "/attributes/"
        + attributeId + "/occurrences";

    OccurrencesResponse actualResponse = target(path).queryParam("steps", 100)
        .queryParam("rangeStart", 0.0).queryParam("rangeEnd", 1.0).request()
        .get(OccurrencesResponse.class);

    // check response and amount of occurrences
    assertNotNull(actualResponse);
    checkOccurrences(actualResponse.getOccurrences());
  }

  private void checkOccurrences(List<Occurrence> occurrences) {
    assertEquals(1, occurrences.size());

    Occurrence receivedOccurence = occurrences.get(0);

    // check start position, should be lower or equals
    AbsoluteTextPosition absoluteStart = receivedOccurence.getStart();
    assertEquals(chapterId, absoluteStart.getChapter());
    assertTrue(ABSOLUTE_START_OFFSET >= absoluteStart.getOffset());

    // check end position, should be greater or equals
    AbsoluteTextPosition absoluteEnd = receivedOccurence.getEnd();
    assertEquals(chapterId, absoluteEnd.getChapter());
    assertTrue(ABSOLUTE_END_OFFSET <= absoluteEnd.getOffset());

    // check the length, should be greater or equals
    assertTrue(TextSpanTestData.TEST_TEXT_SPAN_LENGTH <= receivedOccurence.getLength());
  }
}
