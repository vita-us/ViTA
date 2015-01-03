package de.unistuttgart.vis.vita.services.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.dao.EntityDao;
import de.unistuttgart.vis.vita.model.dao.PersonDao;
import de.unistuttgart.vis.vita.model.dao.PlaceDao;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityType;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewCharacter;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewPlace;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewResponse;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewScene;

/**
 * Represents a service sending scenes, persons and places for the current document to the client 
 * in order to give an overview of the document plot.
 */
@ManagedBean
public class PlotViewService {

  private String documentId;
  
  @Inject
  private DocumentDao documentDao;
  
  @Inject
  private PersonDao personDao;

  @Inject
  private PlaceDao placeDao;

  @Inject
  private EntityDao entityDao;

  /**
   * Sets the id of the Document this service should refer to
   *
   * @param docId - the id of the Document which this EntitiesService should refer to
   */
  public PlotViewService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  /**
   * Returns the Service to access the Entity with the given id.
   *
   * @param id - the id of the Entity to be accessed
   * @return the EntityService to access the given Entity with the given id
   */
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public PlotViewResponse getPlotView() {
    PlotViewResponse response = new PlotViewResponse();

    List<Entity> entities = new ArrayList<>();
    
    int personIndex = 0;
    List<Person> persons = personDao.findInDocument(documentId, 0, 10);
    for (Person person : persons) {
      response.getCharacters().add(new PlotViewCharacter(person.getDisplayName(),
          person.getId(), personIndex++));
      entities.add(person);
    }
    
    int placeIndex = 0;
    List<Place> places = placeDao.readSpecialPlacesFromDatabase(documentId, 10);
    for (Entity place : places) {
      response.getPlaces().add(new PlotViewPlace(place.getId(), place.getDisplayName()));
    }

    Document document = documentDao.findById(documentId);
    int index = 0;
    for (DocumentPart part : document.getContent().getParts()) {
      for (Chapter chapter: part.getChapters()) {
        
        // fetch range offsets for current chapter
        int start = chapter.getRange().getStart().getOffset();
        int end = chapter.getRange().getEnd().getOffset();
        
        // get all persons occurring in the current chapter
        List<Entity> occurringPersons = entityDao.getOccurringEntities(start, end, entities, EntityType.PERSON);
        
        List<String> entityIds = new ArrayList<>();
        for (Entity entity : occurringPersons) {
          entityIds.add(entity.getId());
        }
        
        // get all special places occurring in the current chapter
        List<Entity> occurringPlaces = entityDao.getOccurringEntities(start, end, places, EntityType.PLACE);
        
        List<String> placeIds = new ArrayList<>();
        String placeNames = "";
        boolean first = true;
        for (Entity place : occurringPlaces) {
          placeIds.add(place.getId());
          String currentName = place.getDisplayName();
          placeNames = first ? currentName : placeNames + ", " + currentName; 
          first = false;
        }
        
        String currentTitle = chapter.getNumber() + " - " + chapter.getTitle() + " @ " + placeNames;
        
        // create scene and add it to response
        PlotViewScene currentScene = new PlotViewScene(index, 1, index++, currentTitle);
        response.getScenes().add(currentScene);
        
        response.setPanels(100);
      }
    }

    return  response;
  }
  
}
