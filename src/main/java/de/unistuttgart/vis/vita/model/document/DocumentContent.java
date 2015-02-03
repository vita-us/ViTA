package de.unistuttgart.vis.vita.model.document;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

/**
 * Represents the content of Document, including its parts, persons and places.
 */
@Embeddable
public class DocumentContent {

  @OneToMany
  @OrderBy("number ASC")
  private List<DocumentPart> parts;

  @OneToMany
  @JoinTable(name = "Document_Person")
  @OrderBy("rankingValue ASC")
  private List<Person> persons;

  @OneToMany
  @JoinTable(name = "Document_Place")
  @OrderBy("rankingValue ASC")
  private List<Place> places;

  @OneToOne
  private WordCloud globalWordCloud;

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

  /**
   * @return the global word cloud for this Document
   */
  public WordCloud getGlobalWordCloud() {
    return globalWordCloud;
  }

  /**
   * Sets the global word cloud for this Document.
   *
   * @param globalWordCloud - the global word cloud representing this Document
   */
  public void setGlobalWordCloud(WordCloud globalWordCloud) {
    this.globalWordCloud = globalWordCloud;
  }

}
