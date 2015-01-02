package de.unistuttgart.vis.vita.services.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.responses.DocumentIdResponse;
import de.unistuttgart.vis.vita.services.responses.DocumentsResponse;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * A service offering a list of documents and the possibility to add new Documents.
 */
@Path("/documents")
@ManagedBean
public class DocumentsService {

  private static final String DOCUMENT_PATH = System.getProperty("user.home") + File.separator
                                              + ".vita" + File.separator + "docs" + File.separator;

  @Inject
  private EntityManager em;

  @Inject
  private AnalysisController analysisController;

  @Inject
  private DocumentService documentService;

  /**
   * Returns a DocumentsResponse including a list of Documents with a given maximum length,
   * starting at an also given offset.
   *
   * @param offset - the first Document to be returned
   * @param count - the maximum amount of Documents to be returned
   * @return DocumentResponse including a list of Documents
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public DocumentsResponse getDocuments(@QueryParam("offset") int offset,
                                        @QueryParam("count") int count) {
    TypedQuery<Document> query = em.createNamedQuery("Document.findAllDocuments", Document.class);
    query.setFirstResult(offset);
    query.setMaxResults(count);

    return new DocumentsResponse(query.getResultList());
  }

  /**
   * Adds a new Document.
   *
   * @return a NewDocumentResponse including the id of the new Document
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public DocumentIdResponse addDocument(@FormDataParam("file") InputStream fileInputStream,
      @FormDataParam("file") FormDataContentDisposition fDispo,
      @FormDataParam("parameters") AnalysisParameters parameters) {
    if (parameters == null) {
      parameters = new AnalysisParameters();
    }

    Set<ConstraintViolation<AnalysisParameters>> violations =
            Validation.buildDefaultValidatorFactory().getValidator().validate(parameters);
    if (!violations.isEmpty()) {
      // TODO this does not work, it produces a generic Bad Request
      throw new ValidationViolationException(violations);
    }

    DocumentIdResponse response = null;

    String fileName = fDispo.getFileName();
    String baseName = FilenameUtils.getBaseName(fileName);
    String fileExtension = FilenameUtils.getExtension(fileName);
    String uuid = UUID.randomUUID().toString();

    // set up path
    String filePath = DOCUMENT_PATH + baseName + "_" + uuid + "." + fileExtension;

    // check path and save file
    if (!checkAndCreateDir(DOCUMENT_PATH)) {
      throw new WebApplicationException("Can not save document!");
    } else {
      // save file on server
      saveFile(fileInputStream, filePath);

      // schedule analysis
      String id = analysisController.scheduleDocumentAnalysis(new File(filePath).toPath(), baseName, parameters);

      // set up Response
      response = new DocumentIdResponse(id);
    }

    return response;
  }

  /**
   * Checks whether given path is a directory. Tries to create directory otherwise.
   *
   * @param path - the path to be checked
   * @return true if path is a directory or directory could be created there, false otherwise
   */
  private boolean checkAndCreateDir(String path) {
    File fileDir = new File(path);

    if (!fileDir.isDirectory()) {
      // try to create directory
      return fileDir.mkdirs();
    } else {
      return true;
    }
  }

  /**
   * Saves file being uploaded at the given path.
   *
   * @param uploadedInputStream - the input stream of the file being uploaded
   * @param filePath - the path where to save this file on the server
   */
  private void saveFile(InputStream uploadedInputStream, String filePath) {
    int read = 0;
    byte[] bytes = new byte[1024];

    try (OutputStream os = new FileOutputStream(new File(filePath))) {
      while ((read = uploadedInputStream.read(bytes)) != -1) {
        os.write(bytes, 0, read);
      }
      os.flush();
    } catch (IOException e) {
      throw new WebApplicationException("IO-Error occured writing file.", e);
    }
  }

  /**
   * Returns the Service to access the Document with the given id.
   *
   * @param id - the id of the Document to be accessed
   * @return the DocumentService to access the given Document with the given id
   */
  @Path("{documentId}")
  public DocumentService  getDocument(@PathParam("documentId") String id) {
    return documentService.setId(id);
  }

}
