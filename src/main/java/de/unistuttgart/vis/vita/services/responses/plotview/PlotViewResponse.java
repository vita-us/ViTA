package de.unistuttgart.vis.vita.services.responses.plotview;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.services.entity.PlotViewService;

/**
 * Represents a response sent by the {@link PlotViewService}, holding lists of scenes, characters
 * and places together with the amount of panels.
 */
@XmlRootElement
public class PlotViewResponse {
  
  @XmlElement(name="characters")
  private List<PlotViewCharacter> characters;

  @XmlElement
  private int panels;

  @XmlElement
  private List<PlotViewScene> scenes;
  
  @XmlElement
  private List<PlotViewPlace> places;

  /**
   * Creates a new empty PlotViewResponse and initializes the lists.
   */
  public PlotViewResponse() {
    // initialize lists so there is no need for list setter
    characters = new ArrayList<>();
    scenes = new ArrayList<>();
    places = new ArrayList<>();
  }

  /**
   * Creates a new PlotViewResponse with given scene, character and place lists and an also given
   * amount of panels.
   * 
   * @param characters - the list of characters to be returned
   * @param panels - the amount of panels
   * @param scenes - the list of scenes to be returned in this response
   * @param places - the list of places to be returned in this response
   */
  public PlotViewResponse(List<PlotViewCharacter> characters, int panels,
      List<PlotViewScene> scenes, List<PlotViewPlace> places) {
    this.characters = characters;
    this.panels = panels;
    this.scenes = scenes;
    this.places = places;
  }

  /**
   * @return the list of occurring persons to be sent in this response
   */
  public List<PlotViewCharacter> getCharacters() {
    return characters;
  }

  /**
   * @return the amount of panels for the plot view
   */
  public int getPanels() {
    return panels;
  }

  /**
   * Sets the amount of panels for this response.
   * 
   * @param panelAmount - the amount of panels for the plot view
   */
  public void setPanels(int panelAmount) {
    this.panels = panelAmount;
  }

  /**
   * @return the list of scenes in the Document to be returned in this response
   */
  public List<PlotViewScene> getScenes() {
    return scenes;
  }

  /**
   * @return the list of special places in the Document to be returned in this response
   */
  public List<PlotViewPlace> getPlaces() {
    return places;
  }

}
