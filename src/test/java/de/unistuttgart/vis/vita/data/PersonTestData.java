package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Holds test data for persons and methods to create test persons and check whether given data
 * matches the test data.
 */
public class PersonTestData {
  
  private static final int DEFAULT_TEST_PERSON_NUMBER = 1;
  
  public static final int TEST_PERSON_1_RANKING_VALUE = 1;
  public static final String TEST_PERSON_1_NAME = "Frodo Baggins";

  public static final int TEST_PERSON_2_RANKING_VALUE = 4;
  public static final String TEST_PERSON_2_NAME = "Gandalf";
  
  /**
   * Creates the default test Person and returns it.
   * 
   * @return default test Person
   */
  public Person createTestPerson() {
    return createTestPerson(DEFAULT_TEST_PERSON_NUMBER);
  }
  
  /**
   * Creates the test Person with the given number, setting attributes to test values and returns 
   * it.
   * 
   * @param number - the number of the test Person
   * @return test Person
   */
  public Person createTestPerson(int number) {
    Person p = new Person();

    if (number == 1) {
      p.setDisplayName(TEST_PERSON_1_NAME);
      p.setRankingValue(TEST_PERSON_1_RANKING_VALUE);
    } else if (number == 2) {
      p.setDisplayName(TEST_PERSON_2_NAME);
      p.setRankingValue(TEST_PERSON_2_RANKING_VALUE);
    } else {
      throw new IllegalArgumentException("Unknown test person number!");
    }

    return p;
  }
  
  /**
   * Checks whether the given Person is the default test Person.
   * 
   * @param PersonToCheck - the Person to be checked
   */
  public void checkData(Person personToCheck) {
    checkData(personToCheck, DEFAULT_TEST_PERSON_NUMBER);
  }
  
  /**
   * Checks whether the given Person is not null and includes the correct test data.
   * 
   * @param personToCheck - the Person to be checked
   * @param number - the test Person number the given Person should be tested against
   */
  public void checkData(Person personToCheck, int number) {
    assertNotNull(personToCheck);
    if (number == 1) {
      assertEquals(TEST_PERSON_1_NAME, personToCheck.getDisplayName());
      assertEquals(TEST_PERSON_1_RANKING_VALUE, personToCheck.getRankingValue());
    } else if (number == 2) {
      assertEquals(TEST_PERSON_2_NAME, personToCheck.getDisplayName());
      assertEquals(TEST_PERSON_2_RANKING_VALUE, personToCheck.getRankingValue());
    }
  }

}
