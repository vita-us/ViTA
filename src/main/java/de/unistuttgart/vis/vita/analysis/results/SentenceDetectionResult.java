package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
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
     * @param chapter the chapter
     * @return the sentences in order
     */
    public List<Sentence> getSentencesInChapter(Chapter chapter);

    /**
     * Gets the sentence a text position is in
     * @return the sentence
     * @throws java.lang.IllegalArgumentException if {@code pos} is outside the document range
     */
    public Sentence getSentenceAt(TextPosition pos);

    /**
     * Creates an occurrence for the range between two global offsets in the document
     * @return the created occurrence
     * @param startOffset the global start
     * @param length the total length of the occurrence
     * @throws java.lang.IllegalArgumentException if startOffset or endOffset do are not in the
     *   document range
     */
    public Occurrence createOccurrence(int startOffset, int length);
}
