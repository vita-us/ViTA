/*
 * OpenNLPModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.results.OpenNLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;

import java.util.Map;
import java.util.Set;

import gate.Annotation;
import gate.Document;

/**
 * OpenNLP module to analyze the text.
 */
public class OpenNLPModule extends ExtendedAbstractNLPModule<OpenNLPResult> {

  /**
   * Initialize a new OpenNLPModule.
   */
  public OpenNLPModule(){
    super(NLPConstants.OPEN_NLP_PLUGIN_DIR, NLPConstants.OPEN_NLP_DEFAULT_FILE);
  }

  @Override
  protected OpenNLPResult buildResult() {
    return new OpenNLPResultImpl(this.chapterToAnnotation, this.chapterToDoc);
  }
  
  /**
   * Can be returned as result of this module.
   */
  private static class OpenNLPResultImpl extends AbstractNLPResult implements OpenNLPResult{
    public OpenNLPResultImpl(Map<Chapter, Set<Annotation>> chapterToAnnotation,
        Map<Chapter, Document> chapterToDoc){
      super(chapterToAnnotation, chapterToDoc);
    }
  }
}
