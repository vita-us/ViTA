package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.analyzers.AutomatedChapterDetection;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;

public class AutomatedChapterDetectionTxtTest {

  @Test
  public void testBigHeadingText() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("BigHeadingChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);

    AutomatedChapterDetection detector = new AutomatedChapterDetection(importer.getLines());
    ChapterPosition position = detector.getChapterPosition();

    testBigFirstChapter(position);
    testBigSecondChapter(position);
    testBigThirdChapter(position);
    testBigFourthChapter(position);
  }

  @Test
  public void testSmallHeadingText() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("SmallHeadingChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);

    AutomatedChapterDetection detector = new AutomatedChapterDetection(importer.getLines());
    ChapterPosition position = detector.getChapterPosition();

    testSmallFirstChapter(position);
    testSmallSecondChapter(position);
    testSmallThirdChapter(position);
    testSmallFourthChapter(position);
  }

  @Test
  public void testAdvancedBigHeadingText() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("AdvancedBigHeadingChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);

    AutomatedChapterDetection detector = new AutomatedChapterDetection(importer.getLines());
    ChapterPosition position = detector.getChapterPosition();

    testAdvancedBigFirstChapter(position);
    testAdvancedBigSecondChapter(position);
    testAdvancedBigThirdChapter(position);
    testAdvancedBigFourthChapter(position);
    testAdvancedBigFifthChapter(position);
    testAdvancedBigSixthChapter(position);
    testAdvancedBigSeventhChapter(position);
    testAdvancedBigEighthChapter(position);
    testAdvancedBigNinthChapter(position);
    testAdvancedBigTenthChapter(position);
  }

  @Test
  public void testMarkedHeadingText() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("MarkedHeadingChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);

    AutomatedChapterDetection detector = new AutomatedChapterDetection(importer.getLines());
    ChapterPosition position = detector.getChapterPosition();

    testMarkedFirstChapter(position);
    testMarkedSecondChapter(position);
    testMarkedThirdChapter(position);
    testMarkedFourthChapter(position);
    testMarkedFifthChapter(position);
    testMarkedSixthChapter(position);
  }

  @Test
  public void testSimpleWhitelinesHeadingText() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("AutomatedWhitelines.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);

    AutomatedChapterDetection detector = new AutomatedChapterDetection(importer.getLines());
    ChapterPosition position = detector.getChapterPosition();

    testSimpleWhitelinesFirstChapter(position);
    testSimpleWhitelinesSecondChapter(position);
    testSimpleWhitelinesThirdChapter(position);
    testSimpleWhitelinesFourthChapter(position);
    testSimpleWhitelinesFifthChapter(position);

  }

  @Test
  public void testFullTextHeadingText() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("AutomatedFullText.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);

    AutomatedChapterDetection detector = new AutomatedChapterDetection(importer.getLines());
    ChapterPosition position = detector.getChapterPosition();

    testFullTextFirstChapter(position);
  }

  private void testBigFirstChapter(ChapterPosition position) {
    int chapterNumber = 1;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 0);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 0);

    assertTrue(position.getStartOfText(chapterNumber) >= 7);
    assertTrue(position.getStartOfText(chapterNumber) <= 8);

    assertTrue(position.getEndOfText(chapterNumber) >= 287);
    assertTrue(position.getEndOfText(chapterNumber) <= 289);
  }

  private void testBigSecondChapter(ChapterPosition position) {
    int chapterNumber = 2;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 288);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 290);

    assertTrue(position.getStartOfText(chapterNumber) >= 291);
    assertTrue(position.getStartOfText(chapterNumber) <= 291);

    assertTrue(position.getEndOfText(chapterNumber) >= 437);
    assertTrue(position.getEndOfText(chapterNumber) <= 439);
  }

  private void testBigThirdChapter(ChapterPosition position) {
    int chapterNumber = 3;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 438);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 440);

    assertTrue(position.getStartOfText(chapterNumber) >= 441);
    assertTrue(position.getStartOfText(chapterNumber) <= 442);

    assertTrue(position.getEndOfText(chapterNumber) >= 567);
    assertTrue(position.getEndOfText(chapterNumber) <= 569);
  }

  private void testBigFourthChapter(ChapterPosition position) {
    int chapterNumber = 4;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 568);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 570);

    assertTrue(position.getStartOfText(chapterNumber) >= 573);
    assertTrue(position.getStartOfText(chapterNumber) <= 575);

    assertTrue(position.getEndOfText(chapterNumber) >= 1140);
    assertTrue(position.getEndOfText(chapterNumber) <= 1141);
  }

  private void testSmallFirstChapter(ChapterPosition position) {
    int chapterNumber = 1;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 23);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 27);

    assertTrue(position.getStartOfText(chapterNumber) >= 28);
    assertTrue(position.getStartOfText(chapterNumber) <= 30);

    assertTrue(position.getEndOfText(chapterNumber) >= 1081);
    assertTrue(position.getEndOfText(chapterNumber) <= 1085);
  }

  private void testSmallSecondChapter(ChapterPosition position) {
    int chapterNumber = 2;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 1082);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 1086);

    assertTrue(position.getStartOfText(chapterNumber) >= 1087);
    assertTrue(position.getStartOfText(chapterNumber) <= 1089);

    assertTrue(position.getEndOfText(chapterNumber) >= 1804);
    assertTrue(position.getEndOfText(chapterNumber) <= 1807);
  }

  private void testSmallThirdChapter(ChapterPosition position) {
    int chapterNumber = 3;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 1805);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 1808);

    assertTrue(position.getStartOfText(chapterNumber) >= 1809);
    assertTrue(position.getStartOfText(chapterNumber) <= 1811);

    assertTrue(position.getEndOfText(chapterNumber) >= 2582);
    assertTrue(position.getEndOfText(chapterNumber) <= 2586);
  }

  private void testSmallFourthChapter(ChapterPosition position) {
    int chapterNumber = 4;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 2583);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 2587);

    assertTrue(position.getStartOfText(chapterNumber) >= 2588);
    assertTrue(position.getStartOfText(chapterNumber) <= 2590);

    assertTrue(position.getEndOfText(chapterNumber) >= 3365);
    assertTrue(position.getEndOfText(chapterNumber) <= 3375);
  }

  private void testAdvancedBigFirstChapter(ChapterPosition position) {
    int chapterNumber = 1;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 58);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 61);

    assertTrue(position.getStartOfText(chapterNumber) >= 64);
    assertTrue(position.getStartOfText(chapterNumber) <= 65);

    assertTrue(position.getEndOfText(chapterNumber) >= 513);
    assertTrue(position.getEndOfText(chapterNumber) <= 515);
  }

  private void testAdvancedBigSecondChapter(ChapterPosition position) {
    int chapterNumber = 2;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 514);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 516);

    assertTrue(position.getStartOfText(chapterNumber) >= 521);
    assertTrue(position.getStartOfText(chapterNumber) <= 522);

    assertTrue(position.getEndOfText(chapterNumber) >= 755);
    assertTrue(position.getEndOfText(chapterNumber) <= 757);
  }

  private void testAdvancedBigThirdChapter(ChapterPosition position) {
    int chapterNumber = 3;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 756);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 758);

    assertTrue(position.getStartOfText(chapterNumber) >= 759);
    assertTrue(position.getStartOfText(chapterNumber) <= 760);

    assertTrue(position.getEndOfText(chapterNumber) >= 1231);
    assertTrue(position.getEndOfText(chapterNumber) <= 1233);
  }

  private void testAdvancedBigFourthChapter(ChapterPosition position) {
    int chapterNumber = 4;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 1232);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 1234);

    assertTrue(position.getStartOfText(chapterNumber) >= 1235);
    assertTrue(position.getStartOfText(chapterNumber) <= 1236);

    assertTrue(position.getEndOfText(chapterNumber) >= 1642);
    assertTrue(position.getEndOfText(chapterNumber) <= 1645);
  }

  private void testAdvancedBigFifthChapter(ChapterPosition position) {
    int chapterNumber = 5;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 1643);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 1646);

    assertTrue(position.getStartOfText(chapterNumber) >= 1649);
    assertTrue(position.getStartOfText(chapterNumber) <= 1650);

    assertTrue(position.getEndOfText(chapterNumber) >= 2022);
    assertTrue(position.getEndOfText(chapterNumber) <= 2025);
  }

  private void testAdvancedBigSixthChapter(ChapterPosition position) {
    int chapterNumber = 6;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 2023);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 2026);

    assertTrue(position.getStartOfText(chapterNumber) >= 2027);
    assertTrue(position.getStartOfText(chapterNumber) <= 2028);

    assertTrue(position.getEndOfText(chapterNumber) >= 2034);
    assertTrue(position.getEndOfText(chapterNumber) <= 2036);
  }

  private void testAdvancedBigSeventhChapter(ChapterPosition position) {
    int chapterNumber = 7;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 2035);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 2037);

    assertTrue(position.getStartOfText(chapterNumber) >= 2040);
    assertTrue(position.getStartOfText(chapterNumber) <= 2041);

    assertTrue(position.getEndOfText(chapterNumber) >= 2589);
    assertTrue(position.getEndOfText(chapterNumber) <= 2591);
  }

  private void testAdvancedBigEighthChapter(ChapterPosition position) {
    int chapterNumber = 8;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 2590);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 2592);

    assertTrue(position.getStartOfText(chapterNumber) >= 2593);
    assertTrue(position.getStartOfText(chapterNumber) <= 2594);

    assertTrue(position.getEndOfText(chapterNumber) >= 2594);
    assertTrue(position.getEndOfText(chapterNumber) <= 2597);
  }

  private void testAdvancedBigNinthChapter(ChapterPosition position) {
    int chapterNumber = 9;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 2595);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 2598);

    assertTrue(position.getStartOfText(chapterNumber) >= 2599);
    assertTrue(position.getStartOfText(chapterNumber) <= 2600);

    assertTrue(position.getEndOfText(chapterNumber) >= 2610);
    assertTrue(position.getEndOfText(chapterNumber) <= 2612);
  }

  private void testAdvancedBigTenthChapter(ChapterPosition position) {
    int chapterNumber = 10;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 2611);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 2613);

    assertTrue(position.getStartOfText(chapterNumber) >= 2614);
    assertTrue(position.getStartOfText(chapterNumber) <= 2615);

    assertTrue(position.getEndOfText(chapterNumber) >= 2621);
    assertTrue(position.getEndOfText(chapterNumber) <= 2622);
  }


  public void testMarkedFirstChapter(ChapterPosition position) {
    int chapterNumber = 1;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 7);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 15);

    assertTrue(position.getStartOfText(chapterNumber) >= 24);
    assertTrue(position.getStartOfText(chapterNumber) <= 25);

    assertTrue(position.getEndOfText(chapterNumber) >= 94);
    assertTrue(position.getEndOfText(chapterNumber) <= 98);
  }

  public void testMarkedSecondChapter(ChapterPosition position) {
    int chapterNumber = 2;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 95);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 99);

    assertTrue(position.getStartOfText(chapterNumber) >= 100);
    assertTrue(position.getStartOfText(chapterNumber) <= 102);

    assertTrue(position.getEndOfText(chapterNumber) >= 980);
    assertTrue(position.getEndOfText(chapterNumber) <= 980);
  }

  public void testMarkedThirdChapter(ChapterPosition position) {
    int chapterNumber = 3;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 981);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 981);

    assertTrue(position.getStartOfText(chapterNumber) >= 982);
    assertTrue(position.getStartOfText(chapterNumber) <= 982);

    assertTrue(position.getEndOfText(chapterNumber) >= 1368);
    assertTrue(position.getEndOfText(chapterNumber) <= 1372);
  }

  public void testMarkedFourthChapter(ChapterPosition position) {
    int chapterNumber = 4;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 1369);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 1373);

    assertTrue(position.getStartOfText(chapterNumber) >= 1375);
    assertTrue(position.getStartOfText(chapterNumber) <= 1378);

    assertTrue(position.getEndOfText(chapterNumber) >= 2905);
    assertTrue(position.getEndOfText(chapterNumber) <= 2909);
  }

  public void testMarkedFifthChapter(ChapterPosition position) {
    int chapterNumber = 5;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 2906);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 2910);

    assertTrue(position.getStartOfText(chapterNumber) >= 2913);
    assertTrue(position.getStartOfText(chapterNumber) <= 2915);

    assertTrue(position.getEndOfText(chapterNumber) >= 8615);
    assertTrue(position.getEndOfText(chapterNumber) <= 8616);
  }

  public void testMarkedSixthChapter(ChapterPosition position) {
    int chapterNumber = 6;

    assertTrue(position.getStartOfHeading(chapterNumber) >= 8616);
    assertTrue(position.getStartOfHeading(chapterNumber) <= 8617);

    assertTrue(position.getStartOfText(chapterNumber) >= 8618);
    assertTrue(position.getStartOfText(chapterNumber) <= 8619);

    assertTrue(position.getEndOfText(chapterNumber) >= 8620);
    assertTrue(position.getEndOfText(chapterNumber) <= 8621);
  }

  public void testSimpleWhitelinesFirstChapter(ChapterPosition position) {
    int chapterNumber = 1;

    assertFalse(position.hasHeading(chapterNumber));

    assertTrue(position.getStartOfText(chapterNumber) >= 0);
    assertTrue(position.getStartOfText(chapterNumber) <= 0);

    assertTrue(position.getEndOfText(chapterNumber) >= 12);
    assertTrue(position.getEndOfText(chapterNumber) <= 14);
  }

  public void testSimpleWhitelinesSecondChapter(ChapterPosition position) {
    int chapterNumber = 2;

    assertFalse(position.hasHeading(chapterNumber));

    assertTrue(position.getStartOfText(chapterNumber) >= 13);
    assertTrue(position.getStartOfText(chapterNumber) <= 15);

    assertTrue(position.getEndOfText(chapterNumber) >= 35);
    assertTrue(position.getEndOfText(chapterNumber) <= 37);
  }

  public void testSimpleWhitelinesThirdChapter(ChapterPosition position) {
    int chapterNumber = 3;

    assertFalse(position.hasHeading(chapterNumber));

    assertTrue(position.getStartOfText(chapterNumber) >= 36);
    assertTrue(position.getStartOfText(chapterNumber) <= 38);

    assertTrue(position.getEndOfText(chapterNumber) >= 56);
    assertTrue(position.getEndOfText(chapterNumber) <= 58);
  }


  public void testSimpleWhitelinesFourthChapter(ChapterPosition position) {
    int chapterNumber = 4;

    assertFalse(position.hasHeading(chapterNumber));

    assertTrue(position.getStartOfText(chapterNumber) >= 57);
    assertTrue(position.getStartOfText(chapterNumber) <= 59);

    assertTrue(position.getEndOfText(chapterNumber) >= 71);
    assertTrue(position.getEndOfText(chapterNumber) <= 73);
  }


  public void testSimpleWhitelinesFifthChapter(ChapterPosition position) {
    int chapterNumber = 5;

    assertFalse(position.hasHeading(chapterNumber));

    assertTrue(position.getStartOfText(chapterNumber) >= 72);
    assertTrue(position.getStartOfText(chapterNumber) <= 74);

    assertTrue(position.getEndOfText(chapterNumber) >= 90);
    assertTrue(position.getEndOfText(chapterNumber) <= 91);
  }

  public void testFullTextFirstChapter(ChapterPosition position) {
    int chapterNumber = 1;

    assertFalse(position.hasHeading(chapterNumber));

    assertTrue(position.getStartOfText(chapterNumber) >= 0);
    assertTrue(position.getStartOfText(chapterNumber) <= 0);

    assertTrue(position.getEndOfText(chapterNumber) >= 86);
    assertTrue(position.getEndOfText(chapterNumber) <= 87);
  }
}
