package de.unistuttgart.vis.vita.analysis.importer.epub;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.extractors.Epub2IdsAndTitlesExtractor;
import de.unistuttgart.vis.vita.importer.epub.input.EpubFileImporter;

/**
 * JUnit test on Epub2IdsAndExtractor
 * 
 *
 */
public class Epub2IdsAndExtractorTest {

  private Epub2IdsAndTitlesExtractor epub2Extractor;
  private List<String> partsTitles = new ArrayList<String>();
  private boolean existsPart = false;
  private List<List<String>> partsChaptersIds = new ArrayList<List<String>>();
  private List<String> tableOfContentsIds = new ArrayList<String>();
  
  // the table of contents contains 18 ids, so therefore the array is created
  private String[] stringNumbers = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
      "10", "11", "12", "13", "14", "15", "16", "17", "18"};

  @Before
  public void setUp() throws URISyntaxException, IOException {
    
    Path testPath = Paths.get(getClass().getResource("pg244.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    epub2Extractor =
        new Epub2IdsAndTitlesExtractor(epubFileImporter.getEbook().getContents(), epubFileImporter
            .getEbook().getNcxResource());
    partsTitles = epub2Extractor.getPartsTitles();
    existsPart = epub2Extractor.existsPart();
    partsChaptersIds = epub2Extractor.getPartsChaptersIds();
    tableOfContentsIds = epub2Extractor.getTocIds();
  }

  @Test
  public void testPartsTitles() {
    assertEquals(2, partsTitles.size());
    assertEquals("PART I.", partsTitles.get(0));
    assertEquals("PART II. The Country of the Saints.", partsTitles.get(1));
  }

  @Test
  public void testExistenceOfPart() {
    assertEquals(true, existsPart);
  }

  @Test
  public void testPartsChaptersIds() {

    // for PART I. regarding chapters
    assertTrue(partsChaptersIds.get(0).size() == 7);
    // first chapter
    assertTrue(partsChaptersIds.get(0).get(0).matches("pgepubid00003"));
    // last chapter
    assertTrue(partsChaptersIds.get(0).get(partsChaptersIds.get(0).size() - 1)
        .matches("pgepubid00009"));

    // for PART II. The Country of the Saints. regarding chapters
    assertTrue(partsChaptersIds.get(1).size() == 8);
    // first chapter
    assertTrue(partsChaptersIds.get(1).get(0).matches("pgepubid00011"));
    // last chapter
    assertTrue(partsChaptersIds.get(1).get(partsChaptersIds.get(0).size() - 1)
        .matches("pgepubid00017"));
  }

  @Test
  public void testTocIds() {
    assertEquals(19, tableOfContentsIds.size());

    for (int i = 0; i < 19; i++) {
      assertTrue(tableOfContentsIds.get(i).matches("pgepubid000" + stringNumbers[i]));
    }
  }
}
