package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.modules.TextImportModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

public class TextImportModuleTest {
  @Test(expected = UnsupportedEncodingException.class)
  public void testImportEmptyFile() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {

    Path testPath = Paths.get(getClass().getResource("Empty.txt").toURI());
    new TextImportModule(testPath).execute(null, null);
  }

  @Test(expected = UnsupportedEncodingException.class)
  public void testImportSpecialSignsFile() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("specialSignsInOneLine.txt").toURI());
    new TextImportModule(testPath).execute(null, null);
  }

  @Test(expected = FileNotFoundException.class)
  public void testImportNotExistingFile() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get("blablabla.txt");
    new TextImportModule(testPath).execute(null, null);
  }

  @Test(expected = InvalidPathException.class)
  public void testImportNotATxt() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("NotATxt").toURI());
    new TextImportModule(testPath).execute(null, null);
  }

  @Test(expected = InvalidPathException.class)
  public void testImportNotNamedFile() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(".txt");
    new TextImportModule(testPath).execute(null, null);
  }

  @Test(expected = FileNotFoundException.class)
  public void testImportFolder() throws URISyntaxException, UnsupportedEncodingException,
      InvalidPathException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("testFolder.txt").toURI());
    new TextImportModule(testPath).execute(null, null);
  }

  @Test
  public void testWithAutomatedDetection() throws URISyntaxException, IOException {
    // The resource file is located in src/test/resources/de/unistuttgart/vis/vita/analysis/importer
    Path testPath = Paths.get(getClass().getResource("ModuleTestBook1.txt").toURI());
    TextImportModule module = new TextImportModule(testPath);
    ImportResult result = module.execute(null, null);

    DocumentMetadata metadata = result.getMetadata();
    assertEquals("Matilde Serao", metadata.getAuthor());
    assertEquals("The conquest of Rome", metadata.getTitle());
    assertEquals(2014, metadata.getPublishYear().intValue());
    assertEquals("", metadata.getEdition());
    assertEquals("", metadata.getGenre());
    assertEquals("", metadata.getPublisher());

    List<DocumentPart> documentParts = result.getParts();
    assertTrue(documentParts.size() == 1);
    DocumentPart documentPart = documentParts.get(0);

    List<Chapter> chapters = documentPart.getChapters();
    assertEquals(17,chapters.size());

    testPart1Chapter1(chapters.get(0).getTitle(), chapters.get(0).getText());
    testPart1Chapter2(chapters.get(1).getTitle(), chapters.get(1).getText());
    testPart1Chapter3(chapters.get(2).getTitle(), chapters.get(2).getText());
    testPart1Chapter4(chapters.get(3).getTitle(), chapters.get(3).getText());
    testPart1Chapter5(chapters.get(4).getTitle(), chapters.get(4).getText());

    testPart2Chapter1(chapters.get(5).getTitle(), chapters.get(5).getText());
    testPart2Chapter2(chapters.get(6).getTitle(), chapters.get(6).getText());
    testPart2Chapter3(chapters.get(7).getTitle(), chapters.get(7).getText());
    testPart2Chapter4(chapters.get(8).getTitle(), chapters.get(8).getText());
    testPart2Chapter5(chapters.get(9).getTitle(), chapters.get(9).getText());

    testPart3Chapter1(chapters.get(10).getTitle(), chapters.get(10).getText());
    testPart3Chapter2(chapters.get(11).getTitle(), chapters.get(11).getText());
    testPart3Chapter3(chapters.get(12).getTitle(), chapters.get(12).getText());
    testPart3Chapter4(chapters.get(13).getTitle(), chapters.get(13).getText());
    testPart3Chapter5(chapters.get(14).getTitle(), chapters.get(14).getText());
    testPart3Chapter6(chapters.get(15).getTitle(), chapters.get(15).getText());
    testPart3Chapter7(chapters.get(16).getTitle(), chapters.get(16).getText());
  }

  @Test
  public void testWithAutomatedDetectionAndOneLinePassages() throws URISyntaxException, IOException {
    // The resource file is located in src/test/resources/de/unistuttgart/vis/vita/analysis/importer
    Path testPath = Paths.get(getClass().getResource("ModuleTestBook2.txt").toURI());
    TextImportModule module = new TextImportModule(testPath);
    ImportResult result = module.execute(null, null);

    DocumentMetadata metadata = result.getMetadata();
    assertEquals("John Buchan", metadata.getAuthor());
    assertEquals("The Thirty-nine Steps", metadata.getTitle());
    assertEquals(1996, metadata.getPublishYear().intValue());
    assertEquals("", metadata.getEdition());
    assertEquals("", metadata.getGenre());
    assertEquals("", metadata.getPublisher());

    List<DocumentPart> documentParts = result.getParts();
    assertTrue(documentParts.size() == 1);
    DocumentPart documentPart = documentParts.get(0);

    List<Chapter> chapters = documentPart.getChapters();
    assertTrue(chapters.size() == 4);

    testAdvancedChapter1(chapters.get(0).getTitle(), chapters.get(0).getText());
    testAdvancedChapter2(chapters.get(1).getTitle(), chapters.get(1).getText());
    testAdvancedChapter3(chapters.get(2).getTitle(), chapters.get(2).getText());
    testAdvancedChapter4(chapters.get(3).getTitle(), chapters.get(3).getText());

  }

  @Test
  public void testWithoutAutomatedDetection() throws URISyntaxException, IOException {
    // The resource file is located in src/test/resources/de/unistuttgart/vis/vita/analysis/importer
    Path testPath = Paths.get(getClass().getResource("ModuleTestBook1.txt").toURI());
    TextImportModule module = new TextImportModule(testPath, false);
    ImportResult result = module.execute(null, null);

    DocumentMetadata metadata = result.getMetadata();
    assertEquals("Matilde Serao", metadata.getAuthor());
    assertEquals("The conquest of Rome", metadata.getTitle());
    assertEquals(2014, metadata.getPublishYear().intValue());
    assertEquals("", metadata.getEdition());
    assertEquals("", metadata.getGenre());
    assertEquals("", metadata.getPublisher());

    List<DocumentPart> documentParts = result.getParts();
    assertTrue(documentParts.size() == 1);
    DocumentPart documentPart = documentParts.get(0);

    List<Chapter> chapters = documentPart.getChapters();
    assertTrue(chapters.size() == 1);

    String text = chapters.get(0).getText();
    String heading = chapters.get(0).getTitle();

    assertEquals("Chapter 1", heading);
    assertTrue(text.startsWith("Produced by sp1nd"));
    assertTrue(text
.endsWith("The conquest of Rome, by Matilde Serao"));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }
  
  @Test
  public void testChapterRangeAndLength() throws InvalidPathException, FileNotFoundException,
      UnsupportedEncodingException, SecurityException, URISyntaxException {
    Path testPath = Paths.get(getClass().getResource("ModuleTestBook2.txt").toURI());
    TextImportModule module = new TextImportModule(testPath);
    ImportResult result = module.execute(null, null);
    
    Chapter chapter = result.getParts().get(0).getChapters().get(1);
    assertThat(chapter.getRange().getStart().getOffset(), is(1052));
    assertThat(chapter.getRange().getEnd().getOffset(), is(1889));
    assertThat(chapter.getLength(), is(837));
  }

  private void testPart1Chapter1(String heading, String text) {
    assertEquals("PART I\n\n\n\n\nCHAPTER I", heading);
    assertTrue(text.startsWith("The train stopped."));
    assertTrue(text
        .endsWith("The Honourable Francesco Sangiorgio was exceedingly pale, and he was cold--in his heart."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart1Chapter2(String heading, String text) {
    assertEquals("CHAPTER II", heading);
    assertTrue(text.startsWith("That day he must resist and not go to Montecitorio."));
    assertTrue(text.endsWith("His heart was over there."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart1Chapter3(String heading, String text) {
    assertEquals("CHAPTER III", heading);
    assertTrue(text.startsWith("In the glove-shop of the Via di Pietra there was a great bustle."));
    assertTrue(text.endsWith("by the Honourable Francesco Sangiorgio."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart1Chapter4(String heading, String text) {
    assertEquals("CHAPTER IV", heading);
    assertTrue(text.startsWith("The door marked No. 50 in the Via Angelo Custode was situated two"));
    assertTrue(text
        .endsWith(", he took the lodgings in the Via Angelo Custode, where there were no women."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart1Chapter5(String heading, String text) {
    assertEquals("CHAPTER V", heading);
    assertTrue(text.startsWith("Another walk from the corner of the Piazza Sciarra"));
    assertTrue(text.endsWith("'I will!' said Francesco Sangiorgio."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart2Chapter1(String heading, String text) {
    assertEquals("PART II\n\n\n\n\nCHAPTER I", heading);
    assertTrue(text.startsWith("The Minister had been speaking for an hour."));
    assertTrue(text.endsWith("invaded by an unfamiliar sensation of sadness."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart2Chapter2(String heading, String text) {
    assertEquals("CHAPTER II", heading);
    assertTrue(text.startsWith("It was the last public ball on the last Tuesday of the carnival,"));
    assertTrue(text.endsWith("Sangiorgio was elected a member of the Budget Committee."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart2Chapter3(String heading, String text) {
    assertEquals("CHAPTER III", heading);
    assertTrue(text.startsWith("Mild, genteel applause, coming from small, female,"));
    assertTrue(text.endsWith("Tullio Martello's 'Storia dell' Internazionale.'"));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart2Chapter4(String heading, String text) {
    assertEquals("CHAPTER IV", heading);
    assertTrue(text.startsWith("When the Honourable Sangiorgio entered the "));
    assertTrue(text.endsWith("On the ground he had remembered nothing but that counsel."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart2Chapter5(String heading, String text) {
    assertEquals("CHAPTER V", heading);
    assertTrue(text.startsWith("The case had come up expectedly two days"));
    assertTrue(text.endsWith(", has persistently refused, and has left for the Basilicata.'"));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart3Chapter1(String heading, String text) {
    assertEquals("PART III\n\n\n\n\nCHAPTER I", heading);
    assertTrue(text.startsWith("A soft breath of lamentation; a dim light,"));
    assertTrue(text.endsWith("stripped stems of the dead roses to her side."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart3Chapter2(String heading, String text) {
    assertEquals("CHAPTER II", heading);
    assertTrue(text.startsWith("From his Centrist bench, where he was pretending to write letters"));
    assertTrue(text.endsWith("and the young, chaste, placid, and serene being, sat a third--Love."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart3Chapter3(String heading, String text) {
    assertEquals("CHAPTER III", heading);
    assertTrue(text.startsWith("Scarcely had Francesco Sangiorgio emerged"));
    assertTrue(text.endsWith("unconsciously pushed about, shouldered, jostled."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart3Chapter4(String heading, String text) {
    assertEquals("CHAPTER IV", heading);
    assertTrue(text.startsWith("Three times they had met on the great"));
    assertTrue(text.endsWith("'I will go--I will go wherever you please.'"));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart3Chapter5(String heading, String text) {
    assertEquals("CHAPTER V", heading);
    assertTrue(text.startsWith("When he returned that night to his modest "));
    assertTrue(text.endsWith("that dear head had rested, and wept long and bitterly."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart3Chapter6(String heading, String text) {
    assertEquals("CHAPTER VI", heading);
    assertTrue(text.startsWith("Angelica only kept her appointments with "));
    assertTrue(text.endsWith("and went away, without looking back."));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testPart3Chapter7(String heading, String text) {
    assertEquals("CHAPTER VII", heading);
    assertTrue(text.startsWith("Sangiorgio was idling under the porch at Montecitorio, "));
    assertTrue(text.endsWith("by Matilde Serao"));
    assertFalse(text.contains("*       *       *       *       *"));
    assertFalse(text.contains("\n\n\n"));
  }

  private void testAdvancedChapter1(String heading, String text) {
    assertEquals("CHAPTER ONE\n\nThe Man Who Died", heading);
    assertTrue(text.startsWith("I returned from the City about three o'clock"));
    assertTrue(text.endsWith("I counted on stopping there for the rest of my days."));
  }

  private void testAdvancedChapter2(String heading, String text) {
    assertEquals("CHAPTER TWO\n\nThe Milkman Sets Out on his Travels", heading);
    assertTrue(text.startsWith("But from the first I was disappointed with it.  "));
    assertTrue(text.endsWith("for I was the best bored man in the United Kingdom."));
  }

  private void testAdvancedChapter3(String heading, String text) {
    assertEquals("CHAPTER THREE\n\nThe Adventure of the Literary Innkeeper", heading);
    assertTrue(text.startsWith("That afternoon I had been worrying my brokers about "));
    assertTrue(text.endsWith(" and he fastened the chain with his own hand."));
  }

  private void testAdvancedChapter4(String heading, String text) {
    assertEquals("CHAPTER FOUR\n\nThe Thirty-Nine Steps", heading);
    assertTrue(text.startsWith("'I'm very sorry,' he said humbly."));
    assertTrue(text.endsWith("He got a little further down than he wanted."));
  }

}
