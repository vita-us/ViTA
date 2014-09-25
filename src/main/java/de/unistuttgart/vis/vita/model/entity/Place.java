package de.unistuttgart.vis.vita.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Represents a place in a document.
 */
@javax.persistence.Entity
@DiscriminatorValue(Place.DISCRIMINATOR_VALUE)
@NamedQueries({
  @NamedQuery(name = "Place.findAllPlaces",
      query = "SELECT pl "
      + "FROM Place pl"),
      
  @NamedQuery(name = "Place.findPlaceById",
      query = "SELECT pl "
      + "FROM Place pl "
      + "WHERE pl.id = :placeId"),
  
  @NamedQuery(name = "Place.findPlaceByName",
      query = "SELECT pl "
      + "FROM Place pl "
      + "WHERE pl.displayName = :placeName")
})
public class Place extends Entity {

  // constants
  public static final String DISCRIMINATOR_VALUE = "Place";

  @Override
  public EntityType getType() {
    return EntityType.PLACE;
  }
  
}
