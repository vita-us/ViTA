package de.unistuttgart.vis.vita.services.responses.plotview;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a scene in a Document for which a plot view should be generated. It holds information
 * like the id, title, start and duration of this scene beside the lists of persons and places 
 * occurring in this scene.
 */
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
  
  @XmlElement(name="places")
  private List<String> places;

  @XmlElement
  private String title;

  /**
   * Creates a new empty PlotViewScene initializing its lists.
   */
  public PlotViewScene() {
    this.chars = new ArrayList<>();
    this.places = new ArrayList<>();
  }

  /**
   * Creates a new PlotViewScene with given start, duration, id and title.
   * 
   * @param start - the start of the scene
   * @param duration - the duration of the scene
   * @param id - the id of the scene
   * @param title - the title of the scene
   */
  public PlotViewScene(int start, int duration, int id, String title) {
    this();
    this.start = start;
    this.duration = duration;
    this.id = id;
    this.title = title;
  }

  /**
   * @return number indicating the start position of this scene
   */
  public int getStart() {
    return start;
  }
  
  /**
   * @return number indicating the duration of this scene
   */
  public int getDuration() {
    return duration;
  }
  
  /**
   * @return the id of this scene
   */
  public int getId() {
    return id;
  }
  
  /**
   * @return list of persons occurring in this scene
   */
  public List<String> getChars() {
    return chars;
  }
  
  /**
   * @return list of places occurring in this scene
   */
  public List<String> getPlaces() {
    return places;
  }

  /**
   * @return the title of this scene
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of this scene.
   * 
   * @param sceneTitle - the new title for this scene to be displayed in the plot view
   */
  public void setTitle(String sceneTitle) {
    this.title = sceneTitle;
  }
  
}
