package de.unistuttgart.vis.vita.analysis.modules;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.EntityRelations;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

@AnalysisModule(dependencies = { BasicEntityCollection.class, ImportResult.class }, weight = 0.1)
public class EntityRelationModule extends Module<EntityRelations> {

  /**
   * The maximum distance in characters two entities my occur at to be considered in a relation
   */
  public static final int MAX_DISTANCE = 50;

  private static final int TIME_STEPS = 20;
  
  private SortedSet<RelationEvent> events = new TreeSet<>();
  private Map<BasicEntity, Integer> presentEntities = new HashMap<>();
  private WeightsMap globalMap = new WeightsMap();
  private WeightsMap currentStepMap = new WeightsMap();
  private int currentStepMapIndex = 0;
  private WeightsMap[] stepMaps = new WeightsMap[TIME_STEPS];
  private int totalLength;
  
  @Override
  public EntityRelations execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    Collection<BasicEntity> entities = results.getResultFor(BasicEntityCollection.class)
        .getEntities();
    totalLength = results.getResultFor(ImportResult.class).getTotalLength();
    List<DocumentPart> parts = results.getResultFor(ImportResult.class).getParts();
    
    // get all chapters
    List<Chapter> chapters = Chapter.transformPartsToChapters(parts);
    
    for (BasicEntity entity : entities) {
      addEvents(entity, chapters);
    }
    
    processEvents();
    
    while (currentStepMapIndex < TIME_STEPS) {
      stepMaps[currentStepMapIndex] = currentStepMap;
      currentStepMapIndex++;
      currentStepMap = new WeightsMap();
    }
    
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
        double[] weights = new double[TIME_STEPS];
        for (int i = 0; i < TIME_STEPS; i++) {
          weights[i] = stepMaps[i].getNormalizedWeight(entity1, entity2);
        }
        return weights;
      }
    };
  }
  
  private void addEvents(BasicEntity entity, List<Chapter> chapters) {
    for (Range occurrence : entity.getOccurences()) {
      occurrence = widenOccurrence(occurrence, chapters);
      events.add(new RelationEvent(entity, EventType.ENTER, occurrence.getStart()));
      events.add(new RelationEvent(entity, EventType.LEAVE, occurrence.getEnd()));
    }
  }
  
  private void processEvents() {
    for (RelationEvent event : events) {
      switch (event.type) {
        case ENTER:
          if (presentEntities.containsKey(event.entity)) {
            presentEntities.put(event.entity, presentEntities.get(event.entity) + 1);
          } else {
            presentEntities.put(event.entity, 1);
          }
          handleEntityEnter(event.entity, event.position);
          break;

        case LEAVE:
          if (presentEntities.containsKey(event.entity)) {
            int newValue = presentEntities.get(event.entity) - 1;

            if (newValue <= 0) {
              presentEntities.remove(event.entity);
            } else {
              presentEntities.put(event.entity, newValue);
            }
          }
          break;
      }
    }
  }
  
  private void handleEntityEnter(BasicEntity entity, TextPosition position) {
    if (presentEntities.isEmpty()) {
      return;
    }

    for (Map.Entry<BasicEntity, Integer> entry : presentEntities.entrySet()) {
      BasicEntity other = entry.getKey();

      if (other == entity) {
        continue;
      }
      
      while (position.getOffset() * TIME_STEPS / totalLength > currentStepMapIndex) {
        stepMaps[currentStepMapIndex] = currentStepMap;
        currentStepMapIndex++;
        currentStepMap = new WeightsMap();
      }

      globalMap.increaseWeights(entity, other, entry.getValue());
      currentStepMap.increaseWeights(entity, other, entry.getValue());
    }
  }
  
  /**
   * Extends the start and the end of the given text span by {@link #MAX_DISTANCE} / 2 each,
   * but not across chapter boundaries
   * @param occurrence the span to widen
   * @param chapters all chapters of the document, sorted by position in the document
   * @return the widened span
   */
  private Range widenOccurrence(Range occurrence, List<Chapter> chapters) {
    int chapterOfOccurrenceStart = Range.findChapterOfRange(0, chapters, occurrence, true);
    int chapterStart = chapters.get(chapterOfOccurrenceStart).getRange().getStart().getOffset();
    int start = occurrence.getStart().getOffset() - MAX_DISTANCE / 2;

    if (start < chapterStart) {
      start = chapterStart;
    }

    int chapterOfOccurrenceEnd = Range.findChapterOfRange(0, chapters, occurrence, false);
    int chapterEnd = chapters.get(chapterOfOccurrenceEnd).getRange().getEnd().getOffset();
    int end = occurrence.getEnd().getOffset() + MAX_DISTANCE / 2;

    if (end > chapterEnd) {
      end = chapterEnd;
    }

    return new Range(TextPosition.fromGlobalOffset(chapters.get(chapterOfOccurrenceStart), start, this.totalLength),
        TextPosition.fromGlobalOffset(chapters.get(chapterOfOccurrenceEnd), end, this.totalLength));
    
  }

  private static enum EventType {
    ENTER, LEAVE
  }

  private static class RelationEvent implements Comparable<RelationEvent> {
    public BasicEntity entity;
    public EventType type;
    public TextPosition position;

    public RelationEvent(BasicEntity entity, EventType type, TextPosition position) {
      this.entity = entity;
      this.type = type;
      this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
      return super.equals(obj);
    }

    @Override
    public int compareTo(RelationEvent o) {
      if (o == null) {
        return 1;
      }

      return position.compareTo(o.position);
    }
  }
  
  private static class WeightsMap {
    private Map<BasicEntity, Map<BasicEntity, Integer>> weights = new HashMap<>();
    private Map<BasicEntity, Map<BasicEntity, Double>> normalizedWeights = new HashMap<>();
    private double maxWeight;

    /**
     * Increases the weighting factor of the two entities
     */
    public void increaseWeights(BasicEntity entity1, BasicEntity entity2, int increment) {
      increaseWeightsAsymmetrically(entity1, entity2, increment);
      increaseWeightsAsymmetrically(entity2, entity1, increment);
    }
    
    /**
     * Increases the weighting factor of the source towards the target
     */
    private void increaseWeightsAsymmetrically(BasicEntity source, BasicEntity target,
        int increment) {
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
          newMap.put(innerEntry.getKey(), (double)innerEntry.getValue() / maxWeight);
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
