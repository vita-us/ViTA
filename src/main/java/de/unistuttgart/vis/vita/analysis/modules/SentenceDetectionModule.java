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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import gate.Annotation;
import gate.creole.ANNIEConstants;

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
      public Occurrence createOccurrence(Chapter chapter, int startOffset, int endOffset) {
        // check relative offsets are inside of chapter
        if(startOffset >= chapter.getLength() || startOffset < 0){
          throw new IllegalArgumentException("startOffset must not lie outside of the chapter");
        }else if(endOffset >= chapter.getLength() || endOffset < 0){
          throw new IllegalArgumentException("endOffset must not lie outside of the chapter.");
        }
        
        // get global positions and documentLength
        int chapterStartOffset = chapter.getRange().getStart().getOffset();
        int documentLength = importResult.getTotalLength();
        int globalStartOffsetOfOccurrence = chapterStartOffset + startOffset;
        int globalEndOffsetOfOccurrence = chapterStartOffset + endOffset;
        
        // check offsets are inside of the document and chapter.
        if(globalStartOffsetOfOccurrence >= documentLength){
          throw new IllegalArgumentException("startOffset must not lie outside of the document.");
        } else if(globalEndOffsetOfOccurrence >= documentLength){
          throw new IllegalArgumentException("endOffset must not lie outside of the document.");
        }
        
        // create occurrence
        Sentence sentence = getSentenceAt(
            TextPosition.fromGlobalOffset(globalStartOffsetOfOccurrence, documentLength));
        Range rangeOfOccurrence = new Range(chapter, startOffset, endOffset, documentLength);
        return new Occurrence(sentence, rangeOfOccurrence);
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

        for (Annotation annotation : anno) {
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
