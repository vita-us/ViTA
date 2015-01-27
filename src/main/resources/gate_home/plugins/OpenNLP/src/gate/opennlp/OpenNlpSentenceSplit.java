/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: OpenNlpSentenceSplit.java 17967 2014-05-11 16:35:51Z ian_roberts $
 */
package gate.opennlp;

import gate.*;
import gate.creole.*;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import gate.creole.metadata.Sharable;
import gate.util.InvalidOffsetException;
import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.regex.*;
import opennlp.tools.sentdetect.*;
import opennlp.tools.util.Span;
import org.apache.log4j.Logger;


/**
 * Wrapper PR for the OpenNLP sentence splitter.
 */
@CreoleResource(name = "OpenNLP Sentence Splitter", 
    comment = "Sentence splitter using an OpenNLP maxent model",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:opennlp")
public class OpenNlpSentenceSplit extends AbstractLanguageAnalyser {

  
  private static final long serialVersionUID = 3833973991517701119L;
  private static final Logger logger = Logger.getLogger(OpenNlpSentenceSplit.class);

  
  /* CREOLE PARAMETERS & WRAPPED COMPONENTS */
  private String annotationSetName = null;
  private URL modelUrl;
  private SentenceDetectorME splitter = null;
  private SentenceModel model = null;
  
  private Pattern punctEnding;
  

  @Override
  public void execute() throws ExecutionException {
    interrupted = false;
    long startTime = System.currentTimeMillis();
    if(document == null) { throw new ExecutionException(
        "No document to process!"); }
    fireStatusChanged("Running " + this.getName() + " on " + document.getName());
    fireProgressChanged(0);

    AnnotationSet annotations = document.getAnnotations(annotationSetName);
    String text = document.getContent().toString();
    checkInterruption();
    Span[] spans = splitter.sentPosDetect(text);
    Arrays.sort(spans);

    for(int i = 0; i < spans.length; i++) {
      Span span = spans[i];
      checkInterruption();
      FeatureMap fm = Factory.newFeatureMap();
      fm.put("source", "OpenNLP");
      FeatureMap sfm = Factory.newFeatureMap();
      sfm.put("source", "OpenNLP");
      long start = (long)span.getStart();
      long end = (long)span.getEnd();
      long splitStart, splitEnd;
      String splitKind;

      /*
       * If the Sentence ends with 1+ punctuation marks, cover them with an
       * internal Split. Otherwise, put an external Split between this Sentence
       * and the next one (or the document end, if this is the last Sentence ---
       * this can produce a 0-width Split, sorry).
       */
      String sentenceContent = Utils.stringFor(document, start, end);
      Matcher matcher = punctEnding.matcher(sentenceContent);
      if(matcher.find()) {
        splitStart = start + (long)matcher.start();
        splitEnd = end;
        splitKind = "internal";
      } else {
        splitStart = end;
        splitEnd = nextStart(spans, i);
        splitKind = "external";
      }

      try {
        annotations.add(start, end, SENTENCE_ANNOTATION_TYPE, fm);
        sfm.put("kind", splitKind);
        annotations.add(splitStart, splitEnd, "Split", sfm);
      } catch(InvalidOffsetException e) {
        throw new ExecutionException(e);
      }
    }

    fireProcessFinished();
    fireStatusChanged("Finished "
        + this.getName()
        + " on "
        + document.getName()
        + " in "
        + NumberFormat.getInstance().format(
            (double)(System.currentTimeMillis() - startTime) / 1000)
        + " seconds!");
  }

  private long nextStart(Span[] spans, int currentSpan) {
    if(currentSpan < (spans.length - 1)) { return spans[currentSpan + 1]
        .getStart(); }
    // implied else: we're working on the last sentence
    return this.document.getContent().size();
  }

  @Override
  public Resource init() throws ResourceInstantiationException {
    /* TODO: This isn't perfect; it matches all punctuation at the
     * end of a Sentence annotation, e.g., ").".     */
    punctEnding = Pattern.compile("\\p{Punct}+$");

    if(model == null) {
      InputStream modelInput = null;
      try {
        modelInput = modelUrl.openStream();
        this.model = new SentenceModel(modelInput);
        logger.info("OpenNLP Splitter: " + modelUrl.toString());
      } catch(IOException e) {
        throw new ResourceInstantiationException(e);
      } finally {
        if(modelInput != null) {
          try {
            modelInput.close();
          } catch(IOException e) {
            throw new ResourceInstantiationException(e);
          }
        }
      }
    }
    this.splitter = new SentenceDetectorME(model);

    super.init();
    return this;
  }

  
  @Override
  public void reInit() throws ResourceInstantiationException {
    model = null;
    init();
  }

  
  private void checkInterruption() throws ExecutionInterruptedException {
    if(isInterrupted()) { throw new ExecutionInterruptedException(
        "Execution of " + this.getName() + " has been abruptly interrupted!"); }
  }
	
  
	/* CREOLE PARAMETERS */

  @RunTime
  @CreoleParameter(defaultValue = "",
      comment = "annotation set for Sentences")
  public void setAnnotationSetName(String a) {
    annotationSetName = a;
  }

  public String getAnnotationSetName() {
    return annotationSetName;
  }
  
  @CreoleParameter(defaultValue = "models/english/en-sent.bin",
      comment = "location of the splitter model")
  public void setModel(URL model) {
    this.modelUrl = model;
  }

  public URL getModel() {
    return modelUrl;
  }

  /**
   * For internal use by the duplication mechanism.
   */
  @Sharable
  public void setSentenceModel(SentenceModel model) {
    this.model = model;
  }

  /**
   * For internal use by the duplication mechanism.
   */
  public SentenceModel getSentenceModel() {
    return model;
  }

}
