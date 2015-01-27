/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: OpenNlpTokenizer.java 17967 2014-05-11 16:35:51Z ian_roberts $
 */
package gate.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import org.apache.log4j.Logger;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import gate.*;
import gate.creole.*;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import gate.creole.metadata.Sharable;
import gate.util.InvalidOffsetException;

/**
 * Wrapper PR for the OpenNLP tokenizer.
 */
@CreoleResource(name = "OpenNLP Tokenizer", 
    comment = "Tokenizer using an OpenNLP maxent model",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:opennlp")
public class OpenNlpTokenizer extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = 6965074842061250720L;
  private static final Logger logger = Logger.getLogger(OpenNlpTokenizer.class);

  /* CREOLE PARAMETERS & WRAPPED COMPONENTS */
  private String annotationSetName = null;
  private URL modelUrl;
  private TokenizerME tokenizer = null;
  private TokenizerModel model = null;  

  
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
    Span[] spans = tokenizer.tokenizePos(text);

    /*
     * The spans ought to be ordered, but the OpenNLP API is unclear. We need to
     * be sure they are in order so we can spot the gaps and put Space Token
     * annotations on them.
     */
    Arrays.sort(spans);
    int previousEnd = 0;

    for(Span span : spans) {
      checkInterruption();
      int tokenStart = span.getStart();
      int tokenEnd = span.getEnd();

      if(tokenStart > previousEnd) {
        FeatureMap sfm = Factory.newFeatureMap();
        sfm.put("source", "OpenNLP");
        sfm.put(TOKEN_STRING_FEATURE_NAME,
            text.substring(previousEnd, tokenStart));
        sfm.put(TOKEN_LENGTH_FEATURE_NAME, tokenStart - previousEnd);
        try {
          annotations.add((long)previousEnd, (long)tokenStart,
              SPACE_TOKEN_ANNOTATION_TYPE, sfm);
        } catch(InvalidOffsetException e) {
          throw new ExecutionException(e);
        }
      }

      previousEnd = tokenEnd;

      FeatureMap fm = Factory.newFeatureMap();
      fm.put("source", "OpenNLP");
      fm.put(TOKEN_STRING_FEATURE_NAME, text.substring(tokenStart, tokenEnd));
      fm.put(TOKEN_LENGTH_FEATURE_NAME, span.length());
      try {
        annotations.add((long)tokenStart, (long)tokenEnd,
            TOKEN_ANNOTATION_TYPE, fm);
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

  
  private void checkInterruption() throws ExecutionInterruptedException {
    if(isInterrupted()) { throw new ExecutionInterruptedException(
        "Execution of " + this.getName() + " has been abruptly interrupted!"); }
  }

  
  public Resource init() throws ResourceInstantiationException {
    if(model == null) {
      InputStream modelInput = null;
      try {
        modelInput = modelUrl.openStream();
        this.model = new TokenizerModel(modelInput);
        logger.info("OpenNLP Tokenizer: " + modelUrl.toString());
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
    this.tokenizer = new TokenizerME(model);

    super.init();
    return this;
  }
  

  public void reInit() throws ResourceInstantiationException {
    model = null;
    init();
  }

  
  /* CREOLE PARAMETERS */

  @RunTime
  @CreoleParameter(defaultValue = "",
      comment = "Output AS for Tokens")
  public void setAnnotationSetName(String a) {
    annotationSetName = a;
  }

  public String getAnnotationSetName() {
    return annotationSetName;
  }
  
  
  @CreoleParameter(defaultValue = "models/english/en-token.bin",
      comment = "location of the tokenizer model")
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
  public void setTokenizerModel(TokenizerModel model) {
    this.model = model;
  }
  
  /**
   * For internal use by the duplication mechanism.
   */
  public TokenizerModel getTokenizerModel() {
    return model;
  }
}
