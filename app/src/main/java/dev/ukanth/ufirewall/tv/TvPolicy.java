/* AFWall+ TV adaptations, GPL-3.0-or-later. */
package dev.ukanth.ufirewall.tv;

/** Maps the TV's three choices to upstream AFWall+ blacklist selections. */
public final class TvPolicy {
  public static final int NORMAL = 0, LAN_ONLY = 1, BLOCKED = 2, CUSTOM = 3;

  private TvPolicy() {}

  /** The TV model always stores 'blocked'; upstream selection depends on its mode. */
  public static boolean selected(boolean blocked, boolean whitelist) {
    return blocked != whitelist;
  }

  public static boolean blocked(boolean selected, boolean anySelected, boolean whitelist) {
    return (selected || anySelected) != whitelist;
  }

  public static boolean hasCustomizations(
      boolean directRulesEnabled, int activeRules, String startup, String shutdown) {
    return (directRulesEnabled && activeRules > 0)
        || (startup != null && !startup.trim().isEmpty())
        || (shutdown != null && !shutdown.trim().isEmpty());
  }

  public static boolean blockedWan(int policy) {
    return policy != NORMAL;
  }

  public static boolean blockedLan(int policy) {
    return policy == BLOCKED;
  }

  public static int classify(
      boolean wifiBlocked,
      boolean lanBlocked,
      boolean dataBlocked,
      boolean vpnBlocked,
      boolean roamBlocked,
      boolean tetherBlocked) {
    if (!wifiBlocked
        && !lanBlocked
        && !dataBlocked
        && !vpnBlocked
        && !roamBlocked
        && !tetherBlocked) return NORMAL;
    if (wifiBlocked && !lanBlocked && dataBlocked && vpnBlocked && roamBlocked && tetherBlocked)
      return LAN_ONLY;
    if (wifiBlocked && lanBlocked && dataBlocked && vpnBlocked && roamBlocked && tetherBlocked)
      return BLOCKED;
    return CUSTOM;
  }

  public static boolean protectedUid(int uid, int ownUid) {
    return uid < 0 || uid % 100000 < 10000 || uid == ownUid;
  }
}
