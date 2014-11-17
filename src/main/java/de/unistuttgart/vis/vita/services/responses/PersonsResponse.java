package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Holds a list of all persons mentioned in a document and their count.
 */
@XmlRootElement
public class PersonsResponse extends AbstractListResponse {
  
  @XmlElement(name = "persons")
  private List<Person> persons;
  
  /**
   * Creates a new instance of PersonsResponse, setting all attributes to default values.
   */
  public PersonsResponse() {
    // must have a non-argument constructor
  }
  
  /**
   * Create a new response including the given persons and their count.
   * 
   * @param personList - the list of persons to be stored in this response
   */
  public PersonsResponse(List<Person> personList) {
    super(personList.size());
    this.setPersons(personList);
  }

  /**
   * @return list of persons stored in this response
   */
  public List<Person> getPersons() {
    return persons;
  }
  
  /**
   * Sets the List of persons to be stored in this response.
   * 
   * @param personList - the list of persons to be stored in this response
   */
  public void setPersons(List<Person> personList) {
    this.persons = personList;
    this.totalCount = personList.size();
  }

}
