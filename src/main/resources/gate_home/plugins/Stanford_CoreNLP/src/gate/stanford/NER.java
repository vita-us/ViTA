/*
 * Copyright (c) 1995-2013, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Leon Derczynski, 22 Oct 2013
 * 
 * $Id: NER.java 15468 2013-10-22 21:13:15Z $
 */

package gate.stanford;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ie.*;
import edu.stanford.nlp.ie.crf.CRFClassifier;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.creole.metadata.Sharable;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import gate.util.OffsetComparator;
import gate.util.SimpleFeatureMapImpl;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class is a wrapper for the Stanford NER tool v3.2.0.
 */
@CreoleResource(name = "Stanford NER", comment = "Stanford Named Entity Recogniser", icon = "ne-transducer", helpURL="http://gate.ac.uk/userguide/sec:misc:creole:stanford")
public class NER extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = -6001372186847970080L;

  public static final String TAG_DOCUMENT_PARAMETER_NAME = "document";

  public static final String TAG_INPUT_AS_PARAMETER_NAME = "inputASName";

  public static final String TAG_ENCODING_PARAMETER_NAME = "encoding";

  public static final String BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME =
    "baseTokenAnnotationType";

  public static final String BASE_SENTENCE_ANNOTATION_TYPE_PARAMETER_NAME =
    "baseSentenceAnnotationType";

  public static final String TAG_OUTPUT_AS_PARAMETER_NAME = "outputASName";

  public static final String TAG_OUTSIDE_LABEL = "outsideLabel";

  @RunTime
  @Optional
  @CreoleParameter(comment = "Throw an exception when there are none of the required input annotations", defaultValue = "true")
  public void setFailOnMissingInputAnnotations(Boolean fail) {
    failOnMissingInputAnnotations = fail;
  }

  public Boolean getFailOnMissingInputAnnotations() {
    return failOnMissingInputAnnotations;
  }

  protected Boolean failOnMissingInputAnnotations = true;

  protected Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public Resource init() throws ResourceInstantiationException {
    if(tagger == null) {
      fireStatusChanged("Loading Stanford NER model");
      try {
        // nasty workaround for stanford NER's path format inconsistency - tagger is content with uris beginning file:, ner labeller is not
        tagger = CRFClassifier.getClassifier(modelFile.toString().substring(5));
      } catch(Exception e) {
        throw new ResourceInstantiationException(e);
      }
    }
    return this;
  }

  @Override
  public void reInit() throws ResourceInstantiationException {
    tagger = null;
    init();
  }

  @Override
  public void execute() throws ExecutionException {
    // check the parameters
    if(document == null)
      throw new ExecutionException("No document to process!");

    AnnotationSet inputAS = document.getAnnotations(inputASName);
    AnnotationSet outputAS = document.getAnnotations(outputASName);

    if(baseTokenAnnotationType == null ||
      baseTokenAnnotationType.trim().length() == 0) { throw new ExecutionException(
      "No base Token Annotation Type provided!"); }

    if(baseSentenceAnnotationType == null ||
      baseSentenceAnnotationType.trim().length() == 0) { throw new ExecutionException(
      "No base Sentence Annotation Type provided!"); }

    AnnotationSet sentencesAS = inputAS.get(baseSentenceAnnotationType);
    AnnotationSet tokensAS = inputAS.get(baseTokenAnnotationType);
    if(sentencesAS != null && sentencesAS.size() > 0 && tokensAS != null &&
      tokensAS.size() > 0) {
      long startTime = System.currentTimeMillis();
      fireStatusChanged("NER searching " + document.getName());
      fireProgressChanged(0);

      // prepare the input for CRFClassifier
      List<CoreLabel> sentenceForTagger = new ArrayList<CoreLabel>();

      // define a comparator for annotations by start offset
      OffsetComparator offsetComparator = new OffsetComparator();

      // read all the tokens and all the sentences
      List<Annotation> sentencesList = new ArrayList<Annotation>(sentencesAS);
      Collections.sort(sentencesList, offsetComparator);
      List<Annotation> tokensList = new ArrayList<Annotation>(tokensAS);
      Collections.sort(tokensList, offsetComparator);

      Iterator<Annotation> sentencesIter = sentencesList.iterator();
      ListIterator<Annotation> tokensIter = tokensList.listIterator();

      List<Annotation> tokensInCurrentSentence = new ArrayList<Annotation>();
      Annotation currentToken = tokensIter.next();
      int sentIndex = 0;
      int sentCnt = sentencesAS.size();

      // go through sentence annotations in the document
      while(sentencesIter.hasNext()) {
        Annotation currentSentence = sentencesIter.next();

        // reset sentence-level processing variables
        tokensInCurrentSentence.clear();
        sentenceForTagger.clear();

        // while we have sane tokens
        while(currentToken != null && 
          currentToken.getEndNode().getOffset()
            .compareTo(currentSentence.getEndNode().getOffset()) <= 0) {

          // If we're only labelling Tokens within baseSentenceAnnotationType,
          // don't add the sentence if the Tokens aren't within the span of
          // baseSentenceAnnotationType
          if(currentToken.withinSpanOf(currentSentence)) {
            tokensInCurrentSentence.add(currentToken);

            // build a stanford nlp representation of the token and add it to the sequence
            CoreLabel currentLabel = new CoreLabel();
            currentLabel.setWord((String)currentToken.getFeatures().get(TOKEN_STRING_FEATURE_NAME));

            sentenceForTagger.add(currentLabel);
          }
          currentToken = (tokensIter.hasNext() ? tokensIter.next() : null);
        }

        // if the sentence doesn't contain any tokens (which is a bit weird but
        // is possible) then don't try running the labeller
        if(sentenceForTagger.isEmpty()) continue;

        // run the labeller
        List<CoreLabel> taggerResults =
          tagger.classifySentence(sentenceForTagger);

        // add the results
        // make sure no malfunction occurred
        if(taggerResults.size() != tokensInCurrentSentence.size())
          throw new ExecutionException(
            "NER labeller malfunction: the output size (" +
              taggerResults.size() + ") is different from the input size (" +
              tokensInCurrentSentence.size() + ")!");

        // proceed through the annotated sequence
        Iterator<CoreLabel> resIter = taggerResults.iterator();
        Iterator<Annotation> tokIter = tokensInCurrentSentence.iterator();

        String previousLabel = outsideLabel;
        Long previousEnd = new Long(-1);
        Long entityStart = new Long(-1);
        Long entityEnd = new Long(-1);

        Annotation annot;
        String nerLabel = "";

        while(resIter.hasNext()) {

          // for each labelled token..
          annot = tokIter.next();
          CoreLabel word = resIter.next();
          nerLabel = word.get(CoreAnnotations.AnswerAnnotation.class);

          // falling edge transition: entity ends
          // guard against this triggering at document start
          if (!nerLabel.equals(previousLabel) && !previousLabel.equals(outsideLabel) && entityStart != -1) {

//            System.out.println("falling edge");
            // get final bound; add new annotation in output AS
            try {
              outputAS.add(entityStart, previousEnd, previousLabel, new SimpleFeatureMapImpl());
            } catch (InvalidOffsetException e) {
              System.out.println("Token alignment problem:" + e);
            }

          }

          // rising edge transition: entity starts
          if (!nerLabel.equals(previousLabel) && !nerLabel.equals(outsideLabel)) {
//            System.out.println("rising edge");
            entityStart = annot.getStartNode().getOffset();
          }
//          System.out.println(word.word() + "/" + nerLabel);

          previousLabel = nerLabel;
          previousEnd = annot.getEndNode().getOffset();

        }

        // clean up, in case last token in sentence was in an entity
        if (!nerLabel.equals(outsideLabel)) {
          try {
            outputAS.add(entityStart, previousEnd, previousLabel, new SimpleFeatureMapImpl());
          } catch (InvalidOffsetException e) {
            System.out.println("Token alignment problem:" + e);
          }
        }

        fireProgressChanged(sentIndex++ * 100 / sentCnt);

      }

      fireProcessFinished();
      fireStatusChanged(document.getName() +
        " tagged in " +
        NumberFormat.getInstance().format(
          (double)(System.currentTimeMillis() - startTime) / 1000) +
        " seconds!");
    } else {
      if(failOnMissingInputAnnotations) {
        throw new ExecutionException(
          "No sentences or tokens to process in document " +
            document.getName() + "\n" + "Please run a sentence splitter " +
            "and tokeniser first!");
      } else {
        Utils
          .logOnce(
            logger,
            Level.INFO,
            "NE labeller: no sentence or token annotations in input document - see debug log for details.");
        logger.debug("No input annotations in document " + document.getName());
      }
    }

  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Input annotation set name", defaultValue = "")
  public void setInputASName(String newInputASName) {
    inputASName = newInputASName;
  }

  public String getInputASName() {
    return inputASName;
  }

  public String getEncoding() {
    return this.encoding;
  }

  public String getBaseTokenAnnotationType() {
    return this.baseTokenAnnotationType;
  }

  public String getBaseSentenceAnnotationType() {
    return this.baseSentenceAnnotationType;
  }

  @RunTime
  @CreoleParameter(comment = "Annotation type for what should be considered as atomic words to label with NEs", defaultValue = "Token")
  public void setBaseTokenAnnotationType(String baseTokenAnnotationType) {
    this.baseTokenAnnotationType = baseTokenAnnotationType;
  }

  @RunTime
  @CreoleParameter(comment = "Sentence-level annotation type", defaultValue = "Sentence")
  public void setBaseSentenceAnnotationType(String baseSentenceAnnotationtype) {
    this.baseSentenceAnnotationType = baseSentenceAnnotationtype;
  }

  public String getOutputASName() {
    return this.outputASName;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Output annotation set name", defaultValue = "")
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }


  @RunTime
  @CreoleParameter(comment = "Label used by model for tokens outside entities", defaultValue = "O")
  public void setOutsideLabel(String outsideLabel) {
    this.outsideLabel = outsideLabel;
  }

  public String getOutsideLabel() {
    return this.outsideLabel;
  }


  @CreoleParameter(comment = "Path to the NER model file", defaultValue = "resources/english.all.3class.distsim.crf.ser.gz", suffixes="tagger;model;gz")
  public void setModelFile(URL modelFile) {
    this.modelFile = modelFile;
  }



  public URL getModelFile() {
    return this.modelFile;
  }

  /**
   * For internal use by the duplication mechanism only.
   */
  @Sharable
  public void setTagger(AbstractSequenceClassifier<CoreLabel> tagger) {
    this.tagger = tagger;
  }

  /**
   * For internal use by the duplication mechanism only.
   */
  public AbstractSequenceClassifier<CoreLabel> getTagger() {
    return this.tagger;
  }

  protected AbstractSequenceClassifier<CoreLabel> tagger;

  private String inputASName;

  private String encoding;

  private String baseTokenAnnotationType;

  private String baseSentenceAnnotationType;

  private String outputASName;

  private String outsideLabel;

  private URL modelFile;
}
