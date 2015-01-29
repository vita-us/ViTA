/*
 *  Copyright (c) 2006-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: Parser.java 17831 2014-04-15 09:37:23Z ian_roberts $
 */
package gate.stanford;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Trees;
import edu.stanford.nlp.trees.TypedDependency;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.Resource;
import gate.creole.ANNIEConstants;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.creole.metadata.Sharable;
import gate.util.Files;
import gate.util.InvalidOffsetException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * GATE PR wrapper around the Stanford Parser. This class expects to find Token
 * and Sentence annotations (such as those created by the ANNIE tokenizer and
 * splitter) already in the inputAS and transforms them into suitable data
 * structures, which it feeds to the LexicalizedParser. The parser's output can
 * be stored in the outputAS in various ways, controlled by CREOLE run-time
 * parameters.
 */
@CreoleResource(name = "StanfordParser", comment = "Stanford parser wrapper",
        helpURL = "http://gate.ac.uk/userguide/sec:parsers:stanford")
public class Parser extends AbstractLanguageAnalyser 
implements ProcessingResource {

  private static final long serialVersionUID = -3062171258011850283L;

  protected LexicalizedParser stanfordParser;

  /* Type "SyntaxTreeNode" with feature "cat" is compatible with the 
   * classic SyntaxTreeViewer.  */
  public static final String PHRASE_ANNOTATION_TYPE   = "SyntaxTreeNode" ;
  public static final String PHRASE_CAT_FEATURE      = "cat" ;
  
  /* But "category" feature is compatible with the ANNIE POS tagger.  */
  private static final String  POS_TAG_FEATURE    = ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME;

  public static final String DEPENDENCY_ANNOTATION_TYPE   = "Dependency";
  public static final String DEPENDENCY_ARG_FEATURE       = "args";
  public static final String DEPENDENCY_LABEL_FEATURE     = "kind"; 

  protected String                         annotationSetName;
  private   URL                            parserFile;
  protected boolean                        debugMode;
  private   boolean                        reusePosTags;

  private Map<String, String>              tagMap;
  protected GrammaticalStructureFactory    gsf;
  

  /*  CREOLE parameters for optional mapping  */
  private boolean                          useMapping = false; 
  private URL                              mappingFileURL;
  
  /*  internal variables for mapping  */
  private File                             mappingFile;
  private boolean                          mappingLoaded = false;
  
  /*  CREOLE parameters: what are we going to annotate, and how?  */
  private String   inputSentenceType;
  private String   inputTokenType;
  private boolean  addConstituentAnnotations;
  private boolean  addDependencyFeatures;
  private boolean  addDependencyAnnotations;
  private boolean  addPosTags;
  private boolean  includeExtraDependencies;
  private DependencyMode dependencyMode;
  

  /**
   * The {@link TreebankLangParserParams} implementation to use. This is
   * where we get the language pack, and then the
   * {@link GrammaticalStructureFactory} used to extract the
   * dependencies from the parse. In most cases you should leave this at
   * the default value, which is suitable for English text.
   */
  private String tlppClass;


  /**
   * The name of the feature to add to tokens. The feature value is a
   * {@link List} of {@link DependencyRelation} objects giving the
   * dependencies from this token to other tokens.
   */
  protected String dependenciesFeature = "dependencies";



  /**
   * Parse the current document.  (This is the principal 
   * method called by a CorpusController.)
   */
  public void execute() throws ExecutionException {
    interrupted = false;
    long startTime = System.currentTimeMillis();
    if(document == null) {
      throw new ExecutionException("No document to process!");
    }
    fireStatusChanged("Running " + this.getName() + " on " + document.getName());
    fireProgressChanged(0);

    if (debugMode) {
      System.out.println("Parsing document: " + document.getName());
    }

    if (useMapping && (! mappingLoaded) ) {
      System.err.println("Warning: no mapping loaded!");
    }
    
    checkInterruption();
    if (addConstituentAnnotations || addDependencyFeatures || addDependencyAnnotations || addPosTags) {
      parseSentences(document.getAnnotations(annotationSetName));
    }
    else {
      System.err.println("There is nothing for the parser to do.");
      System.err.println("Please enable at least one of the \"add...\" options.");
    }
    
    fireProcessFinished();
    fireStatusChanged("Finished " + this.getName() + " on " + document.getName()
        + " in " + NumberFormat.getInstance().format(
            (double)(System.currentTimeMillis() - startTime) / 1000)
            + " seconds!");
  }

  
  /**
   * Initialize the Parser resource.  In particular, load the trained data
   * file.
   */
  public Resource init() throws ResourceInstantiationException {
    instantiateStanfordParser();
    if (mappingFile != null) {
      loadTagMapping(mappingFile);
    }

    super.init();
    
    if(tlppClass == null || tlppClass.equals("")) {
      throw new ResourceInstantiationException(
              "TLPP class name must be specified");
    }
    try {
      Class<?> tlppClassObj =
              Class.forName(tlppClass);
      if(!TreebankLangParserParams.class.isAssignableFrom(tlppClassObj)) {
        throw new ResourceInstantiationException(tlppClassObj
                + " does not implement "
                + TreebankLangParserParams.class.getName());
      }
      TreebankLangParserParams tlpp =
              TreebankLangParserParams.class.cast(tlppClassObj.newInstance());
      gsf = tlpp.treebankLanguagePack().grammaticalStructureFactory();
    }
    catch(UnsupportedOperationException e) {
      throw new ResourceInstantiationException(e);
    }
    catch(ClassNotFoundException e) {
      throw new ResourceInstantiationException("Class " + tlppClass
              + " not found", e);
    }
    catch(InstantiationException e) {
      throw new ResourceInstantiationException("Error creating TLPP object", e);
    }
    catch(IllegalAccessException e) {
      throw new ResourceInstantiationException("Error creating TLPP object", e);
    }
    return this;
  }


  /**
   * Re-initialize the Parser resource.  In particular, reload the trained
   * data file.
   */
  @Override 
  public void reInit() throws ResourceInstantiationException {
    stanfordParser = null;
    init();
  }  



  /**
   * Find all the Sentence annotations and iterate through them, parsing one
   * sentence at a time and storing the result in the output AS. (Sentences are
   * scanned for Tokens. You have to run the ANNIE tokenizer and splitter before
   * this PR.)
   * @throws ExecutionInterruptedException 
   */
  private void parseSentences(AnnotationSet annotationSet) throws ExecutionInterruptedException { 
    List<Annotation> sentences = gate.Utils.inDocumentOrder(annotationSet.get(inputSentenceType));
    int sentencesDone = 0;
    int nbrSentences = sentences.size();

    for (Annotation sentence : sentences) {
      parseOneSentence(annotationSet, sentence, sentencesDone, nbrSentences);
      sentencesDone++;
      checkInterruption();
    }
    
    sentencesDone++;
    fireProgressChanged((int)(100 * sentencesDone / nbrSentences));

   }
    


  /**
   * Generate the special data structure for one sentence and pass the List of
   * Word to the parser.  Apply the annotations back to the document.
   * 
   * @param sentence
   *          the Sentence annotation
   * @param s
   *          sentence number of debugging output
   * @param ofS
   *          total number of sentences for debugging output
   * @return  null if the sentence is empty
   * @throws ExecutionInterruptedException 
   */
  private void parseOneSentence(AnnotationSet annotationSet, Annotation sentence, int sentCtr, int sentCount) throws ExecutionInterruptedException {
    Tree tree;
    
    StanfordSentence stanfordSentence = new StanfordSentence(sentence, inputTokenType, annotationSet, reusePosTags);
    if (debugMode) {
      System.out.println(stanfordSentence.toString());
    }

    /* Ignore an empty Sentence (sometimes the regex splitter can create one
     * with no Token annotations in it).
     */
    if ( stanfordSentence.isNotEmpty() ) {
      List<Word> wordList = stanfordSentence.getWordList();

      if (reusePosTags) {
        int nbrMissingTags = stanfordSentence.numberOfMissingPosTags();
        if (nbrMissingTags > 0)  {
          double percentMissing = Math.ceil(100.0 * ((float) nbrMissingTags) /
                  ((float) stanfordSentence.numberOfTokens()) );
          System.err.println("Warning (sentence " + sentCtr + "): " + (int) percentMissing 
                  + "% of the Tokens are missing POS tags." );
        }
      }

      tree = stanfordParser.parseTree(wordList); 
      checkInterruption();

      if (addConstituentAnnotations || addPosTags) {
        annotatePhraseStructureRecursively(annotationSet, stanfordSentence, tree, tree);
      }

      checkInterruption();
      if (addDependencyFeatures || addDependencyAnnotations) {
        annotateDependencies(annotationSet, stanfordSentence, tree);
      }

      if (debugMode) {
        System.out.println("Parsed sentence " + sentCtr + " of " + sentCount);
      }
    }
    
    else if (debugMode) {
      System.out.println("Ignored empty sentence " + sentCtr + " of " + sentCount);
    }
  }


  /**
   * Generate a SyntaxTreeNode Annotation corresponding to this Tree.  Work 
   * recursively so that the annotations are actually generated from the 
   * bottom up, in order to build the consists list of annotation IDs.
   * 
   * @param tree  the current subtree
   * @param rootTree  the whole sentence, used to find the span of the current subtree
   * @return a GATE Annotation of type "SyntaxTreeNode"
   */
  protected Annotation annotatePhraseStructureRecursively(AnnotationSet annotationSet, StanfordSentence stanfordSentence, Tree tree, Tree rootTree) {
    Annotation annotation = null;
    Annotation child;
    String label   = tree.value();

    List<Tree> children = tree.getChildrenAsList();

    if (children.size() == 0) {
      return null;
    }
    /* implied else */

    /* following line generates ClassCastException
     * 		IntPair span = tree.getSpan();
     * edu.stanford.nlp.ling.CategoryWordTag
     * at edu.stanford.nlp.trees.Tree.getSpan(Tree.java:393)
     * but I think it's a bug in the parser, so I'm hacking 
     * around it as follows. */
    int startPos = Trees.leftEdge(tree, rootTree);
    int endPos   = Trees.rightEdge(tree, rootTree);
    
    Long startNode = stanfordSentence.startPos2offset(startPos);
    Long endNode   = stanfordSentence.endPos2offset(endPos);

    List<Integer> consists = new ArrayList<Integer>();

    Iterator<Tree> childIter = children.iterator();
    while (childIter.hasNext()) {
      child = annotatePhraseStructureRecursively(annotationSet, stanfordSentence, childIter.next(), rootTree);
      if  ( (child != null)  &&
        (! child.getType().equals(inputTokenType) )) {
        consists.add(child.getId());
      }
    }
    annotation = annotatePhraseStructureConstituent(annotationSet, startNode, endNode, label, consists, tree.depth());

    return annotation;
  }



  /**
   * Record one constituent as an annotation.
   * 
   * @param startOffset
   * @param endOffset
   * @param label
   * @param consists
   * @param depth
   * @return
   */
  private Annotation annotatePhraseStructureConstituent(AnnotationSet annotationSet, Long startOffset, Long endOffset, String label, 
    List<Integer> consists, int depth) {
    Annotation phrAnnotation = null;
    Integer phrID;

    try {
      String cat;
      if (useMapping && mappingLoaded) {
        cat  = translateTag(label);
      }
      else {
        cat = label; 
      }
      
      if (addConstituentAnnotations) {
        String text = document.getContent().getContent(startOffset, endOffset).toString();
        FeatureMap fm = gate.Factory.newFeatureMap();
        fm.put(PHRASE_CAT_FEATURE, cat);
        fm.put("text", text);

        /* Ignore empty list features on the token-equivalent annotations. */
        if (consists.size() > 0) {
          fm.put("consists", consists);
        }

        phrID  = annotationSet.add(startOffset, endOffset, PHRASE_ANNOTATION_TYPE, fm);
        phrAnnotation = annotationSet.get(phrID);
        recordID(annotationSet, phrID);
      }

      if ( addPosTags && (depth == 1) ) {
        /* Expected to be a singleton set! */
        AnnotationSet tokenSet = annotationSet.get(inputTokenType, startOffset, endOffset);
        if (tokenSet.size() == 1) {
          Annotation token = tokenSet.iterator().next();

          /* Add POS tag to token.  
           * (Note: GATE/Hepple uses "(" and ")" for Penn/Stanford's
           * "-LRB-" and "-RRB-". */
          String hepCat = StanfordSentence.unescapePosTag(cat);
          token.getFeatures().put(POS_TAG_FEATURE, hepCat);
          
        }
        else {
          System.err.println("Found a tokenSet with " + tokenSet.size() + " members!");
        }
      }
    }
    catch (InvalidOffsetException e) {
      e.printStackTrace();
    }
    
    return phrAnnotation;
  }

  
  
  @SuppressWarnings("unchecked")
  private void annotateDependencies(AnnotationSet annotationSet, StanfordSentence stanfordSentence, Tree tree) {
    GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
    Collection<TypedDependency> dependencies = DependencyMode.getDependencies(gs, dependencyMode, includeExtraDependencies);

    if (dependencies == null) {
      if (debugMode) {
        System.out.println("dependencies == null");
      }
      return;
    }
    
    String dependencyKind;
    FeatureMap depFeatures;
    Integer dependentTokenID, governorTokenID;
    List<Integer> argList;
    Long offsetLH0, offsetRH0, offsetLH1, offsetRH1, depLH, depRH;
    Annotation governor, dependent;

    for(TypedDependency dependency : dependencies) {
      if(debugMode) {
        System.out.println(dependency);
      }

      int governorIndex = dependency.gov().label().index() - 1;
      governor  = stanfordSentence.startPos2token(governorIndex);
      
      int dependentIndex = dependency.dep().label().index() - 1;
      dependent = stanfordSentence.startPos2token(dependentIndex);

      dependencyKind = dependency.reln().toString();
      governorTokenID = governor.getId();
      dependentTokenID = dependent.getId();
      
      if (addDependencyFeatures) {
        List<DependencyRelation> depsForTok =
          (List<DependencyRelation>) governor.getFeatures().get(dependenciesFeature);
        
        if(depsForTok == null) {
          depsForTok = new ArrayList<DependencyRelation>();
          governor.getFeatures().put(dependenciesFeature, depsForTok);
        }
        
        depsForTok.add(new DependencyRelation(dependencyKind, dependentTokenID));
      }
      
      if (addDependencyAnnotations) {
        depFeatures = gate.Factory.newFeatureMap();
        argList = new ArrayList<Integer>();
        argList.add(governorTokenID);
        argList.add(dependentTokenID);
        depFeatures.put(DEPENDENCY_ARG_FEATURE, argList);
        depFeatures.put(DEPENDENCY_LABEL_FEATURE, dependencyKind);
        
        offsetLH0 = governor.getStartNode().getOffset();
        offsetRH0 = governor.getEndNode().getOffset();
        offsetLH1 = dependent.getStartNode().getOffset();
        offsetRH1 = dependent.getEndNode().getOffset();
        
        depLH = Math.min(offsetLH0, offsetLH1);
        depRH = Math.max(offsetRH0, offsetRH1);
        
        try {
          annotationSet.add(depLH, depRH, DEPENDENCY_ANNOTATION_TYPE, depFeatures);
        }
        catch(InvalidOffsetException e) {
          e.printStackTrace();
        }  
      }
    }
  }

  

  private void instantiateStanfordParser()
    throws ResourceInstantiationException {
    if(stanfordParser != null) return;
    
    try {
      String filepath = Files.fileFromURL(parserFile).getAbsolutePath();
      stanfordParser = LexicalizedParser.getParserFromSerializedFile(filepath);
    }
    catch(Exception e) {
      throw new ResourceInstantiationException(e);
    }
  }	


  private void loadTagMapping(File mappingFile)  { 
    tagMap = new HashMap<String, String>();
    mappingLoaded = false;

    try {
      if (mappingFile.exists() && mappingFile.canRead()) {

        BufferedReader br = new BufferedReader(new FileReader(mappingFile));
        String line = "";

        // read until it reaches to an end of the file
        while((line = br.readLine()) != null) {
          // two columns delimited by whitespace 
          String [] data = line.split("\\s+", 2);

          // are there key and value available
          if(data == null || data.length < 2) {
            continue;
          } else {
            // and add it to the map
            tagMap.put(data[0].trim(), data[1].trim());
          }
        }

        br.close();
      }

      else {
        System.err.println("Can't find or read mapping file " 
          + mappingFile.getPath() + " so no mappings will be used.");
      }
    } 
    catch(Exception e) {
      System.err.println("Exception trying to load mapping file "
        + mappingFile.getPath());
      e.printStackTrace();
    }

    int nbrMapped = tagMap.size();
    System.out.println("Loaded " + nbrMapped + " mappings from file " + mappingFile);
    mappingLoaded = (nbrMapped > 0);
  }


  /**
   * This method stores the annotation ID as a value of feature "ID" on the
   * relevant annotation. (Mainly to make the ID visible in the GUI for
   * debugging.)
   * 
   * @param annSet
   * @param annotationID
   */
  private void recordID(AnnotationSet annSet, Integer annotationID) {
    annSet.get(annotationID).getFeatures().put("ID", annotationID);
  }

  
  private void checkInterruption() throws ExecutionInterruptedException {
    if(isInterrupted()) { throw new ExecutionInterruptedException(
        "Execution of " + this.getName() + " has been abruptly interrupted!"); }
  }


  /**
   * Translate the tag in the map, or leave it the same if there is no
   * translation.
   * 
   * @param stanfordTag
   * @return
   */
  private String translateTag(String stanfordTag) {
    String translatedTag = stanfordTag;

    if (tagMap.containsKey(stanfordTag)) {
      translatedTag = tagMap.get(stanfordTag);
    }
    
    return translatedTag;
  }


  /* get & set methods for the CREOLE parameters */
  @CreoleParameter(comment = "TreebankLangParserParams implementation used to extract the dependencies",
      defaultValue = "edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams")
  public void setTlppClass(String tlppClass) {
    this.tlppClass = tlppClass;
  }
  
  public String getTlppClass() {
    return tlppClass;
  }


  @Optional
  @RunTime
  @CreoleParameter(comment = "annotationSet used for input (Token and "
      + "Sentence annotations) and output")
  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }

  public String getAnnotationSetName() {
    return this.annotationSetName;
  }

  @CreoleParameter(comment = "path to the parser's grammar file",
      defaultValue = "resources/englishRNN.ser.gz")
  public void setParserFile(URL parserFile) {
    this.parserFile = parserFile;
  }

  public URL getParserFile() {
    return this.parserFile;
  }

  @RunTime
  @CreoleParameter(comment = "The document to be processed")
  public void setDocument(gate.Document document) {
    this.document = document;
  }

  public gate.Document getDocument() {
    return this.document;
  }

  @RunTime
  @CreoleParameter(comment = "verbose mode for debugging",
      defaultValue = "false")
  public void setDebug(Boolean debug) {
    this.debugMode = debug.booleanValue();
  }

  public Boolean getDebug() {
    return new Boolean(this.debugMode);
  }
  
  @RunTime
  @CreoleParameter(comment = "Re-use existing POS tags on tokens",
      defaultValue = "false")
  public void setReusePosTags(Boolean reusePosTags) {
    this.reusePosTags = reusePosTags.booleanValue();
  }

  public Boolean getReusePosTags() {
    return new Boolean(this.reusePosTags);
  }
  
  @RunTime
  @CreoleParameter(comment = "Create POS tags on the Token annotations",
      defaultValue = "false")
  public void setAddPosTags(Boolean posTagTokens) {
    this.addPosTags = posTagTokens.booleanValue();
  }
  
  public Boolean getAddPosTags() {
    return new Boolean(this.addPosTags);
  }

  @RunTime
  @CreoleParameter(comment = "use tag mapping",
      defaultValue = "false")
  public void setUseMapping(Boolean useMapping) {
    this.useMapping = useMapping.booleanValue();
  }
  
  public Boolean getUseMapping() {
    return new Boolean(this.useMapping);
  }
  
  @RunTime
  @CreoleParameter(comment = "Create dependency features on Token annotations",
      defaultValue = "true")
  public void setAddDependencyFeatures(Boolean useDependency) {
    this.addDependencyFeatures = useDependency.booleanValue();
  }
  
  public Boolean getAddDependencyFeatures() {
    return new Boolean(this.addDependencyFeatures);
  }
  
  @RunTime
  @CreoleParameter(comment = "Create annotations to show dependencies",
      defaultValue = "true")
  public void setAddDependencyAnnotations(Boolean useDependency) {
    this.addDependencyAnnotations = useDependency.booleanValue();
  }
  
  public Boolean getAddDependencyAnnotations() {
    return new Boolean(this.addDependencyAnnotations);
  }
  
  
  @RunTime
  @CreoleParameter(comment = "input annotation type for each sentence",
      defaultValue = ANNIEConstants.SENTENCE_ANNOTATION_TYPE )
  public void setInputSentenceType(String sType) {
    this.inputSentenceType = sType;
  }
  
  public String getInputSentenceType() {
    return this.inputSentenceType;
  }
  

  @RunTime
  @CreoleParameter(comment = "input annotation type for each token",
      defaultValue = ANNIEConstants.TOKEN_ANNOTATION_TYPE )
  public void setInputTokenType(String tType) {
    this.inputTokenType = tType;
  }
  
  public String getInputTokenType() {
    return this.inputTokenType;
  }

  
  @RunTime
  @CreoleParameter(comment = "Create annotations to show phrase structures",
      defaultValue = "true")
  public void setAddConstituentAnnotations(Boolean usePhraseStructure) {
    this.addConstituentAnnotations = usePhraseStructure.booleanValue();
  }
  
  public Boolean getAddConstituentAnnotations() {
    return new Boolean(this.addConstituentAnnotations);
  }
  
  
  @RunTime
  @CreoleParameter(comment = "Dependency Mode",
      defaultValue = "Typed")
  public void setDependencyMode(DependencyMode mode) {
    this.dependencyMode = mode;
  }

  public DependencyMode getDependencyMode() {
    return this.dependencyMode;
  }
  
  @RunTime
  @CreoleParameter(comment = "include extra dependencies",
      defaultValue = "false")
  public void setIncludeExtraDependencies(Boolean include) {
    this.includeExtraDependencies = include;
  }
  
  public Boolean getIncludeExtraDependencies() {
    return this.includeExtraDependencies;
  }
  
  
  /* Made mappingFile an init parameter to simplify things.
   * The CREOLE parameter is called "mappingFile" but it's actually a URL.
   */
  @Optional
  @CreoleParameter(comment = "path to the tag mapping file")
  public void setMappingFile(URL mappingFileURL) {
    this.mappingFile = null; // override below
    this.mappingFileURL = mappingFileURL;

    if ( (this.mappingFileURL != null) &&
      (! this.mappingFileURL.toString().trim().equals("")) ) {
      try {
        this.mappingFile = new File(this.mappingFileURL.toURI());
      }
      catch(URISyntaxException e) {
        e.printStackTrace();
      }
    }

  }

  public URL getMappingFile() {
    return this.mappingFileURL;
  }

  /**
   * Inject an existing instance of the LexicalizedParser.
   * <b>This method is intended for use by {@link Factory#ducplicate}
   * and should not be called directly.</b>
   */
  @Sharable
  public void setStanfordParser(LexicalizedParser parser) {
    this.stanfordParser = parser;
  }
  
  /**
   * Get the LexicalizedParser used internally by this PR.
   * <b>This method is intended for use by {@link Factory#ducplicate}
   * and should not be called directly.</b>
   */
  public LexicalizedParser getStanfordParser() {
    return stanfordParser;
  }

}
