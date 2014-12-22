package de.unistuttgart.vis.vita.analysis.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

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
    BasicEntity existing = getEntityCalledAs(entity);

    if (existing != null) {
      existing.merge(entity);
      addEntityNames(existing);
      return;
    }

    result.add(entity);
    addEntityNames(entity);
  }

  private BasicEntity getEntityCalledAs(BasicEntity entity) {
    for (Attribute attr : entity.getNameAttributes()) {
      if (entitiesByName.containsKey(attr.getContent())) {
        return entitiesByName.get(attr.getContent());
      }
    }
    return null;
  }

  /**
   * Gets a view of the merged entity list
   * @return
   */
  public List<BasicEntity> getResult() {
    return Collections.unmodifiableList(result);
  }

  private void addEntityNames(BasicEntity entity) {
    for (Attribute attr : entity.getNameAttributes()) {
      entitiesByName.put(attr.getContent(), entity);
    }
  }
}
