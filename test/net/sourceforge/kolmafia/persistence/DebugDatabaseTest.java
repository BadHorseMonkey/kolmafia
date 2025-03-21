package net.sourceforge.kolmafia.persistence;

import static internal.helpers.Networking.html;
import static internal.helpers.Player.withNextResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import internal.helpers.Cleanups;
import internal.helpers.RequestLoggerOutput;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import net.sourceforge.kolmafia.KoLConstants;
import net.sourceforge.kolmafia.RequestLogger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

public class DebugDatabaseTest {

  private static final String LS = System.lineSeparator();

  @Test
  public void parseName() {
    String goodName = DebugDatabase.parseName("<b>goodName</b>");
    String badName = DebugDatabase.parseName("badName");
    assertEquals("goodName", goodName, "Could not parse name");
    assertEquals("", badName, "Name Returned " + badName);
  }

  @Nested
  class ParseAccess {
    @ParameterizedTest
    @ValueSource(strings = {"autumnaton"})
    public void canIdentifyQuestItems(final String itemName) {
      var access = DebugDatabase.parseAccess(html("request/test_desc_item_" + itemName + ".html"));
      assertThat(access, is("q"));
    }
  }

  @Test
  public void checkMuseumPlurals() {
    // The output isn't normally pretty-printed, but this is easier for a human to interpret.
    String fakeMuseumJson =
        """
        [
            {"itemid":-999,"name":"non-existent item"},
            {"itemid":1,"name":"misnamed seal-clubbing club"},
            {"itemid":2,"name":"seal tooth"},
            {"itemid":3,"name":"helmet turtle","plural":"bad plural for helmet turtle"},
            {"itemid":8,"name":"spices","plural":"spiceses"},
        ]
        """;
    File plurals = new File(KoLConstants.DATA_LOCATION, "plurals.txt");
    try (var cleanups =
        new Cleanups(withNextResponse(200, fakeMuseumJson), new Cleanups(plurals::delete))) {
      DebugDatabase.checkMuseumPlurals();
      assertTrue(plurals.exists());
      assertEquals(
          Files.readString(plurals.toPath()),
          """
          Unrecognised item -999: "non-existent item"
          item 1 has name "misnamed seal-clubbing club" but Mafia says "seal-clubbing club"
          Item 2: "seal tooth" has default plural, but Mafia says "seal teeth"
          Item 3: "helmet turtle" has plural unknown to Mafia: "bad plural for helmet turtle"
          Item 8: "spices" has plural "spiceses" but Mafia says "spices"
          """);
      DebugDatabase.checkMuseumPlurals();
    } catch (IOException e) {
      fail("unexpected exception: ", e);
    }
  }

  @Test
  public void checkMuseumItems() {
    // The output isn't normally pretty-printed, but this is easier for a human to interpret.
    String fakeMuseumJson =
        """
        [
            {"id":-999,"name":"non-existent item"},
            {"id":8884,"name":"plate of Val-U Brand Every Bean Salad","descid":880233878,"image":"beans","type":"food","itemclass":"salad","power":0,"multiple":false,"smith":false,"cook":false,"mix":false,"jewelry":false,"d":true,"t":true,"q":false,"g":false,"autosell":25,"plural":"plates of Val-U Brand Every Bean Salad"}
        ]
        """;
    File plurals = new File(KoLConstants.DATA_LOCATION, "museum_items.txt");
    try (var cleanups =
        new Cleanups(withNextResponse(200, fakeMuseumJson), new Cleanups(plurals::delete))) {
      DebugDatabase.checkMuseumItems();
      assertTrue(plurals.exists());
      assertEquals(
          Files.readString(plurals.toPath()),
          """
          Unrecognised item -999: "non-existent item"
          Mismatch - 8884:plate of Val-U Brand Every Bean Salad - image - Mafia: franksbeans.gif - Museum: beans.gif
          Mismatch - 8884:plate of Val-U Brand Every Bean Salad - salad - Mafia: false - Museum: true
          Mismatch - 8884:plate of Val-U Brand Every Bean Salad - beans - Mafia: true - Museum: false
          """);
      DebugDatabase.checkMuseumItems();
    } catch (IOException e) {
      fail("unexpected exception: ", e);
    }
  }

  @Test
  @Disabled("Accesses Coldfront which is returning malformed XML")
  public void checkPulverizationData() {
    String expectedOutput = "Checking pulverization data...\n";
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(ostream);
    // Inject custom output stream.
    RequestLogger.openCustom(out);

    DebugDatabase.checkPulverizationData();

    String output = ostream.toString();
    assertEquals(expectedOutput, output, "checkPulverizationData variances: \n" + output);
  }

  @Test
  @Disabled("Relies on external resources (wiki)")
  public void checkZapGroups() {
    String expectedOutput = "Checking zap groups...\n";
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(ostream);
    // Inject custom output stream.
    RequestLogger.openCustom(out);

    DebugDatabase.checkZapGroups();

    String output = ostream.toString();
    assertEquals(expectedOutput, output, "checkZapGroups variances: \n" + output);
    // deal with zapgroups
    File zfo = new File(KoLConstants.DATA_LOCATION, "zapreport.txt");
    if (zfo.exists()) {
      assertEquals(0, zfo.length(), "zapgroups.out expected to be empty.");
      zfo.delete();
    }
  }

  @Test
  public void checkManuel() {
    String expectedOutput =
        "Checking Monster Manuel...\n"
            + "Page A\n"
            + "Page B\n"
            + "Page C\n"
            + "Page D\n"
            + "Page E\n"
            + "Page F\n"
            + "Page G\n"
            + "Page H\n"
            + "Page I\n"
            + "Page J\n"
            + "Page K\n"
            + "Page L\n"
            + "Page M\n"
            + "Page N\n"
            + "Page O\n"
            + "Page P\n"
            + "Page Q\n"
            + "Page R\n"
            + "Page S\n"
            + "Page T\n"
            + "Page U\n"
            + "Page V\n"
            + "Page W\n"
            + "Page X\n"
            + "Page Y\n"
            + "Page Z\n"
            + "Page -\n";
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(ostream);
    // Inject custom output stream.
    RequestLogger.openCustom(out);

    DebugDatabase.checkManuel();

    String output = ostream.toString();
    assertEquals(expectedOutput, output, "checkManuel variances: \n" + output);
  }

  @Test
  @Disabled("Relies on external resources (wiki)")
  public void checkMeat() {
    String expectedOutput = "";
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(ostream);
    // Inject custom output stream.
    RequestLogger.openCustom(out);
    DebugDatabase.checkMeat();
    String output = ostream.toString();
    assertEquals(expectedOutput, output, "checkMeat variances: \n" + output);
  }

  @Test
  public void itShouldFindSVNDuplicatesSimple() {
    RequestLoggerOutput.startStream();
    File svnRoot = mockSimpleSystem();
    DebugDatabase.checkLocalSVNRepository(svnRoot);
    String expected = "Found 1 repo files." + LS;
    String output = RequestLoggerOutput.stopStream();
    assertEquals(expected, output, "Output off");
  }

  private File mockSimpleSystem() {
    File mockDot = mockFile(".svn");
    File mockDep = mockFile("dependencies.txt");
    File mockOne = mockFile("file.txt");
    File[] contents = {mockDep, mockDot, mockOne};
    return mockDir("Root", contents);
  }

  @Test
  public void itShouldFindSVNDuplicatesMoreComplex() {
    RequestLoggerOutput.startStream();
    File svnRoot = mockMoreComplexSystem();
    DebugDatabase.checkLocalSVNRepository(svnRoot);
    String expected = "Found 3 repo files." + LS;
    String output = RequestLoggerOutput.stopStream();
    assertEquals(expected, output, "Output off");
  }

  private File mockMoreComplexSystem() {
    File mockDot = mockFile(".svn");
    File mockDep = mockFile("dependencies.txt");
    File mockOne = mockFile("file.txt");
    File a = mockFile("meatfarm.ash");
    File b = mockFile("farmmeat.ash");
    File[] moreContents = {a, b};
    File mockDir = mockDir("scripts", moreContents);
    File[] contents = {mockDep, mockDot, mockOne, mockDir};
    return mockDir("root", contents);
  }

  private File mockFile(String name) {
    File retVal = Mockito.mock(File.class);
    Mockito.when(retVal.getName()).thenReturn(name);
    Mockito.when(retVal.isDirectory()).thenReturn(false);
    Mockito.when(retVal.toString()).thenReturn(name);
    return retVal;
  }

  @Test
  public void itShouldFindSVNDuplicatesWhenThereAreSome() {
    RequestLoggerOutput.startStream();
    File svnRoot = mockDupes();
    DebugDatabase.checkLocalSVNRepository(svnRoot);
    String expected =
        "Found 2 repo files." + LS + "***" + LS + "test.ash" + LS + "test.ash" + LS + "***" + LS;
    String output = RequestLoggerOutput.stopStream();
    assertEquals(expected, output, "Output off");
  }

  private File mockDupes() {
    File a = mockFile("test.ash");
    File b = mockFile("test.ash");
    File[] x = {a};
    File relay = mockDir("relay", x);
    File[] y = {b};
    File scripts = mockDir("scripts", y);
    File[] z = {relay};
    File one = mockDir("cheeks", z);
    File[] xx = {scripts};
    File two = mockDir("bail", xx);
    File[] yy = {one, two, mockFile(".svn"), mockFile("dependencies.txt")};
    return mockDir("root", yy);
  }

  private File mockDir(String dirname, File[] contents) {
    File retVal = mockFile(dirname);
    Mockito.when(retVal.isDirectory()).thenReturn(true);
    Mockito.when(retVal.listFiles()).thenReturn(contents);
    return retVal;
  }
}
