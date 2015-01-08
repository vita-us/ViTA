package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.SentenceDetectionResult;
import de.unistuttgart.vis.vita.model.document.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits the chapters into sentences
 */
@AnalysisModule
public class SentenceDetectionModule extends Module<SentenceDetectionResult> {
    @Override public SentenceDetectionResult execute(ModuleResultProvider results,
        ProgressListener progressListener) throws Exception {
        return new SentenceDetectionResult() {
            @Override
            public List<Sentence> getSentencesInChapter(Chapter chapter) {
                return new ArrayList<>();
            }

            @Override
            public Sentence getSentenceAt(TextPosition pos) {
                throw new UnsupportedOperationException("to be implemented");
            }

            @Override
            public Occurence createOccurrence(int startOffset, int endOffset) {
                TextPosition start = null; // TODO
                TextPosition end = null; // TODO
                Range range = new Range(start, end);
                return new Occurence(getSentenceAt(start), range);
            }
        };
    }
}
