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
 * Calculates a word cloud for each entity. This is done by looking at the text around the entity
 * occurrences.
 */
@AnalysisModule(dependencies = {BasicEntityCollection.class, AnalysisParameters.class})
public class EntityWordCloudModule extends Module<EntityWordCloudResult> {
  List<Chapter> allChapters;
  int documentLength;
  private static final int RADIUS = 100;
  private int count;

  @Override
  public EntityWordCloudResult execute(ModuleResultProvider results,
      ProgressListener progressListener) throws IOException {

    Collection<BasicEntity> entities =
        results.getResultFor(BasicEntityCollection.class).getEntities();
    boolean stopWordListEnabled =
        results.getResultFor(AnalysisParameters.class).getStopWordListEnabled();
    count = results.getResultFor(AnalysisParameters.class).getWordCloudItemsCount();

    final Map<BasicEntity, WordCloud> wordClouds = new HashMap<>();

    for (BasicEntity entity : entities) {
      wordClouds.put(entity, getWordCloudForEntity(entity, entities, stopWordListEnabled));
    }

    return new EntityWordCloudResult() {
      @Override
      public WordCloud getWordCloudForEntity(BasicEntity entity) {
        return wordClouds.get(entity);
      }
    };
  }


  private WordCloud getWordCloudForEntity(BasicEntity entity, Collection<BasicEntity> entities,
      boolean stopWordListEnabled) throws IOException {

    List<Sentence> sentences = new ArrayList<Sentence>(getSentencesOfEntityOccurrences(entity));
    Map<String, Integer> frequencies = new HashMap<>();

    Set<String> stopWordList;
    if (stopWordListEnabled) {
      stopWordList = StopWordList.getStopWords();
    } else {
      stopWordList = new HashSet<String>();
    }

    // The entity name itself should not be included in the word cloud
    Set<String> entityNameTokens = new HashSet<>();
    for (Attribute attr : entity.getNameAttributes()) {
      entityNameTokens.addAll(Arrays.asList(tokenize(attr.getContent())));
    }

    for (Sentence sentence : sentences) {

      Chapter currentChapter = sentence.getChapter();
      String text = currentChapter.getText();
      String substr =
          text.substring(sentence.getRange().getStart().getLocalOffset(currentChapter),
              sentence.getRange().getEnd().getLocalOffset(currentChapter));

      String[] tokens = tokenize(substr);
  
      for (int i = 0; i < tokens.length; i++) {
        String token = tokens[i].trim();
        if (!StringUtils.isEmpty(token) && token.length() > 1 // additional "stop words"
            && !entityNameTokens.contains(token) && !stopWordList.contains(token)) {
          if (frequencies.containsKey(token)) {
            frequencies.put(token, frequencies.get(token) + 1);
          } else {
            frequencies.put(token, 1);
          }
        }
      }
    }

    List<WordCloudItem> items = new ArrayList<WordCloudItem>();
    for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
      items.add(new WordCloudItem(entry.getKey(), entry.getValue()));
    }

    Collections.sort(items, Collections.reverseOrder());
    if (items.size() > count)
      items = items.subList(0, count);

    setWordCloudItemsEntitiyId(items, entities);

    return new WordCloud(items);
  }

  private String[] tokenize(String str) {
    return str.toLowerCase().replaceAll("\\W", " ").split("\\s+?");
  }

  private Set<Sentence> getSentencesOfEntityOccurrences(BasicEntity entity) {

    Set<Sentence> sentences = new HashSet<Sentence>();
    for (Occurrence occurrence : entity.getOccurences()) {
      sentences.add(occurrence.getSentence());
    }
    return sentences;
  }

  /**
   * Sets the entity id of the word cloud item if the entity id for this item is existing
   * 
   * @param wordCloud
   * @param basicEntities
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
