package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.NLPResult;
import de.unistuttgart.vis.vita.analysis.results.SentenceDetectionResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import gate.Annotation;
import gate.creole.ANNIEConstants;

/**
 * Splits the Chapters into Sentences.
 */
@AnalysisModule(dependencies = {NLPResult.class, ImportResult.class})
public class SentenceDetectionModule extends Module<SentenceDetectionResult> {

  private ProgressListener progressListener;
  private NLPResult nlpResult;
  private ImportResult importResult;
  private Map<Chapter, List<Sentence>> chapterToSentence;
  private Map<Integer, Sentence> startOffsetToSentence;
  private NavigableSet<Integer> sortedStartOffset;

  /**
   * An Implementation of {@link SentenceDetectionResult} which should be returned at the end of
   * this modules execution.
   */
  private class SentenceDetectionResultImpl implements SentenceDetectionResult {
    @Override
    public List<Sentence> getSentencesInChapter(Chapter chapter) {
      if (!chapterToSentence.containsKey(chapter)) {
        throw new IllegalStateException("Sentences map does not contain this chapter: " + chapter);
      }

      return chapterToSentence.get(chapter);
    }

    @Override
    public Sentence getSentenceAt(TextPosition pos) {
      int offset = pos.getOffset();

      if (offset > importResult.getTotalLength()) {
        throw new IllegalStateException("Offset is higher than document length!");
      }

      Sentence sentence;

      if (startOffsetToSentence.containsKey(offset)) {
        sentence = startOffsetToSentence.get(offset);
      } else {
        Integer foundOffset = sortedStartOffset.floor(offset);
        sentence = startOffsetToSentence.get(foundOffset);
      }

      return sentence;
    }

    @Override
    public Occurrence createOccurrence(Chapter chapter, int startOffset, int endOffset) {
      if (isOffsetOutsideOfChapter(startOffset, chapter)) {
        throw new IllegalArgumentException("startOffset must not lie outside of the chapter");
      } else if (isOffsetOutsideOfChapter(endOffset, chapter)) {
        throw new IllegalArgumentException("endOffset must not lie outside of the chapter.");
      }

      // get global positions and documentLength
      int chapterStartOffset = chapter.getRange().getStart().getOffset();
      int documentLength = importResult.getTotalLength();
      int globalStartOffsetOfOccurrence = chapterStartOffset + startOffset;
      int globalEndOffsetOfOccurrence = chapterStartOffset + endOffset;

      // check offsets are inside of the document and chapter.
      if (globalStartOffsetOfOccurrence > documentLength) {
        throw new IllegalArgumentException("startOffset must not lie outside of the document.");
      } else if (globalEndOffsetOfOccurrence > documentLength) {
        throw new IllegalArgumentException("endOffset must not lie outside of the document.");
      }

      // create occurrence
      Sentence sentence =
          getSentenceAt(TextPosition
              .fromGlobalOffset(globalStartOffsetOfOccurrence, documentLength));
      Range rangeOfOccurrence = new Range(chapter, startOffset, endOffset, documentLength);
      return new Occurrence(sentence, rangeOfOccurrence);
    }

    /**
     * Check whether a relative offset is outside of a Chapter or not.
     * 
     * @param offset - The relative offset in the Chapter.
     * @param chapter - The Chapter.
     * @return true: offset is outside of the Chapter. false: everything is okay, offset is inside
     *         of the Chapter.
     */
    private boolean isOffsetOutsideOfChapter(int offset, Chapter chapter) {
      return offset >= chapter.getLength() || offset < 0;
    }
  }


  /**
   * Compares Annotations depending on their start position.
   */
  private class AnnotationComparator implements Comparator<Annotation> {
    @Override
    public int compare(Annotation o1, Annotation o2) {
      return o1.getStartNode().getOffset().intValue() - o2.getStartNode().getOffset().intValue();
    }
  }


  @Override
  public SentenceDetectionResult execute(ModuleResultProvider results,
      ProgressListener progressListener) throws Exception {
    importResult = results.getResultFor(ImportResult.class);
    nlpResult = results.getResultFor(NLPResult.class);
    this.progressListener = progressListener;
    buildResults();
    this.progressListener.observeProgress(1);

    return new SentenceDetectionResultImpl();
  }

  /**
   * Executes the sentence detection. The results are stored in the class attributes after this
   * method finished its execution. Calling this method again will replace the existing results.
   */
  private void buildResults() {
    chapterToSentence = new HashMap<>();
    startOffsetToSentence = new TreeMap<>();
    int index = 0;
    List<Sentence> sentences;

    for (DocumentPart documentPart : importResult.getParts()) {
      for (Chapter chapter : documentPart.getChapters()) {
        sentences = new ArrayList<>();
        List<Annotation> sortedAnnotations = getSortedSentenceAnnotations(chapter);

        for (Annotation annotation : sortedAnnotations) {
          Sentence sentence = buildSentenceFromAnnotation(annotation, chapter, index);
          sentences.add(sentence);
          index++;
        }

        chapterToSentence.put(chapter, sentences);
      }
    }

    sortedStartOffset = new TreeSet<>(startOffsetToSentence.keySet());
  }

  /**
   * Get the Sentence Annotations for a Chapter and returns them sorted.
   * 
   * @param chapter - The Chapter.
   * @return The sorted Annotations.
   */
  private List<Annotation> getSortedSentenceAnnotations(Chapter chapter) {
    Set<Annotation> anno =
        nlpResult.getAnnotationsForChapter(chapter,
            Arrays.asList(ANNIEConstants.SENTENCE_ANNOTATION_TYPE));
    List<Annotation> sortedAnnotations = new ArrayList<Annotation>(anno);
    Collections.sort(sortedAnnotations, new AnnotationComparator());
    return sortedAnnotations;
  }

  /**
   * Builds a Sentence from a given Sentence Annotation and adds it to the list for sentence start
   * offsets.
   * 
   * @param annotation - The Annotation which describes the Sentence position.
   * @param chapter - The Chapter the Sentence lies in.
   * @param index - The Index of the Sentence.
   * @return the Sentence.
   */
  private Sentence buildSentenceFromAnnotation(Annotation annotation, Chapter chapter, int index) {
    int localStartOffset = annotation.getStartNode().getOffset().intValue();
    int localEndOffset = annotation.getEndNode().getOffset().intValue();
    TextPosition start =
        TextPosition.fromLocalOffset(chapter, localStartOffset, this.importResult.getTotalLength());
    TextPosition end =
        TextPosition.fromLocalOffset(chapter, localEndOffset, this.importResult.getTotalLength());
    Range range = new Range(start, end);
    Sentence sentence = new Sentence(range, chapter, index);

    startOffsetToSentence.put(start.getOffset(), sentence);

    return sentence;
  }

}
