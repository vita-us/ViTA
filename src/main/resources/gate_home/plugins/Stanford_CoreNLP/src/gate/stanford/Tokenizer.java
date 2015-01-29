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
 * $Id: Tokenizer.java 15468 2013-10-22 21:13:15Z $
 */

package gate.stanford;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

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

import java.io.StringReader;
import java.io.IOException;
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
 * This class is a wrapper for the Stanford Tokenizer v3.2.0.
 */
@CreoleResource(name = "Stanford PTB Tokenizer", comment = "Stanford Penn Treebank v3 Tokenizer, for English", icon = "tokeniser", helpURL="http://gate.ac.uk/userguide/sec:misc:creole:stanford")
public class Tokenizer extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = -6001371186847970080L;

  public static final String TAG_DOCUMENT_PARAMETER_NAME = "document";

  public static final String TAG_INPUT_AS_PARAMETER_NAME = "inputASName";

  public static final String TAG_ENCODING_PARAMETER_NAME = "encoding";

  public static final String TAG_OUTPUT_AS_PARAMETER_NAME = "outputASName";

  public static final String TOKEN_LABEL = "tokenLabel";

  public static final String SPACE_LABEL = "spaceLabel";

  public static final String TOKEN_STRING_FEATURE = "string";

  @RunTime
  @Optional
  @CreoleParameter(comment = "Throw an exception when there are none of the required input annotations", defaultValue = "false")
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
    return this;
  }

  @Override
  public void reInit() throws ResourceInstantiationException {
    init();
  }

  @Override
  public void execute() throws ExecutionException {
    // check the parameters
    if(document == null)
      throw new ExecutionException("No document to process!");

    AnnotationSet inputAS = document.getAnnotations(inputASName);
    AnnotationSet outputAS = document.getAnnotations(outputASName);


    long startTime = System.currentTimeMillis();
    fireStatusChanged("Tokenising " + document.getName());
    fireProgressChanged(0); 


    // tokenising goes here
    String rawText = "";
    try {
      rawText = document.getContent().getContent(new Long(0), document.getContent().size()).toString();
    } catch (Exception e) {
      System.out.println("Document content offsets wrong: " + e);
    }

    PTBTokenizer<CoreLabel> ptbt;
    try {
      ptbt = new PTBTokenizer<CoreLabel>(new StringReader(rawText), new CoreLabelTokenFactory(), "invertible=true");
    } catch (Exception e) {
      System.out.println("Failed when calling tokenizer: " + e);
      return;
    }

    Long tokenStart;
    Long tokenEnd;
    Long prevTokenEnd = new Long(0); // this default value lets us capture leading spaces

    for (CoreLabel label; ptbt.hasNext(); ) {
      label = ptbt.next();
      tokenStart = new Long(label.beginPosition());
      tokenEnd = new Long(label.endPosition());


      SimpleFeatureMapImpl tokenMap = new SimpleFeatureMapImpl();

      // add the token annotation
      try {
        tokenMap.put(TOKEN_STRING_FEATURE, document.getContent().getContent(tokenStart, tokenEnd).toString());
        outputAS.add(tokenStart, tokenEnd, tokenLabel, tokenMap);
      } catch (InvalidOffsetException e) {
        System.out.println("Token alignment problem:" + e);
      }

      // do we need to add a space annotation?
      if (tokenStart > prevTokenEnd) {
        try {
          outputAS.add(prevTokenEnd, tokenStart, spaceLabel, new SimpleFeatureMapImpl());
        } catch (InvalidOffsetException e) {
          System.out.println("Space token alignment problem:" + e);
        }

      }

      prevTokenEnd = tokenEnd;

    }


    fireProcessFinished();
    fireStatusChanged(document.getName() +
      " tokenised in " +
      NumberFormat.getInstance().format(
        (double)(System.currentTimeMillis() - startTime) / 1000) +
      " seconds!");
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

  public String getOutputASName() {
    return this.outputASName;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Output annotation set name", defaultValue = "")
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }


  public String getTokenLabel() {
    return this.tokenLabel;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Annotation type for tokens", defaultValue = "Token")
  public void setTokenLabel(String tokenLabel) {
    this.tokenLabel = tokenLabel;
  }

  public String getSpaceLabel() {
    return this.spaceLabel;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "Annotation type for spaces", defaultValue = "SpaceToken")
  public void setSpaceLabel(String spaceLabel) {
    this.spaceLabel = spaceLabel;
  }

  private String inputASName;

  private String encoding;

  private String outputASName;

  private String tokenLabel;

  private String spaceLabel;

}
