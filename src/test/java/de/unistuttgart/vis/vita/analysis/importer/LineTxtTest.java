package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.*;

import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.importer.txt.util.LineType;
import de.unistuttgart.vis.vita.importer.txt.util.TxtModuleLine;

public class LineTxtTest {

  @Test
  public void testSetText() {
    String lineText = "A line's text.";
    Line testLine = new TxtModuleLine(lineText);
    assertEquals(lineText, testLine.getText());

    String anotherLineText = "  Another line text   ";
    testLine.setText(anotherLineText);
    assertEquals(anotherLineText, testLine.getText());
  }

  @Test
  public void testSetType() {
    String lineText = "A text.";
    Line testLine = new TxtModuleLine(lineText, true);
    testLine.setType(LineType.UNKNOWN);
    assertEquals(LineType.UNKNOWN, testLine.getType());
    assertEquals(lineText, testLine.getText());
  }

  @Test
  public void testSetAutomatedChapterComputation() {
    String lineText = "A line's text.";

    Line testLine = new TxtModuleLine(lineText, true);
    assertTrue(testLine.isAutomatedTypeComputation());

    testLine.setAutomatedTypeComputation(false);
    assertFalse(testLine.isAutomatedTypeComputation());

    testLine.setAutomatedTypeComputation(true);
    assertTrue(testLine.isAutomatedTypeComputation());

    testLine = new TxtModuleLine(lineText, false);
    assertFalse(testLine.isAutomatedTypeComputation());

    testLine.setAutomatedTypeComputation(true);
    assertTrue(testLine.isAutomatedTypeComputation());

    testLine.setAutomatedTypeComputation(false);
    assertFalse(testLine.isAutomatedTypeComputation());
  }

  @Test
  public void testUnknownType() {
    String lineText = "A line's text.";
    Line testLine = new TxtModuleLine(lineText, false);
    assertEquals(LineType.UNKNOWN, testLine.getType());
  }

  @Test
  public void testWhitelineTypeWithTabs() {
    String lineText = "\t\t";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.WHITELINE, testLine.getType());
  }

  @Test
  public void testWhitelineTypeWithEmpty() {
    String lineText = "";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.WHITELINE, testLine.getType());
  }

  @Test
  public void testWhitelineTypeWithSpaces() {
    String lineText = "   ";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.WHITELINE, testLine.getType());
  }

  @Test
  public void testWhitelineTypeWithTabsAndSpaces() {
    String lineText = " \t \t ";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.WHITELINE, testLine.getType());
  }

  @Test
  public void testDatadividerTypeSimpleForm() {
    String lineText = "*******";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());

    lineText = " ******* ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());
  }

  @Test
  public void testDatadividerTypeFilledSimpleForm() {
    String lineText = "*** START OF BOOK ****";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());

    lineText = " *** END OF BOOK **** ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.DATADIVIDER, testLine.getType());
  }

  @Test
  public void testDatadividerTypePartialForm() {
    String lineText = "*** START OF";
    Line testLine = new TxtModuleLine(lineText, true);
    assertFalse(LineType.DATADIVIDER.equals(testLine.getType()));

    lineText = " BOOK*** ";
    testLine = new TxtModuleLine(lineText, true);
    assertFalse(LineType.DATADIVIDER.equals(testLine.getType()));
  }

  @Test
  public void testSpecialSignsTypeTypicalForm() {
    String lineText = " ???? ";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SPECIALSIGNS, testLine.getType());

    lineText = " *\t*\t* ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SPECIALSIGNS, testLine.getType());
  }

  @Test
  public void testTextType() {
    String lineText = "...?\"";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TEXT, testLine.getType());

    lineText = "...";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TEXT, testLine.getType());
  }

  @Test
  public void testMarkedHeadingType() {
    String lineText = "#A Heading";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.MARKEDHEADING, testLine.getType());

    lineText = "#";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.MARKEDHEADING, testLine.getType());

    lineText = "\t# Heading ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.MARKEDHEADING, testLine.getType());
  }

  @Test
  public void testPrefaceTypeTypicalForm() {
    String lineText = "Preface";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());

    lineText = " PREFACE.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());

    lineText = "preface: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());
  }

  @Test
  public void testPrefaceTypeAdditionalForm() {
    String lineText = "To The Reader";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());

    lineText = " to \t the reader.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());

    lineText = "TO THE READER: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.PREFACE, testLine.getType());
  }

  @Test
  public void testTableOfContentsTypeTypicalForm() {
    String lineText = "Table of Contents";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = " TABLE\tOF CONTENTS.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = "table of contents: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());
  }

  @Test
  public void testTableOfContentsTypeContentsForm() {
    String lineText = "Contents";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = " CONTENTS.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = "contents: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());
  }

  @Test
  public void testTableOfContentsTypeIndexForm() {
    String lineText = "Index";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = " INDEX.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());

    lineText = "index: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.TABLEOFCONTENTS, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeTypcialForm() {
    String lineText = "MARIA'S WEDDING";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());


    lineText = " HOW, WITHOUT INCOMMDING HIMSELF, ATHOS PROCURES HIS EQUIPMENT";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " CHAPTER I.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "THE \"AGIASMO\": ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeRomanNumbers() {
    String lineText = "I";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " VI.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "XXI: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeArabianNumbers() {
    String lineText = "2";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " 12.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "24: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeNumbersAndText() {
    String lineText = "I MARIA'S WEDDING";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "1 THE THREE PRESENTS OF D’ARTAGNAN THE ELDER";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " VI. CHAPTER";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "13: AN UNSCRUPULOUS WOMAN: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeDoubleQuotationMark() {
    String lineText = "\"HAMILTON OF THE HOUSSAS\"";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " \"THE DUKE'S MYSTERY\" ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "\"13: THE \"AGIASMO\":\"";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeSingleQuotationMark() {
    String lineText = " \'MARIA'S WEDDING\' ";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "\'HAMILTON OF THE HOUSSAS\'";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " '13: THE \"AGIASMO\":'";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testBigHeadingTypeExtended() {
    String lineText = "_13: THE \"AGIASMO\":_";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = " _13: A GRAND RAILWAY \"PLANT\":_";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());

    lineText = "_13: THE \"AGIASMO\":_ ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.BIGHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeTypcialForm() {
    String lineText = "The Dry-Fly Fisherman";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " Chapter 1 ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "The Sounding of the Call: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeWithChapterExtension() {
    String lineText = "Chapter I. Into the Primitive";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " Chapter VI.";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "Chapter 12 : ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeWithNumberExtension() {
    String lineText = "I. Into the Primitive";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " 5. Chapter";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "12. Who Has Won to Mastership: ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeDoubleQuotationMark() {
    String lineText = "\"HAMILTON Of The Houssas\"";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " \"The Milkman Sets Out on his Travels\" ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "\"13: Application for Patents, Etc. Picture of U.S. Patent Office.:\"";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeSingleQuotationMark() {
    String lineText = " \'Maria's Wedding\' ";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "\'Our Little Grecian Cousin\'";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " '13. The \"Agiasmo\":'";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }

  @Test
  public void testSmallHeadingTypeExtended() {
    String lineText = "_13. The \"Agiasmo\":_";
    Line testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = " _The French Invasion--and after_  ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());

    lineText = "_13. Maria's Wedding:_ ";
    testLine = new TxtModuleLine(lineText, true);
    assertEquals(LineType.SMALLHEADING, testLine.getType());
  }
}
