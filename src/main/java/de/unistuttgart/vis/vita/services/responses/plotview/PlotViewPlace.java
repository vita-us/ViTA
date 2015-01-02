package de.unistuttgart.vis.vita.services.responses.plotview;

import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a place returned by the {@link PlotViewService}, holding the id and name of a place.
 */
public class PlotViewPlace {
  
  @XmlElement
  private String id;
  
  @XmlElement
  private String name;
  
  public PlotViewPlace() {
    // zero argument constructor needed
  }
  
  public PlotViewPlace(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
