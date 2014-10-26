package de.unistuttgart.vis.vita.services.occurrence;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;
import de.unistuttgart.vis.vita.model.document.TextSpan;

public class SearchInDocumentService extends OccurrencesService {
	private String documentId;
	
	private EntityManager em;
	
	@Context
	private ResourceContext resourceContext;
	
	@Inject
	public SearchInDocumentService(Model model) {
		em = model.getEntityManager();
	}
	
	public SearchInDocumentService setDocumentId(String documentId) {
		this.documentId = documentId;
		return this;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public OccurrencesResponse getOccurrences(@QueryParam("steps") int steps, 
														  @QueryParam("rangeStart") double rangeStart, 
														  @QueryParam("rangeEnd") double rangeEnd) {
		 
		// gets the data
		List<TextSpan> readTextSpans = readTextSpansFromDatabase(steps);
		
		// convert TextSpans to Occurences
		List<Occurrence> occurences = covertSpansToOccurrences(readTextSpans);
		
	    return new OccurrencesResponse(occurences);
	}

	private List<TextSpan> readTextSpansFromDatabase(int steps) {
		TypedQuery<TextSpan> query = em.createNamedQuery("TextSpan.findTextSpansForQueryString",
				  TextSpan.class);
		query.setParameter("documentId", documentId);
		query.setMaxResults(steps);
		return query.getResultList();
	}

}
