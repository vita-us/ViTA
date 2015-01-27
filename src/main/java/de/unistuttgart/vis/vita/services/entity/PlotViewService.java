package de.unistuttgart.vis.vita.services.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.dao.*;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityType;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.BaseService;
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
public class PlotViewService extends BaseService {

  public static final int MAX_PERSON_COUNT = 10;
  public static final int DEFAULT_PANEL_AMOUNT = 100;

  private String documentId;

  private DocumentDao documentDao;
  
  private ChapterDao chapterDao;
  
  private PersonDao personDao;

  private PlaceDao placeDao;

  private EntityDao entityDao;

  private final Logger LOGGER = Logger.getLogger(PlotViewService.class.getName());

  @Override public void postConstruct() {
    super.postConstruct();
    documentDao = getDaoFactory().getDocumentDao();
    personDao = getDaoFactory().getPersonDao();
    entityDao = getDaoFactory().getEntityDao();
    chapterDao = getDaoFactory().getChapterDao();
    placeDao = getDaoFactory().getPlaceDao();
    entityDao = getDaoFactory().getEntityDao();
  }

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
   * Returns a response including persons, places and scenes for the plot view.
   */
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public PlotViewResponse getPlotView() {

    if (!documentDao.isAnalysisFinished(documentId)) {
      LOGGER.log(Level.FINEST, "Plot view requested, but analysis not finished yet.");
      // send HTTP 409 Conflict instead of empty response to avoid wrong caching
      throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
    }

    PlotViewResponse response = new PlotViewResponse();

    List<Entity> entities = new ArrayList<>();
    
    int personIndex = 0;
    List<Person> persons = personDao.findInDocument(documentId, 0, MAX_PERSON_COUNT);
    for (Person person : persons) {
      response.getCharacters().add(new PlotViewCharacter(person.getDisplayName(),
          person.getId(), personIndex++));
      entities.add(person);
    }

    long avgChapterLength = chapterDao.getAverageChapterLength(documentId);
    List<Place> places = placeDao.readSpecialPlacesFromDatabase(documentId, avgChapterLength, 0.1, 5);
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
        
        // create scene and add it to response
        PlotViewScene currentScene = new PlotViewScene(index, 1, index++, "");
        
        // get all persons occurring in the current chapter and add them to the scene
        List<Entity> occurringPersons = entityDao.getOccurringEntities(start, end, entities, EntityType.PERSON);
        for (Entity entity : occurringPersons) {
          currentScene.getChars().add(entity.getId());
        }
        
        // get all special places occurring in the current chapter
        List<Entity> occurringPlaces = entityDao.getOccurringEntities(start, end, places, EntityType.PLACE);
        
        String placeNames = "";
        boolean first = true;
        for (Entity place : occurringPlaces) {
          currentScene.getPlaces().add(place.getId());
          
          // save names for scene title
          String currentName = place.getDisplayName();
          placeNames = first ? " @ " + currentName : placeNames + ", " + currentName; 
          first = false;
        }
        
        // create and set title of the scene
        String currentTitle = chapter.getNumber() + " - " + chapter.getTitle() + placeNames;
        currentScene.setTitle(currentTitle);
        
        response.getScenes().add(currentScene);
        response.setPanels(DEFAULT_PANEL_AMOUNT);
      }
    }

    return  response;
  }
  
}
