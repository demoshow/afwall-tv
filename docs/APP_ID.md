# Application identity and coexistence

The installed application ID is `io.github.demoshow.afwalltv`. The Java/resource
namespace stays `dev.ukanth.ufirewall` so source paths, imports, component class
names and upstream merges do not require a package rename.

## Namespaced identifiers

- The profile ContentProvider uses the manifest `${applicationId}` placeholder;
  dependency providers are also resolved by manifest merging.
- Application-specific status, request, UI, theme, notification and log-service
  actions/extras use `BuildConfig.APPLICATION_ID`. Manifest receiver filters use
  the matching `${applicationId}` values.
- Tasker/Locale plugin Bundle keys use `BuildConfig.APPLICATION_ID`; the shared
  `com.twofortyfouram.locale.*` discovery/actions and standard protocol constants
  are intentionally unchanged, otherwise plugin discovery would stop working.
- Static shortcut target packages use a Gradle-generated `tv_application_id`
  string resource. Their target **class** names remain in the Java namespace.
- Android framework actions, Java reflection/class names, fragment names, and
  explicit references to the separately installed upstream donation/unlocker
  products are not identifiers owned by this app. They are not blindly renamed.
  Upstream donation/unlocker references are retained for attribution and upstream
  compatibility; see [UPSTREAM_LINKS.md](UPSTREAM_LINKS.md). This TV port does not
  sell an unlocker.

This change does not modify signing keys, signing algorithms/configuration, the
Tasker action-signing implementation, or the inherited iptables chain names.

## Migration and limitations

The new ID is a separate Android application, not an in-place update of a build
with a different ID. Preferences, root-manager grants and saved Tasker actions
do not automatically move across application IDs; recreate/review them in the
new installation. Do not assume exported profiles are interchangeable without
reviewing them.

It can be installed alongside upstream AFWall+ without a provider/shortcut
package collision, but **do not enable both firewalls at once**: their inherited
rule chains are shared. Disable the old firewall first and retain local recovery
access. Namespace tests are not a physical-device installation or root test.

中文：新安装标识为 `io.github.demoshow.afwalltv`，Java 包名仍为
`dev.ukanth.ufirewall`。私有广播和插件键随新标识生成；公共 Tasker/Locale 协议、
类名以及指向原版捐赠/解锁应用的外部引用不盲目替换。不同包名不会自动迁移数据、
root 授权或 Tasker 动作。可以共存安装，但不得同时启用防火墙规则。
