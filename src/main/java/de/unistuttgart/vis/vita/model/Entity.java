package de.unistuttgart.vis.vita.model;

import java.util.Set;

/**
 * Represents an entity found in the document.
 * 
 * @author Marc Weise
 */
public class Entity {

  private int id;
  private EntityType type;
  private String displayName;
  private Set<Attribute> attributes;
  private int rankingValue;
  private Set<TextSpan> occurences;
  private boolean[] fingerprint;

  private Set<EntityRelation<Entity>> entityRelations;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public EntityType getType() {
    return type;
  }

  public void setType(EntityType type) {
    this.type = type;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Set<Attribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Set<Attribute> attributes) {
    this.attributes = attributes;
  }

  public int getRankingValue() {
    return rankingValue;
  }

  public void setRankingValue(int rankingValue) {
    this.rankingValue = rankingValue;
  }

  public Set<TextSpan> getOccurences() {
    return occurences;
  }

  public void setOccurences(Set<TextSpan> occurences) {
    this.occurences = occurences;
  }

  public boolean[] getFingerprint() {
    return fingerprint;
  }

  public void setFingerprint(boolean[] fingerprint) {
    this.fingerprint = fingerprint;
  }

  public Set<EntityRelation<Entity>> getEntityRelations() {
    return entityRelations;
  }

  public void setEntityRelations(Set<EntityRelation<Entity>> entityRelations) {
    this.entityRelations = entityRelations;
  }
  
}
