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
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewCharacter;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewResponse;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewScene;

/**
 * Redirects entity and relations requests for the current Document to the right sub service.
 */
@ManagedBean
public class PlotViewService {

  private String documentId;
  
  @Inject
  private DocumentDao documentDao;
  
  @Inject
  private PersonDao personDao;
  
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

    int personIndex = 0;
    List<Person> persons = personDao.findInDocument(documentId, 0, 10);
    for (Person person : persons) {
      response.getCharacters().add(new PlotViewCharacter(person.getDisplayName(),
          person.getId(), personIndex++));
    }

    Document document = documentDao.findById(documentId);
    response.setPanels(document.getMetrics().getCharacterCount());
    int index = 0;
    for (DocumentPart part : document.getContent().getParts()) {
      for (Chapter chapter: part.getChapters()) {
        List<Entity> occurringEntities = entityDao.findOccurringPersons(
            chapter.getRange().getStart().getOffset(),
            chapter.getRange().getEnd().getOffset(), persons);

        List<String> entityIds = new ArrayList<String>();
        for (Entity entity : occurringEntities) {
          entityIds.add(entity.getId());
        }

        response.getScenes().add(new PlotViewScene(
            index,//chapter.getRange().getStart().getOffset(),
            1,//chapter.getRange().getLength(),
            index++,
            entityIds,
            chapter.getNumber() + " - " + chapter.getTitle()));
      }
    }
    response.setPanels(index);

    return  response;
  }

}
