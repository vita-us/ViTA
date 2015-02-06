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
  public Phrase extractPhrase(String[] words, List<String> tokens, int position,
      String chapterText, String tokenizerClass) throws IOException {
    
    StringReader reader = new StringReader(chapterText);
    Tokenizer tokenizer;
    if (tokenizerClass.contains("StandardTokenizer")) {
      tokenizer = new StandardTokenizer(reader);
    } else {
      tokenizer = new WhitespaceTokenizer(reader);
    }
    
    CharTermAttribute charTermAttri = tokenizer.getAttribute(CharTermAttribute.class);
    OffsetAttribute offsetAttr = tokenizer.getAttribute(OffsetAttribute.class);
    int count = -1;

    // TODO: PERFORMACE - Going through the chapter again makes the search slow
    
    tokenizer.reset();
    while (tokenizer.incrementToken()) {
      count++;
      if (count == position) {
        break;
      }
    }

    int i = 0;

    while (i != words.length - 1 && tokenizer.incrementToken()) {
      tokens.add(charTermAttri.toString());
      i++;
    }

    Joiner joiner = Joiner.on(" ");
    Phrase phrase = new Phrase(offsetAttr.endOffset(), joiner.join(tokens));
    tokenizer.end();
    tokenizer.close();
    
    return phrase;
  }
}
