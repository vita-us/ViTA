package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;

/**
 * Performs tests whether instances of Attribute can be persisted correctly.
 */
public class AttributePersistenceTest extends AbstractPersistenceTest {
  
  // test data
  private static final AttributeType TEST_ATTRIBUTE_NAME_TYPE = AttributeType.NAME;
  private static final String TEST_ATTRIBUTE_NAME_CONTENT = "Bilbo Baggins";
 
  @Test
  public void testPersistOneAttribute() {
    // first set up an Attribute
    Attribute attribute = new Attribute();
    attribute.setType(TEST_ATTRIBUTE_NAME_TYPE);
    attribute.setContent(TEST_ATTRIBUTE_NAME_CONTENT);
    
    // persist this Attribute
    em.persist(attribute);
    startNewTransaction();
    
    // read persisted attributes from database
    List<Attribute> attributes = readAttributesFromDb();
    
    // check whether data is correct
    assertEquals(1, attributes.size());
    Attribute readAttribute = attributes.get(0);
    assertEquals(TEST_ATTRIBUTE_NAME_TYPE, readAttribute.getType());
    assertEquals(TEST_ATTRIBUTE_NAME_CONTENT, readAttribute.getContent());
  }
  
  /**
   * Reads Attributes from database and returns them.
   * 
   * @return list of attributes
   */
  private List<Attribute> readAttributesFromDb() {
    TypedQuery<Attribute> query = em.createQuery("from Attribute", Attribute.class);
    List<Attribute> result = query.getResultList();
    return result;
  }
}
