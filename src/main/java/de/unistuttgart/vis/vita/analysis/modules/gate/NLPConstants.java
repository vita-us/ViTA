/*
 * NLPConstants.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import gate.creole.ANNIEConstants;

/**
 * Different constants which are needed for the nlp related stuff.
 */
public class NLPConstants {

  public static final String GATE_HOME_DIR = "/gate_home";

  public static final String ANNIE_NLP_PLUGIN_DIR = ANNIEConstants.PLUGIN_DIR;
  public static final String ANNIE_NLP_DEFAULT_FILE = ANNIEConstants.DEFAULT_FILE;
  
  public static final String OPEN_NLP_PLUGIN_DIR = "OpenNLP";
  public static final String OPEN_NLP_DEFAULT_FILE = "opennlp_state.xgapp";
  
  public static final String STANFORD_NLP_PLUGIN_DIR = "Stanford_CoreNLP";
  public static final String STANFORD_NLP_DEFAULT_FILE = "stanford_ner_state.xgapp";
  
  public static final String TYPE_PERSON_STANFORD = "PERSON";
  public static final String TYPE_LOCATION_STANFORD = "LOCATION";

  public static final String FEATURE_GENDER = "gender";

  public static final String LR_TYPE_CORP = "gate.corpora.SerialCorpusImpl";
  public static final String LR_TYPE_DOC = "gate.corpora.DocumentImpl";
}
