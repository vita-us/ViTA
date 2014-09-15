package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.Line;
import de.unistuttgart.vis.vita.importer.txt.LineType;

public class LineTxtTest {

  @Test
  public void testSetText() {
    String lineText = "A line's text.";
    Line testLine = new Line(lineText);
    assertEquals(lineText, testLine.getText());

    String anotherLineText = "  Another line text   ";
    testLine.setText(anotherLineText);
    assertEquals(anotherLineText, testLine.getText());
  }

  @Test
  public void testSetType() {
    String lineText = "A text.";
    Line testLine = new Line(lineText, true);
    testLine.setType(LineType.UNKNOWN);
    assertEquals(LineType.UNKNOWN, testLine.getType());
    assertEquals(lineText, testLine.getText());
  }

  @Test
  public void testSetAutomatedChapterComputation() {
    String lineText = "A line's text.";

    Line testLine = new Line(lineText, true);
    assertTrue(testLine.isAutomatedTypeComputation());

    testLine.setAutomatedTypeComputation(false);
    assertFalse(testLine.isAutomatedTypeComputation());

    testLine.setAutomatedTypeComputation(true);
    assertTrue(testLine.isAutomatedTypeComputation());

    testLine = new Line(lineText, false);
    assertFalse(testLine.isAutomatedTypeComputation());

    testLine.setAutomatedTypeComputation(true);
    assertTrue(testLine.isAutomatedTypeComputation());

    testLine.setAutomatedTypeComputation(false);
    assertFalse(testLine.isAutomatedTypeComputation());
  }

  @Test
  public void testUnknownType() {
    String lineText = "A line's text.";
    Line testLine = new Line(lineText, false);
    assertEquals(LineType.UNKNOWN, testLine.getType());
  }

  @Test
  public void testWhitelineTypeWithTabs() {
    String lineText = "\t\t";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.WHITELINE, testLine.getType());
  }

  @Test
  public void testWhitelineTypeWithEmpty() {
    String lineText = "";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.WHITELINE, testLine.getType());
  }

  @Test
  public void testWhitelineTypeWithSpaces() {
    String lineText = "   ";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.WHITELINE, testLine.getType());
  }

  @Test
  public void testWhitelineTypeWithTabsAndSpaces() {
    String lineText = " \t \t ";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.WHITELINE, testLine.getType());
  }

  @Test
  public void testDatadividerTypeSimpleForm() {
    String lineText = "*******";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());

    lineText = " ******* ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());
  }

  @Test
  public void testDatadividerTypeFilledSimpleForm() {
    String lineText = "*** START OF BOOK ****";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());

    lineText = " *** END OF BOOK **** ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());
  }

  @Test
  public void testDatadividerTypePartialForm() {
    String lineText = "*** START OF";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());

    lineText = " BOOK*** ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());
  }

  @Test
  public void testSpecialSignsTypeTypicalForm() {
    String lineText = " ???? ";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.SPECIALSIGNS, testLine.getType());

    lineText = " *\t*\t* ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SPECIALSIGNS, testLine.getType());
  }

  @Test
  public void testTextType() {
    String lineText = "...?\"";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.TEXT, testLine.getType());

    lineText = "...";
    testLine = new Line(lineText, true);
    assertEquals(LineType.TEXT, testLine.getType());
  }

  @Test
  public void testMarkedHeadingType() {
    String lineText = "#A Heading";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.MARKEDHEADING, testLine.getType());

    lineText = "#";
    testLine = new Line(lineText, true);
    assertEquals(LineType.MARKEDHEADING, testLine.getType());

    lineText = "\t# Heading ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.MARKEDHEADING, testLine.getType());
  }

  @Test
  public void testPrefaceTypeTypicalForm() {
    String lineText = "Preface";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());

    lineText = " PREFACE.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());

    lineText = "preface: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());
  }

  @Test
  public void testPrefaceTypeAdditionalForm() {
    String lineText = "To The Reader";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());

    lineText = " to \t the reader.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());

    lineText = "TO THE READER: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());
  }

  @Test
  public void testTableOfContentsTypeTypicalForm() {
    String lineText = "Table of Contents";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = " TABLE\tOF CONTENTS.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = "table of contents: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());
  }

  @Test
  public void testTableOfContentsTypeContentsForm() {
    String lineText = "Contents";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = " CONTENTS.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = "contents: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());
  }

  @Test
  public void testTableOfContentsTypeIndexForm() {
    String lineText = "Index";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = " INDEX.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = "index: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeTypcialForm() {
    String lineText = "MARIA'S WEDDING";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());


    lineText = " HOW, WITHOUT INCOMMDING HIMSELF, ATHOS PROCURES HIS EQUIPMENT";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " CHAPTER I.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "THE \"AGIASMO\": ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeRomanNumbers() {
    String lineText = "I";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " VI.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "XXI: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeArabianNumbers() {
    String lineText = "2";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " 12.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "24: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeNumbersAndText() {
    String lineText = "I MARIA'S WEDDING";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "1 THE THREE PRESENTS OF D’ARTAGNAN THE ELDER";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " VI. CHAPTER";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "13: AN UNSCRUPULOUS WOMAN: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeDoubleQuotationMark() {
    String lineText = "\"HAMILTON OF THE HOUSSAS\"";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " \"THE DUKE'S MYSTERY\" ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "\"13: THE \"AGIASMO\":\"";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeSingleQuotationMark() {
    String lineText = " \'MARIA'S WEDDING\' ";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "\'HAMILTON OF THE HOUSSAS\'";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " '13: THE \"AGIASMO\":'";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeExtended() {
    String lineText = "_13: THE \"AGIASMO\":_";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " _13: A GRAND RAILWAY \"PLANT\":_";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "_13: THE \"AGIASMO\":_ ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeTypcialForm() {
    String lineText = "The Dry-Fly Fisherman";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " Chapter 1 ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "The Sounding of the Call: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeWithChapterExtension() {
    String lineText = "Chapter I. Into the Primitive";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " Chapter VI.";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "Chapter 12 : ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeWithNumberExtension() {
    String lineText = "I. Into the Primitive";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " 5. Chapter";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "12. Who Has Won to Mastership: ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeDoubleQuotationMark() {
    String lineText = "\"HAMILTON Of The Houssas\"";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " \"The Milkman Sets Out on his Travels\" ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "\"13: Application for Patents, Etc. Picture of U.S. Patent Office.:\"";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeSingleQuotationMark() {
    String lineText = " \'Maria's Wedding\' ";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "\'Our Little Grecian Cousin\'";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " '13. The \"Agiasmo\":'";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeExtended() {
    String lineText = "_13. The \"Agiasmo\":_";
    Line testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " _The French Invasion--and after_  ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "_13. Maria's Wedding:_ ";
    testLine = new Line(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }



}
