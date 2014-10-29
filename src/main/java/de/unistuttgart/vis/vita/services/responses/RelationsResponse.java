package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds lists with entity ids and the relations between them.s
 */
@XmlRootElement
public class RelationsResponse {
  
  @XmlElementWrapper(name = "entityIds")
  @XmlElement(name = "entityId")
  private List<String> entityIds;
  
  @XmlElementWrapper(name = "relations")
  @XmlElement(name = "relation")
  private List<RelationConfiguration> relations;

  /**
   * Creates a new instance of RelationsResponse, setting all attributes to default values.
   */
  public RelationsResponse() {
    // argument-less constructor needed
  }

  /**
   * Creates a new RelationsResponse with the given entity ids and relations.
   * 
   * @param eIds - the ids of the entities concerned
   * @param relations - the relations between entities to be sent in this RelationResponse
   */
  public RelationsResponse(List<String> eIds, List<RelationConfiguration> relations) {
    this.entityIds = eIds;
    this.relations = relations;
  }

  /**
   * @return a list including all ids of related entities
   */
  public List<String> getEntityIds() {
    return entityIds;
  }

  /**
   * Sets the ids of the related entities.
   * 
   * @param ids - a list including all ids of related entities
   */
  public void setEntityIds(List<String> ids) {
    this.entityIds = ids;
  }

  /**
   * @return a list of relations between entities to be sent in this response
   */
  public List<RelationConfiguration> getRelations() {
    return relations;
  }
  
  /**
   * Sets the relations to be sent with this response.
   * 
   * @param rel - List of relations which should be sent in this RelationsResponse
   */
  public void setRelations(List<RelationConfiguration> rel) {
    this.relations = rel;
  }

}
