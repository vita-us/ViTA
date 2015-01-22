package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;

import java.util.List;

/**
 * The result of splitting the document chapters into sentences
 */
public interface SentenceDetectionResult {
  /**
   * Gets all the sentences in a chapter
   * 
   * @param chapter the chapter
   * @return the sentences in order
   */
  public List<Sentence> getSentencesInChapter(Chapter chapter);

  /**
   * Gets the sentence a text position is in
   * 
   * @param pos - the text position
   * @return the sentence
   * @throws java.lang.IllegalArgumentException if {@code pos} is outside the document range
   */
  public Sentence getSentenceAt(TextPosition pos);

  /**
   * Creates an occurrence for the range between two global offsets in the document. The range
   * typically describes an Entity, which lies inside of a sentence. The startOffset and endOffset
   * describe the position of the entity. It is assumed that both offsets lie inside of the same
   * sentence, so only the startOffset will be used to get the sentence.
   * 
   * @param chapter - the chapter the occurrence lies in.
   * @param startOffset - the start of the occurrence, not the sentence. Relative to the beginning of the chapter.
   * @param endOffset - the end of the occurrence, not the sentence. Relative to the beginning of the chapter.
   * @return the created occurrence
   * @throws java.lang.IllegalArgumentException if startOffset or endOffset are not in the
   *         document range or in the chapter range.
   */
  public Occurrence createOccurrence(Chapter chapter, int startOffset, int endOffset);
}
