package de.unistuttgart.vis.vita.services.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.lucene.queryparser.classic.ParseException;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.search.Searcher;
import de.unistuttgart.vis.vita.services.occurrence.IllegalRangeException;
import de.unistuttgart.vis.vita.services.occurrence.OccurrencesService;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

@ManagedBean
public class SearchInDocumentService extends OccurrencesService {

  private List<TextSpan> textSpans;

  @Inject
  private Model model;

  private final Logger LOGGER = Logger.getLogger(SearchInDocumentService.class.getName());

  /**
   * Sets the id of the document in which this service should search in.
   *
   * @param documentId - the id of the document to search in
   * @return this SearchInDocumentService
   */
  public SearchInDocumentService setDocumentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public OccurrencesResponse getOccurrences(@QueryParam("steps") int steps,
                                            @QueryParam("rangeStart") double rangeStart,
                                            @QueryParam("rangeEnd") @DefaultValue("1") double rangeEnd,
                                            @QueryParam("query") @DefaultValue("") String query) throws IOException {
    // first check amount of steps
    if (steps < 0 || steps > 1000) {
      throw new WebApplicationException(new IllegalArgumentException("Illegal amount of steps!"), 500);
    }

    // check range
    if (rangeEnd < rangeStart) {
      throw new WebApplicationException("Illegal range!");
    }

    int startOffset;
    int endOffset;

    // calculate offsets
    try {
      startOffset = getStartOffset(rangeStart);
      endOffset = getEndOffset(rangeEnd);
    } catch(IllegalRangeException ire) {
      throw new WebApplicationException(ire);
    }

    if (!documentDao.isAnalysisFinished(documentId)) {
      LOGGER.log(Level.INFO, "Cannot search in document while analysis is still running.");
      throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
    }

    Chapter startChapter = getSurroundingChapter(startOffset);
    Chapter endChapter = getSurroundingChapter(endOffset);
    List<Chapter> chapters = getChaptersInRange(startChapter, endChapter);

    Searcher searcher = new Searcher();
    try {
      textSpans = searcher.searchString(documentId, query, chapters, model);
    } catch (ParseException e) {
      LOGGER.log(Level.INFO, "Invalid search query: " + query, e);
      return new OccurrencesResponse(new ArrayList<Occurrence>());
    }

    List<Occurrence> occurrences;
    if (steps == 0) {
      occurrences = convertSpansToOccurrences(textSpans);
    } else {
      occurrences = getGranularEntityOccurrences(steps, startOffset, endOffset);
    }

    // put occurrences into a response and send it
    return new OccurrencesResponse(occurrences);
  }

  private List<Chapter> getChaptersInRange(Chapter startChapter, Chapter endChapter) {
    Document document = documentDao.findById(documentId);
    boolean within = false;
    List<Chapter> chapters = new ArrayList<>();
    for (DocumentPart part : document.getContent().getParts()) {
      for (Chapter chapter : part.getChapters()) {
        if (chapter.equals(startChapter)) {
          within = true;
        }
        if (within) {
          chapters.add(chapter);
        }
        if (chapter.equals(endChapter)) {
          return chapters;
        }
      }
    }
    return chapters;
  }

  @Override
  protected long getNumberOfSpansInStep(int stepStart, int stepEnd) {
    int count = 0;
    for (TextSpan span : textSpans) {
      if (span.getEnd().getOffset() > stepEnd)
        break;
      if (span.getStart().getOffset() >= stepStart)
        count++;
    }
    return count;
  }

}
