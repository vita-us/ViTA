package de.unistuttgart.vis.vita.model.entity;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Converts an EntityRelation into its flat representation {@link FlatEntityRelation} which only
 * holds the id of the related entity and the weight of the original EntityRelation.
 */
public class FlatEntityRelationAdapter extends XmlAdapter<FlatEntityRelation, EntityRelation> {

  /**
   * Unmarshalling is only for test purposes, obviously the lost information of the flat 
   * representation could not be restored
   */
  @Override
  public EntityRelation unmarshal(FlatEntityRelation flatRel) throws Exception {
    EntityRelation rel = new EntityRelation();
    rel.setWeight(flatRel.getWeight());
    return rel;
  }

  /**
   * Marshalling a given EntityRelation means to convert it into its flat representation, this 
   * means information like the origin entity is lost.
   */
  @Override
  public FlatEntityRelation marshal(EntityRelation rel) throws Exception {
    return new FlatEntityRelation(rel.getRelatedEntity().getId(),
                                  rel.getWeight(),
                                  rel.getRelatedEntity().getType());
  }

}
