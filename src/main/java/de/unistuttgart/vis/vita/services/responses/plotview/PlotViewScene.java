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

  @XmlElement
  private String title;

  public PlotViewScene() {

  }

  public PlotViewScene(int start, int duration, int id, List<String> chars, String title) {
    this.start = start;
    this.duration = duration;
    this.id = id;
    this.chars = chars;
    this.title = title;
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

  public String getTitle() {
    return title;
  }
}
