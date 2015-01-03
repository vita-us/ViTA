package de.unistuttgart.vis.vita.services.responses.plotview;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a person to be shown in the plot view, holding its name, id and group.
 */
@XmlRootElement
public class PlotViewCharacter {

  @XmlElement
  private String name;

  @XmlElement
  private String id;

  @XmlElement
  private int group;

  /**
   * Creates a new empty PlotViewCharacter.
   */
  public PlotViewCharacter() {
    // zero argument constructor needed
  }

  /**
   * Creates a new PlotViewCharacter with given id, name and group.
   * 
   * @param name - the name of the occurring Person this PlotViewCharacter refers to
   * @param id - the id of the occurring Person this PlotViewCharacter refers to
   * @param group - the number of the group this PlotViewCharacter belongs to
   */
  public PlotViewCharacter(String name, String id, int group) {
    this.name = name;
    this.id = id;
    this.group = group;
  }

  /**
   * @return the name of the occurring Person this PlotViewCharacter refers to
   */
  public String getName() {
    return name;
  }

  /**
   * @return the id of the occurring Person this PlotViewCharacter refers to
   */
  public String getId() {
    return id;
  }

  /**
   * @return the number of the group this PlotViewCharacter belongs to
   */
  public int getGroup() {
    return group;
  }

}
