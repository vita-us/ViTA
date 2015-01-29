package de.unistuttgart.vis.vita.analysis.modules;

import java.util.*;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;

/**
 * Merges entities with the same names into each other.
 */
public class EntityMerger {
  // The map is used to assure no name of a known entity is used twice.
  private Map<EntityIdentifier, BasicEntity> entitiesByName = new HashMap<>();
  private List<BasicEntity> result = new ArrayList<BasicEntity>();

  /**
   * The Entity Identifier defines whether an Entity equals another Entity or not. To do so, this
   * class needs some attributes of the entity, but does not know the entity itself.
   */
  private static class EntityIdentifier {
    private String name;
    private EntityType type;

    public EntityIdentifier(String name, EntityType type) {
      this.name = name;
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      EntityIdentifier that = (EntityIdentifier) o;

      if (!name.equals(that.name)) {
        return false;
      }
      if (type != that.type) {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int hashCode = name.hashCode();
      hashCode = 31 * hashCode + type.hashCode();
      return hashCode;
    }
  }

  /**
   * Adds all the Entities and merges them into the already added Entities.
   * 
   * @param entities - The new Entities.
   */
  public void addAll(Iterable<BasicEntity> entities) {
    for (BasicEntity entity : entities) {
      add(entity);
    }
  }

  /**
   * Adds an Entity and merges it into the already added Entities.
   * 
   * @param entity - The new Entity.
   */
  public void add(BasicEntity entity) {
    Set<BasicEntity> existingEntitiesWithSameName = getEntitiesCalledAs(entity);

    // Merge all old entities in the new one, and re-point the names to the new one
    for (BasicEntity existingEntity : existingEntitiesWithSameName) {
      entity.merge(existingEntity);
      result.remove(existingEntity);
    }

    result.add(entity);
    addEntityNames(entity);
  }

  /**
   * Gets all the known entities that have a name the given entity also has.
   * 
   * @param entity the entity whose names to look for
   * @return the set of entities that match
   */
  private Set<BasicEntity> getEntitiesCalledAs(BasicEntity entity) {
    Set<BasicEntity> entitiesWithSameName = new HashSet<>();
    for (Attribute attr : entity.getNameAttributes()) {
      EntityIdentifier identifier = new EntityIdentifier(attr.getContent(), entity.getType());
      if (entitiesByName.containsKey(identifier)) {
        entitiesWithSameName.add(entitiesByName.get(identifier));
      }
    }
    return entitiesWithSameName;
  }

  /**
   * Gets a view of the merged entity list. Make sure all entities were added, otherwise this is not
   * the final result!
   * 
   * @return The merged Entities at this moment.
   */
  public List<BasicEntity> getResult() {
    return Collections.unmodifiableList(result);
  }

  /**
   * Adds references for all the names. Will override already existing references.
   * 
   * @param entity the entity whose names to add
   */
  private void addEntityNames(BasicEntity entity) {
    for (Attribute attr : entity.getNameAttributes()) {
      entitiesByName.put(new EntityIdentifier(attr.getContent(), entity.getType()), entity);
    }
  }
}
