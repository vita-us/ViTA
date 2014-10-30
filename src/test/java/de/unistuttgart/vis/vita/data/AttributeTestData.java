package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.services.responses.BasicAttribute;

/**
 * Holds test data for Attributes and methods to create test Attributes and check whether given data
 * matches the test data.
 */
public class AttributeTestData {
  
  private static final int DEFAULT_ATTRIBUTE_NUMBER = 1;
  
  public static final AttributeType TEST_ATTRIBUTE_1_TYPE = AttributeType.NAME;
  public static final String TEST_ATTRIBUTE_1_CONTENT = "Bilbo Baggins";
  
  public static final AttributeType TEST_ATTRIBUTE_2_TYPE = AttributeType.AGE;
  public static final String TEST_ATTRIBUTE_2_CONTENT = "111";
  
  /**
   * Creates the default test Attribute.
   * 
   * @return default test Attribute
   */
  public Attribute createTestAttribute() {
    return createTestAttribute(DEFAULT_ATTRIBUTE_NUMBER);
  }
  
  /**
   * Creates a new Attribute, setting its fields to test values and returns it.
   * 
   * @return test attribute
   */
  public Attribute createTestAttribute(int number) {
    Attribute attribute = new Attribute();

    if (number == 1) {
      attribute.setType(TEST_ATTRIBUTE_1_TYPE);
      attribute.setContent(TEST_ATTRIBUTE_1_CONTENT);
    } else if (number == 2) {
      attribute.setType(TEST_ATTRIBUTE_2_TYPE);
      attribute.setContent(TEST_ATTRIBUTE_2_CONTENT);
    } else {
      throw new IllegalArgumentException("Unknown attribute number!");
    }

    return attribute;
  }
  
  /**
   * Checks whether given Attribute is the default test Attribute.
   * 
   * @param attributeToCheck - the Attribute to be checked
   */
  public void checkData(Attribute attributeToCheck) {
    checkData(attributeToCheck, DEFAULT_ATTRIBUTE_NUMBER);
  }
  
  /**
   * Checks whether the given Attribute is not <code>null</code> and includes the correct test data.
   * 
   * @param attributeToCheck - the Attribute to be checked
   * @param number - the number of the test Attribute this should match
   */
  public void checkData(Attribute attributeToCheck, int number) {
    assertNotNull(attributeToCheck);
    if (number == 1) {
      assertEquals(TEST_ATTRIBUTE_1_TYPE, attributeToCheck.getType());
      assertEquals(TEST_ATTRIBUTE_1_CONTENT, attributeToCheck.getContent());
    } else if (number == 2) {
      assertEquals(TEST_ATTRIBUTE_2_TYPE, attributeToCheck.getType());
      assertEquals(TEST_ATTRIBUTE_2_CONTENT, attributeToCheck.getContent());
    } else {
      throw new IllegalArgumentException("Unknown attribute number!");
    }
  }
  
  /**
   * Checks whether given BasicAttribute represents the default test Attribute.
   * 
   * @param attributeToCheck - the Attribute to be checked
   */
  public void checkData(BasicAttribute attributeToCheck) {
    checkData(attributeToCheck, DEFAULT_ATTRIBUTE_NUMBER);
  }
  
  /**
   * Checks whether the given BasicAttribute is not null and includes the correct test data.
   * 
   * @param attributeToCheck - the BasicAttribute to be checked
   * @param number - the number of the test Attribute this should match
   */
  public void checkData(BasicAttribute attributeToCheck, int number) {
    assertNotNull(attributeToCheck);
    if (number == 1) {
      assertEquals(TEST_ATTRIBUTE_1_TYPE.toString(), attributeToCheck.getType());
      assertEquals(TEST_ATTRIBUTE_1_CONTENT, attributeToCheck.getContent());
    } else if (number == 2) {
      assertEquals(TEST_ATTRIBUTE_2_TYPE.toString(), attributeToCheck.getType());
      assertEquals(TEST_ATTRIBUTE_2_CONTENT, attributeToCheck.getContent());
    } else {
      throw new IllegalArgumentException("Unknown attribute number!");
    }
  }

}
