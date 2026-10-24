/* AFWall+ TV additions, GPL-3.0-or-later. Modified 2026-10-18.  * Modified by demoshow for AFWall+ TV
 */
package dev.ukanth.ufirewall.util;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import dev.ukanth.ufirewall.R;

/** Focus support only: never reads or writes firewall policy. */
public final class TvRemoteNavigation {
  private TvRemoteNavigation() {}

  public static boolean isTelevision(Context context) {
    return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK)
        == Configuration.UI_MODE_TYPE_TELEVISION;
  }

  public static void prepare(View view) {
    if (!isTelevision(view.getContext())) return;
    if (view instanceof ListView) {
      ((ListView) view).setItemsCanFocus(true);
      ((ListView) view).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
    }
    if (view.isClickable() && view.getTag(R.id.tv_remote_prepared) == null) {
      view.setTag(R.id.tv_remote_prepared, Boolean.TRUE);
      view.setFocusable(true);
      // Preserve native button/checkbox drawables; only add a focus background.
      if (!(view instanceof ViewGroup)) {
        view.setBackgroundResource(R.drawable.tv_remote_focus);
      }
    }
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++) prepare(group.getChildAt(i));
    }
  }
}
