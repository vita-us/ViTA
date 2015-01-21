package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.SentenceDetectionResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import gate.Annotation;
import gate.creole.ANNIEConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Splits the chapters into sentences
 */
@AnalysisModule(dependencies = {AnnieNLPResult.class, ImportResult.class})
public class SentenceDetectionModule extends Module<SentenceDetectionResult> {

  private ProgressListener progressListener;
  private AnnieNLPResult annieNLPResult;
  private ImportResult importResult;
  private Map<Chapter, List<Sentence>> chapterToSentence;
  private Map<Integer, Sentence> startOffsetToSentence;
  private TreeSet<Integer> sortedStartOffset;

  @Override
  public SentenceDetectionResult execute(ModuleResultProvider results,
      ProgressListener progressListener) throws Exception {
    importResult = results.getResultFor(ImportResult.class);
    annieNLPResult = results.getResultFor(AnnieNLPResult.class);
    this.progressListener = progressListener;
    buildResults();
    this.progressListener.observeProgress(1);

    return new SentenceDetectionResult() {
      @Override
      public List<Sentence> getSentencesInChapter(Chapter chapter) {
        return chapterToSentence.get(chapter);
      }

      @Override
      public Sentence getSentenceAt(TextPosition pos) {
        int offset = pos.getOffset();
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
      public Occurrence createOccurrence(int startOffset) {
        Sentence sentence = getSentenceAt(
            TextPosition.fromGlobalOffset(startOffset, importResult.getTotalLength()));

        return new Occurrence(sentence, sentence.getRange());
      }
    };
  }

  private void buildResults() {
    chapterToSentence = new HashMap<>();
    startOffsetToSentence = new TreeMap<>();
    int index = 0;
    List<Sentence> sentences;

    for (DocumentPart documentPart : importResult.getParts()) {
      for (Chapter chapter : documentPart.getChapters()) {
        sentences = new ArrayList<>();

        Set<Annotation> anno =
            annieNLPResult.getAnnotationsForChapter(chapter,
                Arrays.asList(ANNIEConstants.SENTENCE_ANNOTATION_TYPE));
        List<Annotation> sortedAnnotations = new ArrayList(anno);
        Collections.sort(sortedAnnotations, new Comparator<Annotation>() {
          @Override public int compare(Annotation o1, Annotation o2) {
            return o1.getStartNode().getOffset().intValue() - o2.getStartNode().getOffset().intValue();
          }
        });

        for (Annotation annotation : sortedAnnotations) {
          int startOffset = annotation.getStartNode().getOffset().intValue();
          int endOffset = annotation.getEndNode().getOffset().intValue();
          int length = endOffset - startOffset;
          TextPosition start =
              TextPosition.fromGlobalOffset(startOffset,
                  this.importResult.getTotalLength());
          TextPosition end =
              TextPosition.fromGlobalOffset(startOffset + length,
                  this.importResult.getTotalLength());
          Range range = new Range(start, end);
          Sentence sentence = new Sentence(range, chapter, index);
          index++;
          sentences.add(sentence);
          startOffsetToSentence.put(startOffset, sentence);
        }

        chapterToSentence.put(chapter, sentences);
      }
    }

    sortedStartOffset = new TreeSet<>(startOffsetToSentence.keySet());
  }
}
