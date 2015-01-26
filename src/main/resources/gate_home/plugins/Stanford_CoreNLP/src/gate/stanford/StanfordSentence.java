/*
 *  Copyright (c) 2006-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: StanfordSentence.java 15600 2012-03-19 15:40:56Z adamfunk $
 */
package gate.stanford;

import java.util.*;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.TaggedWord;
import gate.*;
import gate.creole.ANNIEConstants;
import gate.util.Strings;

/**
 * The Stanford Parser itself takes as input a List of edu.stanford.nlp.ling.Word.
 * This data structure is constructed from a Sentence Annotation, using the enclosed
 * Token Annotations, and yields the required List, as well as methods for
 * converting the parser's output spans into GATE Annotation offsets.
 */
public class StanfordSentence {
  
  private Map<Integer, Long> startPosToOffset;
  private Map<Integer, Long> endPosToOffset;
  private Map<Integer, Annotation> startPosToToken;
  private Map<Integer, String> startPosToString;
  private List<Word>         words;
  private Long               sentenceStartOffset, sentenceEndOffset;
  private List<Annotation>   tokens;

  private static final String  POS_TAG_FEATURE    = ANNIEConstants.TOKEN_CATEGORY_FEATURE_NAME;
  private static final String  STRING_FEATURE     = ANNIEConstants.TOKEN_STRING_FEATURE_NAME;
  
  int nbrOfTokens, nbrOfMissingPosTags;
  
  
  /* This is probably dodgy, but I can't find an "unknown" tag 
   * in the Penn documentation.    */
  private static final String  UNKNOWN_TAG     = "NN";
  

  public StanfordSentence(Annotation sentence, String tokenType, 
    AnnotationSet inputAS, boolean usePosTags) {
    
    startPosToOffset = new HashMap<Integer, Long>();
    endPosToOffset   = new HashMap<Integer, Long>();
    startPosToToken  = new HashMap<Integer, Annotation>();
    startPosToString = new HashMap<Integer, String>();
    
    sentenceStartOffset = sentence.getStartNode().getOffset();
    sentenceEndOffset   = sentence.getEndNode().getOffset();
   
    nbrOfTokens   = 0;
    nbrOfMissingPosTags = 0;
    
    tokens = Utils.inDocumentOrder(inputAS.getContained(sentenceStartOffset, sentenceEndOffset).get(tokenType));
    words = new ArrayList<Word>();

    add(-1, sentence, "S");
    
    int tokenNo = 0;

    for (Annotation token : tokens) {
      String tokenString = escapeToken(token.getFeatures().get(STRING_FEATURE).toString());
      add(tokenNo, token, tokenString);
      
      /* The FAQ says the parser will automatically use existing POS tags
       * if the List elements are of type TaggedWord.  
       * http://nlp.stanford.edu/software/parser-faq.shtml#f
       */
      
      if (usePosTags)  {
        words.add(new TaggedWord(tokenString, getEscapedPosTag(token)));
      }
      else {
        words.add(new Word(tokenString));
      }

      tokenNo++;
    }
    
    nbrOfTokens = tokenNo;
  }

  
  public String toString() {
    StringBuffer output = new StringBuffer();
    output.append("S: ").append(Strings.toString(startPosToOffset)).append('\n');
    output.append("   ").append(Strings.toString(startPosToString)).append('\n');
    output.append("   ").append(Strings.toString(endPosToOffset));
    return output.toString();
  }
  
  
  private String getEscapedPosTag(Annotation token)  {
    String pos = UNKNOWN_TAG;
    FeatureMap tokenFeatures = token.getFeatures();

    if (tokenFeatures.containsKey(POS_TAG_FEATURE)) {
      Object temp = tokenFeatures.get(POS_TAG_FEATURE);
      
      if (temp instanceof String) {
        pos = (String) temp;
      }
      else {
        nbrOfMissingPosTags++;
      }
      
    }
    else {
      nbrOfMissingPosTags++;
    }
    
    return escapePosTag(pos);
  }
  


  private void add(int tokenNbr, Annotation token, String tokenString) {
    Long tokenStartOffset = token.getStartNode().getOffset();
    Long tokenEndOffset   = token.getEndNode().getOffset();

    startPosToOffset.put(tokenNbr, tokenStartOffset);
    endPosToOffset.put(new Integer(tokenNbr + 1), tokenEndOffset);
    startPosToToken.put(tokenNbr, token);
    startPosToString.put(tokenNbr, tokenString);
  }
  

  
  /* Explanation of the position conversion:
   * The output of the Stanford Parser specifies each constituent's span in terms of 
   * token boundaries re-numbered within each sentence, which we need to convert to 
   * GATE character offsets within the whole document.
   * 
   * Example: 
   * "This is a test." starting at document offset 100, containing five tokens.
   * Stanford says "This" starts at 0 and ends at 1; GATE says 100 to 104.
   * Stanford says "is a test" starts at 1 and ends at 4;
   * GATE says 105 to 114.
   */
  
  
  public int numberOfTokens() {
    return nbrOfTokens;
  }
  
  public int numberOfMissingPosTags() {
    return nbrOfMissingPosTags;
  }
  
  public boolean isNotEmpty() {
    return (nbrOfTokens > 0);
  }
  
  
  /**
   * Change the Token's string to match the Penn Treebank's 
   * escaping system.
   * See Stanford parser FAQ "How can I provide the correct tokenization of my 
   * sentence to the parser?"  

   * @param token original string feature of Token
   * @return escaped version of string
   */
  protected static String escapeToken(String token) {
    //   (  -->  -LRB-
    if (token.equals("(")) {
      return "-LRB-";
    }
    
    //   )  -->  -RRB-
    if (token.equals(")")) {
      return "-RRB-";
    }
    
    //   /  -->  \/
    //   *  -->  \*
    if (token.contains("/") || token.contains("*")) {
      return token.replace("/", "\\/").replace("*", "\\*");
    }
    
    return token;
  }
  

  protected static String escapePosTag(String tag) {
    //   (  -->  -LRB-
    if (tag.equals("(")) {
      return "-LRB-";
    }
    
    //   )  -->  -RRB-
    if (tag.equals(")")) {
      return "-RRB-";
    }
    
    return tag;
  }

  
  protected static String unescapePosTag(String tag) {
    //   (  <--  -LRB-
    if (tag.equals("-LRB-")) {
      return "(";
    }
    
    //   )  <--  -RRB-
    if (tag.equals("-RRB-")) {
      return ")";
    }
    
    return tag;
  }
  

  /**
   * Convert a Stanford start position to the GATE Annotation of type
   * "Token" that starts there.
   */
  public Annotation startPos2token(int startPos) {
    return startPosToToken.get(startPos);
  }

  /**
   * Convert a Stanford start position to a GATE offset.
   * @param startPos
   * @return the offset in the GATE document
   */
  public Long startPos2offset(int startPos) {
    return startPosToOffset.get(startPos);
  }

  /**
   * Convert a Stanford end position to a GATE offset.
   * @param endPos
   * @return the offset in the GATE document
   */
  public Long endPos2offset(int endPos) {
    return endPosToOffset.get(endPos);
  }

  
  /**
   * @return The data structure that is passed to the Stanford Parser itself.
   */
  public List<Word> getWordList() {
    return words;
  }
}
