package de.unistuttgart.vis.vita.services.responses;

/**
 * Represents a list response with total count.
 */
public abstract class AbstractListResponse {

  protected int totalCount;
  
  /**
   * Creates a list response.
   */
  public AbstractListResponse() {
    
  }
  
  /**
   * Creates a list response with a total count.
   * 
   * @param pTotalCount - the total count of the list elements
   */
  public AbstractListResponse(int pTotalCount) {
    this.totalCount = pTotalCount;
  }

  /**
   * @return the total amount of documents
   */
  public int getTotalCount() {
    return totalCount;
  }

  /**
   * Sets the total count of Documents for this response.
   * 
   * @param totalCount - the number of documents
   */
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }

}
