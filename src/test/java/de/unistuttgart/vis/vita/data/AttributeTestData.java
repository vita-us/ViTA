package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;

/**
 * Holds test data for Attributes and methods to create test Attributes and check whether given data
 * matches the test data.
 */
public class AttributeTestData {
  
  public static final AttributeType TEST_ATTRIBUTE_NAME_TYPE = AttributeType.NAME;
  public static final String TEST_ATTRIBUTE_NAME_CONTENT = "Bilbo Baggins";
  
  /**
   * Creates a new Attribute, setting its fields to test values and returns it.
   * 
   * @return test attribute
   */
  public Attribute createTestAttribute() {
    Attribute attribute = new Attribute();

    attribute.setType(TEST_ATTRIBUTE_NAME_TYPE);
    attribute.setContent(TEST_ATTRIBUTE_NAME_CONTENT);

    return attribute;
  }
  
  /**
   * Checks whether the given Attribute is not <code>null</code> and includes the correct test data.
   * 
   * @param attributeToCheck
   */
  public void checkData(Attribute attributeToCheck) {
    assertNotNull(attributeToCheck);
    assertEquals(TEST_ATTRIBUTE_NAME_TYPE, attributeToCheck.getType());
    assertEquals(TEST_ATTRIBUTE_NAME_CONTENT, attributeToCheck.getContent());
  }

}
