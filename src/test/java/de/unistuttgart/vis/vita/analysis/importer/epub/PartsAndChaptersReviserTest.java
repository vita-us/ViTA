package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.Epubline;
import de.unistuttgart.vis.vita.importer.epub.PartsAndChaptersReviser;

/**
 * JUnit test on PartsAndChapterReviser
 * 
 *
 */
public class PartsAndChaptersReviserTest {

  private List<List<Epubline>> formatedPartEpub2 = new ArrayList<List<Epubline>>();
  private List<List<List<Epubline>>> formatedPartsEpub2 = new ArrayList<List<List<Epubline>>>();

  private List<List<String>> formatedPartEpub3 = new ArrayList<List<String>>();
  private List<List<List<String>>> formatedPartsEpub3 = new ArrayList<List<List<String>>>();

  private List<Epubline> linesToAnnotate = new ArrayList<Epubline>();
  private PartsAndChaptersReviser reviser = new PartsAndChaptersReviser();

  @Before
  public void setUp() throws IOException {
    fillEpub2Parts();
    fillEpub3Parts();
    fillEpublinesToAnnotate();
  }

  private void fillEpub2Parts() {
    List<List<String>> partOne = new ArrayList<List<String>>();
    List<String> chapterOne = new ArrayList<String>();
    chapterOne.add("Text");
    chapterOne.add("Text");
    partOne.add(chapterOne);
    formatedPartEpub3 = reviser.formatePartEpub3(partOne);


    List<List<String>> partTwo = new ArrayList<List<String>>();
    List<String> chapterTwo = new ArrayList<String>();
    chapterTwo.add("Text a");
    partTwo.add(chapterTwo);
    List<List<List<String>>> parts = new ArrayList<List<List<String>>>();
    parts.add(partOne);
    parts.add(partTwo);
    formatedPartsEpub3 = reviser.formatePartsEpub3(parts);
  }

  private void fillEpub3Parts() throws IOException {
    List<List<Epubline>> partOne = new ArrayList<List<Epubline>>();
    List<Epubline> chapterOne = new ArrayList<Epubline>();
    chapterOne.add(new Epubline("", "Text", ""));
    chapterOne.add(new Epubline("", "Text", ""));
    partOne.add(chapterOne);
    formatedPartEpub2 = reviser.formatePartEpub2(partOne);


    List<List<Epubline>> partTwo = new ArrayList<List<Epubline>>();
    List<Epubline> chapterTwo = new ArrayList<Epubline>();
    chapterTwo.add(new Epubline("", "Text b", ""));
    partTwo.add(chapterTwo);
    List<List<List<Epubline>>> parts = new ArrayList<List<List<Epubline>>>();
    parts.add(partOne);
    parts.add(partTwo);
    formatedPartsEpub2 = reviser.formatePartsEpub2(parts);
  }

  public void fillEpublinesToAnnotate() {

    linesToAnnotate.add(new Epubline("Heading", "Chapter I.", ""));
    linesToAnnotate.add(new Epubline("Text", "Text a", ""));
    linesToAnnotate.add(new Epubline("Text", "Text b", ""));
    linesToAnnotate.add(new Epubline("Text", "Text c", ""));
     
    // annotate the lines with correct mode("Heading", "TextStart" and "TextEnd")
    reviser.annotateTextStartAndEndOfEpublines(linesToAnnotate);
  }

  @Test
  public void formatedPartChapterEpub3Size() {
    assertEquals(4, formatedPartEpub3.get(0).size());
  }

  @Test
  public void formatedPartsChapterEpub3Size() {
    assertEquals(4, formatedPartsEpub3.get(0).get(0).size());
    assertEquals(2, formatedPartsEpub3.get(1).get(0).size());

  }

  @Test
  public void formatedPartChapterEpub3Content() {
    assertEquals("Text", formatedPartEpub3.get(0).get(0));
    assertEquals("", formatedPartEpub3.get(0).get(1));
    assertEquals("Text", formatedPartEpub3.get(0).get(2));
    assertEquals("", formatedPartEpub3.get(0).get(3));
  }

  @Test
  public void formatedPartsChapterEpub3Content() {
    assertEquals("Text", formatedPartsEpub3.get(0).get(0).get(0));
    assertEquals("", formatedPartsEpub3.get(0).get(0).get(1));
    assertEquals("Text", formatedPartsEpub3.get(0).get(0).get(2));
    assertEquals("", formatedPartsEpub3.get(0).get(0).get(3));

    assertEquals("Text a", formatedPartsEpub3.get(1).get(0).get(0));
    assertEquals("", formatedPartsEpub3.get(1).get(0).get(1));

  }

  @Test
  public void formatedPartChapterEpub2Content() {
    assertEquals("Text", formatedPartEpub2.get(0).get(0).getEpubline());
    assertEquals("", formatedPartEpub2.get(0).get(1).getEpubline());
    assertEquals("Text", formatedPartEpub2.get(0).get(2).getEpubline());
    assertEquals("", formatedPartEpub2.get(0).get(3).getEpubline());
  }

  @Test
  public void formatedPartsChapterEpub2Content() {
    assertEquals("Text", formatedPartsEpub2.get(0).get(0).get(0).getEpubline());
    assertEquals("", formatedPartsEpub2.get(0).get(0).get(1).getEpubline());
    assertEquals("Text", formatedPartsEpub2.get(0).get(0).get(2).getEpubline());
    assertEquals("", formatedPartsEpub2.get(0).get(0).get(3).getEpubline());

    assertEquals("Text b", formatedPartsEpub2.get(1).get(0).get(0).getEpubline());
    assertEquals("", formatedPartsEpub2.get(1).get(0).get(1).getEpubline());

  }

  @Test
  public void testAnnotation() {
    
    assertTrue(linesToAnnotate.get(0).getMode().matches("Heading"));
    assertTrue(linesToAnnotate.get(1).getMode().matches("Textstart"));
    assertTrue(linesToAnnotate.get(2).getMode().matches("Text"));
    assertTrue(linesToAnnotate.get(3).getMode().matches("Textend"));

  }
}
