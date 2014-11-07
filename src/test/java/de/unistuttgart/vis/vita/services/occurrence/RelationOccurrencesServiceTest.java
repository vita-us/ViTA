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
import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Performs test on RelationOccurrencesService, persisting persons with overlapping occurrences and
 * fetch their relation using REST.
 */
public class RelationOccurrencesServiceTest extends ServiceTest {

  private PersonTestData personTestData;
  private EntityRelationTestData relationTestData;
  
  private String docId;
  private String entityIds;

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
    TextPosition chapterStart = TextPosition.fromGlobalOffset(null, ChapterTestData.TEST_CHAPTER_RANGE_START);
    TextPosition chapterEnd = TextPosition.fromGlobalOffset(null, ChapterTestData.TEST_CHAPTER_RANGE_END);
    TextSpan chapterRange = new TextSpan(chapterStart, chapterEnd);
    testChapter.setRange(chapterRange);
    
    // set up first person
    Person originPerson = personTestData.createTestPerson(1);
    
    // non overlapping TextSpan
    TextPosition originSeparateStart = TextPosition.fromGlobalOffset(testChapter, ChapterTestData.TEST_CHAPTER_RANGE_START);
    TextPosition originSeparateEnd = TextPosition.fromGlobalOffset(testChapter, 101000);
    TextSpan originSeparateSpan = new TextSpan(originSeparateStart, originSeparateEnd);
    
    // overlapping TextSpan
    TextPosition originStart = TextPosition.fromGlobalOffset(testChapter, 105000);
    TextPosition originEnd = TextPosition.fromGlobalOffset(testChapter, 115000);
    TextSpan originSpan = new TextSpan(originStart, originEnd);
    
    originPerson.getOccurrences().add(originSpan);
    originPerson.getOccurrences().add(originSeparateSpan);
    
    // set up second person
    Person targetPerson = personTestData.createTestPerson(2);
    
    // non-overlapping TextSpan
    TextPosition targetSeparateStart = TextPosition.fromGlobalOffset(testChapter, 122000);
    TextPosition targetSeparateEnd  = TextPosition.fromGlobalOffset(testChapter, 123000);
    TextSpan targetSeparateSpan = new TextSpan(targetSeparateStart, targetSeparateEnd);
    
    // overlapping TextSpan
    TextPosition targetStart = TextPosition.fromGlobalOffset(testChapter, 110000);
    TextPosition targetEnd = TextPosition.fromGlobalOffset(testChapter, 120000);
    TextSpan targetSpan = new TextSpan(targetStart, targetEnd);
    
    targetPerson.getOccurrences().add(targetSpan);
    targetPerson.getOccurrences().add(targetSeparateSpan);
    
    // set up relation between them
    EntityRelation<Entity> testRelation = relationTestData.createTestRelation(originPerson, targetPerson);
    
    // save ids for query
    docId = testDoc.getId();
    addId(originPerson.getId());
    addId(targetPerson.getId());
    
    // persist test data
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    em.getTransaction().begin();
    em.persist(testDoc);
    em.persist(chapterRange);
    em.persist(testChapter);
    em.persist(originSpan);
    em.persist(originSeparateSpan);
    em.persist(originPerson);
    em.persist(targetSpan);
    em.persist(targetSeparateSpan);
    em.persist(targetPerson);
    em.persist(testRelation);
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
  
  /**
   * Tests whether the right occurrences can be caught using REST.
   */
  @Test
  public void testGetOccurrences() {
    String path = "/documents/"+ docId +"/entities/relations/occurrences";
    OccurrencesResponse actualResponse = target(path).queryParam("steps", 200)
                                                      .queryParam("rangeStart", 0.0)
                                                      .queryParam("rangeEnd", 1.0)
                                                      .queryParam("entityIds", entityIds)
                                                      .request().get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    List<Occurrence> receivedOccurrences = actualResponse.getOccurrences();
    
    assertEquals(2, receivedOccurrences.size());
    
    // check content of response
    for (Occurrence occ: receivedOccurrences) {
      assertTrue(105000 <= occ.getStart().getOffset());
      assertTrue(110000 >= occ.getStart().getOffset());
      assertTrue(115000 <= occ.getEnd().getOffset());
      assertTrue(120000 >= occ.getEnd().getOffset());
    }
  }

}
