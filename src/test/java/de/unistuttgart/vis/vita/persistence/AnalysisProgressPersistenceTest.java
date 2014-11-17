package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.ProgressTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

public class AnalysisProgressPersistenceTest extends AbstractEntityBaseTest {
  
  private ProgressTestData testData;

  /**
   * Creates a test data instance.
   */
  @Override
  public void setUp() {
    super.setUp();
    
    this.testData = new ProgressTestData();
  }

  /**
   * Checks whether one AnalysisProgress can be persisted.
   */
  @Test
  public void testPersistOneProgress() {
    // set up test data
    AnalysisProgress testProgress = testData.createTestProgress();
    
    // persist progress and document
    em.persist(testProgress);
    startNewTransaction();
    
    // check data
    List<AnalysisProgress> readProgresses = readProgressFromDatabase();
    assertEquals(1, readProgresses.size());
    testData.checkData(readProgresses.get(0));
  }

  /**
   * Reads all analysis progresses from the database and returns them.
   * 
   * @return all analysis progresses from database
   */
  private List<AnalysisProgress> readProgressFromDatabase() {
    TypedQuery<AnalysisProgress> query = em.createQuery("from AnalysisProgress", 
                                                        AnalysisProgress.class);
    List<AnalysisProgress> progresses = query.getResultList();
    return progresses;
  }
  
  /**
   * Checks whether all Named Queries of AnalysisProgress are working correctly.
   */
  @Test
  public void testNamedQuery() {
    // set up test data
    AnalysisProgress testProgress = testData.createTestProgress();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.setProgress(testProgress);
    
    // save ids for queries
    String progressId = testProgress.getId();
    String documentId = testDoc.getId();
    
    // persist test data
    em.persist(testProgress);
    em.persist(testDoc);
    
    // check Named Query finding all progresses
    TypedQuery<AnalysisProgress> allQ = em.createNamedQuery("AnalysisProgress.findAllProgresses",
                                                            AnalysisProgress.class);
    List<AnalysisProgress> allProgresses = allQ.getResultList();
    assertTrue(0 < allProgresses.size());
    testData.checkData(allProgresses.get(0));
    
    // check Named Query finding progress by id
    TypedQuery<AnalysisProgress> idQ = em.createNamedQuery("AnalysisProgress.findProgressById", 
                                                            AnalysisProgress.class);
    idQ.setParameter("progressId", progressId);
    testData.checkData(idQ.getSingleResult());

    // check Named Query finding progress by document id
    TypedQuery<AnalysisProgress> docQ =
        em.createNamedQuery("AnalysisProgress.findProgressByDocumentId", AnalysisProgress.class);
    docQ.setParameter("documentId", documentId);
    testData.checkData(docQ.getSingleResult());
  }

}
