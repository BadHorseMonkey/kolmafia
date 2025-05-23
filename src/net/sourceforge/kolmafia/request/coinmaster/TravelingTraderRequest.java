package net.sourceforge.kolmafia.request.coinmaster;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.kolmafia.AdventureResult;
import net.sourceforge.kolmafia.CoinmasterData;
import net.sourceforge.kolmafia.KoLConstants;
import net.sourceforge.kolmafia.KoLConstants.MafiaState;
import net.sourceforge.kolmafia.KoLmafia;
import net.sourceforge.kolmafia.objectpool.ItemPool;
import net.sourceforge.kolmafia.persistence.ItemDatabase;
import net.sourceforge.kolmafia.request.GenericRequest;
import net.sourceforge.kolmafia.request.PurchaseRequest;
import net.sourceforge.kolmafia.utilities.StringUtilities;

public class TravelingTraderRequest extends CoinMasterRequest {
  public static final String master = "Traveling Trader";

  // traveler.php?action=For Gnomeregan!&whichitem=xxxx&quantity=1&tradeall=1&usehagnk=1&pwd
  private static final AdventureResult item = ItemPool.get(ItemPool.TWINKLY_WAD, 1);

  public static final CoinmasterData TRAVELER =
      new CoinmasterData(master, "trader", TravelingTraderRequest.class)
          .withToken("twinkly wad")
          .withItem(item)
          .withBuyURL("traveler.php")
          .withBuyAction("For Gnomeregan!")
          .withBuyItems()
          .withBuyPrices()
          .withItemField("whichitem")
          .withItemPattern(GenericRequest.WHICHITEM_PATTERN)
          .withCountField("quantity")
          .withItemPattern(GenericRequest.QUANTITY_PATTERN)
          .withStorageAction("usehagnk=1")
          .withTradeAllAction("tradeall=1")
          .withAccessible(TravelingTraderRequest::accessible);

  public TravelingTraderRequest() {
    super(TRAVELER);
  }

  public TravelingTraderRequest(final boolean buying, final AdventureResult[] attachments) {
    super(TRAVELER, buying, attachments);
  }

  @Override
  public void processResults() {
    if (this.responseText.length() == 0) {
      KoLmafia.updateDisplay(MafiaState.ERROR, "The Traveling Trader is not in the Market Square.");
      return;
    }
    parseResponse(this.getURLString(), this.responseText);
  }

  // The traveling trader is looking to acquire:<br><img class='hand
  // item' onclick='descitem(503220568);'
  // src='http://images.kingdomofloathing.com/itemimages/scwad.gif'>
  // <b>twinkly wads</b><br>

  private static final Pattern ACQUIRE_PATTERN =
      Pattern.compile(
          "The traveling trader is looking to acquire.*?descitem\\(([\\d]+)\\).*?<b>([^<]*)</b>");

  // (You have <b>3,348</b> on you.)

  private static final Pattern INVENTORY_PATTERN =
      Pattern.compile("\\(You have <b>([\\d,]*|none)</b> on you.\\)");

  // You currently have <b>1,022</b> twinkly wads in Hagnk's Ancestral Storage

  private static final Pattern STORAGE_PATTERN =
      Pattern.compile(
          "You currently have <b>([\\d,]+)</b> (.*?) in Hagnk's Ancestral Storage", Pattern.DOTALL);

  // <tr><td><input type=radio name=whichitem value=4411
  // checked="checked"></td><td><a class=nounder
  // href='javascript:descitem(629749615);'> <img class='hand item'
  // src='http://images.kingdomofloathing.com/itemimages/music.gif'>
  // <b>Inigo's Incantation of Inspiration</b></a></td><td>100 twinkly
  // wads</td></tr

  private static final Pattern ITEM_PATTERN =
      Pattern.compile(
          "name=whichitem value=([\\d]+).*?>.*?descitem.*?([\\d]+).*?<b>([^<]*)</b></a></td><td>([\\d]+)",
          Pattern.DOTALL);

  public static void parseResponse(final String urlString, final String responseText) {
    if (!urlString.startsWith("traveler.php")) {
      return;
    }

    // First, see what item he's trading for
    Matcher matcher = ACQUIRE_PATTERN.matcher(responseText);
    if (!matcher.find()) {
      return;
    }

    String descId = matcher.group(1);
    String plural1 = matcher.group(2);

    int itemId = ItemDatabase.lookupItemIdFromDescription(descId);

    // We know the item. Set the item and token in the CoinmasterData

    CoinmasterData data = TRAVELER;
    AdventureResult item = ItemPool.get(itemId, PurchaseRequest.MAX_QUANTITY);
    data.setItem(item);
    data.setToken(item.getName());

    // Sanity check number of that item we have in inventory
    matcher = INVENTORY_PATTERN.matcher(responseText);
    if (matcher.find()) {
      String num = matcher.group(1);
      int num1 =
          num == null
              ? 0
              : num.equals("none") ? 0 : num.equals("one") ? 1 : StringUtilities.parseInt(num);

      int icount = item.getCount(KoLConstants.inventory);
      int idelta = num1 - icount;
      if (idelta != 0) {
        AdventureResult result = ItemPool.get(itemId, idelta);
        AdventureResult.addResultToList(KoLConstants.inventory, result);
        AdventureResult.addResultToList(KoLConstants.tally, result);
      }
    }

    // Sanity check number of that item we have in storage
    String plural2 = null;

    matcher = STORAGE_PATTERN.matcher(responseText);
    if (matcher.find()) {
      String num = matcher.group(1);
      int num2 =
          num == null
              ? 0
              : num.equals("none") ? 0 : num.equals("one") ? 1 : StringUtilities.parseInt(num);
      plural2 = matcher.group(2);

      int scount = item.getCount(KoLConstants.storage);
      int sdelta = num2 - scount;
      if (sdelta != 0) {
        AdventureResult result = ItemPool.get(itemId, sdelta);
        AdventureResult.addResultToList(KoLConstants.storage, result);
      }
    }

    // Refresh the coinmaster lists every time we visit.
    // Learn new trade items by simply visiting the Traveling Trader

    List<AdventureResult> items = TRAVELER.getBuyItems();
    Map<Integer, Integer> prices = TRAVELER.getBuyPrices();
    items.clear();
    prices.clear();

    matcher = ITEM_PATTERN.matcher(responseText);
    while (matcher.find()) {
      int id = StringUtilities.parseInt(matcher.group(1));
      String desc = matcher.group(2);
      String name = matcher.group(3).trim();
      int price = StringUtilities.parseInt(matcher.group(4));

      String match = ItemDatabase.getItemDataName(itemId);
      if (match == null || !match.equals(name)) {
        ItemDatabase.registerItem(id, name, desc);
      }

      // Add it to the Traveling Trader inventory
      AdventureResult offering = ItemPool.get(id, 1);
      items.add(offering);
      prices.put(id, price);
    }

    // Register the purchase requests, now that we know what is available
    data.registerPurchaseRequests();

    CoinMasterRequest.parseResponse(data, urlString, responseText);
  }

  public static String accessible() {
    // *** Is there something we can do here? Like the Time Twitching Tower.
    return null;
  }

  public static boolean registerRequest(final String urlString) {
    if (!urlString.startsWith("traveler.php")) {
      return false;
    }

    return CoinMasterRequest.registerRequest(TRAVELER, urlString, true);
  }
}
