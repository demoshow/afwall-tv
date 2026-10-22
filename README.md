# AFWall+ TV

[English](README.md) · [简体中文](README.zh-CN.md)

**AFWall+ TV is a TV-focused adaptation of
[AFWall+](https://github.com/ukanth/afwall), not a firewall built from scratch.**

> **The firewall core and the substantial majority of this application come from
> AFWall+.** Credit for the upstream application, rule engine and established
> capabilities belongs to Umakanthan Chandran and the AFWall+ contributors,
> building on earlier work by Rodrigo Zechin Rosauro and other authors credited
> in the source. This project’s additions focus on the TV interface, remote
> navigation, policy presentation and localization.

**Upstream project:** [ukanth/afwall](https://github.com/ukanth/afwall) ·
**Upstream documentation:** [AFWall+ Wiki](https://github.com/ukanth/afwall/wiki)

**Unofficial derivative · Android 7.0+ · Root required · GPLv3**

Thank you to the AFWall+ maintainers and contributors for the implementation and
ongoing work that make this adaptation possible. If this project is useful to
you, please also recognize and support
[the original project](https://github.com/ukanth/afwall#support-afwall-development).
This is an independent, unofficial source distribution, not a claim of original
authorship of the inherited application or an assurance of complete network isolation.

## What comes from AFWall+, and what this project adds

### Inherited from AFWall+

The application architecture, iptables/ip6tables rule generation and application,
UID-based network filtering, IPv4/IPv6 handling, network-type configuration,
profiles, logging, boot restoration, advanced settings and much of the existing
UI, translations and test suite are upstream AFWall+ work. The bundled native
tools also come from upstream and their respective authors. **These are not
features newly implemented by AFWall+ TV.**

### TV-specific additions and adaptations

- **Three explicit policies:** Unrestricted, LAN only, and Blocked, shown by app
  UID group instead of ambiguous allow/block checkboxes.
- **Remote control support:** D-pad list/toolbar navigation, OK policy selection,
  Back/discard confirmation, and visible focus in the advanced interface.
- **Shared/core UID handling:** packages sharing a UID are grouped; Android,
  maintenance and this app’s own UIDs are protected in the TV interface.
- **English and Chinese TV resources:** follows the system language. The TV layer
  uses Simplified Chinese for Chinese locales and English otherwise. Existing
  upstream translations remain available in advanced screens.
- **Explicit application:** editing a selection creates a draft. Applying and
  enabling requires confirmation and root authorization through the root manager.
- **Advanced configuration retained:** existing allowlist/blocklist mode, scripts,
  direct rules and Tor settings are not silently replaced by a new UI. These
  capabilities are inherited; the TV-specific work is their policy mapping and
  integration with the simplified interface.

The three-policy interface delegates enforcement to AFWall+; it does not replace
or independently reimplement the upstream firewall engine. Additional regression
and localization tests cover the adaptation, alongside the retained upstream tests.

## Policies and their limits

| TV policy | Intended direct app traffic |
| --- | --- |
| Unrestricted | No additional restriction from the TV policy |
| LAN only | Allow the engine’s LAN/private/discovery destinations; restrict internet traffic on Wi-Fi/Ethernet, mobile and VPN paths |
| Blocked | Restrict direct LAN and WAN traffic, subject to inherited engine exceptions |

These labels describe intended rules, **not absolute guarantees**. Loopback,
DNS, system proxies, root privileges, shared UIDs, VPN routing, kernel behavior,
advanced rules and boot timing can change observed results. “LAN only” is not an
allowlist for one chosen NAS or IP address. Packages sharing a UID cannot have
independent policies. New apps follow the active upstream mode: allowed by
default in blocklist mode, blocked by default in allowlist mode.

The advanced screen retains upstream checkbox semantics: checked means allowed
in allowlist mode and blocked in blocklist mode. The TV screen translates those
selections so its three labels keep the same meaning in either mode.

## Requirements and first use

1. Use Android 7.0/API 24 or newer, a compatible root manager and a kernel with
   working iptables/ip6tables support. A minimum SDK is not a device certification.
2. Build and sign the app yourself using [BUILDING.md](docs/BUILDING.md). This
   initial GitHub publication is source-only; no APK or signing key is uploaded.
3. Open **AFWall+ TV**, select a nonessential app, press OK and choose a policy.
4. Select **Apply rules** and read the confirmation. Grant root yourself when
   prompted. On application, LAN/IPv6/VPN/roaming/tethering columns are enabled
   to represent the policies; existing Tor settings and advanced mode remain.
5. Verify LAN/WAN and IPv4/IPv6 behavior on that device. **Disable firewall**
   removes this app’s rules and retains the saved selections.

Do not enable this app and upstream AFWall+ simultaneously: both use the same
iptables chains. Keep local recovery access, avoid restrictive system/root/ADB
rules, and disable the firewall before uninstalling. See [SECURITY.md](SECURITY.md).

## Build and test

JDK 17, Android SDK 34, Build Tools 34.0.0, Gradle 8.7 and AGP 8.5.0 are used.
The wrapper is included with a distribution checksum.

```sh
git clone https://github.com/demoshow/afwall-tv.git
cd afwall-tv
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleRelease --no-daemon
python3 scripts/verify_tv_resources.py
```

The release APK is unsigned by default. Keep signing credentials outside the
repository. The application ID is `io.github.demoshow.afwalltv`; it installs
separately from previous local TV builds and upstream AFWall+. Existing app data
is not automatically migrated. The project is not tied to one TV box model. First builds
download dependencies. Native helpers inherited from upstream are included;
their sources, modifications and licenses are documented in
[THIRD_PARTY.md](THIRD_PARTY.md). No claim of byte-reproducible native builds is made.

Policy regression tests and Android resource-resolution tests are included.
They cover the default custom-rule-flag regression, policy conversions, core UID
protection, English/Chinese selection and fallback. They do not replace hardware,
root, boot or real network-filtering tests. Follow the
[manual acceptance checklist](docs/TESTING.md).

## Source layout

```text
app/src/main/java/dev/ukanth/ufirewall/
  TvActivity.java                  TV interface and explicit apply flow
  tv/TvPolicy.java                 Mode-independent policy mapping
  util/TvRemoteNavigation.java     Advanced-interface D-pad focus support
  Api.java                        Inherited firewall engine
app/src/main/res/values*/          Upstream and TV language resources
app/src/test/                     Upstream, policy and localization tests
external/                         Native source and configuration
third_party/sources/              Official native source archives and hashes
docs/                             Build, acceptance and upstream provenance
```

## Privacy and external dependencies

The TV additions introduce no analytics service, remote-control endpoint or
cloud account requirement. The inherited application still contains optional
external links and DNS/hostname lookup features; this is not an audited claim
that all upstream behavior is offline. Root authorization is managed by the
installed root manager, not a service provided by this repository.

The build resolves dependencies from the repositories declared in Gradle;
Robolectric may download Android framework artifacts for tests. Do not share
unredacted inventories or logs in public issues. Report TV-specific problems in
[this repository](https://github.com/demoshow/afwall-tv/issues), not to upstream
unless reproduced there independently.

## License, provenance and contributions

Based on upstream commit
[`acfebf0f04c807acd7fe80b27d09c04e9b581e74`](https://github.com/ukanth/afwall/tree/acfebf0f04c807acd7fe80b27d09c04e9b581e74).
Original copyright and license notices are retained. The application remains
under GNU GPL version 3, with later-version permission where present in the
original notices; separately licensed components retain their terms.

This repository intentionally started from a fresh source snapshot rather than
importing the previous Git history, and is not a GitHub-network fork. **That
repository layout does not make the inherited code original work of this
project.** Attribution, copyright and copyleft obligations remain unchanged.
See [LICENSE](LICENSE), [NOTICE.md](NOTICE.md), [THIRD_PARTY.md](THIRD_PARTY.md),
and the preserved [upstream README](docs/upstream/README.md).
Contributions are described in [CONTRIBUTING.md](CONTRIBUTING.md).
