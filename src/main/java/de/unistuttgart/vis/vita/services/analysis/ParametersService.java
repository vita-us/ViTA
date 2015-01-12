/*
 * ParametersService.java
 *
 */

package de.unistuttgart.vis.vita.services.analysis;

import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.responses.parameters.AbstractParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.BooleanParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.MinMaxParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.ParametersResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@ManagedBean
public class ParametersService {

  private String id;

  @Inject
  private DocumentDao documentDao;

  private Map<String, Type> nameToType = new HashMap<>();
  private Map<String, List<Annotation>> nameToAnnotations = new HashMap<>();

  public static void main(String[] args) {
    ParametersService service = new ParametersService();
    ParametersResponse availableParameters = service.getAvailableParameters();

    for (AbstractParameter parameter : availableParameters.getParameters()) {
      System.out.println(parameter.getName() + " " + parameter.getType());
    }
  }

  /**
   * Sets the id of the document this resource should represent
   *
   * @param id the id
   */
  public ParametersService setId(String id) {
    this.id = id;
    return this;
  }

  @GET
  @Path("/available")
  @Produces(MediaType.APPLICATION_JSON)
  public ParametersResponse getAvailableParameters() {
    Class<AnalysisParameters> params = AnalysisParameters.class;
    List<AbstractParameter> parameterList = new ArrayList<>();
    AbstractParameter parameter;

    for (Field field : params.getDeclaredFields()) {
      field.setAccessible(true);

      if (field.getType() != boolean.class) {
        long min = field.getAnnotation(Min.class).value();
        long max = field.getAnnotation(Max.class).value();
        parameter = new MinMaxParameter(field.getName(), field.getType(), min, max);
      } else {
        parameter = new BooleanParameter(field.getName(), field.getType());
      }

      parameterList.add(parameter);
    }

    return new ParametersResponse(parameterList);
  }

  @GET
  @Path("/current")
  @Produces(MediaType.APPLICATION_JSON)
  public AnalysisParameters getParameters() {
    Document readDoc;
    try {
      readDoc = documentDao.findById(id);
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }

    return readDoc.getParameters();
  }
}
