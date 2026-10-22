# Third-party components

The top-level GPL license does not relicense separately licensed components.
Original notices in individual files and dependency artifacts take precedence.
This is a source/provenance inventory, not a declaration that every dependency
has undergone a security audit.

| Component | Version/source | License and location |
| --- | --- | --- |
| AFWall+ application | `ukanth/afwall` at `acfebf0f04c807acd7fe80b27d09c04e9b581e74` | GPL-3.0, with later-version permission in applicable headers; `LICENSE`, source notices |
| BusyBox executable resources | 1.36.1, inherited unchanged from that snapshot | GPL-2.0; official source archive including its license under `third_party/sources/`; modifications/configuration in `build-binaries.sh` and `external/` |
| iptables/ip6tables executable resources | 1.8.10 legacy, inherited unchanged | GPL-2.0; official source archive and COPYING under `third_party/sources/`; build script retained |
| NFLOG helper | `external/nflog/`, inherited executable resources | MPL-2.0 for `nflog.c`; libmnl portions LGPL-2.1-or-later; original per-file notices retained |
| Locale/Tasker integration | `com/twofortyfouram/` and plugin sources | Apache-2.0; original notices retained |
| libsu | 6.0.0 | Apache-2.0; fetched by Gradle from the declared upstream dependency |
| AndroidX / Material Components | Versions declared in `app/build.gradle` | Apache-2.0; licenses retained in resolved artifacts |
| DBFlow | 4.2.4 | MIT; Gradle dependency |
| Material Dialogs | 0.9.6.0 | Apache-2.0; Gradle dependency |
| RxJava | See `app/build.gradle` | Apache-2.0; Gradle dependency |
| dnsjava | 3.6.5 | BSD-3-Clause; Gradle dependency |
| Android Lock Pattern | 8.0.4 | Apache-2.0; Gradle dependency |
| JUnit, Robolectric, org.json | See `app/build.gradle`; test-only | Their upstream licenses apply; not part of the release APK runtime |

## Native source availability

Official source archives for the inherited BusyBox and iptables versions are
provided alongside the source tree, with retrieval URLs and hashes in
`third_party/sources/README.md`. Native build modifications and the NFLOG source
are included. The existing binary files were not recompiled for this release;
byte-for-byte reproducibility has not been established. The legacy
`external/Makefile` describes older tool versions; use the documented modern
`build-binaries.sh` path when evaluating native rebuilds, not that legacy path.

Do not copy the inherited APK/native resources into another binary release and
discard the accompanying sources, modifications, or license notices. A future
binary release must make its complete corresponding source available and
review all component-specific obligations.

## 中文说明

主许可证不覆盖或替换第三方组件自己的授权。仓库保留原作者及文件内声明，附上所
继承 BusyBox/iptables 版本的官方源码归档、原生构建修改和 NFLOG 源码。常规 APK
构建沿用上游工具，并不重新编译它们；本次不声称位级可复现或完成安全审计。
发布二进制时仍须同时提供完整对应源码及各组件必要的许可声明。
