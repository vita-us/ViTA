package gate.opennlp;

import gate.Annotation;
import gate.AnnotationSet;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.Span;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Wrapper for the opennlp parser
 */
@CreoleResource(name="OpenNLP Parser",
    comment = "Syntactic parser from Apache OpenNLP",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:opennlp")
public class OpenNlpParser extends AbstractLanguageAnalyser {

  public static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(OpenNlpChunker.class);

  String inputASName;

  public String getInputASName() {
    return inputASName;
  }

  @CreoleParameter
  @RunTime
  @Optional
  public void setInputASName(String inputASName) {
    this.inputASName = inputASName;
  }

  Parser parser = null;

  private URL model;

  private AnnotationSet annotations;

  @Override
  public void execute() throws ExecutionException {
    // text doc annotations
    if(inputASName != null && inputASName.length() > 0) {
      annotations = document.getAnnotations(inputASName);
    } else {
      annotations = document.getAnnotations();
    }

    // get token and sentence annotations
    AnnotationSet sentences = annotations.get("Sentence");
    AnnotationSet tokensAS = annotations.get("Token");

    if(sentences != null && sentences.size() > 0 && tokensAS != null &&
      tokensAS.size() > 0) {

      List<Annotation> sentList = new ArrayList<Annotation>(sentences);
      java.util.Collections.sort(sentList, new gate.util.OffsetComparator());

      try {
        for(Annotation annotation : sentList) {
          AnnotationSet sentenceTokens =
            annotations.get("Token", annotation.getStartNode().getOffset(),
              annotation.getEndNode().getOffset());

          List<Annotation> annList = new ArrayList<Annotation>(sentenceTokens);
          Collections.sort(annList, new gate.util.OffsetComparator());

          Long sentStart = annotation.getStartNode().getOffset();
          Long sentEnd = annotation.getEndNode().getOffset();
          String text =
            document.getContent().getContent(sentStart, sentEnd).toString();
          Parse parse =
            new Parse(text, new Span(0, text.length()), "INC", 1, null);

          for(Annotation ann : annList) {
            Long start = ann.getStartNode().getOffset() - sentStart;
            Long end = ann.getEndNode().getOffset() - sentStart;
            parse.insert(new Parse(text, new Span(start.intValue(), end
              .intValue()), "TK", 0, 0));
          }

          Parse result = parser.parse(parse);

          annotate(result, sentStart);
        }
      } catch(gate.util.InvalidOffsetException e) {
        e.printStackTrace();
        throw new ExecutionException(e);
      }
    } else {
      throw new ExecutionException("No sentences or tokens to process!\n"
        + "Please run a sentence splitter " + "and tokeniser first!");
    }
  }

  private Integer annotate(Parse p, Long sentStart)
    throws gate.util.InvalidOffsetException {

    List<Integer> childIDs = new ArrayList<Integer>();
    Parse[] children = p.getChildren();
    for(Parse cp : children) {
      Integer childID = annotate(cp, sentStart);
      if(childID >= 0) childIDs.add(childID);
    }

    String type = p.getType();
    if(type.equals("TK")) return -1;

    Span span = p.getSpan();
    Long start = sentStart + span.getStart();
    Long end = sentStart + span.getEnd();

    FeatureMap fm = gate.Factory.newFeatureMap();
    String text = document.getContent().getContent(start, end).toString();
    fm.put("text", text);
    fm.put("cat", p.getType());
    if(!childIDs.isEmpty()) fm.put("consists", childIDs);

    return annotations.add(start, end, "SyntaxTreeNode", fm);
  }

  public URL getModel() {
    return model;
  }

  /* getters and setters for the PR */
  /* public members */

  @Override
  public Resource init() throws ResourceInstantiationException {
    InputStream modelIn = null;
    try {
      modelIn = model.openStream();

      ParserModel model = new ParserModel(modelIn);

      parser = ParserFactory.create(model);
    } catch(Exception e) {
      e.printStackTrace();
      logger.error("Parser can not be initialized!");
      throw new RuntimeException("Parser cannot be initialized!", e);
    } finally {
      IOUtils.closeQuietly(modelIn);
    }
    return this;
  }

  @Override
  public void reInit() throws ResourceInstantiationException {
    init();
  }

  @CreoleParameter(defaultValue = "models/english/en-parser-chunking.bin",
      comment = "location of the parser model")
  public void setModel(URL model) {
    this.model = model;
  }

}
