package net.sourceforge.kolmafia;

import static net.sourceforge.kolmafia.KoLConstants.ConsumptionType.FAMILIAR_EQUIPMENT;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import net.java.dev.spellcast.utilities.UtilityConstants;
import net.sourceforge.kolmafia.chat.StyledChatBuffer;
import net.sourceforge.kolmafia.request.UseSkillRequest;
import net.sourceforge.kolmafia.session.EncounterManager.RegisteredEncounter;
import net.sourceforge.kolmafia.swingui.menu.PartialMRUList;
import net.sourceforge.kolmafia.swingui.menu.ScriptMRUList;
import net.sourceforge.kolmafia.utilities.LockableListFactory;

public interface KoLConstants extends UtilityConstants {
  // General constants used for calculations and formatting of
  // strings, as well as for string parsing.

  Random RNG = new Random();
  Pattern LINE_BREAK_PATTERN = Pattern.compile("\\s*[\r\n]+\\s*");

  Pattern ANYTAG_PATTERN = Pattern.compile("<.*?>");
  Pattern ANYTAG_BUT_ITALIC_PATTERN = Pattern.compile("<(?!i>)(?!/i>).*?>");
  Pattern SCRIPT_PATTERN = Pattern.compile("<script.*?</script>", Pattern.DOTALL);
  Pattern STYLE_PATTERN = Pattern.compile("<style.*?</style>", Pattern.DOTALL);
  Pattern COMMENT_PATTERN = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
  Pattern HEAD_PATTERN = Pattern.compile("<head>(.*?)</head>");
  Pattern BODY_PATTERN = Pattern.compile("<body>(.*?)</body>");

  DecimalFormat COMMA_FORMAT =
      new DecimalFormat("#,##0;-#,##0", new DecimalFormatSymbols(Locale.US));
  DecimalFormat MODIFIER_FORMAT = new DecimalFormat("+#0;-#0", new DecimalFormatSymbols(Locale.US));
  DecimalFormat SINGLE_PRECISION_FORMAT =
      new DecimalFormat("+#,##0.0;-#,##0.0", new DecimalFormatSymbols(Locale.US));
  DecimalFormat FLOAT_FORMAT =
      new DecimalFormat("#,##0.00;-#,##0.00", new DecimalFormatSymbols(Locale.US));
  // About 45 places needed to show the smallest denorm float.
  DecimalFormat NONSCIENTIFIC_FORMAT =
      new DecimalFormat(
          "#0.0############################################", new DecimalFormatSymbols(Locale.US));
  DecimalFormat ROUNDED_MODIFIER_FORMAT =
      new DecimalFormat("+#0.00;-#0.00", new DecimalFormatSymbols(Locale.US));
  DecimalFormat CHAT_LASTSEEN_FORMAT = new DecimalFormat("0000000000");
  DecimalFormat HUMAN_READABLE_FORMAT = new DecimalFormat("#.##");

  SimpleDateFormat DAILY_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.US);
  DateTimeFormatter DAILY_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.US);
  SimpleDateFormat WEEKLY_FORMAT = new SimpleDateFormat("yyyyMM_'w'W", Locale.US);
  SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss z", Locale.US);

  // Generic constants which indicate null values.  Used in
  // order to preserve memory.

  Class<?>[] NOPARAMS = new Class<?>[0];

  // Menus rendered in the relay browser and the KoLmafia mini-browser.
  // Ensures that the two menus always contain the same information.

  // Additional navigation destinations for convenience

  String[][] GOTO_MENU =
      new String[][] {
        {"Council", "council.php"},
        {"Guild Hall", "guild.php"},
        {"Spookyraven", "place.php?whichplace=manor1"},
        {"Cobb's Knob", "cobbsknob.php"},
        {"Defiled Cyrpt", "crypt.php"},
        {"Mt. McLargeHuge", "place.php?whichplace=mclargehuge"},
        {"The Beanstalk", "place.php?whichplace=beanstalk"},
        {"Elemental International Airport", "place.php?whichplace=airport"},
        {"The Sea", "place.php?whichplace=thesea"},
      };

  String[][] FRAME_NAMES = {
    {"Adventure", "AdventureFrame"},
    {"Mini-Browser", "RequestFrame"},
    {"Relay Server", "LocalRelayServer"},
    {"Purchases", "MallSearchFrame"},
    {"Graphical CLI", "CommandDisplayFrame"},
    {"Daily Deeds", "CharSheetFrame"},
    {"Item Manager", "ItemManageFrame"},
    {"Gear Changer", "GearChangeFrame"},
    {"Store Manager", "StoreManageFrame"},
    {"Coin Masters", "CoinmastersFrame"},
    {"Museum Display", "MuseumFrame"},
    {"Meat Manager", "MeatManageFrame"},
    {"Skill Casting", "SkillBuffFrame"},
    {"Contact List", "ContactListFrame"},
    {"Buffbot Manager", "BuffBotFrame"},
    {"Purchase Buffs", "BuffRequestFrame"},
    {"Request a Fax", "FaxRequestFrame"},
    {"Mushroom Plot", "MushroomFrame"},
    {"Familiar Trainer", "FamiliarTrainingFrame"},
    {"Loathing Chat", "ChatManager"},
    {"Clan Management", "ClanManageFrame"},
    {"Farmer's Almanac", "CalendarFrame"},
    {"Encyclopedia", "DatabaseFrame"},
    {"Preferences", "OptionsFrame"},
    {"Sweet Synthesis", "SynthesizeFrame"},
    {"Modifier Maximizer", "MaximizerFrame"}
  };

  // Scripting-related constants.  Used throughout KoLmafia in
  // order to ensure proper handling of scripts.

  List<File> scripts = Collections.synchronizedList(new ArrayList<>());
  List<String> bookmarks = LockableListFactory.getInstance(String.class);

  ArrayList<String> disabledScripts = new ArrayList<>();
  ScriptMRUList scriptMRUList = new ScriptMRUList("scriptMRUList", "scriptMRULength");
  PartialMRUList maximizerMList =
      new PartialMRUList("maximizerMRUList", "maximizerMRUSize", "maximizerList");

  List<AdventureResult> junkList = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> singletonList =
      LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> mementoList = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> profitableList =
      LockableListFactory.getSortedInstance(AdventureResult.class);

  String BUFFBOT_DIRECTORY = "buffs/";
  String CCS_DIRECTORY = "ccs/";
  String CHATLOG_DIRECTORY = "chats/";
  String PLOTS_DIRECTORY = "planting/";
  String RELAY_DIRECTORY = "relay/";
  String SCRIPT_DIRECTORY = "scripts/";
  String SESSIONS_DIRECTORY = "sessions/";
  String SVN_DIRECTORY = "svn/";
  String GIT_DIRECTORY = "git/";

  File BUFFBOT_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.BUFFBOT_DIRECTORY);
  File CCS_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.CCS_DIRECTORY);
  File CHATLOG_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.CHATLOG_DIRECTORY);
  File PLOTS_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.PLOTS_DIRECTORY);
  File SCRIPT_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.SCRIPT_DIRECTORY);
  File SESSIONS_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.SESSIONS_DIRECTORY);
  File RELAY_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.RELAY_DIRECTORY);
  File SVN_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.SVN_DIRECTORY);
  File SVN_REPO_FILE = new File(KoLConstants.DATA_LOCATION, "svnrepo.json");
  File GIT_LOCATION = new File(KoLConstants.ROOT_LOCATION, KoLConstants.GIT_DIRECTORY);

  // All data files that can be overridden

  String[] OVERRIDE_DATA = {
    "adventures.txt",
    "buffbots.txt",
    "classskills.txt",
    "coinmasters.txt",
    "combats.txt",
    "concoctions.txt",
    "consequences.txt",
    "defaults.txt",
    "equipment.txt",
    "familiars.txt",
    "faxbots.txt",
    "foldgroups.txt",
    "fullness.txt",
    "inebriety.txt",
    "items.txt",
    "modifiers.txt",
    "monsters.txt",
    "nonfilling.txt",
    "npcstores.txt",
    "outfits.txt",
    "packages.txt",
    "pulverize.txt",
    "spleenhit.txt",
    "statuseffects.txt",
    "zapgroups.txt",
    "zonelist.txt"
  };

  // The current version number of each data file

  int ADVENTURES_VERSION = 6;
  int BOOKOFFACTS_VERSION = 1;
  int BOUNTY_VERSION = 2;
  int BUFFBOTS_VERSION = 1;
  int CAFE_BOOZE_VERSION = 1;
  int CAFE_FOOD_VERSION = 1;
  int CLASSSKILLS_VERSION = 6;
  int COINMASTERS_VERSION = 3;
  int COMBATS_VERSION = 1;
  int CONCOCTIONS_VERSION = 3;
  int CONSEQUENCES_VERSION = 1;
  int CULTSHORTS_VERSION = 1;
  int DAILYLIMITS_VERSION = 1;
  int DEFAULTS_VERSION = 2;
  int ENCOUNTERS_VERSION = 1;
  int EQUIPMENT_VERSION = 2;
  int FAMBATTLE_VERSION = 1;
  int FAMILIARS_VERSION = 4;
  int FAXBOTS_VERSION = 1;
  int FOLDGROUPS_VERSION = 1;
  int FULLNESS_VERSION = 2;
  int INEBRIETY_VERSION = 2;
  int ITEMS_VERSION = 1;
  int MODIFIERS_VERSION = 3;
  int MONSTERS_VERSION = 8;
  int MONSTER_PARTS_VERSION = 1;
  int NONFILLING_VERSION = 1;
  int NPCSTORES_VERSION = 2;
  int OUTFITS_VERSION = 3;
  int PACKAGES_VERSION = 1;
  int PULVERIZE_VERSION = 2;
  int RESTORES_VERSION = 2;
  int QUESTSCOUNCIL_VERSION = 1;
  int QUESTSLOG_VERSION = 1;
  int SHOPS_VERSION = 2;
  int SHOPROWS_VERSION = 2;
  int SPLEENHIT_VERSION = 3;
  int STANDARD_REWARDS_VERSION = 1;
  int STANDARD_PULVERIZED_VERSION = 1;
  int STATUSEFFECTS_VERSION = 4;
  int WEREPROFESSOR_VERSION = 1;
  int WITCHESS_SOLUTIONS_VERSION = 1;
  int ZAPGROUPS_VERSION = 1;
  int ZONELIST_VERSION = 2;

  // mallprices.txt can be updated by typing a filename, so give
  // it a distinct version number in case the user types the name
  // of another one of the data files.
  int MALLPRICES_VERSION = 0xF00D5;

  // The current versioned name of each KoLmafia-supplied relay file
  String AFTERLIFE_ASH = "afterlife.1.ash";
  String BARREL_SOUNDS_JS = "barrel_sounds.js";
  String BASEMENT_JS = "basement.js";
  String BASICS_CSS = "basics.1.css";
  String BASICS_JS = "basics.js";
  String CHAT_HTML = "chat.html";
  String CLI_HTML = "cli.html";
  String COMBATFILTER_JS = "combatfilter.1.js";
  String HOTKEYS_JS = "hotkeys.js";
  String IRCM_JS = "ircm_extend.3.js";
  String MACROHELPER_JS = "macrohelper.6.js";
  String ONFOCUS_JS = "onfocus.1.js";
  String SORTTABLE_JS = "sorttable.2.js";
  String STATIONARYBUTTONS_CSS = "stationarybuttons.2.css";
  String STATIONARYBUTTONS_JS = "stationarybuttons.2.js";
  String VOLCANOMAZE_JS = "volcanomaze.5.js";

  String[] RELAY_FILES = {
    AFTERLIFE_ASH,
    BARREL_SOUNDS_JS,
    BASEMENT_JS,
    BASICS_CSS,
    BASICS_JS,
    CHAT_HTML,
    CLI_HTML,
    COMBATFILTER_JS,
    HOTKEYS_JS,
    IRCM_JS,
    MACROHELPER_JS,
    ONFOCUS_JS,
    SORTTABLE_JS,
    STATIONARYBUTTONS_CSS,
    STATIONARYBUTTONS_JS,
    VOLCANOMAZE_JS,
  };

  /**
   * Returns true if a given consumption type relates to equipment
   *
   * @param type Consumption type constant
   * @return True if the type relates to equipment
   */
  static boolean isEquipmentType(
      final ConsumptionType type, final boolean includeFamiliarEquipment) {
    return switch (type) {
      case ACCESSORY, CONTAINER, HAT, SHIRT, PANTS, WEAPON, OFFHAND -> true;
      default -> includeFamiliarEquipment && type == FAMILIAR_EQUIPMENT;
    };
  }

  // Different states of KoLmafia.  Used in order to determine
  // what is still permitted.
  enum MafiaState {
    ENABLE,
    ERROR,
    ABORT,
    PENDING,
    CONTINUE
  }

  enum Stat {
    MUSCLE("Muscle"),
    MYSTICALITY("Mysticality"),
    MOXIE("Moxie"),
    SUBMUSCLE("SubMuscle"),
    SUBMYST("SubMysticality"),
    SUBMOXIE("SubMoxie"),
    NONE("None");

    public static Stat find(final String name) {
      return Arrays.stream(values())
          .filter(s -> s.toString().equalsIgnoreCase(name))
          .findFirst()
          .orElse(Stat.NONE);
    }

    private final String name;

    Stat(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  enum ZodiacType {
    NONE,
    MUSCLE,
    MYSTICALITY,
    MOXIE,
    BAD_MOON
  }

  enum ZodiacZone {
    KNOLL,
    CANADIA,
    GNOMADS,
    NONE
  }

  enum WeaponType {
    MELEE,
    RANGED,
    NONE
  }

  // Mystical Book Types
  enum BookType {
    TOME,
    GRIMOIRE,
    LIBRAM
  }

  // for maximizer these are things we can filer on...
  enum filterType {
    EQUIP,
    CAST,
    USABLE,
    BOOZE,
    FOOD,
    SPLEEN,
    OTHER
  }

  /**
   * Ways to use items.
   *
   * <p>A conflation of a few concepts. Most are "primary uses" as appear in items.txt
   *
   * <p>Primary uses determine how you can interact with an item. Some items are "usable" in
   * addition to their primary use (for example, the fortune cookie or glitch season reward).
   *
   * <p>ConsumptionType is also used for how the user /intends/ to interact with an item. This is
   * what the "Familiar 'uses'" are used for: when the user uses a command specifically for feeding
   * a familiar.
   *
   * <p>Most of this is handled in UseItemRequest.
   */
  enum ConsumptionType {
    UNKNOWN("unknown"),
    // Cannot be "used" in any way by itself
    NONE("none"),

    // Consumables
    EAT("food"),
    DRINK("drink"),
    SPLEEN("spleen"),

    // Usables
    USE("usable"),
    USE_MULTIPLE("multiple"),
    USE_INFINITE("reusable"),
    USE_MESSAGE_DISPLAY("message"),

    // Familiar hatchlings
    FAMILIAR_HATCHLING("grow"),

    // Equipment
    HAT("hat"),
    WEAPON("weapon"),
    OFFHAND("offhand"),
    CONTAINER("container"),
    SHIRT("shirt"),
    PANTS("pants"),
    ACCESSORY("accessory"),
    FAMILIAR_EQUIPMENT("familiar"),

    // Customizable "equipment"
    STICKER("sticker"),
    CARD("card"),
    FOLDER("folder"),
    BOOTSKIN("bootskin"),
    BOOTSPUR("bootspur"),
    SIXGUN("sixgun"),

    // Special "uses"
    FOOD_HELPER("food helper"),
    DRINK_HELPER("drink helper"),
    ZAP("zap"),
    EL_VIBRATO_SPHERE("sphere"),
    PASTA_GUARDIAN("guardian"),
    POKEPILL("pokepill"),

    // Potions
    POTION("potion"),
    AVATAR_POTION("avatar"),

    // Familiar "uses"
    ROBORTENDER("robortender"),
    STOCKING_MIMIC("stocking mimic"),
    SLIMELING("slimeling"),
    SPIRIT_HOBO("spirit hobo"),
    GLUTTONOUS_GHOST("gluttonous ghost");

    public final String description;
    private static final Map<String, ConsumptionType> consumptionTypeByDescription =
        new HashMap<>();

    ConsumptionType(String description) {
      this.description = description;
    }

    public static ConsumptionType byDescription(String description) {
      var lookup = consumptionTypeByDescription.get(description);
      if (lookup != null) return lookup;
      var search =
          Arrays.stream(ConsumptionType.values())
              .filter(x -> x.description.equals(description))
              .findAny();
      search.ifPresent(x -> consumptionTypeByDescription.put(description, x));
      return search.orElse(null);
    }
  }

  enum CraftingType {
    SUBCLASS, // ???
    NOCREATE,
    COMBINE, // Items anybody can create using meat paste or The Plunger
    COOK, // Items created with an E-Z Cook Oven or Dramatic Range
    MIX, // Items created with a Shaker or Cocktailcrafting Kit
    SMITH, // Items anybody can create with a tenderizing hammer or via Innabox
    SSMITH, // Items that can only be created with a tenderizing hammer, not via Innabox
    STILL, // Items requiring access to Nash Crosby's Still
    MALUS, // Items requiring access to the Malus of Forethought
    JEWELRY, // Items created with jewelry-making pliers
    STARCHART, // Items anybody can create with starcharts, stars, and lines
    PIXEL, // Items anybody can create with pixels
    ROLLING_PIN, // Items created with a rolling pin or and an unrolling pin
    GNOME_TINKER, // Items requiring access to the Gnome supertinker
    STAFF, // Items requiring access to Roderic the Staffcrafter
    SUSHI, // Items anybody can create with a sushi-rolling mat
    // Items created by single (or multi) using a single item.
    // Extra ingredients might also be consumed.
    // Multi-using more than one of the item creates multiple results.
    SINGLE_USE,
    // Items created by multi-using specific numbers of a single item.
    // Extra ingredients might also be consumed.
    // You must create multiple result items one at a time.
    MULTI_USE,
    SEWER, // Items fished out of the sewer using chewing gum on a string
    CRIMBO05, // Items formerly creatable in Crimbo Town during Crimbo 2005
    CRIMBO06, // Items formerly creatable in Crimbo Town during Crimbo 2006
    CRIMBO07, // Items formerly creatable in Crimbo Town during Crimbo 2007
    CRIMBO12, // Items creatable in Crimbo Town during Crimbo 2012
    CRIMBO16, // Items creatable in Crimbo Town during Crimbo 2016
    SUGAR_FOLDING, // Items anybody can create by folding a sugar sheet
    PHINEAS, // Items requiring access to Phineas
    COOK_FANCY, // Items created with an Dramatic Range
    MIX_FANCY, // Items created with a Cocktailcrafting Kit
    ACOMBINE, // Un-untinkerable Ideas
    COINMASTER, // Coinmaster purchase
    CLIPART, // Tome of Clip Art summons
    JARLS, // Items that can only be made by an Avatar of Jarlsberg
    GRANDMA, // Items made by Grandma Sea Monkee
    CHEMCLASS, // Items made in Chemistry Class at KOLHS
    ARTCLASS, // Items made in Art Class at KOLHS
    SHOPCLASS, // Items made in Shop Class at KOLHS
    BEER, // Items made with ingredients from A Beer Garden
    JUNK, // Items made from using a Worse Homes and Gardens
    WINTER, // Items made with ingredients from A Winter Garden
    RUMPLE, // Items made at Rumpelstiltskin's Workshop
    FIVE_D, // Xiblaxian 5d Printer
    VYKEA, // Items made with VYKEA instructions
    DUTYFREE, // Elemental International Airport Duty Free Shop
    FLOUNDRY, // Items made in Clan Floundry
    TERMINAL, // Items extruded from Source Terminal
    BARREL, // Items granted at the Barrel Shrine
    WAX, // Items made from globs of melted wax
    SPANT, // Items made from spant chitin/tendons
    SPACEGATE, // Items granted at Spacegate Equipment Requisition
    XO, // Items made using Xes and Os
    SLIEMCE, // Items made using slime blobs
    NEWSPAPER, // Items made from burning newspaper
    METEOROID, // Items made from metal meteoroid
    FANTASY_REALM, // Items made by visiting FantasyRealm welcome center
    SAUSAGE_O_MATIC, // Items made by Kramco Sausage-o-Matic
    KRINGLE, // Items made from waterlogged items
    STILLSUIT, // Distillate from the stillsuit
    WOOL, // Items made from grubby wool
    SHADOW_FORGE, // Items made at The Shadow Forge
    FIXODENT, // Items made from fixodent
    BURNING_LEAVES, // Items made at the Pile of Burning Leaves
    TINKERING_BENCH, // Items made at the Tinkering Bench
    MAYAM, // Items made at the Mayam Calendar
    PHOTO_BOOTH, // Items made at the Clan Photo Booth
    TAKERSPACE, // Items made at the TakerSpace
    GNOME_PART, // Parts for your Reagnimated Gnome, made at the Cake-Shaped Arena
  }

  enum CraftingRequirements {
    MALE,
    FEMALE,
    SSPD, // Requires SSPD
    HAMMER, // Requires tenderizing hammer (implied for SMITH & SSMITH)
    GRIMACITE, // Requires depleted Grimacite hammer
    TORSO,
    SUPER_MEATSMITHING,
    ARMORCRAFTINESS,
    ELDRITCH, // Requires Eldritch Intellect
    EXPENSIVE, // Requires Really Expensive Jewelrycrafting
    REAGENT, // Requires Advanced Saucecrafting
    WAY, // Requires The Way of Sauce
    DEEP_SAUCERY,
    PASTA,
    TRANSNOODLE, // Requires Transcendental Noodlecraft
    TEMPURAMANCY, // Requires Tempuramancy
    PATENT, // Requires Patent Medicine
    AC, // Requires Advanced Cocktailcrafting
    SHC, // Requires Superhuman Cocktailcrafting
    SALACIOUS, // Requires Salacious Cocktailcrafting
    TIKI, // Requires Tiki Mixology
    NOBEE, // Not on Bees Hate You path
    BAKE, // Requires Avatar of Jarlsberg skill Bake
    BLEND, // Requires Avatar of Jarlsberg skill Blend
    BOIL, // Requires Avatar of Jarlsberg skill Boil
    CHOP, // Requires Avatar of Jarlsberg skill Chop
    CURDLE, // Requires Avatar of Jarlsberg skill Curdle
    FREEZE, // Requires Avatar of Jarlsberg skill Freeze
    FRY, // Requires Avatar of Jarlsberg skill Fry
    GRILL, // Requires Avatar of Jarlsberg skill Grill
    SLICE, // Requires Avatar of Jarlsberg skill Slice
  }

  enum CraftingMisc {
    TRIPLE_SAUCE, // Saucerors make 3 of this item at a time
    NODISCOVERY, // Recipe unexpectedly does not appear in Discoveries
    MANUAL, // Recipe should never be used automatically
  }

  // Colors which are used to handle the various KoLmafia states.
  // Used when changing the display.

  Color ERROR_COLOR = new Color(255, 192, 192);
  Color ENABLED_COLOR = new Color(192, 255, 192);
  Color DISABLED_COLOR = null;

  // Constants which are useful, but not necessarily used very often.
  // Includes win game text.

  String DEFAULT_KMAIL = "Keep the contents of this message top-sekrit, ultra hush-hush.";

  String[][] WIN_GAME_TEXT =
      new String[][] {
        {
          "Petitioning the Seaside Town Council for automatic game completion...",
          "The Seaside Town Council has rejected your petition.  Game incomplete.",
          "You reject the Seaside Town's decision.  Fighting the council...",
          "You have been defeated by the Seaside Town Council."
        },
        {
          "You enter the super-secret code into the Strange Leaflet...",
          "Your ruby W and heavy D fuse to form the mysterious R!",
          "Moxie sign backdoor accessed.	Supertinkering The Ultimate Weapon...",
          "Supertinkering complete.  Executing tower script...",
          "Your RNG spawns an enraged cow on Floors 1-6."
        },
        {
          "You win the game. What, you were expecting more?",
          "You are now standing in an open field to the west of the Kingdom.",
          "You hear a gurgling ocean to the south, and a path leads north into Valhalla.",
          "What now, Adventurer?"
        },
        {
          "You touch your star starfish!	You surge with power!",
          "Accessing tower backdoor.  Fighting Naughty Sorceress...",
          "Connection timed out during post.  Retrying...",
          "Connection timed out during reply.  Retrying...",
          "Star power expired.  You slink away, dejected and defeated."
        },
        {
          "You raise your metallic A to the sky. Victory is yours!",
          "Original game concept by Jick (Asymmetric Publications).",
          "Co-written by Mr. Skullhead, Riff, and the /dev team.",
          "Special thanks to: the Mods, the Ascension testers, and you.",
          "We present you a new quest, which is the same thing, only harder.",
          "Segmentation fault.  Core dumped."
        },
        {
          "Unlocking secret Arizona trail script...",
          "Crossing first obstacle, admiring landmarks...",
          "Path set to oxygenarian, familiar pace set to grue-ing...",
          "You have died from KoLera."
        },
        {
          "Challenging Naughty Sorceress to a game of chess...",
          "Naughty Sorceress accepts, and invites you to go first.  Calculating move...",
          "Timeout in findBestMove(), making random move: &#9817;f2-f3",
          "Naughty Sorceress moves: &#9823;e7-e5",
          "Timeout in findBestMove(), making random move: &#9817;g2-g4",
          "Naughty Sorceress moves: &#9819;d8-h4",
          "Checkmate."
        },
      };

  // Variables which relate to a given session.  These are made
  // global in order to ensure that any element of KoLmafia can
  // access session-specific information.

  List<String> saveStateNames = LockableListFactory.getSortedInstance(String.class);

  List<AdventureResult> inventory = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> closet = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> storage = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> unlimited = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> freepulls = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> nopulls = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> collection = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> campground = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> chateau = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> falloutShelter =
      LockableListFactory.getSortedInstance(AdventureResult.class);
  List<AdventureResult> pulverizeQueue =
      LockableListFactory.getSortedInstance(AdventureResult.class);

  List<UseSkillRequest> usableSkills = LockableListFactory.getInstance(UseSkillRequest.class);
  List<UseSkillRequest> summoningSkills = LockableListFactory.getInstance(UseSkillRequest.class);
  List<UseSkillRequest> remedySkills = LockableListFactory.getInstance(UseSkillRequest.class);
  List<UseSkillRequest> selfOnlySkills = LockableListFactory.getInstance(UseSkillRequest.class);
  List<UseSkillRequest> buffSkills = LockableListFactory.getInstance(UseSkillRequest.class);
  List<UseSkillRequest> songSkills = LockableListFactory.getInstance(UseSkillRequest.class);
  List<UseSkillRequest> expressionSkills = LockableListFactory.getInstance(UseSkillRequest.class);
  List<UseSkillRequest> walkSkills = LockableListFactory.getInstance(UseSkillRequest.class);
  List<UseSkillRequest> availableSkills = LockableListFactory.getInstance(UseSkillRequest.class);
  Set<Integer> availableSkillsSet = new HashSet<>();
  // The list of combat skills displayed in skills dropdown from the current (last) fight.php
  List<Integer> availableCombatSkillsList = new ArrayList<>();
  Set<Integer> availableCombatSkillsSet = new HashSet<>();
  List<UseSkillRequest> permedSkills = LockableListFactory.getInstance(UseSkillRequest.class);
  Set<Integer> hardcorePermedSkills = new HashSet<>();
  List<UseSkillRequest> combatSkills = LockableListFactory.getInstance(UseSkillRequest.class);

  List<AdventureResult> activeEffects = LockableListFactory.getInstance(AdventureResult.class);
  ArrayList<AdventureResult> recentEffects = new ArrayList<>();

  List<AdventureResult> hermitItems = LockableListFactory.getInstance(AdventureResult.class);
  List<String> restaurantItems = LockableListFactory.getInstance(String.class);
  List<String> microbreweryItems = LockableListFactory.getInstance(String.class);
  List<String> kitchenItems = LockableListFactory.getInstance(String.class);
  List<String> cafeItems = LockableListFactory.getInstance(String.class);

  List<AdventureResult> tally = LockableListFactory.getSortedInstance(AdventureResult.class);
  List<RegisteredEncounter> adventureList =
      LockableListFactory.getInstance(RegisteredEncounter.class);
  List<RegisteredEncounter> encounterList =
      LockableListFactory.getSortedInstance(RegisteredEncounter.class);

  // Locations where session information is displayed for the user.
  // Include just the event history buffer and the command line buffer.

  StyledChatBuffer commandBuffer = new StyledChatBuffer("", "blue", false);

  Comparator<String> ignoreCaseComparator =
      new Comparator<>() {
        @Override
        public int compare(String s1, String s2) {
          return s1.compareToIgnoreCase(s2);
        }
      };
}
