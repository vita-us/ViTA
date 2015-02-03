package de.unistuttgart.vis.vita.analysis.modules;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.EntityRelations;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

@AnalysisModule(dependencies = {BasicEntityCollection.class, ImportResult.class,
    AnalysisParameters.class}, weight = 0.1)
public class EntityRelationModule extends Module<EntityRelations> {

  private int timeSteps;

  private WeightsMap globalMap = new WeightsMap();
  private WeightsMap[] stepMaps;
  private int totalLength;

  @Override
  public EntityRelations execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    Collection<BasicEntity> basicEntities =
        results.getResultFor(BasicEntityCollection.class).getEntities();
    totalLength = results.getResultFor(ImportResult.class).getTotalLength();

    timeSteps = results.getResultFor(AnalysisParameters.class).getRelationTimeStepCount();
    stepMaps = new WeightsMap[timeSteps];
    initialiseWeightsMapElements();

    Map<Sentence, Set<BasicEntity>> entitiesInSentenceMap =
        removeUnnecessaryEntries(getSentenceEntitesMap(basicEntities));

    createRelationsBetweenEntities(entitiesInSentenceMap);


    globalMap.normalizeWeights();
    for (WeightsMap map : stepMaps) {
      // Normalize each step individually for better normalization over time periods
      // This means that relations in ranges with few relations are ranked higher, but that
      // may actually be accurate, because it would be a more important relation.
      map.normalizeWeights();
    }

    return new EntityRelations() {
      @Override
      public Map<BasicEntity, Double> getRelatedEntities(BasicEntity entity) {
        return globalMap.getMap().get(entity);
      }

      @Override
      public double[] getWeightOverTime(BasicEntity entity1, BasicEntity entity2) {
        double[] weights = new double[timeSteps];
        for (int i = 0; i < timeSteps; i++) {
          weights[i] = stepMaps[i].getNormalizedWeight(entity1, entity2);
        }
        return weights;
      }
    };
  }

  private void initialiseWeightsMapElements() {
    for (int i = 0; i < stepMaps.length; i++) {
      stepMaps[i] = new WeightsMap();
    }
  }

  /**
   * Creates the relationship between entities of every sentence in the globalMap and stepsMap.
   * 
   * @param entitiesInSentenceMap
   */
  private void createRelationsBetweenEntities(Map<Sentence, Set<BasicEntity>> entitiesInSentenceMap) {

    for (Map.Entry<Sentence, Set<BasicEntity>> entry : entitiesInSentenceMap.entrySet()) {
      int currentStepMapIndex =
          (entry.getKey().getRange().getStart().getOffset() * timeSteps) / totalLength;
      for (BasicEntity sourceEntity : entry.getValue()) {
        for (BasicEntity targetEntity : entry.getValue()) {
          if (sourceEntity != targetEntity) {
            globalMap.increaseWeightsAsymmetrically(sourceEntity, targetEntity, timeSteps);
            stepMaps[currentStepMapIndex].increaseWeightsAsymmetrically(sourceEntity, targetEntity,
                timeSteps);
          }
        }
      }
    }
  }

  /**
   * Removes those entries of a map which have not more than one entity
   * 
   * @param entitesInSentencesMap
   * @return
   */
  private Map<Sentence, Set<BasicEntity>> removeUnnecessaryEntries(
      Map<Sentence, Set<BasicEntity>> entitesInSentencesMap) {
    Iterator<Map.Entry<Sentence, Set<BasicEntity>>> iterator =
        entitesInSentencesMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Sentence, Set<BasicEntity>> entry = iterator.next();
      if (!(entry.getValue().size() > 1)) {
        iterator.remove();
      }
    }
    return entitesInSentencesMap;
  }

  /**
   * Creates a Map which shows the relationship between a sentence and its entities
   * 
   * @param basicEntities
   * @return entitesInSentencesMap
   */
  private Map<Sentence, Set<BasicEntity>> getSentenceEntitesMap(
      Collection<BasicEntity> basicEntities) {
    Map<Sentence, Set<BasicEntity>> entitesInSentencesMap =
        new HashMap<Sentence, Set<BasicEntity>>();

    for (BasicEntity entity : basicEntities) {
      for (Occurrence occurrence : entity.getOccurrences()) {
        Sentence currentSentence = occurrence.getSentence();
        if (!entitesInSentencesMap.containsKey(currentSentence)) {
          Set<BasicEntity> entities = new HashSet<>();
          entities.add(entity);
          entitesInSentencesMap.put(currentSentence, entities);
        } else {
          entitesInSentencesMap.get(currentSentence).add(entity);
        }
      }
    }
    return entitesInSentencesMap;
  }

  private static class WeightsMap {
    private Map<BasicEntity, Map<BasicEntity, Integer>> weights = new HashMap<>();
    private Map<BasicEntity, Map<BasicEntity, Double>> normalizedWeights = new HashMap<>();
    private double maxWeight;

    /**
     * /** Increases the weighting factor of the source towards the target
     */
    public void increaseWeightsAsymmetrically(BasicEntity source, BasicEntity target, int increment) {
      Map<BasicEntity, Integer> relationMap;

      if (!weights.containsKey(source)) {
        relationMap = new HashMap<>();
        weights.put(source, relationMap);
      } else {
        relationMap = weights.get(source);
      }

      int currentValue;

      if (relationMap.containsKey(target)) {
        currentValue = relationMap.get(target);
      } else {
        currentValue = 0;
      }

      int newValue = currentValue + increment;

      if (newValue > maxWeight) {
        maxWeight = newValue;
      }

      relationMap.put(target, newValue);
    }

    public void normalizeWeights() {
      if (maxWeight == 0) {
        // no relations, skip just to be sure no division by zero is done
        return;
      }

      for (Map.Entry<BasicEntity, Map<BasicEntity, Integer>> entry : weights.entrySet()) {
        Map<BasicEntity, Double> newMap = new HashMap<>();
        normalizedWeights.put(entry.getKey(), newMap);

        for (Map.Entry<BasicEntity, Integer> innerEntry : entry.getValue().entrySet()) {
          newMap.put(innerEntry.getKey(), (double) innerEntry.getValue() / maxWeight);
        }
      }
    }

    public Map<BasicEntity, Map<BasicEntity, Double>> getMap() {
      return normalizedWeights;
    }

    public double getNormalizedWeight(BasicEntity entity1, BasicEntity entity2) {
      Map<BasicEntity, Map<BasicEntity, Double>> map = getMap();
      if (!map.containsKey(entity1)) {
        return 0;
      }
      Map<BasicEntity, Double> innerMap = map.get(entity1);
      if (!innerMap.containsKey(entity2)) {
        return 0;
      }

      return innerMap.get(entity2);
    }
  }
}
