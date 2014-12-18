package de.unistuttgart.vis.vita.model.search;



import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;


import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * This class performs the searching for a word or phrase in a document
 * 
 *
 */

public class Searcher {

  private static final String NO_SPECIAL_CHARACTERS = "[\\d\\s\\p{Alpha}]+";
  private static final String CHAPTER_ID = "chapterId";
  private static final String CHAPTER_TEXT = "chapterText";

  public List<TextSpan> searchString(String documentId, String searchString,
      List<Chapter> chapters, Model model) throws IOException, ParseException {
    if (chapters.isEmpty()) {
      return new ArrayList<TextSpan>();
    }

    // This empty set allows to search for stop words
    CharArraySet charArraySet = new CharArraySet(0, true);
    StandardAnalyzer analyzer = new StandardAnalyzer(charArraySet);
    List<TextSpan> textSpans = new ArrayList<TextSpan>();
    QueryParser queryParser = new QueryParser(CHAPTER_TEXT, analyzer);
    Query query = queryParser.parse(searchString);
    IndexSearcher indexSearcher = model.getTextRepository().getIndexSearcherForDocument(documentId);
    // That are documents in an index, which contains the searchString
    ScoreDoc[] hits =
        indexSearcher.search(query, indexSearcher.getIndexReader().numDocs()).scoreDocs;

    callCorrectTokenizers(searchString, chapters, textSpans, indexSearcher, hits);
    indexSearcher.getIndexReader().close();

    Collections.sort(textSpans);
    return textSpans;
  }

  /**
   * Calls the right Tokenizer regarding the searchString
   *
   * @param searchString
   * @param chapters
   * @param textSpans
   * @param indexSearcher
   * @param hits
   * @throws IOException
   */
  private void callCorrectTokenizers(String searchString, List<Chapter> chapters,
      List<TextSpan> textSpans, IndexSearcher indexSearcher, ScoreDoc[] hits) throws IOException {
    for (int i = 0; i < hits.length; i++) {
      
      String chapterText = indexSearcher.doc(hits[i].doc).getField(CHAPTER_TEXT).stringValue();
      StringReader reader = 
          new StringReader(chapterText);
      String[] words = searchString.split(" ");
      Tokenizer tokenizer;

      if (searchString.matches(NO_SPECIAL_CHARACTERS)) {
        tokenizer = new StandardTokenizer(reader);
      } else {
        tokenizer = new WhitespaceTokenizer(reader);
      }

      Chapter chapter = getCorrectChapter(indexSearcher.doc(hits[i].doc), chapters);
      if (chapter == null) {
        // We retrieved a hit for a chapter that was not requested
        continue;
      }

      addTextSpansToList(tokenizer, searchString, words,
          getCorrectChapter(indexSearcher.doc(hits[i].doc), chapters), textSpans, chapterText);
    }
  }

  /**
   * Returns the correct chapter regarding the current hit document
   * 
   * @param document
   * @param chapters
   * @return
   */
  private Chapter getCorrectChapter(Document document, List<Chapter> chapters) {

    for (Chapter chapter : chapters) {
      if (document.getField(CHAPTER_ID).stringValue().equals(chapter.getId())) {
        return chapter;
      }
    }
    return null;
  }

  /**
   * Produces the textspans and them to textSpans list
   * 
   * @param tokenizer
   * @param searchString
   * @param words
   * @param currentChapter
   * @param textSpans
   * @throws IOException
   */
  private void addTextSpansToList(Tokenizer tokenizer, String searchString, String[] words,
      Chapter currentChapter, List<TextSpan> textSpans, String chapterText) throws IOException {

    // if it is a single word
    if (words != null && words.length == 1) {

      CharTermAttribute charTermAttrib = tokenizer.getAttribute(CharTermAttribute.class);
      OffsetAttribute offset = tokenizer.getAttribute(OffsetAttribute.class);

      tokenizer.reset();
      while (tokenizer.incrementToken()) {
        if (charTermAttrib.toString().toLowerCase().matches(searchString.toLowerCase())) {
          int startOffset = offset.startOffset() + currentChapter.getRange().getStart().getOffset();
          int endOffset = offset.endOffset() + currentChapter.getRange().getStart().getOffset();

          textSpans.add(new TextSpan(TextPosition.fromGlobalOffset(currentChapter, startOffset),
              TextPosition.fromGlobalOffset(currentChapter, endOffset)));
        }
      }
      tokenizer.end();
      tokenizer.close();
    }
    // if it is a phrase
    else if (words != null && words.length > 1) {

      CharTermAttribute charTermAttrib = tokenizer.getAttribute(CharTermAttribute.class);
      OffsetAttribute offset = tokenizer.getAttribute(OffsetAttribute.class);

      List<String> tokens = new ArrayList<String>();
      tokenizer.reset();
      int position = -1;
      while (tokenizer.incrementToken()) {
        position++;

        if (charTermAttrib.toString().toLowerCase().matches(words[0].toLowerCase())) {
          int startOffset = offset.startOffset() + currentChapter.getRange().getStart().getOffset();
          tokens.add(charTermAttrib.toString());

          PhraseExtractor phraseExtracter = new PhraseExtractor();
          Phrase tokenInfo = phraseExtracter.extractPhrase(words, tokens,position,chapterText,tokenizer.getClass().toString());
          String phrase = tokenInfo.getPhrase();

          if (phrase.toLowerCase().equals(searchString.toLowerCase())) {
            int endOffset = tokenInfo.getEndOffset() + currentChapter.getRange().getStart().getOffset();

            textSpans.add(new TextSpan(TextPosition.fromGlobalOffset(currentChapter, startOffset),
                TextPosition.fromGlobalOffset(currentChapter, endOffset)));
          }
        }
        tokens.clear();
      }
      tokenizer.end();
      tokenizer.close();
    }

  }
}