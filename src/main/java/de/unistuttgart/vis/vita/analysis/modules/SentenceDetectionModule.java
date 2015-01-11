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
import de.unistuttgart.vis.vita.model.document.Occurence;
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

    @Override public SentenceDetectionResult execute(ModuleResultProvider results,
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
                for (Sentence sentence : chapterToSentence.get(pos.getChapter())) {
                    TextPosition start = sentence.getRange().getStart();
                    TextPosition end = sentence.getRange().getEnd();
                    boolean overStart = start.getLocalOffset() <= pos.getLocalOffset();
                    boolean notOverEnd = end.getLocalOffset() >= pos.getLocalOffset();

                    if (overStart && notOverEnd) {
                        return sentence;
                    }
                }

                throw new IllegalArgumentException("No sentence for given position: " + pos);
            }

            @Override
            public Occurence createOccurrence(int startOffset, int endOffset) {
                Sentence sentence;

                if (startOffsetToSentence.containsKey(startOffset)) {
                    sentence = startOffsetToSentence.get(startOffset);
                } else {
                    TreeSet<Integer> test = new TreeSet<>(startOffsetToSentence.keySet());
                    Integer foundOffset = test.floor(startOffset);

                    sentence = startOffsetToSentence.get(foundOffset);
                }

                return new Occurence(sentence, sentence.getRange());
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

                Set<Annotation> anno = annieNLPResult.getAnnotationsForChapter(chapter, Arrays
                    .asList(ANNIEConstants.SENTENCE_ANNOTATION_TYPE));

                for (Annotation annotation : anno) {
                    int startOffset = annotation.getStartNode().getOffset().intValue();
                    int endOffset = annotation.getEndNode().getOffset().intValue();
                    int length = endOffset - startOffset;
                    TextPosition start = TextPosition.fromGlobalOffset(chapter, startOffset);
                    TextPosition end = TextPosition.fromGlobalOffset(chapter, startOffset + length);
                    Range range = new Range(start, end);
                    Sentence sentence = new Sentence(range, chapter, index);
                    index++;
                    sentences.add(sentence);
                    startOffsetToSentence.put(startOffset, sentence);
                }

                chapterToSentence.put(chapter, sentences);
            }
        }
    }
}
