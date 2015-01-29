/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: OpenNlpChunker.java 17967 2014-05-11 16:35:51Z ian_roberts $
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

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import org.apache.log4j.Logger;

/**
 * Wrapper PR for the OpenNLP chunker.
 */
@CreoleResource(name = "OpenNLP Chunker", 
    comment = "Chunker using an OpenNLP maxent model",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:opennlp")
public class OpenNlpChunker extends AbstractLanguageAnalyser  {

  private static final long serialVersionUID = 3254481728303447340L;
  private static final Logger logger = Logger.getLogger(OpenNlpChunker.class);


  /* CREOLE PARAMETERS & SUCH*/
  private String inputASName, outputASName, chunkFeature;
  private URL modelUrl;
  private ChunkerModel model;
  private ChunkerME chunker;


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
    boolean sameAS = inputAS.equals(outputAS);

    AnnotationSet sentences = inputAS.get(SENTENCE_ANNOTATION_TYPE);
    int nbrDone = 0;
    int nbrSentences = sentences.size();

    for (Annotation sentence : sentences)  {
      AnnotationSet tokenSet = Utils.getContainedAnnotations(inputAS, sentence, TOKEN_ANNOTATION_TYPE);
      Sentence tokens = new Sentence(tokenSet, TOKEN_STRING_FEATURE_NAME, TOKEN_CATEGORY_FEATURE_NAME);
      String[] strings = tokens.getStrings();
      String[] posTags = tokens.getTags();

      String[] chunkTags = chunker.chunk(strings, posTags);



      for (int i=0 ; i < chunkTags.length ; i++) {

        if (sameAS) { 
          // add feature to existing annotation
          tokens.get(i).getFeatures().put(chunkFeature, chunkTags[i]);
        }
        else { 
          // new annotation with old features and new one
          Annotation oldToken = tokens.get(i);
          long start = oldToken.getStartNode().getOffset();
          long end   = oldToken.getEndNode().getOffset();
          FeatureMap fm = Factory.newFeatureMap();
          fm.putAll(oldToken.getFeatures());
          fm.put(chunkFeature, chunkTags[i]);
          try {
            outputAS.add(start, end, TOKEN_ANNOTATION_TYPE, fm);
          }
          catch (InvalidOffsetException e) {
            throw new ExecutionException(e);
          }
        }
        if(isInterrupted()) { 
          throw new ExecutionInterruptedException("Execution of " + 
              this.getName() + " has been abruptly interrupted!");
        }
      } // end loop over chunk tags within one sentence

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
    if(model == null) {
      InputStream modelInput = null;
      try {
        modelInput = modelUrl.openStream();
        this.model = new ChunkerModel(modelInput);
        logger.info("OpenNLP POS Chunker: " + modelUrl.toString());
      }
      catch(IOException e) {
        throw new ResourceInstantiationException(e);
      }
      finally {
        if (modelInput != null) {
          try {
            modelInput.close();
          }
          catch (IOException e) {
            throw new ResourceInstantiationException(e);
          }
        }
      }
    }
    this.chunker = new ChunkerME(model);

    super.init();
    return this;
  }

  @Override
  public void reInit() throws ResourceInstantiationException {
    model = null;
    init();
  }


  /* CREOLE PARAMETERS */

  @RunTime
  @CreoleParameter(defaultValue = "",
      comment = "annotation set containing tokens and sentences")
  public void setInputASName(String name) {
    this.inputASName = name;
  }

  public String getInputASName() {
    return this.inputASName;
  }

  @RunTime
  @CreoleParameter(defaultValue = "",
      comment = "annotation set for tagged tokens")
  public void setOutputASName(String name) {
    this.outputASName = name;
  }

  public String getOutputASName() {
    return this.outputASName;
  }


  @RunTime
  @CreoleParameter(defaultValue = "chunk",
      comment = "feature for chunk tags")
  public void setChunkFeature(String name) {
    this.chunkFeature = name;
  }

  public String getChunkFeature() {
    return this.chunkFeature;
  }


  @CreoleParameter(defaultValue = "models/english/en-chunker.bin",
      comment = "location of the tagger model")
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
  public void setChunkerModel(ChunkerModel model) {
    this.model = model;
  }
  
  /**
   * For internal use by the duplication mechanism.
   */
  public ChunkerModel getChunkerModel() {
    return model;
  }

}
