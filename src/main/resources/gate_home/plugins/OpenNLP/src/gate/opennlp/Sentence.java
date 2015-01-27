/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: Sentence.java 16275 2012-11-14 12:52:58Z adamfunk $
 */
package gate.opennlp;

import java.util.*;
import opennlp.tools.util.Span;
import gate.*;


public class Sentence 
  implements Iterable<Annotation> {

  private static final long serialVersionUID = -6202949266926641966L;
  private List<Annotation> tokens;
  private int size;
  private String[] strings;
  private String[] tags;
  private String stringFeature, categoryFeature;
  
  
  public Sentence(AnnotationSet annotations, String stringFeature, String categoryFeature) {
    this.stringFeature = stringFeature;
    this.categoryFeature = categoryFeature;
    this.tokens = Utils.inDocumentOrder(annotations);
    this.size = this.tokens.size();
    this.strings = new String[size];
    this.tags = new String[size];
    
    boolean tagged = (categoryFeature != null) && (categoryFeature.length() > 0);
    
    for (int i=0 ; i < size ; i++) {
      Annotation token = this.tokens.get(i);
      FeatureMap fm = token.getFeatures();

      if (fm.containsKey(this.stringFeature)) {
        this.strings[i] = fm.get(this.stringFeature).toString();
      }
      else {
        this.strings[i] = "";
      }

      if (tagged && fm.containsKey(this.categoryFeature)) {
        this.tags[i] = fm.get(this.categoryFeature).toString();
      }
      else {
        this.tags[i] = "";
      }
    }
  }
  
  
  /**
   * Start character offset of the last token in the given Span. Note that 
   * OpenNLP's chunker and NER return token offsets between tokens, so a 
   * chunk marked (1,3) covers the document content from the beginning 
   * of token 1 to the end of token 2.
   * Watch out for -1 (error code) output. 
   * @param i
   * @return the GATE-style start offset in characters
   */
  public long getStartOffset(Span span) {
    int start = span.getStart();
    if ( (start >= 0) && (start < size) ) {
      return this.tokens.get(start).getStartNode().getOffset();
    }
    // error code
    return -1L;
  }


  /**
   * End character offset of the last token in the given Span. Note that
   * OpenNLP's chunker and NER return token offsets between tokens, so a 
   * chunk marked (1,3) covers the document content from the beginning 
   * of token 1 to the end of token 2. 
   * This method does the subtraction and range-checking for you,
   * but watch out for -1 (error code) output. 
   * @param i
   * @return the GATE-style end offset in characters
   */
  public long getEndOffset(Span span) {
    int end = span.getEnd() - 1;
    if ( (end >= 0) && (end < size) ) {
      return this.tokens.get(end).getEndNode().getOffset();
    }
    // error code
    return -1L;
  }


  /**
   * String array of the Token.string features.
   * @return
   */
  public String[] getStrings() {
    return this.strings;
  }

  /**
   * String array of the Token.category features.
   * @return
   */
  public String[] getTags() {
    return this.tags;
  }
  
  public Annotation get(int i) {
    return this.tokens.get(i);
  }

  public int size() {
    return this.size;
  }

  /**
   * Iterator over the Token annotations that went into
   * the constructor, but now in LR order.
   */
  public Iterator<Annotation> iterator() {
    return this.tokens.listIterator();
  }
  
  
}
