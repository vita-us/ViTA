package de.unistuttgart.vis.vita.model.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test on PhraseExtractor
 * 
 *
 */
public class PhraseExtractorTest {

  List<Phrase> phrases = new ArrayList<Phrase>();

  @Before
  public void setUp() throws IOException {

    // splitted searchString(He turned on his)
    String[] words = {"He", "turned", "on", "his"};
    String chapterText =
        "He was gone. Harry lives in London. \"Goodluck, Harry,\" he murmured. He turned on his heel and with a swish of his cloak, he was gone.";
    PhraseExtractor phraseExtractor = new PhraseExtractor();

    phrases.add(phraseExtractor.extractPhrase(words, new ArrayList<String>(), 0, chapterText,
        "WhiteSpaceTokenizer"));
    phrases.add(phraseExtractor.extractPhrase(words, new ArrayList<String>(), 9, chapterText,
        "WhiteSpaceTokenizer"));
    phrases.add(phraseExtractor.extractPhrase(words, new ArrayList<String>(), 11, chapterText,
        "WhiteSpaceTokenizer"));
    phrases.add(phraseExtractor.extractPhrase(words, new ArrayList<String>(), 23, chapterText,
        "WhiteSpaceTokenizer"));
  }

  @Test
  public void testPhraseRecognition() {

    // for the first word of the array, in this case "He", phrases will be build for every occurence
    // of "He/he" and the length of the phrase is length of the words array(4)
    assertEquals("He was gone. Harry", "He " + phrases.get(0).getPhrase());
    assertEquals(18, phrases.get(0).getEndOffset());

    assertEquals("he murmured. He turned", "he " + phrases.get(1).getPhrase());
    assertEquals(77, phrases.get(1).getEndOffset());

    assertEquals("He turned on his", "He " + phrases.get(2).getPhrase());
    assertEquals(84, phrases.get(2).getEndOffset());

    // the last phrase has length three and offset 0, because this one is at the end of the chapter
    // and there is one word missing to get the length of the words array(4)
    assertEquals("he was gone.", "he " + phrases.get(3).getPhrase());
    assertEquals(0, phrases.get(3).getEndOffset());

  }
}
