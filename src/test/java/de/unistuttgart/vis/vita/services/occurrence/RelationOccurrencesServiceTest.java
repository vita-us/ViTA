package de.unistuttgart.vis.vita.services.occurrence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Performs test on RelationOccurrencesService, persisting persons with overlapping occurrences and
 * fetch their relation using REST.
 */
public class RelationOccurrencesServiceTest extends OccurrencesServiceTest {

  /*
   * SP for the separate TextSpans without an overlap
   */
  private static final int SP1_START_OFFSET = 100000;
  private static final int SP1_END_OFFSET   = 101000;

  private static final int SP2_START_OFFSET = 122000;
  private static final int SP2_END_OFFSET   = 123000;

  /*
   * OL for overlapping TextSpans with an overlap
   */
  private static final int OL1_START_OFFSET = 105000;
  private static final int OL1_END_OFFSET   = 115000;

  private static final int OL2_START_OFFSET = 110000;
  private static final int OL2_END_OFFSET   = 120000;

  private static final int OL_LENGTH = OL1_END_OFFSET - OL2_START_OFFSET;

  private PersonTestData personTestData;
  private EntityRelationTestData relationTestData;

  private String docId;
  private String entityIds;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    // set up test data
    this.personTestData = new PersonTestData();
    this.relationTestData = new EntityRelationTestData();

    // set up document
    Document testDoc = new DocumentTestData().createTestDocument(1);

    // set up test chapter
    Chapter testChapter = new ChapterTestData().createTestChapter();
    TextPosition chapterStart = TextPosition.fromGlobalOffset(testChapter, 0);
    TextPosition chapterEnd = TextPosition.fromGlobalOffset(testChapter, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextSpan chapterRange = new TextSpan(chapterStart, chapterEnd);
    testChapter.setRange(chapterRange);

    // set up first person
    Person originPerson = personTestData.createTestPerson(1);

    // non overlapping TextSpan
    TextPosition originSeparateStart = TextPosition.fromGlobalOffset(testChapter, SP1_START_OFFSET);
    TextPosition originSeparateEnd = TextPosition.fromGlobalOffset(testChapter, SP1_END_OFFSET);
    TextSpan originSeparateSpan = new TextSpan(originSeparateStart, originSeparateEnd);

    // overlapping TextSpan
    TextPosition originStart = TextPosition.fromGlobalOffset(testChapter, OL1_START_OFFSET);
    TextPosition originEnd = TextPosition.fromGlobalOffset(testChapter, OL1_END_OFFSET);
    TextSpan originSpan = new TextSpan(originStart, originEnd);

    originPerson.getOccurrences().add(originSpan);
    originPerson.getOccurrences().add(originSeparateSpan);

    // set up second person
    Person targetPerson = personTestData.createTestPerson(2);

    // non-overlapping TextSpan
    TextPosition targetSeparateStart = TextPosition.fromGlobalOffset(testChapter, SP2_START_OFFSET);
    TextPosition targetSeparateEnd  = TextPosition.fromGlobalOffset(testChapter, SP2_END_OFFSET);
    TextSpan targetSeparateSpan = new TextSpan(targetSeparateStart, targetSeparateEnd);

    // overlapping TextSpan
    TextPosition targetStart = TextPosition.fromGlobalOffset(testChapter, OL2_START_OFFSET);
    TextPosition targetEnd = TextPosition.fromGlobalOffset(testChapter, OL2_END_OFFSET);
    TextSpan targetSpan = new TextSpan(targetStart, targetEnd);
    
    targetPerson.getOccurrences().add(targetSpan);
    targetPerson.getOccurrences().add(targetSeparateSpan);
    
    // set up relation between them
    EntityRelation testRelation = relationTestData.createTestRelation(originPerson, 
                                                                              targetPerson);
    
    // save ids for query
    docId = testDoc.getId();
    addId(originPerson.getId());
    addId(targetPerson.getId());
    
    DocumentPart testPart = new DocumentPart();
    testPart.getChapters().add(testChapter);
    testDoc.getContent().getParts().add(testPart);

    // persist test data
    EntityManager em = getModel().getEntityManager();
    em.getTransaction().begin();
    em.persist(chapterRange);
    em.persist(testChapter);
    em.persist(originSpan);
    em.persist(originSeparateSpan);
    em.persist(originPerson);
    em.persist(targetSpan);
    em.persist(targetSeparateSpan);
    em.persist(targetPerson);
    em.persist(testRelation);
    em.persist(testPart);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }

  /**
   * Add given id to comma separated String with entity ids to search for.
   *
   * @param idToAdd - the id which should be added to the search string
   */
  private void addId(String idToAdd) {
    if (idToAdd == null || idToAdd.isEmpty()) {
      throw new IllegalArgumentException("Id must not be empty or null!");
    }

    if (entityIds == null) {
      entityIds = idToAdd;
    } else {
      entityIds = entityIds + "," + idToAdd;
    }
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(RelationOccurrencesService.class);
  }

  @Override
  protected String getPath() {
    return "/documents/"+ docId +"/entities/relations/occurrences";
  }
  
  @Override
  protected WebTarget prepareWebTarget(int steps, double rangeStart, double rangeEnd) {
    return super.prepareWebTarget(steps, rangeStart, rangeEnd).queryParam("entityIds", entityIds);
  }

  @Override
  public void testGetExactOccurrences() {
    OccurrencesResponse actualResponse = target(getPath())
                                          .queryParam("rangeStart", DEFAULT_RANGE_START)
                                          .queryParam("rangeEnd", DEFAULT_RANGE_END)
                                          .queryParam("entityIds", entityIds)
                                          .request().get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    List<Occurrence> receivedOccurrences = actualResponse.getOccurrences();

    assertEquals(1, receivedOccurrences.size());

    // check content of response
    Occurrence receivedOccurrence = receivedOccurrences.get(0);
    assertEquals(OL_LENGTH, receivedOccurrence.getLength());
  }

  @Override
  public void testGetStepwiseOccurences() {
    OccurrencesResponse actualResponse = prepareWebTarget(DEFAULT_STEP_AMOUNT, 
                                                            DEFAULT_RANGE_START, 
                                                            DEFAULT_RANGE_END).request()
                                                            .get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    List<Occurrence> receivedOccurrences = actualResponse.getOccurrences();

    assertEquals(1, receivedOccurrences.size());

    // check content of response
    Occurrence receivedOccurrence = receivedOccurrences.get(0);
    assertTrue(OL_LENGTH <= receivedOccurrence.getLength());
  }

}
