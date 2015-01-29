package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.EntityWordCloudResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

/**
 * Calculates a word cloud for each entity. This is done by looking at the text around the entity's
 * occurrences. Analysis Parameters can determine to which value the number of different words in the word
 * cloud is limited and if the stop word list should be used.
 */
@AnalysisModule(dependencies = {BasicEntityCollection.class, AnalysisParameters.class})
public class EntityWordCloudModule extends Module<EntityWordCloudResult> {
  private int maxWordCloudItemsCount;

  @Override
  public EntityWordCloudResult execute(ModuleResultProvider results,
      ProgressListener progressListener) throws IOException {

    // get parameters
    Collection<BasicEntity> entities =
        results.getResultFor(BasicEntityCollection.class).getEntities();
    boolean stopWordListEnabled =
        results.getResultFor(AnalysisParameters.class).getStopWordListEnabled();
    maxWordCloudItemsCount = results.getResultFor(AnalysisParameters.class).getWordCloudItemsCount();

    // create word clouds for entities
    final Map<BasicEntity, WordCloud> wordClouds = new HashMap<>();
    for (BasicEntity entity : entities) {
      wordClouds.put(entity, getWordCloudForEntity(entity, entities, stopWordListEnabled));
    }

    // return word clouds
    return new EntityWordCloudResult() {
      @Override
      public WordCloud getWordCloudForEntity(BasicEntity entity) {
        return wordClouds.get(entity);
      }
    };
  }


  /**
   * Creates a Word Cloud for one Entity by analyzing all words in the same Sentences as the Entity.
   * 
   * @param entity - The Entity the Word Cloud belongs to.
   * @param entities - All Entities found in the document. Needed to connect found words and
   *        entities.
   * @param stopWordListEnabled - true: defined stop words should be used to filter the Word Cloud.
   *        false: only basic filtering.
   * @return The Word Cloud for the given Entity.
   * @throws IOException - Thrown if this methods was unable to get the stop words.
   */
  private WordCloud getWordCloudForEntity(BasicEntity entity, Collection<BasicEntity> entities,
      boolean stopWordListEnabled) throws IOException {
    // prepare data
    Map<String, Integer> frequencies = new HashMap<>();
    List<Sentence> sentences = new ArrayList<Sentence>(getSentencesOfEntityOccurrences(entity));
    Set<String> stopWordList = prepareStopWordsSet(stopWordListEnabled);
    Set<String> entityNameTokens = prepareNameTokensOfEntitySet(entity);

    // fill frequencies
    for (Sentence sentence : sentences) {
      getFrequenciesForOneSentence(sentence, frequencies, entityNameTokens, stopWordList);
    }

    // build and return word cloud
    List<WordCloudItem> items = createWordCloudItems(frequencies);
    setWordCloudItemsEntitiyId(items, entities);
    return new WordCloud(items);
  }
  

  /**
   * Searches words in the given sentence and updates the counters in frequencies. Ignores words
   * from the stop list or names of the entity the word cloud belongs to.
   * 
   * @param sentence - the sentence to analyze.
   * @param frequencies - the counters for each word.
   * @param entityNameTokens - the names of the entity itself.
   * @param stopWordList - words on this list will be ignored.
   */
  private void getFrequenciesForOneSentence(Sentence sentence, Map<String, Integer> frequencies,
      Set<String> entityNameTokens, Set<String> stopWordList) {
    String sentenceText = getSentenceText(sentence);
    String[] tokens = tokenize(sentenceText);

    for (int i = 0; i < tokens.length; i++) {
      String token = tokens[i].trim();
      boolean additionalStopWords = !StringUtils.isEmpty(token) && token.length() > 1;

      // count this token?
      if (additionalStopWords && !entityNameTokens.contains(token) && !stopWordList.contains(token)) {
        if (frequencies.containsKey(token)) {
          frequencies.put(token, frequencies.get(token) + 1);
        } else {
          frequencies.put(token, 1);
        }
      }
    }
  }
  
  /**
   * Get the stop words if enabled, otherwise an empty set.
   * 
   * @param stopWordListEnabled - true: defined stop words should be used to filter the Word Cloud.
   *        false: only basic filtering, no stop words.
   * @return all stop words
   * @throws IOException - Thrown if this methods was unable to get the stop words.
   */
  private Set<String> prepareStopWordsSet(boolean stopWordListEnabled) throws IOException {
    Set<String> stopWordList = new HashSet<String>();
    if (stopWordListEnabled) {
      stopWordList = StopWordList.getStopWords();
    } else {
      stopWordList = new HashSet<String>();
    }
    return stopWordList;
  }

  /**
   * Get all the tokens of all the names of an Entity.
   * 
   * @param entity - The Entity.
   * @return The tokens.
   */
  private Set<String> prepareNameTokensOfEntitySet(BasicEntity entity) {
    Set<String> entityNames = new HashSet<>();
    for (Attribute attr : entity.getNameAttributes()) {
      entityNames.addAll(Arrays.asList(tokenize(attr.getContent())));
    }
    return entityNames;
  }


  /**
   * Get the text of a Sentence as String.
   * 
   * @param sentence - The Sentence, attributes should be set, should not be null.
   * @return the text of the Sentence.
   */
  private String getSentenceText(Sentence sentence) {
    Chapter currentChapter = sentence.getChapter();
    String text = currentChapter.getText();
    return text.substring(sentence.getRange().getStart().getLocalOffset(currentChapter), sentence
        .getRange().getEnd().getLocalOffset(currentChapter));
  }

  /**
   * Creates the WordCloudItems from a list of frequencies for tokens. Some rare items may be
   * removed, if there are too much.
   * 
   * @param frequencies - Contains the name of the tokens and how often it was found.
   * @return The WordCloutItems to create a Word Cloud, they are reverse ordered and the number of
   *         elements is restricted by the input parameters.
   */
  private List<WordCloudItem> createWordCloudItems(Map<String, Integer> frequencies) {
    // transform frequencies to items
    List<WordCloudItem> items = new ArrayList<WordCloudItem>();
    for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
      items.add(new WordCloudItem(entry.getKey(), entry.getValue()));
    }

    // order list and remove rare items, if there are too much
    Collections.sort(items, Collections.reverseOrder());
    if (items.size() > maxWordCloudItemsCount){
      items = items.subList(0, maxWordCloudItemsCount);
    }
    return items;
  }

  /**
   * Find all tokens in the given String. A token is a word containing only characters and numbers.
   * 
   * @param str - the String.
   * @return all tokens as Strings in lower case.
   */
  private String[] tokenize(String str) {
    return str.toLowerCase().replaceAll("\\W", " ").split("\\s+?");
  }

  /**
   * Find all Sentences for a given Entity.
   * 
   * @param entity - the Entity.
   * @return all Sentences the Entity lies in.
   */
  private Set<Sentence> getSentencesOfEntityOccurrences(BasicEntity entity) {
    Set<Sentence> sentences = new HashSet<Sentence>();
    for (Occurrence occurrence : entity.getOccurences()) {
      sentences.add(occurrence.getSentence());
    }
    return sentences;
  }

  /**
   * Sets the entity id of the word cloud item if the entity id for this item is existing. So if
   * there is a word in items which is also the name of an entity, the item will know the id of this
   * entity.
   * 
   * @param items - should be all word cloud items, which will be used in the word cloud.
   * @param basicEntities - should be all entities.
   */
  private void setWordCloudItemsEntitiyId(List<WordCloudItem> items,
      Collection<BasicEntity> basicEntities) {
    for (WordCloudItem wordCloudItem : items) {
      for (BasicEntity basicEntity : basicEntities) {
        for (Attribute attribute : basicEntity.getNameAttributes()) {
          if (wordCloudItem.getWord().equalsIgnoreCase(attribute.getContent())) {
            wordCloudItem.setEntityId(basicEntity.getEntityId());
          }
        }
      }
    }
  }
}
