package de.unistuttgart.vis.vita.analysis.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.StanfordNLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.Function;

@AnalysisModule(dependencies = {ImportResult.class})
public class StanfordNLPModule implements Module<StanfordNLPResult> {
  private StanfordCoreNLP pipeline;
  private int totalLength;
  private int progressInChars;
  private ImportResult importResult;
  private Map<Chapter, Annotation> annotations = new HashMap<>();
  private ProgressListener progressListener;

  /**
   * The fraction of the progress report which is reserved for loading the models If this value is
   * 0.1, the progress will be 10% once the models are loaded, but before any chapter is annotated.
   */
  private static final double MODEL_LOAD_PROGRESS_FRACTION = 0.1;

  private static final String ANNOTATORS = "tokenize, ssplit, pos, lemma, ner, parse, dcoref";

  @Override
  public void observeProgress(double progress) {}

  @Override
  public StanfordNLPResult execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    importResult = results.getResultFor(ImportResult.class);
    this.progressListener = progressListener;
    prepareInput();
    initializePipeline();
    progressListener.observeProgress(MODEL_LOAD_PROGRESS_FRACTION);
    runAnalysis();

    return buildResult();
  }

  private void prepareInput() {
    totalLength = 0;
    for (DocumentPart part : importResult.getParts()) {
      for (Chapter chapter : part.getChapters()) {
        totalLength += chapter.getLength();
        Annotation annotation = new Annotation(chapter.getText());
        annotations.put(chapter, annotation);
      }
    }

    // Guard against division by zero, is only used for progress report
    if (totalLength == 0) {
      totalLength = 1;
    }
  }

  private void initializePipeline() {
    Properties props = new Properties();
    props.put("annotators", ANNOTATORS);
    pipeline = new StanfordCoreNLP(props);
  }

  private void runAnalysis() {
    pipeline.annotate(annotations.values(), new Function<Annotation, Object>() {
      // called when annotation is finished
      @Override
      public Object apply(Annotation in) {
        progressInChars += in.get(TextAnnotation.class).length();
        double progress = MODEL_LOAD_PROGRESS_FRACTION +
            (progressInChars / totalLength) * (1 - MODEL_LOAD_PROGRESS_FRACTION);
        progressListener.observeProgress(progress);
        return null; // ignored
      }
    });
  }

  private StanfordNLPResult buildResult() {
    return new StanfordNLPResult() {
      @Override
      public Annotation getAnnotationForChapter(Chapter chapter) {
        if (!annotations.containsKey(chapter)) {
          throw new IllegalArgumentException("This chapter has not been analyzed");
        }

        return annotations.get(chapter);
      }
    };
  }
}
