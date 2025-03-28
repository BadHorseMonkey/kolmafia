package net.sourceforge.kolmafia.session;

import static internal.helpers.Networking.html;
import static internal.helpers.Player.*;
import static internal.matchers.Preference.isSetTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import internal.helpers.Cleanups;
import internal.helpers.RequestLoggerOutput;
import net.sourceforge.kolmafia.KoLAdventure;
import net.sourceforge.kolmafia.KoLCharacter;
import net.sourceforge.kolmafia.objectpool.AdventurePool;
import net.sourceforge.kolmafia.objectpool.ItemPool;
import net.sourceforge.kolmafia.persistence.AdventureDatabase;
import net.sourceforge.kolmafia.preferences.Preferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AutumnatonManagerTest {
  @BeforeEach
  public void beforeEach() {
    KoLCharacter.reset("AutumnatonManagerTest");
    Preferences.reset("AutumnatonManagerTest");
  }

  @ParameterizedTest
  @CsvSource({
    "all_upgrades, 'base_blackhat,cowcatcher,dualexhaust,leftarm1,leftleg1,periscope,radardish,rightarm1,rightleg1'",
    "many_upgrades, 'dualexhaust,leftleg1,periscope,radardish,rightarm1,rightleg1'",
    "with_hat, 'base_blackhat,cowcatcher,leftleg1,rightleg1'",
  })
  public void canDetectUpgrades(final String fixture, final String upgrades) {
    var cleanups =
        new Cleanups(
            withProperty("autumnatonUpgrades", ""),
            withChoice(1483, html("request/test_choice_autumnaton_" + fixture + ".html")));

    try (cleanups) {
      assertThat("autumnatonUpgrades", isSetTo(upgrades));
    }
  }

  @ParameterizedTest
  @CsvSource({
    "upgrade_right_leg, '', 'rightleg1'",
    "upgrade_multiple, '', 'cowcatcher,leftleg1,rightarm1'",
    "upgrade_multiple, 'leftarm1', 'cowcatcher,leftarm1,leftleg1,rightarm1'"
  })
  public void canDetectNewUpgrade(
      final String fixture, final String upgradesBefore, final String expectedUpgradesAfter) {
    var cleanups =
        new Cleanups(
            withProperty("autumnatonUpgrades", upgradesBefore),
            withChoice(1483, 1, html("request/test_choice_autumnaton_" + fixture + ".html")));

    try (cleanups) {
      assertThat("autumnatonUpgrades", isSetTo(expectedUpgradesAfter));
    }
  }

  @ParameterizedTest
  @CsvSource({
    "0, '', 11",
    "1, '', 11",
    "4, '', 44",
    "2, 'leftleg1', 11",
    "4, 'rightleg1', 33",
    "0, 'leftleg1,rightleg1', 11",
    "2, 'leftleg1,rightleg1', 11",
    "4, 'leftleg1,rightleg1', 22",
  })
  public void canDetectQuest(final int questsToday, final String upgrades, final int expected) {
    var cleanups =
        new Cleanups(
            withTurnsPlayed(0),
            withProperty("autumnatonUpgrades", upgrades),
            withProperty("_autumnatonQuests", questsToday),
            withItem(ItemPool.AUTUMNATON));

    try (cleanups) {
      AutumnatonManager.postChoice(
          2,
          html("request/test_choice_autumnaton_quest_kitchen.html"),
          AdventurePool.HAUNTED_KITCHEN);
      assertFalse(InventoryManager.hasItem(ItemPool.AUTUMNATON));
      assertThat("autumnatonQuestLocation", isSetTo("The Haunted Kitchen"));
      assertThat("autumnatonQuestTurn", isSetTo(expected));
    }
  }

  @Test
  public void installingUpgradeLogsUpgrade() {
    ChoiceManager.lastResponseText =
        html("request/test_choice_autumnaton_upgrade_available_one.html");
    RequestLoggerOutput.startStream();
    String URL = "choice.php?option=1&pwd&whichchoice=1483";
    assertTrue(ChoiceControl.registerRequest(URL));
    String text = RequestLoggerOutput.stopStream().trim();
    String expected = "Attach the dual exhaust that you found.";
    assertEquals(expected, text);
  }

  @Test
  public void sendingToLocationLogsAdventureName() {
    String adventureName = "Wartime Hippy Camp (Frat Disguise)";
    KoLAdventure adventure = AdventureDatabase.getAdventure(adventureName);
    int snarfblat = adventure.getSnarfblat();

    RequestLoggerOutput.startStream();
    String URL = "choice.php?option=2&pwd&whichchoice=1483&heythereprogrammer=" + snarfblat;
    assertTrue(ChoiceControl.registerRequest(URL));
    String text = RequestLoggerOutput.stopStream().trim();
    String expected = "Send your autumn-aton to " + adventureName;
    assertEquals(expected, text);
  }

  @Test
  public void canParseLocations() {
    var locs =
        AutumnatonManager.parseLocations(html("request/test_choice_autumnaton_all_upgrades.html"));
    assertThat(locs, hasItem(AdventureDatabase.getAdventure(258)));
    assertThat(locs, hasItem(AdventureDatabase.getAdventure(439)));
    assertThat(locs, not(hasItem(AdventureDatabase.getAdventure(11))));
  }
}
