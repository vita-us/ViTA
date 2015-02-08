/*
 * ParametersService.java
 *
 */

package de.unistuttgart.vis.vita.services.analysis;

import de.unistuttgart.vis.vita.analysis.annotations.Description;
import de.unistuttgart.vis.vita.analysis.annotations.Label;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.EnumNLP;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.responses.parameters.AbstractParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.BooleanParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.EnumParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.MinMaxParameter;
import de.unistuttgart.vis.vita.services.responses.parameters.ParametersResponse;
import de.unistuttgart.vis.vita.services.responses.parameters.StringParameter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
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

  private static final Logger LOGGER = Logger.getLogger(ParametersService.class.getName());

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
        parameter = createBooleanParameter(field);
      } else if (field.getType() == String.class) {
        parameter = new StringParameter(field.getName());
      } else if (field.getType() == int.class || field.getType() == long.class) {
        parameter = createIntParameter(field);
      } else if (field.getType() == EnumNLP.class) {
        parameter = createEnumParameter(field);
      } else {
        continue;
      }

      parameter = setGeneralParameterValues(field, parameter);
      parameterList.add(parameter);
    }
    return new ParametersResponse(parameterList);
  }
  
  /**
   * Creates a boolean parameter from the field.
   * 
   * @param field - The field of type boolean.
   * @return The boolean Parameter
   */
  private AbstractParameter createBooleanParameter(Field field){
    return new BooleanParameter(field.getName(), "boolean");
  }
  
  /**
   * Creates a long or int parameter from the field.
   * 
   * @param field - The field of type long or int.
   * @return The int Parameter
   */
  private AbstractParameter createIntParameter(Field field){
    long min = Integer.MIN_VALUE;
    long max = Integer.MAX_VALUE;
    if (field.getAnnotation(Min.class) != null) {
      min = field.getAnnotation(Min.class).value();
    }
    if (field.getAnnotation(Max.class) != null) {
      max = field.getAnnotation(Max.class).value();
    }
    return new MinMaxParameter(field.getName(), "int", min, max);
  }
  
  /**
   * Creates a enum parameter from the field.
   * 
   * @param field - The field of type EnumNLP.
   * @return The enum Parameter
   */
  private AbstractParameter createEnumParameter(Field field){
    AbstractParameter parameter = new EnumParameter(field.getName(), "enum");
    EnumParameter temp = (EnumParameter) parameter;
    EnumNLP[] enumConstants = (EnumNLP[]) field.getType().getEnumConstants();
    temp.addValues(Arrays.asList(enumConstants));
    return parameter;
  }
  
  /**
   * For a given field adds the values which do not depend on the field type
   * 
   * @param field - A field of the analysis parameters.
   * @param parameter - The parameter created for the given field.
   * @return The parameter with added values
   */
  private AbstractParameter setGeneralParameterValues(Field field, AbstractParameter parameter){
    
    if (field.getAnnotation(Description.class) != null) {
      parameter.setDescription(field.getAnnotation(Description.class).value());
    }

    if (field.getAnnotation(Label.class) != null) {
      parameter.setLabel(field.getAnnotation(Label.class).value());
    }

    try {
      // used for default values
      AnalysisParameters instance = new AnalysisParameters();
      parameter.setDefaultValue(field.get(instance));
    } catch (IllegalAccessException e) {
      LOGGER.log(Level.WARNING, "Error getting default value of parameter "  + field.getName(), e);
    }
    
    return parameter;
  }
}
