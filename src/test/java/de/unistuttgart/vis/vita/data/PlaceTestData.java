package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Holds test data for places and methods to create test places and check whether given data
 * matches the test data.
 */
public class PlaceTestData {
  
  private static final int DEFAULT_TEST_PLACE_NUMBER = 1;
  
  public static final int TEST_PLACE_1_RANKING_VALUE = 3;
  public static final String TEST_PLACE_1_NAME = "Rivendell";
  
  public static final int TEST_PLACE_2_RANKING_VALUE = 5;
  public static final String TEST_PLACE_2_NAME = "Moria";
  
  /**
   * Creates the default test place and returns it.
   * 
   * @return default test Place
   */
  public Place createTestPlace() {
    return createTestPlace(DEFAULT_TEST_PLACE_NUMBER);
  }
 
  /**
   * Creates the test Place with the given number, setting attributes to test values and returns 
   * it.
   *
   * @param number - the number of the test Place
   * @return test Place
   */
  public Place createTestPlace(int number) {
    Place testPlace = new Place();
    
    if (number == 1) {
      testPlace.setDisplayName(TEST_PLACE_1_NAME);
      testPlace.setRankingValue(TEST_PLACE_1_RANKING_VALUE);
    } else if(number == 2) {
      testPlace.setDisplayName(TEST_PLACE_2_NAME);
      testPlace.setRankingValue(TEST_PLACE_2_RANKING_VALUE);
    } else {
      throw new IllegalArgumentException("Unknown test place number!");
    }

    return testPlace;
  }
  
  /**
   * Checks whether the given Place is the default test place.
   * 
   * @param placeToCheck - the Place to be checked
   */
  public void checkData(Place placeToCheck) {
    checkData(placeToCheck, DEFAULT_TEST_PLACE_NUMBER);
  }

  /**
   * Checks whether the given place is not null and includes the correct test data.
   * 
   * @param readPlace - the place which should be checked
   *  @param number - the test place number the given Place should be tested against
   */
  public void checkData(Place readPlace, int number) {
    assertNotNull(readPlace);
    if (number == 1) {
      assertEquals(TEST_PLACE_1_NAME, readPlace.getDisplayName());
      assertEquals(TEST_PLACE_1_RANKING_VALUE, readPlace.getRankingValue());
    } else if(number == 2) {
      assertEquals(TEST_PLACE_2_NAME, readPlace.getDisplayName());
      assertEquals(TEST_PLACE_2_RANKING_VALUE, readPlace.getRankingValue()); 
    }
  }

}
