/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: OpenNLPNameFin.java 17967 2014-05-11 16:35:51Z ian_roberts $
 */
package gate.opennlp;

import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

import org.apache.log4j.Logger;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import gate.*;
import gate.creole.*;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import gate.creole.metadata.Sharable;
import gate.util.InvalidOffsetException;


/**
 * Wrapper for the OpenNLP NameFinder
 */
@CreoleResource(name = "OpenNLP NER", 
    comment = "NER PR using a set of OpenNLP maxent models",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:opennlp")
public class OpenNLPNameFin extends AbstractLanguageAnalyser {

  private static final long serialVersionUID = -5507338627058320125L;
  private static final Logger logger = Logger.getLogger(OpenNLPNameFin.class);


  /* CREOLE PARAMETERS & WRAPPED COMPONENTS */
  private String inputASName, outputASName;
  private URL configUrl;
  private Map<NameFinderME, String> finders;
  private Map<String, TokenNameFinderModel> models;


  @Override
  public void execute() throws ExecutionException {
    interrupted = false;
    long startTime = System.currentTimeMillis();
    if(document == null) {
      throw new ExecutionException("No document to process!");
    }
    fireStatusChanged("Running " + this.getName() + " on " + document.getName());
    fireProgressChanged(0);

    AnnotationSet inputAS = document.getAnnotations(inputASName);
    AnnotationSet outputAS = document.getAnnotations(outputASName);
    AnnotationSet sentences = inputAS.get(SENTENCE_ANNOTATION_TYPE);

    int nbrDone = 0;
    int nbrSentences = sentences.size();

    for (Annotation sentence : sentences) {
      /* For each input Sentence annotation, produce a list of
       * Token.string values and the data structure for translating
       * offsets.		   */
      AnnotationSet tokenSet = Utils.getContainedAnnotations(inputAS, sentence, TOKEN_ANNOTATION_TYPE);
      Sentence tokens = new Sentence(tokenSet, TOKEN_STRING_FEATURE_NAME, TOKEN_CATEGORY_FEATURE_NAME);
      String[] strings = tokens.getStrings();

      // Run each NameFinder over the sentence
      for (NameFinderME finder : finders.keySet()) {
        String type = finders.get(finder);
        Span[] spans = finder.find(strings);

        for (Span span : spans) {
          // Translate the offsets and create the output NE annotation
          long start = tokens.getStartOffset(span);
          long end = tokens.getEndOffset(span);
          FeatureMap fm = Factory.newFeatureMap();
          fm.put("source", "OpenNLP");
          try {
            outputAS.add(start, end, type, fm);
          }
          catch (InvalidOffsetException e) {
            throw new ExecutionException(e);
          }

          if(isInterrupted()) { 
            throw new ExecutionInterruptedException("Execution of " + 
                this.getName() + " has been abruptly interrupted!");
          }
        } // end loop over names from 1 finder in 1 sentence
      } // end loop over NameFinders within one sentence
      nbrDone++;
      fireProgressChanged((int)(100 * nbrDone / nbrSentences));
    } // end for sentence : sentences

    fireProcessFinished();
    fireStatusChanged("Finished " + this.getName() + " on " + document.getName()
        + " in " + NumberFormat.getInstance().format(
            (double)(System.currentTimeMillis() - startTime) / 1000)
            + " seconds!");
  }


  @Override
  public Resource init() throws ResourceInstantiationException {
    try {
      loadModels(this.configUrl);
    }
    catch (IOException e) {
      throw new ResourceInstantiationException(e);
    }
    super.init();
    return this;
  }


  private void loadModels(URL configUrl) throws IOException  {
    // Make an empty finder->annotationType table
    this.finders = new HashMap<NameFinderME, String>();
    
    // Load the config file (flat table, 2 columns)
    Properties properties = new Properties();
    InputStream configInput = null;
    try {
      configInput = configUrl.openStream();
      properties.load(configInput);
    }
    finally {
      if (configInput != null) {
        configInput.close();
      }
    }

    // Go through the config entries
    Set<String> modelFiles = properties.stringPropertyNames();
    if(models == null) {
      // models will be non-null if we're duplicating
      models = new HashMap<String, TokenNameFinderModel>();
    }
    for (String filename : modelFiles) {
      InputStream modelInput = null;
      try {
        String type = properties.getProperty(filename);
        if(!models.containsKey(filename)) {
          // Initialize a NameFinder with this model
          URL modelUrl = new URL(configUrl, filename);
          modelInput = modelUrl.openStream();
          models.put(filename, new TokenNameFinderModel(modelInput));
          logger.info("OpenNLP NameFinder: " + modelUrl.toString() + " -> " + type);
        }
        NameFinderME finder = new NameFinderME(models.get(filename));
        // Add it to the table with its annotation type
        this.finders.put(finder, type);
      }
      finally {
        if (modelInput != null) {
          modelInput.close();
        }
      }
    }
  }


  @Override
  public void reInit() throws ResourceInstantiationException {
    models = null;
    init();
  }


 /* CREOLE PARAMETERS */
  
  @RunTime
  @CreoleParameter(defaultValue = "",
      comment = "Input AS with Token and Sentence annotations")
  public void setInputASName(String name) {
    this.inputASName = name;
  }

  public String getInputASName() {
    return this.inputASName;
  }

  @RunTime
  @CreoleParameter(defaultValue = "",
      comment = "Output AS for named entities")
  public void setOutputASName(String name) {
    this.outputASName = name;
  }

  public String getOutputASName() {
    return this.outputASName;
  }

  
  @CreoleParameter(defaultValue = "models/english/en-ner.conf",
      comment = "config file for the NER models")
  public void setConfig(URL model) {
    this.configUrl = model;
  }

  public URL getConfig() {
    return configUrl;
  }
  
  /**
   * For internal use by the duplication mechanism.
   */
  @Sharable
  public void setModelsMap(Map<String, TokenNameFinderModel> models) {
    this.models = models;
  }
  
  /**
   * For internal use by the duplication mechanism.
   */
  public Map<String, TokenNameFinderModel> getModelsMap() {
    return models;
  }

}
