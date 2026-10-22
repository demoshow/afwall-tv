/* AFWall+ TV additions, GPL-3.0-or-later. */
package dev.ukanth.ufirewall.tv;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import dev.ukanth.ufirewall.Api;
import dev.ukanth.ufirewall.BuildConfig;
import dev.ukanth.ufirewall.R;
import dev.ukanth.ufirewall.plugin.PluginBundleManager;
import dev.ukanth.ufirewall.service.LogService;
import dev.ukanth.ufirewall.widget.WidgetActionActivity;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class AppIdentityTest {
    private static final String ID = "io.github.demoshow.afwalltv";

    @Test public void applicationIdChangesWithoutRenamingTheJavaNamespace() {
        assertEquals(ID, BuildConfig.APPLICATION_ID);
        assertEquals(ID, RuntimeEnvironment.getApplication().getPackageName());
        assertEquals("dev.ukanth.ufirewall", Api.class.getPackage().getName());
    }

    @Test public void mergedProvidersBelongToTheInstalledApplication() throws Exception {
        PackageInfo info = RuntimeEnvironment.getApplication().getPackageManager()
                .getPackageInfo(ID, PackageManager.GET_PROVIDERS);
        assertNotNull(info.providers);
        boolean profileProvider = false;
        for (ProviderInfo provider : info.providers) {
            assertTrue(provider.authority, provider.authority.startsWith(ID));
            if (provider.name.endsWith("ShareProfilePreference")) {
                assertEquals(ID, provider.authority);
                profileProvider = true;
            }
        }
        assertTrue(profileProvider);
    }

    @Test public void privateActionsAndExtrasUseTheApplicationId() {
        String[] identifiers = {Api.STATUS_CHANGED_MSG, Api.TOGGLE_REQUEST_MSG,
                Api.CUSTOM_SCRIPT_MSG, Api.STATUS_EXTRA, Api.SCRIPT_EXTRA, Api.SCRIPT2_EXTRA,
                LogService.ACTION_GRACEFUL_SHUTDOWN, LogService.ACTION_CHANGE_LOG_TARGET,
                WidgetActionActivity.EXTRA_ACTION};
        for (String identifier : identifiers) assertTrue(identifier, identifier.startsWith(ID + "."));
    }

    @Test public void statusReceiverFiltersMatchTheNamespacedStatusAction() {
        Intent intent = new Intent(Api.STATUS_CHANGED_MSG).setPackage(ID);
        assertEquals(3, RuntimeEnvironment.getApplication().getPackageManager()
                .queryBroadcastReceivers(intent, 0).size());
    }

    @Test public void pluginBundleKeysAreNamespacedButTheLocaleProtocolIsStable() throws Exception {
        for (Field field : PluginBundleManager.class.getDeclaredFields()) {
            if (field.getType() == String.class && Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                String value = (String) field.get(null);
                String prefix = field.getName().startsWith("TASKER_")
                        ? "net.dinglisch.android.tasker." : ID + ".";
                assertTrue(field.getName(), value.startsWith(prefix));
            }
        }
        assertEquals("com.twofortyfouram.locale.intent.action.EDIT_SETTING",
                com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING);
        assertEquals("com.twofortyfouram.locale.intent.action.FIRE_SETTING",
                com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING);
    }

    @Test public void staticShortcutsTargetThisAppAndKeepTheOriginalClassNames() throws Exception {
        Application app = RuntimeEnvironment.getApplication();
        int count = 0;
        try (XmlResourceParser xml = app.getResources().getXml(R.xml.shortcuts)) {
            for (int event = xml.next(); event != XmlResourceParser.END_DOCUMENT; event = xml.next()) {
                if (event != XmlResourceParser.START_TAG || !"intent".equals(xml.getName())) continue;
                String ns = "http://schemas.android.com/apk/res/android";
                int packageResource = xml.getAttributeResourceValue(ns, "targetPackage", 0);
                assertEquals(ID, app.getString(packageResource));
                assertTrue(xml.getAttributeValue(ns, "targetClass").startsWith("dev.ukanth.ufirewall."));
                count++;
            }
        }
        assertEquals(3, count);
    }
}
