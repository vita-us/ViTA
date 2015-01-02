package de.unistuttgart.vis.vita.services.responses.plotview;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlotViewCharacter {
  @XmlElement
  private String name;
  @XmlElement
  private String id;
  @XmlElement
  private int group;

  public PlotViewCharacter() {
    // zero argument constructor needed
  }

  public PlotViewCharacter(String name, String id, int group) {
    this.name = name;
    this.id = id;
    this.group = group;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public int getGroup() {
    return group;
  }
}
