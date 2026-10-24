/* Modified by demoshow for AFWall+ TV */
package dev.ukanth.ufirewall.tv;

import static org.junit.Assert.*;

import org.junit.Test;

public class TvPolicyTest {
  @Test
  public void defaultEnabledFlagIsNotEvidenceOfCustomRules() {
    assertFalse(TvPolicy.hasCustomizations(true, 0, "", ""));
    assertFalse(TvPolicy.hasCustomizations(true, 0, "  \n", null));
    assertFalse(TvPolicy.hasCustomizations(false, 4, "", ""));
  }

  @Test
  public void actualCustomizationsOnlyWarnWithoutInventingAToggle() {
    assertTrue(TvPolicy.hasCustomizations(true, 1, "", ""));
    assertTrue(TvPolicy.hasCustomizations(false, 0, "iptables -A afwall -j RETURN", ""));
    assertTrue(TvPolicy.hasCustomizations(false, 0, "", "iptables -F afwall"));
  }

  @Test
  public void modesSurviveSaveAndReloadInBothEngineModes() {
    for (boolean white : new boolean[] {false, true}) {
      for (int mode = 0; mode < 3; mode++) {
        boolean wan = TvPolicy.blockedWan(mode), lan = TvPolicy.blockedLan(mode);
        boolean wanReload = TvPolicy.blocked(TvPolicy.selected(wan, white), false, white);
        boolean lanReload = TvPolicy.blocked(TvPolicy.selected(lan, white), false, white);
        assertEquals(
            mode,
            TvPolicy.classify(wanReload, lanReload, wanReload, wanReload, wanReload, wanReload));
      }
    }
  }

  @Test
  public void anyApplicationSelectionIsAccountedForWhenLoading() {
    assertTrue(TvPolicy.blocked(false, true, false));
    assertFalse(TvPolicy.blocked(false, true, true));
    assertFalse(TvPolicy.blocked(true, true, true));
  }

  @Test
  public void ordinarySelectionsHaveOppositeMeaningsInEngineModes() {
    assertTrue(TvPolicy.selected(true, false));
    assertFalse(TvPolicy.selected(true, true));
    assertFalse(TvPolicy.selected(false, false));
    assertTrue(TvPolicy.selected(false, true));
  }

  @Test
  public void threeModesMapToBlacklist() {
    for (int mode = 0; mode < 3; mode++) {
      boolean wan = TvPolicy.blockedWan(mode), lan = TvPolicy.blockedLan(mode);
      assertEquals(mode, TvPolicy.classify(wan, lan, wan, wan, wan, wan));
    }
  }

  @Test
  public void detectsCustomRulesInsteadOfMislabeling() {
    assertEquals(TvPolicy.CUSTOM, TvPolicy.classify(false, true, false, false, false, false));
    assertEquals(TvPolicy.CUSTOM, TvPolicy.classify(true, false, false, false, false, false));
  }

  @Test
  public void protectsSystemAdbAndOwnUidIncludingSecondaryUsers() {
    assertTrue(TvPolicy.protectedUid(0, 10123));
    assertTrue(TvPolicy.protectedUid(1000, 10123));
    assertTrue(TvPolicy.protectedUid(2000, 10123));
    assertTrue(TvPolicy.protectedUid(-10, 10123));
    assertTrue(TvPolicy.protectedUid(101000, 10123));
    assertTrue(TvPolicy.protectedUid(10123, 10123));
    assertFalse(TvPolicy.protectedUid(10057, 10123));
    assertFalse(TvPolicy.protectedUid(110057, 10123));
  }
}
