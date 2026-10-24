/* TV additions, GPL-3.0-or-later. Modified 2026-10-22.  * Modified by demoshow for AFWall+ TV
 */
package dev.ukanth.ufirewall.tv;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import dev.ukanth.ufirewall.R;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/** Real Android resource selection; no root, network rules, or physical device required. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class TvLocalizationTest {
  private Resources resources(String languageTag) {
    Application app = RuntimeEnvironment.getApplication();
    Configuration config = new Configuration(app.getResources().getConfiguration());
    config.setLocales(new LocaleList(Locale.forLanguageTag(languageTag)));
    return app.createConfigurationContext(config).getResources();
  }

  @Test
  public void englishUsesEnglishPoliciesAndActions() {
    Resources r = resources("en-US");
    assertEquals("Apply rules", r.getString(R.string.tv_apply));
    assertArrayEquals(
        new String[] {"Unrestricted", "LAN only", "Blocked", "Custom (advanced rules)"},
        r.getStringArray(R.array.tv_modes));
  }

  @Test
  @Config(sdk = 24)
  public void android7AlsoResolvesChineseAndEnglish() {
    assertEquals("应用规则", resources("zh-CN").getString(R.string.tv_apply));
    assertEquals("Apply rules", resources("en-US").getString(R.string.tv_apply));
  }

  @Test
  public void chineseUsesChinesePoliciesAndActions() {
    Resources r = resources("zh-CN");
    assertEquals("应用规则", r.getString(R.string.tv_apply));
    assertArrayEquals(
        new String[] {"正常联网", "仅内网", "禁止联网", "自定义（高级规则）"}, r.getStringArray(R.array.tv_modes));
  }

  @Test
  public void otherChineseRegionsHaveChineseTvResources() {
    assertEquals("应用规则", resources("zh-TW").getString(R.string.tv_apply));
    assertEquals("应用规则", resources("zh-HK").getString(R.string.tv_apply));
  }

  @Test
  public void untranslatedTvLocalesFallBackToEnglish() {
    assertEquals("Apply rules", resources("de-DE").getString(R.string.tv_apply));
  }

  @Test
  public void resourceSelectionCanFollowASystemLanguageChange() {
    assertEquals("高级设置", resources("zh-CN").getString(R.string.tv_advanced));
    assertEquals("Advanced", resources("en-GB").getString(R.string.tv_advanced));
    assertEquals("高级设置", resources("zh-CN").getString(R.string.tv_advanced));
  }

  @Test
  public void formattedStatusAndFailureMessagesUseBothLocales() {
    for (String locale : new String[] {"en-US", "zh-CN"}) {
      Resources r = resources(locale);
      assertTrue(r.getString(R.string.tv_status, "STATE", 17, "", "").contains("17"));
      assertTrue(r.getString(R.string.tv_shared_uid, 3, "UID 10001").contains("10001"));
      String error = r.getString(R.string.tv_rollback, 7, "OLD", "FAILED", "DETAILS");
      assertTrue(error.contains("7"));
      assertTrue(error.contains("DETAILS"));
      assertFalse(error.contains("%1$"));
    }
  }
}
