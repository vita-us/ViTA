/*
 * DocumentTest.java
 *
 */

package de.unistuttgart.vis.vita.model.document;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class DocumentTest {

  private Document document;
  private Chapter chapter1;
  private Chapter chapter2;
  private Chapter chapter3;
  
  private final static int DOCUMENT_LENGTH = 367;

  @Before
  public void setUp() {
    document = new Document();
    DocumentContent content = new DocumentContent();
    DocumentPart documentPart = new DocumentPart();

    chapter1 = new Chapter();
    Range
        range1 =
        new Range(TextPosition.fromGlobalOffset(chapter1, 0, DOCUMENT_LENGTH),
                     TextPosition.fromGlobalOffset(chapter1, 250, DOCUMENT_LENGTH));
    chapter1.setRange(range1);

    chapter2 = new Chapter();
    Range
        range2 =
        new Range(TextPosition.fromGlobalOffset(chapter2, 251, DOCUMENT_LENGTH),
                     TextPosition.fromGlobalOffset(chapter2, 300, DOCUMENT_LENGTH));
    chapter2.setRange(range2);

    chapter3 = new Chapter();
    Range
        range3 =
        new Range(TextPosition.fromGlobalOffset(chapter3, 301, DOCUMENT_LENGTH),
                     TextPosition.fromGlobalOffset(chapter3, 366, DOCUMENT_LENGTH));
    chapter3.setRange(range3);

    documentPart.getChapters().addAll(Arrays.asList(chapter1, chapter2, chapter3));
    content.getParts().add(documentPart);
    document.setContent(content);
  }

  @Test
  public void testGetChapterAtBoundaries() {
    assertThat(document.getChapterAt(0), is(chapter1));
    assertThat(document.getChapterAt(250), is(chapter1));

    assertThat(document.getChapterAt(251), is(chapter2));
    assertThat(document.getChapterAt(300), is(chapter2));

    assertThat(document.getChapterAt(301), is(chapter3));
    assertThat(document.getChapterAt(366), is(chapter3));
  }

  @Test
  public void testGetChapterAtDefault() {
    assertThat(document.getChapterAt(23), is(chapter1));

    assertThat(document.getChapterAt(260), is(chapter2));

    assertThat(document.getChapterAt(350), is(chapter3));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetChapterAtIllegalHigh() {
    document.getChapterAt(5555);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetChapterAtIllegalNegative() {
    document.getChapterAt(-1);
  }
}
