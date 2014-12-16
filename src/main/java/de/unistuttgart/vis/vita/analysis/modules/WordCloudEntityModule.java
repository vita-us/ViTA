package de.unistuttgart.vis.vita.analysis.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.GlobalWordCloudResult;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;
import de.unistuttgart.vis.vita.model.progress.FeatureProgress;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloudItem;

/**
 * The feature module that stores the word cloud with the existing entities ids.
 * 
 * This module depends on the GlobalWordCloudResult and BasicEntityCollection because the global
 * word cloud and basic entities must have been build so the entities ids can be searched in the
 * global word cloud
 */
@AnalysisModule(dependencies = {GlobalWordCloudResult.class, BasicEntityCollection.class})
public class WordCloudEntityModule extends AbstractFeatureModule<WordCloudEntityModule> {

  @Override
  protected WordCloudEntityModule storeResults(ModuleResultProvider result, Document document,
      EntityManager em) throws Exception {

    WordCloud wordCloud = result.getResultFor(GlobalWordCloudResult.class).getGlobalWordCloud();
    Collection<BasicEntity> basicEntitiyCollection =
        result.getResultFor(BasicEntityCollection.class).getEntities();
    setWordCloudItemsEntitiyId(wordCloud, new ArrayList<BasicEntity>(basicEntitiyCollection));
    em.persist(wordCloud);
    return this;
  }

  @Override
  protected Iterable<FeatureProgress> getProgresses(AnalysisProgress progress) {
    return new ArrayList<FeatureProgress>();
  }

  /**
   * Sets the entity id of the word cloud item if the entity id for this item is existing
   * 
   * @param wordCloud
   * @param basicEntities
   */
  private void setWordCloudItemsEntitiyId(WordCloud wordCloud, List<BasicEntity> basicEntities) {
    for (WordCloudItem wordCloudItem : wordCloud.getItems()) {
      for (BasicEntity basicEntity : basicEntities) {
        for (Attribute attribute : basicEntity.getNameAttributes()) {
          if (wordCloudItem.getWord().matches(attribute.getContent())) {
            wordCloudItem.setEntityId(attribute.getId());
          }
        }
      }
    }
  }
}
