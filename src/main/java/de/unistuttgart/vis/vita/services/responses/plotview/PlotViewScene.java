package de.unistuttgart.vis.vita.services.responses.plotview;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlotViewScene {
  @XmlElement
  private int start;
  @XmlElement
  private int duration;
  @XmlElement
  private int id;
  @XmlElement(name="chars")
  private List<String> chars;

  public PlotViewScene() {

  }

  public PlotViewScene(int start, int duration, int id, List<String> chars) {
    this.start = start;
    this.duration = duration;
    this.id = id;
    this.chars = chars;
  }

  public int getStart() {
    return start;
  }
  public int getDuration() {
    return duration;
  }
  public int getId() {
    return id;
  }
  public List<String> getChars() {
    return chars;
  }
}
