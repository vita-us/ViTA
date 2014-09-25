package de.unistuttgart.vis.vita.persistence;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;

import de.unistuttgart.vis.vita.model.Model;

/**
 * A test framework that provides an entity manager
 */
public abstract class AbstractPersistenceTest {
  protected Model model;
  protected EntityManager em;

  /**
   * Creates Model and EntityManager and starts the first Transaction.
   */
  @Before
  public void setUp() {
    model = Model.createUnitTestModel();
    em = model.getEntityManager();
    em.getTransaction().begin();
  }
  
  /**
   * Commits the current transaction and starts a new one
   */
  protected void startNewTransaction() {
    em.getTransaction().commit();
    em.close();
    
    em = model.getEntityManager();
    em.getTransaction().begin();
  }

  /**
   * Finally closes the EntityManager.
   */
  @After
  public void tearDown() {
    em.close();
  }
}
