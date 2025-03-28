package net.sourceforge.kolmafia.request;

import static internal.helpers.Networking.html;
import static internal.helpers.Player.withSign;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import internal.helpers.Cleanups;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.kolmafia.RequestLogger;
import net.sourceforge.kolmafia.ZodiacSign;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MallSearchRequestTest {

  private static String lastSessionLogLine = "";

  private static Cleanups mockRequestLogger() {
    var mocked = mockStatic(RequestLogger.class, Mockito.CALLS_REAL_METHODS);
    mocked
        .when(() -> RequestLogger.updateSessionLog())
        .thenAnswer(
            invocation -> {
              lastSessionLogLine = "";
              return false;
            });
    mocked
        .when(() -> RequestLogger.updateSessionLog(anyString()))
        .thenAnswer(
            invocation -> {
              Object[] arguments = invocation.getArguments();
              String line = (String) arguments[0];
              lastSessionLogLine = line;
              return false;
            });
    return new Cleanups(mocked::close);
  }

  @Test
  public void canRegisterMallSearchRequests() {
    try (var cleanups = mockRequestLogger()) {

      String url;
      boolean result;

      url = "nope.php";
      result = MallSearchRequest.registerRequest(url);
      assertFalse(result);

      url = "mallstore.php?whichstore=1053259&buying=1";
      result = MallSearchRequest.registerRequest(url);
      assertFalse(result);

      url = "mallstore.php?whichstore=1053259";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch shop #1053259", lastSessionLogLine);

      url =
          "mall.php?didadv=0&pudnuggler=%22black+%26+tan%22&category=allitems&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=0&consumable_tier_5=0";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch \"black & tan\"", lastSessionLogLine);

      url =
          "mall.php?didadv=0&pudnuggler=%22tea%22&category=allitems&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=0&consumable_tier_5=0";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch \"tea\"", lastSessionLogLine);

      url =
          "mall.php?didadv=0&pudnuggler=tea&category=allitems&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=0&consumable_tier_5=0";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch tea", lastSessionLogLine);

      url =
          "mall.php?didadv=0&pudnuggler=tea&category=allitems&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=0&consumable_tier_5=0&start=30";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch tea (page 2)", lastSessionLogLine);

      url =
          "mall.php?didadv=0&pudnuggler=tea&category=allitems&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=0&consumable_tier_5=0&start=60";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch tea (page 3)", lastSessionLogLine);

      url =
          "mall.php?didadv=0&pudnuggler=tea&category=allitems&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=0&consumable_tier_5=0&start=90";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch tea (page 4)", lastSessionLogLine);

      url =
          "mall.php?didadv=1&pudnuggler=&category=food&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=0&consumable_tier_5=0&consumable_tier_5=1";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch category food [EPIC]", lastSessionLogLine);

      url =
          "mall.php?didadv=1&pudnuggler=&category=food&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=0&consumable_tier_4=1&consumable_tier_5=0&consumable_tier_5=1";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch category food [awesome, EPIC]", lastSessionLogLine);

      url =
          "mall.php?didadv=1&pudnuggler=&category=food&food_sortitemsby=name&booze_sortitemsby=name&othercon_sortitemsby=name&consumable_byme=0&hats_sortitemsby=name&shirts_sortitemsby=name&pants_sortitemsby=name&weapons_sortitemsby=name&weaponattribute=3&weaponhands=3&acc_sortitemsby=name&offhand_sortitemsby=name&wearable_byme=0&famequip_sortitemsby=name&nolimits=0&justitems=0&sortresultsby=price&max_price=0&x_cheapest=0&consumable_tier_1=0&consumable_tier_2=0&consumable_tier_3=0&consumable_tier_4=1&consumable_tier_5=1&start=30";
      result = MallSearchRequest.registerRequest(url);
      assertTrue(result);
      assertEquals("mallsearch category food [awesome, EPIC] (page 2)", lastSessionLogLine);

      MallSearchRequest request = new MallSearchRequest("booze", "EPIC awesome good decent crappy");
      RequestLogger.registerRequest(request, request.getURLString());
      assertEquals(
          "mallsearch category booze [crappy, decent, good, awesome, EPIC]", lastSessionLogLine);
    }
  }

  @Nested
  class ParsingResults {
    @Test
    public void isNotNPCIfNotKnollSign() {
      Cleanups cleanups = new Cleanups(withSign(ZodiacSign.WOMBAT));
      try (cleanups) {
        List<PurchaseRequest> results = new ArrayList<>();
        MallSearchRequest request = new MallSearchRequest("sprocket", 100, results);
        request.responseText = html("request/test_mall_search_sprocket.html");
        request.processResults();
        assertEquals(results.size(), 60);
      }
    }

    @Test
    public void isNPCIfKnollSign() {
      Cleanups cleanups = new Cleanups(withSign(ZodiacSign.MONGOOSE));
      try (cleanups) {
        List<PurchaseRequest> results = new ArrayList<>();
        MallSearchRequest request = new MallSearchRequest("sprocket", 100, results);
        request.responseText = html("request/test_mall_search_sprocket.html");
        request.processResults();
        assertEquals(results.size(), 61);
      }
    }
  }
}
