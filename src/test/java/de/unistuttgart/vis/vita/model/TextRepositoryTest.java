package de.unistuttgart.vis.vita.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * JUnit test on TextRepository
 * 
 *
 */
public class TextRepositoryTest {
  
  private static final String CHAPTER_ID = "chapterId";
  private static final String CHAPTER_TEXT = "chapterText";
  private TextRepository textRepository = new TextRepository();
  private List<String> chapterIds = new ArrayList<String>();
  private List<String> storedChapterIds = new ArrayList<String>();
  private List<String> chapterTexts = new ArrayList<String>();
  private List<String> storedChapterTexts = new ArrayList<String>();
  private Chapter chapter1;
  private IndexReader indexReader;
  private String changedChapter1Text;

  @Before
  public void setUp() throws IOException {
     List<Chapter> chapterList1 = new ArrayList<Chapter>();
     List<Chapter> chapterList2 = new ArrayList<Chapter>();
    // add to the chapterList1 two chapters with setted text
    chapterList1 = new ArrayList<Chapter>();
    chapter1 = new Chapter();
    chapter1.setText("This is the text of chapter one");
    chapterTexts.add(chapter1.getText());
    chapterIds.add(chapter1.getId());
    chapterList1.add(chapter1);
    Chapter chapter2 = new Chapter();
    chapter2.setText("This is the text of chapter two");
    chapterTexts.add(chapter2.getText());
    chapterIds.add(chapter2.getId());
    chapterList1.add(chapter2);

    // add to the chapterList2 two chapters with setted text
    chapterList2 = new ArrayList<Chapter>();
    Chapter chapter3 = new Chapter();
    chapter3.setText("This is the text of chapter three");
    chapterTexts.add(chapter3.getText());
    chapterIds.add(chapter3.getId());
    chapterList2.add(chapter3);
    Chapter chapter4 = new Chapter();
    chapter4.setText("This is the text of chapter four");
    chapterTexts.add(chapter4.getText());
    chapterIds.add(chapter4.getId());
    chapterList2.add(chapter4);

    // store two lists of chapters in lucene
    textRepository.storeChaptersTexts(chapterList1);
    textRepository.storeChaptersTexts(chapterList2);

    // change the text of chapter1 to "This is a false text"
    chapter1.setText("This is a false text");
    changedChapter1Text = chapter1.getText();

    // after populating the chapter text, the text of chapter1 is the stored text of chapter1
    // "This is the text of chapter one"
    textRepository.populateChapterText(chapter1);


    for (Directory index : textRepository.getIndexes()) {
      indexReader = IndexReader.open(index);
      for (int i = 0; i < indexReader.maxDoc(); i++) {
        storedChapterIds.add(indexReader.document(i).getField(CHAPTER_ID).stringValue());
        storedChapterTexts.add(indexReader.document(i).getField(CHAPTER_TEXT).stringValue());

      }
    }
  }

  /**
   * Tests the size of the stored indexes
   */
  @Test
  public void testIndexesSize() {
    assertEquals(2, textRepository.getIndexes().size());
  }
  
  /**
   * Tests the equality of the commited chapter ids and texts with the stored ones
   */
  @Test
  public void testStoreChapterTextsAndIds() {
    for (int i = 0; i < 4; i++) {
      assertEquals(chapterIds.get(i), storedChapterIds.get(i));
      assertEquals(chapterTexts.get(i), storedChapterTexts.get(i));
    }
  }
  
  /**
   * Tests the non equality of the changedChapter1Text and the chapter1 text after populating
   */
  @Test
  public void testPopulateChapterText() {
    assertFalse(chapter1.getText().equals(changedChapter1Text));
  }
}

