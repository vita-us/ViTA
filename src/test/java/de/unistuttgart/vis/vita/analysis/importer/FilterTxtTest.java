package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.Filter;
import de.unistuttgart.vis.vita.importer.txt.Line;
import de.unistuttgart.vis.vita.importer.txt.TextFileImporter;

/**
 * JUnit test on Filter
 * 
 *
 */
public class FilterTxtTest {

  private List<Line> filteredList = new ArrayList<Line>();

  /**
   * Sets the filteredList after the imported List is filtered
   * @throws URISyntaxException
   * @throws IllegalArgumentException
   * @throws FileNotFoundException
   * @throws IllegalStateException
   * @throws SecurityException
   */
  @Before
  public void setUp() throws URISyntaxException, IllegalArgumentException, FileNotFoundException,
      IllegalStateException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("text1.txt").toURI());
    TextFileImporter textFileImporter = new TextFileImporter(testPath);
    Filter filter = new Filter(textFileImporter.getLines());
    filteredList = filter.filterEbookText();
  }

  @Test
  public void testFilterEbookText() {
    assertFalse(filteredList.get(11).getText().equals("[Ebook The Lord of the Rings]"));
    assertEquals(filteredList.get(12).getText(), "The text of the first chapter.");
  }
}
