package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

  private List<List<Epubline>> formatedPartEpub3 = new ArrayList<List<Epubline>>();
  private List<List<List<Epubline>>> formatedPartsEpub3 = new ArrayList<List<List<Epubline>>>();

  private List<Epubline> linesToAnnotate = new ArrayList<Epubline>();
  private PartsAndChaptersReviser reviser = new PartsAndChaptersReviser();
  private Document document;
  private Element currentElement;
  private List<Element> editedElements = new ArrayList<Element>();
  private List<Epubline> epublines = new ArrayList<Epubline>();

  @Before
  public void setUp() throws IOException, URISyntaxException {
    
    fillEpubParts();
    fillEpublinesToAnnotate();
    fillSpanElements();
    Path testPath = Paths.get(getClass().getResource("text.html").toURI());
    Document document = Jsoup.parse(testPath.toFile(), "Cp437");
    reviser.addText(epublines, document.getAllElements().get(9), false, "");
    reviser.addDivTexts(epublines, document.getAllElements().get(4), new ArrayList<Element>(), "");
  }
  
  private void fillSpanElements(){
    document = new Document("http://example.com/");
    Element element = document.appendElement("p");
    for(int i = 0; i < 2; i++){
      element.appendElement("span");
    } 
    currentElement = element;
    editedElements.add(element);
  }

  private void fillEpubParts() throws IOException {
    
    List<List<Epubline>> partOne = new ArrayList<List<Epubline>>();
    List<Epubline> chapterOne = new ArrayList<Epubline>();
    chapterOne.add(new Epubline("", "Text", ""));
    chapterOne.add(new Epubline("", "Text", ""));
    partOne.add(chapterOne);
    
    formatedPartEpub2 = reviser.formatePartEpub2(partOne);
    formatedPartEpub3 = reviser.formatePartEpub3(partOne);
 
    List<List<Epubline>> partTwo = new ArrayList<List<Epubline>>();
    List<Epubline> chapterTwo = new ArrayList<Epubline>();
    chapterTwo.add(new Epubline("", "Text a", ""));
    partTwo.add(chapterTwo);
    
    List<List<List<Epubline>>> parts = new ArrayList<List<List<Epubline>>>();
    parts.add(partOne);
    parts.add(partTwo);
    
    formatedPartsEpub2 = reviser.formatePartsEpub2(parts);
    formatedPartsEpub3 = reviser.formatePartsEpub3(parts);
  }


  private void fillEpublinesToAnnotate() {

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
    assertEquals("Text", formatedPartEpub3.get(0).get(0).getEpubline());
    assertEquals("", formatedPartEpub3.get(0).get(1).getEpubline());
    assertEquals("Text", formatedPartEpub3.get(0).get(2).getEpubline());
    assertEquals("", formatedPartEpub3.get(0).get(3).getEpubline());
  }

  @Test
  public void formatedPartsChapterEpub3Content() {
    assertEquals("Text", formatedPartsEpub3.get(0).get(0).get(0).getEpubline());
    assertEquals("", formatedPartsEpub3.get(0).get(0).get(1).getEpubline());
    assertEquals("Text", formatedPartsEpub3.get(0).get(0).get(2).getEpubline());
    assertEquals("", formatedPartsEpub3.get(0).get(0).get(3).getEpubline());

    assertEquals("Text a", formatedPartsEpub3.get(1).get(0).get(0).getEpubline());
    assertEquals("", formatedPartsEpub3.get(1).get(0).get(1).getEpubline());

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

    assertEquals("Text a", formatedPartsEpub2.get(1).get(0).get(0).getEpubline());
    assertEquals("", formatedPartsEpub2.get(1).get(0).get(1).getEpubline());

  }

  @Test
  public void testAnnotation() {
    
    assertTrue(linesToAnnotate.get(0).getMode().matches("Heading"));
    assertTrue(linesToAnnotate.get(1).getMode().matches("Textstart"));
    assertTrue(linesToAnnotate.get(2).getMode().matches("Text"));
    assertTrue(linesToAnnotate.get(3).getMode().matches("Textend"));

  }
  
  @Test
  public void testExistenceOfElements(){
    assertTrue(reviser.existsSpan(document.select("span").first()));
    assertFalse(reviser.existsDiv(document.select("span").first()));
    assertFalse(reviser.allElementsNotSpans((document.select("span").first())));
    assertTrue(reviser.elementEdited(editedElements, currentElement));
  }
  
  @Test
  public void testContentAndSize(){
    assertEquals(3, epublines.size());
    assertTrue(epublines.get(0).getEpubline().startsWith("s that venerable and learned poet"));
    assertEquals("Paragraph 1", epublines.get(1).getEpubline());
    assertEquals("Paragraph 2", epublines.get(2).getEpubline());

  }
}
