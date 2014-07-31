package de.unistuttgart.vis.vita.test.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the creation of TextSpans and the computation its lengths.
 * 
 * @author Marc Weise
 * @version 0.1 31.07.2014
 */
public class TextSpanTest {

  @Before
  public void setUp() throws Exception {
    // TODO not implemented yet!
  }

  @Test(expected=IllegalArgumentException.class)
  public void testEndMultipleChaptersBeforeStart() {
    fail("not implemented yet!"); 
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testEndOneChapterBeforeStart() {
    fail("not implemented yet!");
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testEndSameChapterBeforeStart() {
    fail("not implemented yet!");
  }
  
  @Test
  public void testStartEqualsEnd() {
    fail("not implemented yet!");
  }
  
  @Test
  public void testEndSameChapterAfterStart() {
    fail("not implemented yet!");
  }
  
  @Test
  public void testWholeChapter() {
    fail("not implemented yet!");
  }
  
  @Test
  public void testEndNextChapterAfterStart() {
    fail("not implemented yet!");  
  }
  
  @Test
  public void testEndMultipleChaptersAfterStart() {
    fail("not implemented yet!");
  }
  
  @Test
  public void testMultipleWholeChapters() {
    fail("not implemented yet!"); 
  }

}
