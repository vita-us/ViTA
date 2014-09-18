package de.unistuttgart.vis.vita.services;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.*;
import de.unistuttgart.vis.vita.services.responses.AttributesResponse;

/**
 * Provides a method to GET all attributes mentioned in the document this service refers to
 *
 */
@ManagedBean
public class AttributesService {

	private String entityId;
	
	@Context
	private EntityManager em;
	private ResourceContext resourceContext;

	/**
	 * Creates a new instance of AttributesService
	 * @param model
	 */
	@Inject
	public AttributesService(Model model) {
		em = model.getEntityManager();
	}

	public AttributesService setEntityId(String id) {
		this.entityId = id;
		return this;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public AttributesResponse getAttributes(@QueryParam("offset") int offset,
			@QueryParam("count") int count) {
		List<Attribute> attributes = readAttributesFromDatabase(offset, count);
		return new AttributesResponse(attributes);
	}

	private List<Attribute> readAttributesFromDatabase(int offset, int count) {
		TypedQuery<Attribute> query = em.createNamedQuery(
				"Attribute.findAttributesFromEntities", Attribute.class);
		query.setParameter("entityId", entityId);

		query.setFirstResult(offset);
		query.setMaxResults(count);
		
		return query.getResultList();
		

	}
	@Path("{attributeId}") 
	public AttributeService getAttribute(@PathParam("attributeId") String id) {
		return resourceContext.getResource(AttributeService.class).setAttributeId(id);
	}

}
