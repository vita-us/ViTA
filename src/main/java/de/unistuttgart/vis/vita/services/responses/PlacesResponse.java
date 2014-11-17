package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Holds a list of all places mentioned in a Document and their count.
 */
@XmlRootElement
public class PlacesResponse extends AbstractListResponse {

  @XmlElement(name = "places")
  private List<Place> places;
  
  /**
   * Creates a new instance of PersonsResponse, setting all attributes to default values.
   */
  public PlacesResponse() {
    // must have a non-argument constructor
  }
  
  /**
   * Create a new response including the given places and their count.
   * 
   * @param placeList - the list of places to be stored in this response
   */
  public PlacesResponse(List<Place> placeList) {
    super(placeList.size());
    this.setPlaces(placeList);
  }

  /**
   * @return list of places stored in this response
   */
  public List<Place> getPlaces() {
    return places;
  }

  /**
   * Sets the List of places to be stored in this response.
   * 
   * @param placeList - the list of places to be stored in this response
   */
  public void setPlaces(List<Place> placeList) {
    this.places = placeList;
    this.totalCount = placeList.size();
  }
  
}
