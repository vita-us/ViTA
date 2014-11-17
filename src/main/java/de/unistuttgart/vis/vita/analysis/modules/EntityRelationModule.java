package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.EntityRelations;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@AnalysisModule(dependencies=BasicEntityCollection.class)
public class EntityRelationModule extends Module<EntityRelations> {

  /**
   * The maximum distance in characters two entities my occur at to be considered in a relation
   */
  private static final int MAX_DISTANCE = 50;
  private SortedSet<RelationEvent> events = new TreeSet<>();
  private Map<BasicEntity, Integer> presentEntities = new HashMap<>();
  private Map<BasicEntity, Map<BasicEntity, Integer>> weights = new HashMap<>();
  private Map<BasicEntity, Map<BasicEntity, Double>> normalizedWeights = new HashMap<>();
  private int maxWeight = 0;
  
  @Override
  public EntityRelations execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    Collection<BasicEntity> entities = results.getResultFor(BasicEntityCollection.class)
        .getEntities();
    
    for (BasicEntity entity : entities) {
      addEvents(entity);
    }
    
    processEvents();
    normalizeWeights();
    
    return new EntityRelations() {
      
      @Override
      public Map<BasicEntity, Double> getRelatedEntities(BasicEntity entity) {
        return normalizedWeights.get(entity);
      }
    };
  }
  
  private void addEvents(BasicEntity entity) {
    for (TextSpan occurrence : entity.getOccurences()) {
      occurrence = widenOccurrence(occurrence);
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
          handleEntityEnter(event.entity);
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
  
  private void handleEntityEnter(BasicEntity entity) {
    if (presentEntities.isEmpty()) {
      return;
    }

    for (Map.Entry<BasicEntity, Integer> entry : presentEntities.entrySet()) {
      BasicEntity other = entry.getKey();

      if (other == entity) {
        continue;
      }

      increaseWeights(entity, other, entry.getValue());
      increaseWeights(other, entity, entry.getValue());
    }
  }
  
  /**
   * Increases the weighting factor of the source towards the target
   */
  private void increaseWeights(BasicEntity source, BasicEntity target, int increment) {
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
  
  private void normalizeWeights() {
    for (Map.Entry<BasicEntity, Map<BasicEntity, Integer>> entry : weights.entrySet()) {
      Map<BasicEntity, Double> newMap = new HashMap<>();
      normalizedWeights.put(entry.getKey(), newMap);

      for (Map.Entry<BasicEntity, Integer> innerEntry : entry.getValue().entrySet()) {
        newMap.put(innerEntry.getKey(), (double)innerEntry.getValue() / maxWeight);
      }
    }
  }
  
  /**
   * Extends the start and the end of the given text span by {@link #MAX_DISTANCE} / 2 each,
   * but not across chapter boundaries
   * @param occurrence the span to widen
   * @return the widened span
   */
  private TextSpan widenOccurrence(TextSpan occurrence) {
    int chapterStart = occurrence.getStart().getChapter().getRange().getStart().getOffset();
    int start = occurrence.getStart().getOffset() - MAX_DISTANCE / 2;

    if (start < chapterStart) {
      start = chapterStart;
    }

    int chapterEnd = occurrence.getEnd().getChapter().getRange().getEnd().getOffset();
    int end = occurrence.getEnd().getOffset() + MAX_DISTANCE / 2;

    if (end > chapterEnd) {
      end = chapterEnd;
    }

    return new TextSpan(TextPosition.fromGlobalOffset(occurrence.getStart().getChapter(), start),
        TextPosition.fromGlobalOffset(occurrence.getEnd().getChapter(), end));
    
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

}
