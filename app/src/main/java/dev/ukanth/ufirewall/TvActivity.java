/*
 * Television UI for AFWall+. Copyright 2026 local TV adaptations.
 * Based on AFWall+ by Umakanthan Chandran and contributors.
 * GPL-3.0-or-later; see LICENSE and NOTICE.md.
 * Modified 2026-10-22: localized TV interface and public source distribution.
  * Modified by demoshow for AFWall+ TV
 */
package dev.ukanth.ufirewall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.topjohnwu.superuser.Shell;
import dev.ukanth.ufirewall.customrules.CustomRule;
import dev.ukanth.ufirewall.customrules.CustomRule_Table;
import dev.ukanth.ufirewall.service.RootCommand;
import dev.ukanth.ufirewall.tv.TvPolicy;
import dev.ukanth.ufirewall.util.AppRuleHelper;
import dev.ukanth.ufirewall.util.G;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** No firewall is enabled by viewing this screen or changing a draft selection. */
public final class TvActivity extends Activity {
  private final ExecutorService worker = Executors.newSingleThreadExecutor();
  private final List<Api.PackageInfoData> apps = new ArrayList<>();
  private TextView status;
  private ListView list;
  private BaseAdapter adapter;
  private boolean busy, dirty, reloadAfterAdvanced;
  private String[] modes;
  private static final int[] COLORS = {0xff67dba0, 0xff75bdff, 0xffffa394, 0xffffd480};

  @Override
  public void onCreate(Bundle saved) {
    super.onCreate(saved);
    modes = getResources().getStringArray(R.array.tv_modes);
    LinearLayout page = new LinearLayout(this);
    page.setOrientation(LinearLayout.VERTICAL);
    page.setPadding(36, 20, 36, 16);
    page.setBackgroundColor(0xff101820);
    TextView title = text(getString(R.string.tv_title), 27);
    title.setTypeface(null, Typeface.BOLD);
    page.addView(title);
    status = text(getString(R.string.tv_initial_status), 17);
    page.addView(status);
    LinearLayout toolbar = new LinearLayout(this);
    toolbar.addView(
        button(getString(R.string.tv_apply), v -> confirmApply()),
        new LinearLayout.LayoutParams(0, 64, 1));
    toolbar.addView(
        button(getString(R.string.tv_disable), v -> confirmDisable()),
        new LinearLayout.LayoutParams(0, 64, 1));
    toolbar.addView(
        button(getString(R.string.tv_inspect), v -> inspect()),
        new LinearLayout.LayoutParams(0, 64, 1));
    toolbar.addView(
        button(
            getString(R.string.tv_advanced),
            v -> {
              if (dirty) {
                message(getString(R.string.tv_draft_title), getString(R.string.tv_draft_body));
                return;
              }
              reloadAfterAdvanced = true;
              startActivity(new Intent(this, MainActivity.class));
            }),
        new LinearLayout.LayoutParams(0, 64, 1));
    page.addView(toolbar);
    page.addView(text(getString(R.string.tv_navigation), 16));
    list = new ListView(this);
    list.setDividerHeight(8);
    list.setItemsCanFocus(false);
    list.setId(View.generateViewId());
    list.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    for (int i = 0; i < toolbar.getChildCount(); i++) {
      View control = toolbar.getChildAt(i);
      control.setNextFocusDownId(list.getId());
      if (i == 0) list.setNextFocusUpId(control.getId());
    }
    list.setSelector(new android.graphics.drawable.ColorDrawable(0xff355472));
    page.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
    page.addView(text(getString(R.string.tv_footer), 14));
    setContentView(page);
    adapter =
        new BaseAdapter() {
          public int getCount() {
            return apps.size();
          }

          public Object getItem(int p) {
            return apps.get(p);
          }

          public long getItemId(int p) {
            return apps.get(p).uid;
          }

          public View getView(int p, View recycled, ViewGroup parent) {
            Api.PackageInfoData app = apps.get(p);
            LinearLayout row = new LinearLayout(TvActivity.this);
            row.setPadding(18, 10, 18, 10);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setMinimumHeight(78);
            LinearLayout info = new LinearLayout(TvActivity.this);
            info.setOrientation(LinearLayout.VERTICAL);
            String names = android.text.TextUtils.join(" / ", app.names);
            info.addView(text(names, 21));
            String[] packages = getPackageManager().getPackagesForUid(app.uid);
            String detail =
                "UID "
                    + app.uid
                    + " · "
                    + (packages == null
                        ? app.pkgName
                        : android.text.TextUtils.join(", ", packages));
            if (packages != null && packages.length > 1)
              detail = getString(R.string.tv_shared_uid, packages.length, detail);
            TextView sub = text(detail, 13);
            sub.setTextColor(0xffaebdca);
            info.addView(sub);
            row.addView(info, new LinearLayout.LayoutParams(0, -2, 1));
            boolean protectedApp = protectedApp(app);
            int mode = policy(app);
            TextView badge =
                text(protectedApp ? getString(R.string.tv_core_badge) : modes[mode], 19);
            badge.setTextColor(protectedApp ? 0xffaebdca : COLORS[mode]);
            badge.setPadding(20, 0, 0, 0);
            row.addView(badge);
            return row;
          }
        };
    list.setAdapter(adapter);
    list.setOnItemClickListener((parent, view, position, id) -> choose(apps.get(position)));
    loadApps();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (reloadAfterAdvanced && !busy) {
      reloadAfterAdvanced = false;
      loadApps();
      return;
    }
    if (status != null && !busy && !apps.isEmpty()) updateStatus();
  }

  private TextView text(String s, int size) {
    TextView v = new TextView(this);
    v.setText(s);
    v.setTextSize(size);
    v.setTextColor(Color.WHITE);
    return v;
  }

  private Button button(String s, View.OnClickListener click) {
    Button b = new Button(this);
    b.setId(View.generateViewId());
    b.setText(s);
    b.setTextSize(17);
    b.setAllCaps(false);
    b.setTextColor(Color.WHITE);
    StateListDrawable bg = new StateListDrawable();
    GradientDrawable focus = new GradientDrawable();
    focus.setColor(0xff366e9f);
    focus.setCornerRadius(10);
    focus.setStroke(3, 0xffb8ddff);
    GradientDrawable normal = new GradientDrawable();
    normal.setColor(0xff243746);
    normal.setCornerRadius(10);
    bg.addState(new int[] {android.R.attr.state_focused}, focus);
    bg.addState(new int[] {android.R.attr.state_pressed}, focus);
    bg.addState(new int[] {}, normal);
    b.setBackground(bg);
    b.setFocusable(true);
    b.setOnClickListener(
        v -> {
          if (!busy) click.onClick(v);
        });
    return b;
  }

  private void updateStatus() {
    status.setText(
        getString(
            R.string.tv_status,
            getString(Api.isEnabled(this) ? R.string.tv_enabled : R.string.tv_disabled),
            apps.size(),
            dirty ? getString(R.string.tv_dirty_suffix) : "",
            getString(G.enableIPv6() ? R.string.tv_ipv6_on : R.string.tv_ipv6_off)));
  }

  private Set<String> saved(String key) {
    return new HashSet<>(Arrays.asList(G.pPrefs.getString(key, "").split("\\|")));
  }

  private boolean blocked(Set<String> selected, String uid, boolean white) {
    return TvPolicy.blocked(
        selected.contains(uid), selected.contains(String.valueOf(Api.SPECIAL_UID_ANY)), white);
  }

  private void loadApps() {
    busy = true;
    status.setText(getString(R.string.tv_loading));
    worker.execute(
        () -> {
          try {
            G.reloadPrefs();
            if (!G.pPrefs.contains(Api.PREF_MODE))
              G.pPrefs.edit().putString(Api.PREF_MODE, Api.MODE_BLACKLIST).commit();
            boolean white =
                Api.MODE_WHITELIST.equals(G.pPrefs.getString(Api.PREF_MODE, Api.MODE_BLACKLIST));
            Set<String> wifi = saved(Api.PREF_WIFI_PKG_UIDS),
                lan = saved(Api.PREF_LAN_PKG_UIDS),
                data = saved(Api.PREF_3G_PKG_UIDS),
                vpn = saved(Api.PREF_VPN_PKG_UIDS),
                roam = saved(Api.PREF_ROAMING_PKG_UIDS),
                tether = saved(Api.PREF_TETHER_PKG_UIDS);
            Map<Integer, Api.PackageInfoData> groups = new TreeMap<>();
            for (PackageInfo pkg :
                getPackageManager()
                    .getInstalledPackages(android.content.pm.PackageManager.GET_PERMISSIONS)) {
              ApplicationInfo ai = pkg.applicationInfo;
              if (ai == null
                  || pkg.requestedPermissions == null
                  || !Arrays.asList(pkg.requestedPermissions)
                      .contains("android.permission.INTERNET")) continue;
              Api.PackageInfoData app = groups.get(ai.uid);
              if (app == null) {
                app = new Api.PackageInfoData();
                app.uid = ai.uid;
                app.pkgName = pkg.packageName;
                app.appinfo = ai;
                app.names = new ArrayList<>();
                app.appType = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ? 0 : 1;
                String uid = String.valueOf(ai.uid);
                app.selected_wifi = blocked(wifi, uid, white);
                app.selected_lan = blocked(lan, uid, white);
                app.selected_3g = blocked(data, uid, white);
                app.selected_vpn = blocked(vpn, uid, white);
                app.selected_roam = blocked(roam, uid, white);
                app.selected_tether = blocked(tether, uid, white);
                groups.put(ai.uid, app);
              }
              app.names.add(ai.loadLabel(getPackageManager()).toString());
            }
            ArrayList<Api.PackageInfoData> loaded = new ArrayList<>(groups.values());
            Collections.sort(
                loaded,
                (a, b) -> {
                  boolean ap = protectedApp(a), bp = protectedApp(b);
                  if (ap != bp) return ap ? 1 : -1;
                  return a.names.get(0).compareToIgnoreCase(b.names.get(0));
                });
            runOnUiThread(
                () -> {
                  apps.clear();
                  apps.addAll(loaded);
                  busy = false;
                  dirty = false;
                  adapter.notifyDataSetChanged();
                  updateStatus();
                  list.requestFocus();
                });
          } catch (Exception e) {
            runOnUiThread(
                () -> {
                  busy = false;
                  message(getString(R.string.tv_load_failed), e.toString());
                });
          }
        });
  }

  private boolean protectedApp(Api.PackageInfoData app) {
    return TvPolicy.protectedUid(app.uid, android.os.Process.myUid());
  }

  private int policy(Api.PackageInfoData a) {
    return TvPolicy.classify(
        a.selected_wifi,
        a.selected_lan,
        a.selected_3g,
        a.selected_vpn,
        a.selected_roam,
        a.selected_tether);
  }

  private void setPolicy(Api.PackageInfoData app, int mode) {
    app.selected_wifi =
        app.selected_3g =
            app.selected_vpn = app.selected_roam = app.selected_tether = TvPolicy.blockedWan(mode);
    app.selected_lan = TvPolicy.blockedLan(mode);
    app.selected_tor = false;
  }

  private void choose(Api.PackageInfoData app) {
    if (busy) return;
    if (protectedApp(app)) {
      message(getString(R.string.tv_core_title), getString(R.string.tv_core_body));
      return;
    }
    new AlertDialog.Builder(this)
        .setTitle(android.text.TextUtils.join(" / ", app.names))
        .setSingleChoiceItems(
            Arrays.copyOf(modes, 3),
            policy(app) == TvPolicy.CUSTOM ? -1 : policy(app),
            (dialog, which) -> {
              setPolicy(app, which);
              dirty = true;
              adapter.notifyDataSetChanged();
              updateStatus();
              dialog.dismiss();
            })
        .setNegativeButton(getString(R.string.tv_cancel), null)
        .setNeutralButton(
            getString(R.string.tv_help),
            (d, w) ->
                message(getString(R.string.tv_modes_title), getString(R.string.tv_modes_body)))
        .show();
  }

  private void message(String title, String body) {
    if (!isFinishing())
      new AlertDialog.Builder(this)
          .setTitle(title)
          .setMessage(body)
          .setPositiveButton(getString(R.string.tv_ok), null)
          .show();
  }

  private void confirmApply() {
    if (busy || apps.isEmpty()) return;
    String customizationWarning = customizationWarning();
    new AlertDialog.Builder(this)
        .setTitle(getString(R.string.tv_apply_title))
        .setMessage(getString(R.string.tv_apply_body) + customizationWarning)
        .setPositiveButton(getString(R.string.tv_apply_confirm), (d, w) -> apply())
        .setNegativeButton(getString(R.string.tv_cancel), null)
        .show();
  }

  private String customizationWarning() {
    try {
      int active = 0;
      if (G.enableCustomRules()) {
        for (CustomRule rule :
            SQLite.select()
                .from(CustomRule.class)
                .where(CustomRule_Table.active.eq(true))
                .queryList()) {
          if (AppRuleHelper.belongsToCurrentProfile(rule)
              && AppRuleHelper.parseRuleName(rule.getName()) != null) active++;
        }
      }
      boolean configured =
          TvPolicy.hasCustomizations(
              G.enableCustomRules(),
              active,
              G.pPrefs.getString(Api.PREF_CUSTOMSCRIPT, ""),
              G.pPrefs.getString(Api.PREF_CUSTOMSCRIPT2, ""));
      return configured ? getString(R.string.tv_custom_warning) : "";
    } catch (Exception e) {
      return getString(R.string.tv_custom_check_failed, e.getClass().getSimpleName());
    }
  }

  private Map<String, ?> previous;
  private Map<String, ?> previousGlobal;
  private boolean previouslyEnabled;

  private void apply() {
    busy = true;
    status.setText(getString(R.string.tv_applying));
    previous = new HashMap<>(G.pPrefs.getAll());
    previousGlobal = new HashMap<>(G.gPrefs.getAll());
    previouslyEnabled = Api.isEnabled(this);
    worker.execute(
        () -> {
          try {
            if (!Shell.getShell().isRoot())
              throw new IllegalStateException(getString(R.string.tv_root_denied));
            G.hasRoot(true);
            if (!Api.assertBinaries(this, false))
              throw new IllegalStateException(getString(R.string.tv_binary_failed));
            G.enableLAN(true);
            G.enableIPv6(true);
            G.enableVPN(true);
            G.enableRoam(true);
            G.enableTether(true);
            boolean white =
                Api.MODE_WHITELIST.equals(G.pPrefs.getString(Api.PREF_MODE, Api.MODE_BLACKLIST));
            for (Api.PackageInfoData app : apps)
              if (protectedApp(app)) setPolicy(app, TvPolicy.NORMAL);
            Api.generateRules(this, engineSelections(white), true);
            enforceProtectedSelections(white);
            Api.applications = null;
            Api.applySavedIptablesRules(
                this,
                false,
                new RootCommand()
                    .setLogging(true)
                    .setCallback(
                        new RootCommand.Callback() {
                          @Override
                          public void cbFunc(RootCommand result) {
                            if (result.exitCode == 0)
                              runOnUiThread(
                                  () -> {
                                    Api.setEnabled(TvActivity.this, true, false);
                                    busy = false;
                                    dirty = false;
                                    updateStatus();
                                    message(
                                        getString(R.string.tv_applied_title),
                                        getString(R.string.tv_applied_body));
                                  });
                            else rollback(result);
                          }
                        }));
          } catch (Exception e) {
            restorePrefs();
            runOnUiThread(
                () -> {
                  busy = false;
                  updateStatus();
                  message(getString(R.string.tv_not_applied), e.toString());
                });
          }
        });
  }

  private List<Api.PackageInfoData> engineSelections(boolean white) {
    List<Api.PackageInfoData> selections = new ArrayList<>();
    Set<String> tor = saved(Api.PREF_TOR_PKG_UIDS);
    for (Api.PackageInfoData app : apps) {
      Api.PackageInfoData out = new Api.PackageInfoData();
      out.uid = app.uid;
      out.selected_wifi = TvPolicy.selected(app.selected_wifi, white);
      out.selected_lan = TvPolicy.selected(app.selected_lan, white);
      out.selected_3g = TvPolicy.selected(app.selected_3g, white);
      out.selected_vpn = TvPolicy.selected(app.selected_vpn, white);
      out.selected_roam = TvPolicy.selected(app.selected_roam, white);
      out.selected_tether = TvPolicy.selected(app.selected_tether, white);
      out.selected_tor = tor.contains(String.valueOf(app.uid));
      selections.add(out);
    }
    return selections;
  }

  private void enforceProtectedSelections(boolean white) {
    String[] keys = {
      Api.PREF_WIFI_PKG_UIDS,
      Api.PREF_LAN_PKG_UIDS,
      Api.PREF_3G_PKG_UIDS,
      Api.PREF_VPN_PKG_UIDS,
      Api.PREF_ROAMING_PKG_UIDS,
      Api.PREF_TETHER_PKG_UIDS
    };
    SharedPreferences.Editor edit = G.pPrefs.edit();
    for (String key : keys) {
      Set<String> safe = new LinkedHashSet<>();
      for (String token : G.pPrefs.getString(key, "").split("\\|")) {
        try {
          int uid = Integer.parseInt(token);
          if (!TvPolicy.protectedUid(uid, android.os.Process.myUid())) safe.add(token);
        } catch (NumberFormatException ignored) {
        }
      }
      if (white) {
        safe.add("0");
        safe.add("1000");
        safe.add("2000");
        safe.add(String.valueOf(android.os.Process.myUid()));
        for (Api.PackageInfoData app : apps)
          if (protectedApp(app)) safe.add(String.valueOf(app.uid));
      }
      edit.putString(key, android.text.TextUtils.join("|", safe));
    }
    edit.commit();
  }

  private void restorePrefs() {
    restoreStore(G.pPrefs, previous);
    restoreStore(G.gPrefs, previousGlobal);
    Api.applications = null;
  }

  @SuppressWarnings("unchecked")
  private void restoreStore(SharedPreferences prefs, Map<String, ?> backup) {
    SharedPreferences.Editor edit = prefs.edit().clear();
    for (Map.Entry<String, ?> entry : backup.entrySet()) {
      Object v = entry.getValue();
      String k = entry.getKey();
      if (v instanceof String) edit.putString(k, (String) v);
      else if (v instanceof Boolean) edit.putBoolean(k, (Boolean) v);
      else if (v instanceof Integer) edit.putInt(k, (Integer) v);
      else if (v instanceof Long) edit.putLong(k, (Long) v);
      else if (v instanceof Float) edit.putFloat(k, (Float) v);
      else if (v instanceof Set) edit.putStringSet(k, (Set<String>) v);
    }
    edit.commit();
  }

  private void rollback(RootCommand original) {
    restorePrefs();
    RootCommand recovery =
        new RootCommand()
            .setLogging(true)
            .setCallback(
                new RootCommand.Callback() {
                  @Override
                  public void cbFunc(RootCommand state) {
                    runOnUiThread(
                        () -> {
                          if (state.exitCode == 0)
                            Api.setEnabled(TvActivity.this, previouslyEnabled, false);
                          busy = false;
                          updateStatus();
                          message(
                              getString(R.string.tv_apply_failed),
                              getString(
                                  R.string.tv_rollback,
                                  original.exitCode,
                                  getString(
                                      previouslyEnabled
                                          ? R.string.tv_old_rules
                                          : R.string.tv_disabled_state),
                                  getString(
                                      state.exitCode == 0
                                          ? R.string.tv_engine_success
                                          : R.string.tv_recovery_failed),
                                  original.res == null ? "" : original.res.toString()));
                        });
                  }
                });
    if (previouslyEnabled) Api.applySavedIptablesRules(this, false, recovery);
    else Api.purgeIptables(this, false, recovery);
  }

  private void confirmDisable() {
    new AlertDialog.Builder(this)
        .setTitle(getString(R.string.tv_disable_title))
        .setMessage(getString(R.string.tv_disable_body))
        .setPositiveButton(
            getString(R.string.tv_disable_confirm),
            (d, w) -> {
              busy = true;
              status.setText(getString(R.string.tv_disabling));
              worker.execute(
                  () ->
                      Api.purgeIptables(
                          this,
                          false,
                          new RootCommand()
                              .setLogging(true)
                              .setCallback(
                                  new RootCommand.Callback() {
                                    @Override
                                    public void cbFunc(RootCommand state) {
                                      runOnUiThread(
                                          () -> {
                                            if (state.exitCode == 0)
                                              Api.setEnabled(TvActivity.this, false, false);
                                            busy = false;
                                            updateStatus();
                                            message(
                                                state.exitCode == 0
                                                    ? getString(R.string.tv_disabled_title)
                                                    : getString(R.string.tv_disable_failed),
                                                state.exitCode == 0
                                                    ? getString(R.string.tv_disabled_body)
                                                    : getString(
                                                        R.string.tv_disable_error, state.exitCode));
                                          });
                                    }
                                  })));
            })
        .setNegativeButton(getString(R.string.tv_cancel), null)
        .show();
  }

  private void inspect() {
    busy = true;
    status.setText(getString(R.string.tv_inspecting));
    worker.execute(
        () -> {
          try {
            Shell.Result r =
                Shell.cmd(
                        "id",
                        "iptables --version",
                        "ip6tables --version",
                        "iptables -S OUTPUT",
                        "ip6tables -S OUTPUT")
                    .exec();
            String output =
                android.text.TextUtils.join("\n", r.getOut())
                    + "\n"
                    + android.text.TextUtils.join("\n", r.getErr());
            runOnUiThread(
                () -> {
                  busy = false;
                  updateStatus();
                  message(getString(R.string.tv_inspect_title), output);
                });
          } catch (Exception e) {
            runOnUiThread(
                () -> {
                  busy = false;
                  updateStatus();
                  message(getString(R.string.tv_inspect_failed), e.toString());
                });
          }
        });
  }

  @Override
  public void onBackPressed() {
    if (busy) {
      Toast.makeText(this, getString(R.string.tv_busy), Toast.LENGTH_SHORT).show();
      return;
    }
    if (dirty)
      new AlertDialog.Builder(this)
          .setTitle(getString(R.string.tv_discard_title))
          .setPositiveButton(getString(R.string.tv_discard), (d, w) -> finish())
          .setNegativeButton(getString(R.string.tv_keep_editing), null)
          .show();
    else super.onBackPressed();
  }

  @Override
  protected void onDestroy() {
    worker.shutdown();
    super.onDestroy();
  }
}
