package de.unistuttgart.vis.vita.services.responses.plotview;

import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a place returned by the {@link PlotViewService}, holding the id and name of the 
 * referring place.
 */
public class PlotViewPlace {
  
  @XmlElement
  private String id;
  
  @XmlElement
  private String name;
  
  /**
   * Creates a new empty PlotViewPlace.
   */
  public PlotViewPlace() {
    // zero argument constructor needed
  }
  
  /**
   * Creates a new PlotViewPlace with given id and name.
   * 
   * @param placeId - the id of the referring Place
   * @param placeName - the name of the referring place to be displayed in the plot view
   */
  public PlotViewPlace(String placeId, String placeName) {
    this.id = placeId;
    this.name = placeName;
  }

  /**
   * @return the id of the referring Place
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id for this PlotViewPlace.
   * 
   * @param placeId - the id of the referring place
   */
  public void setId(String placeId) {
    this.id = placeId;
  }

  /**
   * @return the name of the referring place to be displayed in the plot view
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this PlotViewPlace to be displayed in the plot view.
   * 
   * @param placeName - the name of the referring place
   */
  public void setName(String placeName) {
    this.name = placeName;
  }

}
