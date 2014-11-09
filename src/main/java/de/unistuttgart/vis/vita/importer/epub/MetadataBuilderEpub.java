package de.unistuttgart.vis.vita.importer.epub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

public class MetadataBuilderEpub extends AbstractMetadataBuilderEpub {

  private Calendar date = null;
  
  /**
   * Concatenates the multilines regarding the metadata and returns the complete metadata
   */
  private String buildMetadataMultiline(List<Line> newMetadataMultilineList) {
    String metadataLine = "";
    StringBuilder stringBuilder = new StringBuilder(metadataLine);

    for (Line metadataMultiLine : newMetadataMultilineList) {
      stringBuilder.append(" ");
      stringBuilder.append(metadataMultiLine.getText().trim());
    }
    metadataLine = stringBuilder.toString();
    metadataLine = metadataLine.trim();
    return metadataLine;
  }
  
  /**
   * Checks if the date format of the publisherYear is valid
   */
  private void setPublishYear(String newPublishYear) {

    List<SimpleDateFormat> dateFormats = new ArrayList<>();
    dateFormats.add(new SimpleDateFormat("yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH));


    date = Calendar.getInstance();
    for (SimpleDateFormat dateFormat : dateFormats) {
      try {
        
        date.setTime(dateFormat.parse(newPublishYear));
        documentMetadata.setPublishYear(date.get(Calendar.YEAR));

      } catch (ParseException ex) {
        // do nothing
      }
    }
  }

  @Override
  public DocumentMetadata getMetadata() {
    return documentMetadata;
  }

  @Override
  public void setTitle(List<Line> titleList) {
    String metadataTitle = "";
    if (titleList != null) {
      metadataTitle = buildMetadataMultiline(titleList);
    }
    documentMetadata.setTitle(metadataTitle);
    
  }

  @Override
  public void setAuthor(List<Line> authorList) {
    String metadataAuthor = "";
    if (authorList != null) {
      metadataAuthor = buildMetadataMultiline(authorList);
    }
    documentMetadata.setAuthor(metadataAuthor);
    
  }

  @Override
  public void setPublishYear(List<Line> publishYearList) {
    String metadataPublishYear = "";
    if (publishYearList != null) {
      metadataPublishYear = buildMetadataMultiline(publishYearList);
      
    }
    setPublishYear(metadataPublishYear);
    
  }

  @Override
  public void setPublisher(List<Line> publisherList) {
    String metadataPublisher = "";
    if (publisherList != null) {
      metadataPublisher = buildMetadataMultiline(publisherList);
    }
    documentMetadata.setPublisher(metadataPublisher);
    
  }

  @Override
  public void setGenre(List<Line> genreList) {
    String metadataGenre = "";
    if (genreList != null) {
      metadataGenre = buildMetadataMultiline(genreList);
    }
    documentMetadata.setGenre(metadataGenre);
    
  }

  @Override
  public void setEdition(List<Line> editionList) {
    String metadataEdition = "";
    if (editionList != null) {
      metadataEdition = buildMetadataMultiline(editionList);
    }
    documentMetadata.setEdition(metadataEdition);
    
  }

}
