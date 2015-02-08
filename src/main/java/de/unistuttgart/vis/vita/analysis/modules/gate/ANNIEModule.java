/*
 * ANNIEModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;

import java.util.Map;
import java.util.Set;

import gate.Annotation;
import gate.Document;

/**
 * Gate ANNIE module which searches for persons and locations.
 */
public class ANNIEModule extends ExtendedAbstractNLPModule<AnnieNLPResult> {

  /**
   * Initialize a new ANNIEModule.
   */
  public ANNIEModule() {
    super(NLPConstants.ANNIE_NLP_PLUGIN_DIR, NLPConstants.ANNIE_NLP_DEFAULT_FILE);
  }

  @Override
  protected AnnieNLPResult buildResult() {
    return new AnnieNLPResultImpl(this.chapterToAnnotation, this.chapterToDoc);
  }

  /**
   * Can be returned as result of this module.
   */
  private static class AnnieNLPResultImpl extends AbstractNLPResult implements AnnieNLPResult{
    public AnnieNLPResultImpl(Map<Chapter, Set<Annotation>> chapterToAnnotation,
        Map<Chapter, Document> chapterToDoc){
      super(chapterToAnnotation, chapterToDoc);
    }
  }  
  
}
