/*
 * ParametersService.java
 *
 */

package de.unistuttgart.vis.vita.services.analysis;

import de.unistuttgart.vis.vita.analysis.annotations.Description;
import de.unistuttgart.vis.vita.analysis.annotations.Label;
import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.responses.parameters.AbstractParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.BooleanParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.MinMaxParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.ParametersResponse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Service for the parameters. Provides a GET method for all available parameters.
 */
@Path("/analysis-parameters")
@ManagedBean
public class ParametersService extends BaseService {
  private DocumentDao documentDao;

  @Override public void postConstruct() {
    super.postConstruct();
    documentDao = getDaoFactory().getDocumentDao();
  }

  /**
   * Method for retrieving all available parameters as JSON response.
   * @return The parameters in JSON.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public ParametersResponse getAvailableParameters() {
    Class<AnalysisParameters> params = AnalysisParameters.class;
    List<AbstractParameter> parameterList = new ArrayList<>();
    AbstractParameter parameter;

    for (Field field : params.getDeclaredFields()) {
      field.setAccessible(true);

      if (field.getType() == boolean.class) {
        parameter = new BooleanParameter(field.getName(), field.getType());
      } else if (field.getType() == int.class || field.getType() == long.class) {
        long min = Integer.MIN_VALUE;
        long max = Integer.MAX_VALUE;
        if (field.getAnnotation(Min.class) != null) {
          min = field.getAnnotation(Min.class).value();
        }
        if (field.getAnnotation(Max.class) != null) {
          max = field.getAnnotation(Max.class).value();
        }
        parameter = new MinMaxParameter(field.getName(), field.getType(), min, max);
      } else {
        continue;
      }

      if (field.getAnnotation(Description.class) != null) {
        parameter.setDescription(field.getAnnotation(Description.class).value());
      }

      if (field.getAnnotation(Label.class) != null) {
        parameter.setLabel(field.getAnnotation(Label.class).value());
      }

      parameterList.add(parameter);
    }

    return new ParametersResponse(parameterList);
  }
}
