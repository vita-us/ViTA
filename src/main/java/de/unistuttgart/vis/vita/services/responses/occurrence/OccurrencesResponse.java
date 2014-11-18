package de.unistuttgart.vis.vita.services.responses.occurrence;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds a list of Occurrences to be sent as a response.
 */
@XmlRootElement
public class OccurrencesResponse {

  @XmlElement(name = "occurrences")
  private List<Occurrence> occurrences;
  
  /**
   * Creates a new instance of OccurrencesResponse, setting all attributes to default values.
   */
  public OccurrencesResponse() {
    // zero-argument constructor needed
  }
  
  /**
   * Creates a new OccurrenceResponse with the given Occurrences.
   * 
   * @param occList - the list of Occurrences to be sent
   */
  public OccurrencesResponse(List<Occurrence> occList) {
    this.occurrences = occList;
  }

  /**
   * @return the Occurrences stored in this response
   */
  public List<Occurrence> getOccurrences() {
    return occurrences;
  }

  /**
   * Sets the list of Occurrences being sent as response.
   * 
   * @param occList - the list of Occurrences to be sent
   */
  public void setOccurrences(List<Occurrence> occList) {
    this.occurrences = occList;
  }

}
