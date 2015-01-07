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
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

/**
 * Calculates a word cloud for each entity. This is done by looking at the text around the entity
 * occurrences.
 */
@AnalysisModule(dependencies = {BasicEntityCollection.class})
public class EntityWordCloudModule extends Module<EntityWordCloudResult> {
  private static final int RADIUS = 100;
  private static final int MAX_COUNT = 100;

  @Override
  public EntityWordCloudResult execute(ModuleResultProvider results,
      ProgressListener progressListener) throws IOException {
    Collection<BasicEntity> entities =
        results.getResultFor(BasicEntityCollection.class).getEntities();

    final Map<BasicEntity, WordCloud> wordClouds = new HashMap<>();

    for (BasicEntity entity : entities) {
      wordClouds.put(entity, getWordCloudForEntity(entity, entities));
    }

    return new EntityWordCloudResult() {
      @Override
      public WordCloud getWordCloudForEntity(BasicEntity entity) {
        return wordClouds.get(entity);
      }
    };
  }

  private WordCloud getWordCloudForEntity(BasicEntity entity, Collection<BasicEntity> entities)
      throws IOException {
    List<TextSpan> spans = getTextSpansAroundEntity(entity);
    Map<String, Integer> frequencies = new HashMap<>();

    // The entity name itself should not be included in the word cloud
    Set<String> entityNameTokens = new HashSet<>();
    for (Attribute attr : entity.getNameAttributes()) {
      entityNameTokens.addAll(Arrays.asList(tokenize(attr.getContent())));
    }

    for (TextSpan span : spans) {
      String text = span.getStart().getChapter().getText();
      String substr =
          text.substring(span.getStart().getLocalOffset(), span.getEnd().getLocalOffset());

      String[] tokens = tokenize(substr);

      // Do not include first and last token, they may not be complete
      for (int i = 1; i < tokens.length - 1; i++) {
        String token = tokens[i].trim();
        if (!StringUtils.isEmpty(token) && token.length() > 1 // additional "stop words"
            && !entityNameTokens.contains(token) && !StopWordList.getStopWords().contains(token)) {
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
    if (items.size() > MAX_COUNT)
      items = items.subList(0, MAX_COUNT);

    setWordCloudItemsEntitiyId(items, entities);

    return new WordCloud(items);
  }

  private String[] tokenize(String str) {
    return str.toLowerCase().replaceAll("\\W", " ").split("\\s+?");
  }

  private List<TextSpan> getTextSpansAroundEntity(BasicEntity entity) {
    List<TextSpan> spans = new ArrayList<>(entity.getOccurences().size());
    for (TextSpan span : entity.getOccurences()) {
      spans.add(span.widen(100));
    }
    return TextSpan.normalizeOverlaps(spans);
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
