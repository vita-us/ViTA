/*
 * StanfordNLPModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.results.StanfordNLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

/**
 * StanfordCoreNLP module to analyze the text.
 */
public class StanfordNLPModule extends ExtendedAbstractNLPModule<StanfordNLPResult> {

  /**
   * Initialize a new StanfordNLPModule.
   */
  public StanfordNLPModule() {
    super(NLPConstants.STANFORD_NLP_PLUGIN_DIR, NLPConstants.STANFORD_NLP_DEFAULT_FILE);
  }
  
  @Override
  protected StanfordNLPResult buildResult() {
    return new StanfordNLPResultImpl(this.chapterToAnnotation, this.chapterToDoc);
  }

  /**
   * Need to be overridden because stanford has slightly different annotation types for persons and
   * locations.
   */
  @Override
  protected void createResultMap() {
    for (Object docObj : corpus) {
      Document doc = (Document) docObj;
      AnnotationSet defaultAnnotSet = doc.getAnnotations();
      Set<String> annotTypesRequired = new HashSet<>();
      annotTypesRequired.add(NLPConstants.TYPE_PERSON_STANFORD);
      annotTypesRequired.add(NLPConstants.TYPE_LOCATION_STANFORD);
      Set<Annotation> peopleAndPlaces = new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
      chapterToAnnotation.put(docToChapter.get(doc), peopleAndPlaces);
    }
  }
  
  /**
   * Can be returned as result of this module.
   */
  private static class StanfordNLPResultImpl extends AbstractNLPResult implements StanfordNLPResult{
    public StanfordNLPResultImpl(Map<Chapter, Set<Annotation>> chapterToAnnotation,
        Map<Chapter, Document> chapterToDoc){
      super(chapterToAnnotation, chapterToDoc);
    }
  }
  
}
