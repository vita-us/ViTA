package de.unistuttgart.vis.vita.model.document;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Represents the content of Document, including its parts, persons and places.
 */
@Embeddable
public class DocumentContent {
  
  @OneToMany
  private List<DocumentPart> parts;

  @OneToMany
  private List<Person> persons;

  @OneToMany
  private List<Place> places;

  /**
   * Creates a new content which is completely empty.
   */
  public DocumentContent() {
    parts = new ArrayList<>();
    persons = new ArrayList<>();
    places = new ArrayList<>();
  }

  /**
   * @return a list of parts the Document is divided into.
   */
  public List<DocumentPart> getParts() {
    return parts;
  }

  /**
   * @return list of persons mentioned in the Document
   */
  public List<Person> getPersons() {
    return persons;
  }

  /**
   * @return list of places mentioned in the Document
   */
  public List<Place> getPlaces() {
    return places;
  }

}
