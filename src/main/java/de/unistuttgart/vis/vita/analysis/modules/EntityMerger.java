package de.unistuttgart.vis.vita.analysis.modules;

import java.util.*;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;

/**
 * Merges entities with the same names into each other
 */
public class EntityMerger {
  private Map<String, BasicEntity> entitiesByName = new HashMap<String, BasicEntity>();
  private List<BasicEntity> result = new ArrayList<BasicEntity>();

  /**
   * Adds all the entities
   * @param entities
   */
  public void addAll(Iterable<BasicEntity> entities) {
    for (BasicEntity entity : entities) {
      add(entity);
    }
  }

  /**
   * Adds and merges an entity
   * @param entity
   */
  public void add(BasicEntity entity) {
    Set<BasicEntity> existing = getEntitiesCalledAs(entity);

    // Merge all old entities in the new one, and re-point the names to the new one
    for (BasicEntity existingEntity : existing) {
      entity.merge(existingEntity);
      result.remove(existingEntity);
    }

    result.add(entity);
    addEntityNames(entity);
  }

  /**
   * Gets all the entities that have a name the given entity also has
   * @param entity the entity whose names to look for
   * @return the set of entities that match
   */
  private Set<BasicEntity> getEntitiesCalledAs(BasicEntity entity) {
    Set<BasicEntity> result = new HashSet<>();
    for (Attribute attr : entity.getNameAttributes()) {
      if (entitiesByName.containsKey(attr.getContent())) {
        result.add(entitiesByName.get(attr.getContent()));
      }
    }
    return result;
  }

  /**
   * Gets a view of the merged entity list
   * @return
   */
  public List<BasicEntity> getResult() {
    return Collections.unmodifiableList(result);
  }

  /**
   * Adds references for all the names
   * @param entity the entity whose names to add
   */
  private void addEntityNames(BasicEntity entity) {
    for (Attribute attr : entity.getNameAttributes()) {
      System.out.println(attr.getContent() + " -> " + entity.getEntityId());
      entitiesByName.put(attr.getContent(), entity);
    }
  }
}
