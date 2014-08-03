package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.Chapter;
import edu.stanford.nlp.pipeline.Annotation;

/**
 * The result of stanford NLP analysis
 * 
 * Contains the results of sentence detector, tokenizer, lemmatizer, part-of-speech-tagger, parser
 * and named entity recognizer
 */
public interface StanfordNLPResult {
  /**
   * Gets the stanford NLP results for a single chapter
   * 
   * @param chapter the chapter
   * @return the stanford NLP annotation map
   */
  public Annotation getAnnotationForChapter(Chapter chapter);
}
