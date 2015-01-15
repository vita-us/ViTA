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
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewCharacter;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewResponse;
import de.unistuttgart.vis.vita.services.responses.plotview.PlotViewScene;

/**
 * Redirects entity and relations requests for the current Document to the right sub service.
 */
@ManagedBean
public class PlotViewService extends BaseService {

  private String documentId;

  private DocumentDao documentDao;

  private PersonDao personDao;
  
  private EntityDao entityDao;

  @Override public void postConstruct() {
    super.postConstruct();
    documentDao = getDaoFactory().getDocumentDao();
    personDao = getDaoFactory().getPersonDao();
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
   * Gets the plot view data
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
