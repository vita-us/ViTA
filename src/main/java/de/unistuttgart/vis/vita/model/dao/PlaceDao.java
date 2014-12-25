package de.unistuttgart.vis.vita.model.dao;

import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Represents a data access object for accessing places.
 */
public class PlaceDao extends EntityDao<Place> {

  /**
   * Creates a new data access object for accessing places.
   */
  public PlaceDao() {
    super(Place.class);
  } 

}
