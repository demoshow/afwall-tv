# Upstream store, donations, unlockers and contacts — decision inventory

## Status / 状态

**Review only: no listed code, asset, contact, donation route or unlocker behavior
has been removed or changed by this inventory task. Owner approval is required.**

This is a source/reference inventory, not a payment, store-availability, licensing
entitlement or reachability test. Line numbers may shift; the named file/symbol
is the stable reference. Original copyright and license notices are out of scope
for removal, regardless of the decisions below.

## 1. `playstore/` inventory

All five inherited files are retained. Their filenames/dimensions describe legacy
upstream store artwork, not screenshots of the TV derivative.

| File | Dimensions | Bytes |
| --- | --- | --- |
| `playstore/feature.png` | 1024 × 500 | 79749 |
| `playstore/playstore.png` | 512 × 512 | 13126 |
| `playstore/promo-graphic.png` | 180 × 120 | 11359 |
| `playstore/screenshots/Main_2.0.png` | 1200 × 1920 | 288613 |
| `playstore/screenshots/Settings_2.0.png` | 1200 × 1920 | 200357 |

**Owner decision:** retain as clearly labeled upstream historical assets, or remove
`playstore/` from this derivative. No decision has been applied.

## 2. Actual donation, product and contact destinations

| Location / symbol | Existing destination or behavior | Owner decision needed |
| --- | --- | --- |
| `app/src/main/java/dev/ukanth/ufirewall/Api.java`, `donateDialog()` | Opens `market://search?q=pub:ukpriya` after the purchase button; uses donation-only resource strings | Retain as explicit upstream-support link, or remove/replace this route? |
| `app/src/main/java/dev/ukanth/ufirewall/util/G.java`, `hasDonateBuild()` | Compares against the external upstream donor application ID `dev.ukanth.ufirewall.donate` | Preserve upstream edition detection, or remove later? |
| Same file, `hasDonateKey()` / `isDoKey()` | Looks for `dev.ukanth.ufirewall.donatekey`, caches `ipurchaseddonatekey` | Keep existing unlocker integration, or remove/rework later? No compatibility/entitlement promise is made. |
| `app/src/main/java/dev/ukanth/ufirewall/activity/RulesActivity.java`, `MENU_SEND_REPORT` | Email chooser prefilled with `afwall-report@googlegroups.com`; subject `AFWall+ problem report - v…`; body contains displayed rules and problem text | Keep upstream recipient, remove prefilled recipient, or later route TV reports to this project? Review privacy before sending. |
| `app/src/main/res/raw/about.html`, Developers section | `mailto:cumakt+afwall@gmail.com` (Umakanthan Chandran) | Retain as historical contact, or remove contact link while keeping attribution? |
| Same file, Developers section | `mailto:cernekee@gmail.com` (Kevin Cernekee) | Same decision; retaining a resource does not prove the current Help screen links to it. |
| Same HTML resource, Report Issues section | `https://github.com/ukanth/afwall/issues`; the adjacent Wiki/source links also point to upstream | Retain as clearly labeled historical links, or later route TV-specific reports to this project? |
| `docs/upstream/README.md`, Support AFWall+ Development | PayPal hosted button `6E4VZTULRB8GU` at `https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=6E4VZTULRB8GU` and its PayPal image | Keep in the preserved upstream snapshot, or edit the historical document with an explicit change notice? |
| Same upstream README | Google Play: `https://play.google.com/store/apps/details?id=dev.ukanth.ufirewall.donate` | Same decision; do not imply buying this unlocker supports/unlocks this derivative. |
| Same upstream README | Amazon gift-card address: `cumakt+amazon at gmail.com` | Same decision |
| Same upstream README | Bitcoin: `bc1q54nf3y9zmdcpasxx9sywkprd6309rfhav3mape` | Same decision |
| Same upstream README | Ethereum: `0x5e65649C2B26eD816fCeD25a8E507C90D4b1D697` | Same decision |
| Same upstream README | Receipt/unlocker contact: `contact@portgenix.com` | Same decision |
| `README.md` and `README.zh-CN.md`, opening upstream relationship section | `https://github.com/ukanth/afwall#support-afwall-development` | Keep as the single explicit upstream-support link, or remove the support link while keeping required source/attribution? |

## 3. About screen and feature-gate call sites

`activity/HelpActivity.java`, `setupContent()`, conditionally adds the `(Donate)`
badge and `donate_thanks`; the active source/issues links already point to this
TV repository, while the Wiki points to upstream. Those links were not changed
by this inventory. `raw/about.html` is a separately retained HTML resource with
the developer mailto links listed above.

All source files calling or defining the donation/unlocker gate methods are:

| Source file | Matching line numbers at inventory time |
| --- | --- |
| `app/src/main/java/dev/ukanth/ufirewall/Api.java` | 3941 |
| `app/src/main/java/dev/ukanth/ufirewall/MainActivity.java` | 917, 931, 1349, 1356, 1359, 1434, 1441 |
| `app/src/main/java/dev/ukanth/ufirewall/activity/HelpActivity.java` | 49 |
| `app/src/main/java/dev/ukanth/ufirewall/activity/LogActivity.java` | 163, 168 |
| `app/src/main/java/dev/ukanth/ufirewall/activity/ProfileActivity.java` | 128, 168 |
| `app/src/main/java/dev/ukanth/ufirewall/broadcast/PackageBroadcast.java` | 183 |
| `app/src/main/java/dev/ukanth/ufirewall/preferences/LogPreferenceFragment.java` | 90 |
| `app/src/main/java/dev/ukanth/ufirewall/preferences/SecPreferenceFragment.java` | 116 |
| `app/src/main/java/dev/ukanth/ufirewall/preferences/UIPreferenceFragment.java` | 30 |
| `app/src/main/java/dev/ukanth/ufirewall/util/G.java` | 407, 1117, 1121, 1130, 1131, 1134 |
| `app/src/main/java/dev/ukanth/ufirewall/util/SecurityUtil.java` | 45, 60 |

These include profile/widget restrictions, import/export options, log/hostname
options, device-security choices, theme/default-connection entitlement checks
and the About badge. Deciding to remove a purchase link is **not** automatically
authorization to rewrite feature entitlements or security checks.

Additional configuration references:

- `app/build.gradle`: commented upstream donate application ID and `DONATE=false` build field.
- `app/src/main/AndroidManifest.xml`: inactive donor icon/label examples.
- `app/src/main/res/values/arrays.xml`: theme labels marked `(Donate)`.
- `util/BackupCodec.java` and `util/BackupCodecTest.java`: exclude the cached
  `ipurchaseddonatekey` flag from portable backups; this is not a payment route.
- Donation icon resources below are retained; do not assume they are active.

## 4. Donation-related resources

Retained donor-specific asset/resource filenames:

- `app/src/main/ic_launcher_donate-web.png`
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher_donate.xml`
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher_donate_round.xml`
- `app/src/main/res/mipmap-hdpi/ic_launcher_donate.png`
- `app/src/main/res/mipmap-hdpi/ic_launcher_donate_round.png`
- `app/src/main/res/mipmap-hdpi/ic_launcher_foreground_donate.png`
- `app/src/main/res/mipmap-mdpi/ic_launcher_donate.png`
- `app/src/main/res/mipmap-mdpi/ic_launcher_donate_round.png`
- `app/src/main/res/mipmap-mdpi/ic_launcher_foreground_donate.png`
- `app/src/main/res/mipmap-xhdpi/ic_launcher_donate.png`
- `app/src/main/res/mipmap-xhdpi/ic_launcher_donate_round.png`
- `app/src/main/res/mipmap-xhdpi/ic_launcher_foreground_donate.png`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher_donate.png`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher_donate_round.png`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher_foreground_donate.png`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher_donate.png`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher_donate_round.png`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground_donate.png`
- `app/src/main/res/values/ic_launcher_background_donate.xml`

All locale files containing the reviewed donation/entitlement string keys:

| Resource file | Keys |
| --- | --- |
| `app/src/main/res/values/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_only_short`, `donate_thanks`, `export_all_warning`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ar/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ast-rES/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-az/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-bg/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-bi/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-bn/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-bs/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ca/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-cs/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-da/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-de/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-el/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-es/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-eu/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-fa/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-fi/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-fr/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-he/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-hi/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-hr/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-hu/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-in/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-it/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ja/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-kn/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ko/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ku/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ky/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ml-rIN/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ms/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-nb/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-nl/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-pl/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-pt/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-pt-rBR/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ro/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ru/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-si/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-sk/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-sl/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-sr/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-sr-rCS/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-sv/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ta/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-th/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-tr/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-uk/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-ur-rPK/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-vi/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-zh/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-zh-rCN/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |
| `app/src/main/res/values-zh-rTW/strings.xml` | `app_name_donate`, `buy_donate`, `default_connection_setting_allow`, `default_connection_setting_block`, `donate_only`, `donate_thanks`, `pref_device_desc`, `showhost_desc` |

These translated labels are not additional payment endpoints. Locale copies must
be reviewed together if their behavior is changed later. Native/third-party
copyright author emails are attribution, not application support routes, and
are not proposed for deletion.

## 5. Pending owner decisions / 待所有者决定

- [ ] Keep or remove the five `playstore/` assets.
- [ ] Keep a clearly labeled upstream-support link, or remove donation/purchase navigation.
- [ ] Keep or remove/rework upstream donor/unlocker checks (separate from link removal).
- [ ] Keep, remove or reroute the prefilled upstream rules-report recipient.
- [ ] Keep historical developer contact links and donation details in retained
  upstream documents/resources, or remove those links while preserving authorship.

中文：本清单只报告，不修改代码、不删除商店素材，也没有点击支付、验证解锁或
发送邮件。尤其需要确认规则报告邮箱：它会把规则内容交给邮件客户端，目标仍是
上游邮件组。是否保留商店素材、上游捐赠链接、解锁机制及联系方式，均等待所有者
选择；删除链接不等于授权绕过功能门槛，更不涉及删除版权或许可证。
