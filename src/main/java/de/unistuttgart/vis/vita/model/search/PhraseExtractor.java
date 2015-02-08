package de.unistuttgart.vis.vita.model.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import com.google.common.base.Joiner;

public class PhraseExtractor {

  /**
   * Returns the phrase(searchString) of the hit document, which is build in this method
   * 
   * @param words
   * @param charTermAttrib
   * @param tokens
   * @param tokenizer
   * @return
   * @throws IOException
   */
  public Phrase extractPhrase(String[] words, List<String> tokens, String chapterText,
      String tokenizerClass, int startOffset) throws IOException {

    // edit the chapter text till to the beginning index of the second token in the phrase
    StringReader reader = new StringReader(chapterText.substring(startOffset));
    Tokenizer tokenizer;
    if (tokenizerClass.contains("StandardTokenizer")) {
      tokenizer = new StandardTokenizer(reader);
    } else {
      tokenizer = new WhitespaceTokenizer(reader);
    }

    CharTermAttribute charTermAttri = tokenizer.getAttribute(CharTermAttribute.class);
    OffsetAttribute offsetAttr = tokenizer.getAttribute(OffsetAttribute.class);

    tokenizer.reset();

    int i = 0;

    while (i != words.length - 1 && tokenizer.incrementToken()) {
      tokens.add(charTermAttri.toString());
      i++;
    }
    tokenizer.end();
    tokenizer.close();

    Joiner joiner = Joiner.on(" ");
    
    return new Phrase(offsetAttr.endOffset(), joiner.join(tokens));
  }
}
