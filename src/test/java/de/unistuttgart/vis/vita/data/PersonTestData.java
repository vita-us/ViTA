package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.entity.Person;

public class PersonTestData {
  
  public static final int TEST_PERSON_RANKING_VALUE = 1;
  public static final String TEST_PERSON_NAME = "Frodo Baggins";
  
  /**
   * Creates a new Person, setting attributes to test values and returns it.
   * 
   * @return test person
   */
  public Person createTestPerson() {
    Person p = new Person();

    p.setDisplayName(TEST_PERSON_NAME);
    p.setRankingValue(TEST_PERSON_RANKING_VALUE);

    return p;
  }
  
  /**
   * Checks whether the given person is not <code>null</code> and includes the correct test data.
   * 
   * @param personToCheck - the person to be checked
   */
  public void checkData(Person personToCheck) {
    assertNotNull(personToCheck);
    assertEquals(TEST_PERSON_NAME, personToCheck.getDisplayName());
    assertEquals(TEST_PERSON_RANKING_VALUE, personToCheck.getRankingValue());
  }

}
