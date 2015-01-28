/*
 * Copyright (c) 1995-2013, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Leon Derczynski, 11 Jun 2012
 * 
 * $Id: Tagger.java 15468 2012-02-25 14:41:15Z $
 */

package gate.stanford;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
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
import gate.util.OffsetComparator;

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
 * This class is a wrapper for the Stanford PoS tagger v3.2.0.
 */
@CreoleResource(name = "Stanford POS Tagger", comment = "Stanford Part-of-Speech Tagger", icon = "pos-tagger", helpURL="http://gate.ac.uk/userguide/sec:misc:creole:stanford")
public class Tagger extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = -6001372186847970081L;

  public static final String TAG_DOCUMENT_PARAMETER_NAME = "document";

  public static final String TAG_INPUT_AS_PARAMETER_NAME = "inputASName";

  public static final String TAG_ENCODING_PARAMETER_NAME = "encoding";

  public static final String BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME =
    "baseTokenAnnotationType";

  public static final String OUTPUT_ANNOTATION_TYPE_PARAMETER_NAME =
    "outputAnnotationType";

  public static final String BASE_SENTENCE_ANNOTATION_TYPE_PARAMETER_NAME =
    "baseSentenceAnnotationType";

  public static final String TAG_OUTPUT_AS_PARAMETER_NAME = "outputASName";

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

  @RunTime
  @Optional
  @CreoleParameter(comment = "Should all Tokens be POS tagged or just those within baseSentenceAnnotationType?", defaultValue = "true")
  public void setPosTagAllTokens(Boolean allTokens) {
    posTagAllTokens = allTokens;
  }

  public Boolean getPosTagAllTokens() {
    return posTagAllTokens;
  }

  // should all Tokens be POS tagged or just those within
  // baseSentenceAnnotationType
  protected Boolean posTagAllTokens = true;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Should existing " + TOKEN_CATEGORY_FEATURE_NAME +
     " features on input annotations be respected (true) or ignored (false)?",
     defaultValue = "true")
  public void setUseExistingTags(Boolean useTags) {
    useExistingTags = useTags;
  }

  public Boolean getUseExistingTags() {
    return useExistingTags;
  }
  private Boolean useExistingTags;

  protected Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public Resource init() throws ResourceInstantiationException {
    if(tagger == null) {
      try {
        tagger = new MaxentTagger(modelFile.toExternalForm());
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

    if(baseTokenAnnotationType == null ||
      baseTokenAnnotationType.trim().length() == 0) { throw new ExecutionException(
      "No base Token Annotation Type provided!"); }

    if(baseSentenceAnnotationType == null ||
      baseSentenceAnnotationType.trim().length() == 0) { throw new ExecutionException(
      "No base Sentence Annotation Type provided!"); }

    if(outputAnnotationType == null ||
      outputAnnotationType.trim().length() == 0) { throw new ExecutionException(
      "No AnnotationType provided to store the new feature!"); }

    AnnotationSet sentencesAS = inputAS.get(baseSentenceAnnotationType);
    AnnotationSet tokensAS = inputAS.get(baseTokenAnnotationType);
    if(sentencesAS != null && sentencesAS.size() > 0 && tokensAS != null &&
      tokensAS.size() > 0) {
      long startTime = System.currentTimeMillis();
      fireStatusChanged("POS tagging " + document.getName());
      fireProgressChanged(0);
      // prepare the input for MaxentTagger
      List<Word> sentenceForTagger = new ArrayList<Word>();

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
      while(sentencesIter.hasNext()) {
        Annotation currentSentence = sentencesIter.next();
        tokensInCurrentSentence.clear();
        sentenceForTagger.clear();
        while(currentToken != null &&
          currentToken.getEndNode().getOffset()
            .compareTo(currentSentence.getEndNode().getOffset()) <= 0) {
          // If we're only POS tagging Tokens within baseSentenceAnnotationType,
          // don't add the sentence if the Tokens aren't within the span of
          // baseSentenceAnnotationType
          if(posTagAllTokens || currentToken.withinSpanOf(currentSentence)) {
            tokensInCurrentSentence.add(currentToken);

            if(useExistingTags && currentToken.getFeatures().containsKey(
                  TOKEN_CATEGORY_FEATURE_NAME)) {
              sentenceForTagger.add(new TaggedWord(
                    (String)currentToken.getFeatures()
                      .get(TOKEN_STRING_FEATURE_NAME),
                    (String)currentToken.getFeatures()
                      .get(TOKEN_CATEGORY_FEATURE_NAME)));
            } else {
              sentenceForTagger.add(new Word((String)currentToken.getFeatures()
                .get(TOKEN_STRING_FEATURE_NAME)));
            }
          }
          currentToken = (tokensIter.hasNext() ? tokensIter.next() : null);
        }

        // if the sentence doesn't contain any tokens (which is a bit weird but
        // is possible) then don't try running the POS tagger as you will get an
        // array index out of bounds exception
        if(sentenceForTagger.isEmpty()) continue;

        // run the POS tagger
        List<TaggedWord> taggerResults =
          tagger.tagSentence(sentenceForTagger, useExistingTags);

        // add the results
        // make sure no malfunction occurred
        if(taggerResults.size() != tokensInCurrentSentence.size())
          throw new ExecutionException(
            "POS Tagger malfunction: the output size (" +
              taggerResults.size() + ") is different from the input size (" +
              tokensInCurrentSentence.size() + ")!");
        Iterator<TaggedWord> resIter = taggerResults.iterator();
        Iterator<Annotation> tokIter = tokensInCurrentSentence.iterator();
        while(resIter.hasNext()) {
          Annotation annot = tokIter.next();
          addFeatures(annot, TOKEN_CATEGORY_FEATURE_NAME, ((String)resIter
            .next().tag()));
        }
        fireProgressChanged(sentIndex++ * 100 / sentCnt);
      }// while(sentencesIter.hasNext())

      if(currentToken != null && posTagAllTokens) {
        // Tag remaining Tokens if we are not considering those only within
        // baseSentenceAnnotationType

        // we have remaining tokens after the last sentence
        tokensInCurrentSentence.clear();
        sentenceForTagger.clear();
        while(currentToken != null) {
          tokensInCurrentSentence.add(currentToken);
          if(useExistingTags && currentToken.getFeatures().containsKey(
                TOKEN_CATEGORY_FEATURE_NAME)) {
            sentenceForTagger.add(new TaggedWord(
                  (String)currentToken.getFeatures()
                    .get(TOKEN_STRING_FEATURE_NAME),
                  (String)currentToken.getFeatures()
                    .get(TOKEN_CATEGORY_FEATURE_NAME)));
          } else {
            sentenceForTagger.add(new Word((String)currentToken.getFeatures()
              .get(TOKEN_STRING_FEATURE_NAME)));
          }
          currentToken = (tokensIter.hasNext() ? tokensIter.next() : null);
        }

        // run the POS tagger on remaining tokens
        List<TaggedWord> taggerResults =
          tagger.tagSentence(sentenceForTagger, useExistingTags);

        // add the results and make sure no malfunction occurred
        if(taggerResults.size() != tokensInCurrentSentence.size())
          throw new ExecutionException(
            "POS Tagger malfunction: the output size (" + taggerResults.size() +
              ") is different from the input size (" +
              tokensInCurrentSentence.size() + ")!");
        Iterator<TaggedWord> resIter = taggerResults.iterator();
        Iterator<Annotation> tokIter = tokensInCurrentSentence.iterator();
        while(resIter.hasNext()) {
          Annotation annot = tokIter.next();
          addFeatures(annot, TOKEN_CATEGORY_FEATURE_NAME, ((String)resIter
            .next().tag()));
        }
      }// if(currentToken != null)
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
            "POS tagger: no sentence or token annotations in input document - see debug log for details.");
        logger.debug("No input annotations in document " + document.getName());
      }
    }

  }

  protected void addFeatures(Annotation annot, String featureName,
                             String featureValue) throws GateRuntimeException {
    String tempIASN = inputASName == null ? "" : inputASName;
    String tempOASN = outputASName == null ? "" : outputASName;
    if(outputAnnotationType.equals(baseTokenAnnotationType) &&
      tempIASN.equals(tempOASN)) {
      annot.getFeatures().put(featureName, featureValue);
      return;
    } else {
      int start = annot.getStartNode().getOffset().intValue();
      int end = annot.getEndNode().getOffset().intValue();

      // get the annotations of type outputAnnotationType
      AnnotationSet outputAS = document.getAnnotations(outputASName);
      AnnotationSet annotations = outputAS.get(outputAnnotationType);
      if(annotations == null || annotations.size() == 0) {
        // add new annotation
        FeatureMap features = Factory.newFeatureMap();
        features.put(featureName, featureValue);
        try {
          outputAS.add(new Long(start), new Long(end), outputAnnotationType,
            features);
        } catch(Exception e) {
          throw new GateRuntimeException("Invalid Offsets");
        }
      } else {
        // search for the annotation if there is one with the same start and end
        // offsets
        ArrayList<Annotation> tempList =
          new ArrayList<Annotation>(annotations.get());
        boolean found = false;
        for(int i = 0; i < tempList.size(); i++) {
          Annotation annotation = (Annotation)tempList.get(i);
          if(annotation.getStartNode().getOffset().intValue() == start &&
            annotation.getEndNode().getOffset().intValue() == end) {
            // this is the one
            annotation.getFeatures().put(featureName, featureValue);
            found = true;
            break;
          }
        }

        if(!found) {
          // add new annotation
          FeatureMap features = Factory.newFeatureMap();
          features.put(featureName, featureValue);
          try {
            outputAS.add(new Long(start), new Long(end), outputAnnotationType,
              features);
          } catch(Exception e) {
            throw new GateRuntimeException("Invalid Offsets");
          }
        }
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

  public String getOutputAnnotationType() {
    return this.outputAnnotationType;
  }

  @RunTime
  @CreoleParameter(comment = "Annotation type for what should be considered as atomic words to PoS tag", defaultValue = "Token")
  public void setBaseTokenAnnotationType(String baseTokenAnnotationType) {
    this.baseTokenAnnotationType = baseTokenAnnotationType;
  }

  @RunTime
  @CreoleParameter(comment = "Sentence-level annotation type", defaultValue = "Sentence")
  public void setBaseSentenceAnnotationType(String baseSentenceAnnotationtype) {
    this.baseSentenceAnnotationType = baseSentenceAnnotationtype;
  }

  @RunTime
  @CreoleParameter(comment = "Output annotation type for words (e.g. Token)", defaultValue = "Token")
  public void setOutputAnnotationType(String outputAnnotationType) {
    this.outputAnnotationType = outputAnnotationType;
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

  @CreoleParameter(comment = "Path to the tagger's model file", defaultValue = "resources/english-left3words-distsim.tagger", suffixes="tagger;model")
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
  public void setTagger(MaxentTagger tagger) {
    this.tagger = tagger;
  }

  /**
   * For internal use by the duplication mechanism only.
   */
  public MaxentTagger getTagger() {
    return this.tagger;
  }

  protected MaxentTagger tagger;

  private String inputASName;

  private String encoding;

  private String baseTokenAnnotationType;

  private String baseSentenceAnnotationType;

  private String outputAnnotationType;

  private String outputASName;

  private URL modelFile;
}
