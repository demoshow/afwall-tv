# Publication verification — 2026-10-22

Scope: source-only public snapshot, application version `1.0.0-tv`, version code
`20261022`, minimum SDK 24 and target/compile SDK 34.

## Completed

- Release build from the independent source directory using the included Gradle
  wrapper, JDK 17 and SDK 34. On this macOS host, SDK 36 AAPT2 was explicitly used
  as a host-tool override, as described in the build troubleshooting instructions.
- **93 JVM tests passed**, including Android 7 and Android 9 resource-resolution
  tests, English/Chinese language changes, Chinese region fallback, formatted
  strings and policy regressions.
- Android lint: **0 errors, 578 warnings**. Warnings remain and are not a security
  audit. Existing upstream translation lint exemptions are retained. The
  QUERY_ALL_PACKAGES exception is documented for per-app firewall enumeration.
- Translation contract: 60 TV strings and four policy labels; English/Chinese
  key parity, matching placeholders, Chinese script fallback, no hardcoded Chinese
  TV literals and no HOME intent in the manifest.
- Upstream top-level license retained verbatim. Attribution, exact upstream
  commit, modification dates and separately licensed native source recorded.
- Publication inventory excludes APKs, signing keys, SDK-local configuration,
  personal device addresses, private credentials, generated reports and caches.
- Initial-snapshot whitespace findings in inherited/vendor files were reviewed;
  the new TV code, translations and public documentation pass their focused
  whitespace checks. Upstream source was not broadly reformatted to hide findings.

## Not claimed

This publication did not install the update on a physical TV box, approve root,
apply firewall rules, use OBS or capture private device screens. Real remote
navigation, root-manager prompts, network isolation, boot restoration and native
helper compatibility still require the [manual procedure](TESTING.md).

Native helpers were inherited unchanged, not rebuilt or independently security
audited. Corresponding official source archives and upstream build modifications
are included, but byte-for-byte reproducibility is not established. No APK,
release signing key, GitHub Actions workflow or GitHub Pages site is included in
the public deliverable.

中文：本次完成独立源码构建、93 项测试、双语资源检查及发布文件审查，lint 无错误
但仍有 578 项警告。未对实体盒子安装、授予 root 或应用规则；遥控器、内核和真实
拦截效果需要人工验收。GitHub 仅发布源码，不上传 APK、私钥、流水线或网站。
