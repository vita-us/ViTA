/*
 * ParametersService.java
 *
 */

package de.unistuttgart.vis.vita.services.analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.unistuttgart.vis.vita.analysis.annotations.Default;
import de.unistuttgart.vis.vita.analysis.annotations.Description;
import de.unistuttgart.vis.vita.analysis.annotations.Label;
import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.EnumNLP;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.responses.parameters.AbstractParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.BooleanParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.EnumParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.MinMaxParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.ParametersResponse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
  public Response getAvailableParameters() {
    Class<AnalysisParameters> params = AnalysisParameters.class;
    List<AbstractParameter> parameterList = new ArrayList<>();
    AbstractParameter parameter;

    for (Field field : params.getDeclaredFields()) {
      field.setAccessible(true);

      if (field.getType() == boolean.class) {
        parameter = new BooleanParameter(field.getName(), "boolean");
      } else if (field.getType() == int.class || field.getType() == long.class) {
        long min = Integer.MIN_VALUE;
        long max = Integer.MAX_VALUE;
        if (field.getAnnotation(Min.class) != null) {
          min = field.getAnnotation(Min.class).value();
        }
        if (field.getAnnotation(Max.class) != null) {
          max = field.getAnnotation(Max.class).value();
        }
        parameter = new MinMaxParameter(field.getName(), "int", min, max);
      } else if (field.getType() == EnumNLP.class) {
        parameter = new EnumParameter(field.getName(), "enum");
        EnumParameter temp = (EnumParameter) parameter;
        EnumNLP[] enumConstants = (EnumNLP[]) field.getType().getEnumConstants();
        temp.getValues().addAll(Arrays.asList(enumConstants));
      } else {
        continue;
      }

      if (field.getAnnotation(Description.class) != null) {
        parameter.setDescription(field.getAnnotation(Description.class).value());
      }

      if (field.getAnnotation(Label.class) != null) {
        parameter.setLabel(field.getAnnotation(Label.class).value());
      }

      if (field.getAnnotation(Default.class) != null) {
        parameter.setDefaultValue(field.getAnnotation(Default.class).value());
      }

      parameterList.add(parameter);
    }
    ObjectMapper mapper = new ObjectMapper();

    try {
      return Response.ok(mapper.writeValueAsString(new ParametersResponse(parameterList))).build();
    } catch (JsonProcessingException e) {
      return Response.serverError().build();
    }
  }
}
