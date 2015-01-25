package de.unistuttgart.vis;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;


public class BasicEntityTest {
  private static final int TEST_NAME_ATTR1_1_SENTENCE_START_OFFSET = 0;
  private static final int TEST_NAME_ATTR1_1_START_OFFSET = 0;
  private static final int TEST_NAME_ATTR1_1_END_OFFSET = 10;
  private static final int TEST_NAME_ATTR1_1_SENTENCE_END_OFFSET = 10;

  private static final int TEST_NAME_ATTR1_2_SENTENCE_START_OFFSET = 30;
  private static final int TEST_NAME_ATTR1_2_START_OFFSET = 30;
  private static final int TEST_NAME_ATTR1_2_END_OFFSET = 40;
  private static final int TEST_NAME_ATTR1_2_SENTENCE_END_OFFSET = 40;

  private static final int TEST_NAME_ATTR2_SENTENCE_START_OFFSET = 100;
  private static final int TEST_NAME_ATTR2_START_OFFSET = 100;
  private static final int TEST_NAME_ATTR2_END_OFFSET = 110;
  private static final int TEST_NAME_ATTR2_SENTENCE_END_OFFSET = 110;

  private static final int TEST_NAME_ATTR3_SENTENCE_START_OFFSET = 130;
  private static final int TEST_NAME_ATTR3_START_OFFSET = 130;
  private static final int TEST_NAME_ATTR3_END_OFFSET = 140;
  private static final int TEST_NAME_ATTR3_SENTENCE_END_OFFSET = 140;

  @Test
  public void testMerge() {
    Chapter chapter = new Chapter();
    BasicEntity e1 = new BasicEntity();
    e1.setDisplayName("Frodo");
    e1.setType(EntityType.PERSON);
    Attribute nameAttr1 = new Attribute(AttributeType.NAME, "Frodo");

    Range nameAttr1Sentence1Range =
        new Range(chapter, TEST_NAME_ATTR1_1_SENTENCE_START_OFFSET,
            TEST_NAME_ATTR1_1_SENTENCE_END_OFFSET, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence nameAttr1Sentence1 = new Sentence(nameAttr1Sentence1Range, chapter, 0);
    Range nameAttr1RangeOccurrence1 =
        new Range(chapter, TEST_NAME_ATTR1_1_START_OFFSET, TEST_NAME_ATTR1_1_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Occurrence nameAttr1Occurrence1 = new Occurrence(nameAttr1Sentence1, nameAttr1RangeOccurrence1);
    nameAttr1.getOccurrences().add(nameAttr1Occurrence1);

    Range nameAttr1Sentence2Range =
        new Range(chapter, TEST_NAME_ATTR1_2_SENTENCE_START_OFFSET,
            TEST_NAME_ATTR1_2_SENTENCE_END_OFFSET, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence nameAttr1Sentence2 = new Sentence(nameAttr1Sentence2Range, chapter, 0);
    Range nameAttr1RangeOccurrence2 =
        new Range(chapter, TEST_NAME_ATTR1_2_START_OFFSET, TEST_NAME_ATTR1_2_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Occurrence nameAttr1Occurrence2 = new Occurrence(nameAttr1Sentence2, nameAttr1RangeOccurrence2);
    nameAttr1.getOccurrences().add(nameAttr1Occurrence2);

    e1.getNameAttributes().add(nameAttr1);

    BasicEntity e2 = new BasicEntity();
    e2.setDisplayName("Mr. Frodo");
    e2.setType(EntityType.PERSON);

    Attribute nameAttr2 = new Attribute(AttributeType.NAME, "Mr. Frodo");
    Range nameAttr2SentenceRange =
        new Range(chapter, TEST_NAME_ATTR2_SENTENCE_START_OFFSET,
            TEST_NAME_ATTR2_SENTENCE_END_OFFSET, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence nameAttr2Sentence = new Sentence(nameAttr2SentenceRange, chapter, 0);
    Range nameAttr2Range =
        new Range(chapter, TEST_NAME_ATTR2_START_OFFSET, TEST_NAME_ATTR2_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Occurrence nameAttr2Occurrence = new Occurrence(nameAttr2Sentence, nameAttr2Range);
    nameAttr2.getOccurrences().add(nameAttr2Occurrence);
    
    e2.getNameAttributes().add(nameAttr2);

    Attribute nameAttr3 = new Attribute(AttributeType.NAME, "Frodo");
    Range nameAttr3SentenceRange =
        new Range(chapter, TEST_NAME_ATTR3_SENTENCE_START_OFFSET,
            TEST_NAME_ATTR3_SENTENCE_END_OFFSET, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence nameAttr3Sentence = new Sentence(nameAttr3SentenceRange, chapter, 0);
    Range nameAttr3Range =
        new Range(chapter, TEST_NAME_ATTR3_START_OFFSET, TEST_NAME_ATTR3_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Occurrence nameAttr3Occurrence = new Occurrence(nameAttr3Sentence, nameAttr3Range);
    nameAttr3.getOccurrences().add(nameAttr3Occurrence);
    
    e2.getNameAttributes().add(nameAttr3);

    e1.merge(e2);
    assertThat(e1.getDisplayName(), is("Frodo"));
    assertThat(e1.getType(), is(EntityType.PERSON));
    assertThat(e1.getNameAttributes(), hasSize(2));

    Attribute firstAttr = e1.getNameAttributes().first();
    Range expectedRange1 =
        new Range(chapter, TEST_NAME_ATTR1_1_START_OFFSET, TEST_NAME_ATTR1_1_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range expectedRange2 =
        new Range(chapter, TEST_NAME_ATTR1_2_START_OFFSET, TEST_NAME_ATTR1_2_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range expectedRange3 =
        new Range(chapter, TEST_NAME_ATTR3_START_OFFSET, TEST_NAME_ATTR3_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);

    assertThat(firstAttr.getType(), is(AttributeType.NAME));
    assertThat(firstAttr.getContent(), is("Frodo")); // occurs most often
    assertThat(getRangesOfOccurrences(firstAttr.getOccurrences()),
        contains(expectedRange1, expectedRange2, expectedRange3));

    Attribute secondAttr = e1.getNameAttributes().last();
    Range expectedRange =
        new Range(chapter, TEST_NAME_ATTR2_START_OFFSET, TEST_NAME_ATTR2_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);

    assertThat(secondAttr.getType(), is(AttributeType.NAME));
    assertThat(secondAttr.getContent(), is("Mr. Frodo"));
    assertThat(getRangesOfOccurrences(secondAttr.getOccurrences()), contains(expectedRange));
  }

  private List<Range> getRangesOfOccurrences(List<Occurrence> occurrences) {
    ArrayList<Range> ranges = new ArrayList<Range>();
    for (Occurrence occurrence : occurrences) {
      ranges.add(occurrence.getRange());
    }
    return ranges;
  }
}
