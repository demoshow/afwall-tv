# Release procedure and notes template

This is the manual fallback procedure. The normal path is the automated
`.github/workflows/release.yml` workflow described in
[CI-SIGNING.md](CI-SIGNING.md): a `v*` tag builds, signs and publishes the APK
together with the corresponding source. Use the steps below when Actions or
signing secrets are unavailable. Do not force-push or replace an existing
release tag.

## 1. Prepare the exact source revision

1. Merge the reviewed changes normally. Confirm a clean working tree and that
   `applicationId` is `io.github.demoshow.afwalltv`; keep Java package names as
   `dev.ukanth.ufirewall`.
2. Review and deliberately update `versionCode` and `versionName` for the release.
   The version code must increase for an in-place update of this application ID.
   A different application ID is a separate installation, not an automatic migration.
3. Verify upstream attribution, original copyright notices, `LICENSE`, `NOTICE.md`,
   `LICENSES/`, dependency terms, native sources/patches/configuration and the
   [third-party inventory](../THIRD_PARTY.md).
4. Confirm the screenshots in `docs/images/` are current and that the
   [upstream-link policy](UPSTREAM_LINKS.md) still matches the build. Do not add a
   TV-port purchase or unlocker route.

## 2. Build and verify

Use the toolchain in [BUILDING.md](BUILDING.md):

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleRelease --no-daemon
python3 scripts/verify_tv_resources.py
git diff --check
```

The release task produces an **unsigned** artifact at
`app/build/outputs/apk/release/app-release-unsigned.apk`. Do the relevant
[manual acceptance checks](TESTING.md), including D-pad operation, root prompts,
real IPv4/IPv6 LAN/WAN behavior, reopening, rebooting and disabling. Keep a local
recovery route. Unit tests and a successful rule-engine exit code are not enough
to certify isolation or kernel/native-tool compatibility.

Record the tested Android/device/root-manager combinations, test counts,
remaining lint warnings and known limitations. Do not imply untested platforms
were verified.

## 3. Create an immutable source tag

Choose the release tag and exact reviewed commit deliberately. For example,
substitute the intended values below rather than tagging an unreviewed worktree:

```sh
AFWALL_TAG=vX.Y.Z-tv
AFWALL_COMMIT=REPLACE_WITH_REVIEWED_FULL_COMMIT_SHA
git tag -a "$AFWALL_TAG" "$AFWALL_COMMIT" -m "AFWall+ TV $AFWALL_TAG"
git show --stat "$AFWALL_TAG"
git push origin "$AFWALL_TAG"
git archive --format=zip --prefix=afwall-tv/ \
  --output="afwall-tv-$AFWALL_TAG-source.zip" "$AFWALL_TAG"
```

The source tag must contain the source actually used for the binary. If the
tree changes after the build, rebuild and reverify from the tagged tree before
publishing. Do not move/overwrite an existing tag to hide a changed binary;
use a new version instead. Read back the remote tag before announcing it.

## 4. Sign locally with your own key, only when authorized

Keep the private keystore and passwords outside the repository. Use an existing
appropriate release key for updates; a differently signed APK cannot update a
previous installation of the same ID. This project does not supply a private key
or require a repository signing configuration.

After ensuring alignment (if needed), sign with Android SDK tools. `apksigner`
will prompt for the keystore credentials; do not put real passwords into example
commands, issue reports, logs, release notes or shell history:

```sh
zipalign -p -f 4 app/build/outputs/apk/release/app-release-unsigned.apk app-release-aligned.apk
apksigner sign --ks /secure/path/your-release-key.jks \
  --out afwall-tv-release.apk app-release-aligned.apk
apksigner verify --verbose --print-certs afwall-tv-release.apk
```

These are future operator instructions, not commands run by this maintenance
task. Inspect the final manifest/application ID, version, minimum SDK and
non-debuggable status. Compute the final SHA-256 and publish the public signing
certificate fingerprint for verification, never the private key. Archive the
exact build inputs privately as appropriate, excluding secrets from public source.

## 5. Publish corresponding source with every binary release

**If an APK is published, its complete corresponding source must be made public
at the same time. An APK-only release is not acceptable.** Include the immutable
source tag/commit, a source archive or an equally accessible complete source
location, relevant build/install instructions, native source/configuration/patches
and all required license notices. Follow each separately licensed component’s
terms as well as the application GPL terms.

For inherited native prebuilts, establish their corresponding source and build
modifications before redistribution. A matching version label alone is not proof;
if correspondence or required licensing material is unclear, stop the binary
release until resolved. Do not promise byte reproducibility when it was not shown.

Only after explicit approval to publish an APK, attach the verified signed binary,
its checksum, the corresponding-source archive and the notes below to the Release.
GitHub-generated source archives are useful but must not substitute for missing
native sources or other required source material. Finally read back the public
tag, Release notes and every asset/checksum, and confirm an unauthenticated user
can obtain both binary and source. Never upload the keystore, signing passwords,
private SDK settings or device logs.

## Release notes template / 发布说明模板

```markdown
# AFWall+ TV <version>

## Upstream / 上游
Based on upstream commit / 基于上游提交:
acfebf0f04c807acd7fe80b27d09c04e9b581e74
Upstream: https://github.com/ukanth/afwall
Unofficial TV adaptation; firewall core remains AFWall+ work.

## Corresponding source / 对应源码
- Source tag / 源码 tag: <exact-tag>
- Source commit / 源码提交: <full-commit-sha>
- Source tree: https://github.com/demoshow/afwall-tv/tree/<exact-tag>
- Complete source archive / 完整源码归档: <public-archive-link>
- Source archive SHA-256: <sha256>

## Changes / 变更
- <TV-specific changes and fixes>

## Requirements and migration / 要求与迁移
- Root required; Android 7.0/API 24+ and compatible kernel/native tools.
- Application ID: io.github.demoshow.afwalltv
- Java namespace unchanged: dev.ukanth.ufirewall
- Different application IDs do not automatically share data or Tasker actions.
- Do not enable this app and upstream AFWall+ at the same time (shared chains).

## Verification / 验证
- Automated tests: <result/count>; lint: <errors/warnings>
- Physical devices, Android versions and root managers: <actually tested list>
- LAN/WAN, IPv4/IPv6, D-pad, reboot and disable checks: <actual results>
- Known limitations: <DNS/loopback/proxy exceptions, boot window, shared UID,
  advanced-rule interactions and other unresolved items>

## Binary and self-signing / 二进制与自行签名
- APK: <approved-asset-link, or explicitly state source-only>
- APK SHA-256: <sha256, if published>
- Public signing-certificate fingerprint: <fingerprint, if published>
- Build/self-signing instructions: docs/BUILDING.md and docs/RELEASING.md
- Use your own private keystore; signing credentials are never provided here.

## License / 许可证
GPL-3.0, with later-version permission where present in original file notices;
third-party components retain their terms. See LICENSE, NOTICE.md and THIRD_PARTY.md.
```

## 中文操作要点

先完成代码审查、版本号更新、构建和真实设备验证，再将准确源码提交打为不可
覆盖的 tag。自行签名使用仓库外的私钥，按工具提示输入凭据，本任务不实际执行
签名。只有得到发布二进制的明确授权后才上传 APK；必须同步公开该 APK 的完整
对应源码、准确 tag/提交、原生源码和必要构建配置、许可声明及校验值。发布后回读
验证公共下载，不发布私钥，不强推，不用改动旧 tag 隐藏修订。
