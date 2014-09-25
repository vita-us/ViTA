package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
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
    Attribute attribute = createTestAttribute();

    // persist this Attribute
    em.persist(attribute);
    startNewTransaction();

    // read persisted attributes from database
    List<Attribute> attributes = readAttributesFromDb();

    // check whether data is correct
    assertEquals(1, attributes.size());
    Attribute readAttribute = attributes.get(0);
    checkData(readAttribute);
  }

  /**
   * Creates a new Attribute, setting its fields to test values and returns it.
   * 
   * @return test attribute
   */
  private Attribute createTestAttribute() {
    Attribute attribute = new Attribute();

    attribute.setType(TEST_ATTRIBUTE_NAME_TYPE);
    attribute.setContent(TEST_ATTRIBUTE_NAME_CONTENT);

    return attribute;
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

  /**
   * Checks whether the given Attribute is not <code>null</code> and includes the correct test data.
   * 
   * @param attributeToCheck
   */
  private void checkData(Attribute attributeToCheck) {
    assertNotNull(attributeToCheck);
    assertEquals(TEST_ATTRIBUTE_NAME_TYPE, attributeToCheck.getType());
    assertEquals(TEST_ATTRIBUTE_NAME_CONTENT, attributeToCheck.getContent());
  }

  /**
   * Check whether all Named Queries of Attribute are working correctly.
   */
  @Test
  public void testNamedQueries() {
    Attribute testAttribute = createTestAttribute();

    em.persist(testAttribute);
    startNewTransaction();

    // check Named Query finding all Attributes
    TypedQuery<Attribute> allQ =
        em.createNamedQuery("Attribute.findAllAttributes", Attribute.class);
    List<Attribute> allAttributes = allQ.getResultList();

    assertTrue(allAttributes.size() > 0);
    Attribute readAttribute = allAttributes.get(0);
    checkData(readAttribute);

    String id = readAttribute.getId();

    // check Named Query finding attributes by id
    TypedQuery<Attribute> idQ = em.createNamedQuery("Attribute.findAttributeById", Attribute.class);
    idQ.setParameter("attributeId", id);
    Attribute idAttribute = idQ.getSingleResult();

    checkData(idAttribute);

    // check Named Query finding attributes by type
    TypedQuery<Attribute> typeQ =
        em.createNamedQuery("Attribute.findAttributeByType", Attribute.class);
    typeQ.setParameter("attributeType", TEST_ATTRIBUTE_NAME_TYPE);
    List<Attribute> typeAttributes = typeQ.getResultList();

    assertTrue(typeAttributes.size() > 0);
    Attribute typeAttribute = typeAttributes.get(0);
    checkData(typeAttribute);
  }

  @Test
  public void testOcurrencesAreSorted() {
    Document doc = new Document();
    Chapter chapter = new Chapter();
    TextPosition pos1 = new TextPosition(chapter, 10);
    TextPosition pos2 = new TextPosition(chapter, 20);
    TextPosition pos3 = new TextPosition(chapter, 30);
    TextPosition pos4 = new TextPosition(chapter, 40);
    TextSpan span1 = new TextSpan(pos1, pos4);
    TextSpan span2 = new TextSpan(pos2, pos4);
    TextSpan span3 = new TextSpan(pos3, pos4);

    Attribute attribute = createTestAttribute();
    // Add the occurrences in an order that is neither the correct one, nor the reverse
    attribute.getOccurrences().add(span1);
    attribute.getOccurrences().add(span3);
    attribute.getOccurrences().add(span2);

    em.persist(doc);
    em.persist(chapter);
    em.persist(span1);
    em.persist(span3);
    em.persist(span2);
    em.persist(attribute);
    startNewTransaction();

    Attribute dbAttribute = em.createQuery("from Attribute", Attribute.class).getSingleResult();
    assertThat(dbAttribute.getOccurrences(),
        IsIterableContainingInOrder.contains(span1, span2, span3));
  }
}
