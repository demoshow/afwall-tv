# Build and verification

## Requirements

- JDK 17.
- Android SDK Platform 34 and Android SDK Build Tools 34.0.0.
- Gradle 8.7, supplied by the checked-in wrapper; AGP 8.5.0.
- Internet access on the first build for Gradle, Google Maven, Maven Central,
  JitPack, and the dependency repositories declared in `build.gradle`.
- The JVM localization tests use Robolectric, which also resolves Android test
  framework artifacts on the first run. They require neither root nor a TV box.

Set `ANDROID_HOME` to your SDK installation, or create an untracked
`local.properties` with `sdk.dir`. Do not commit local paths or signing material.

```sh
git clone https://github.com/demoshow/afwall-tv.git
cd afwall-tv
./gradlew --version
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleRelease --no-daemon
```

On Windows, use `gradlew.bat`. The release task produces an **unsigned** APK at
`app/build/outputs/apk/release/app-release-unsigned.apk`. A development build is
available with `./gradlew :app:assembleDebug`. Sign distribution builds using
your own private keystore outside the repository and verify them with Android
SDK `apksigner`. No private key, release credential, or automatic signing policy
is provided here. Builds signed by different keys cannot update one another.

If the Maven-provided AAPT2 cannot run on your host, use a compatible locally
installed SDK AAPT2 explicitly, for example:

```sh
./gradlew :app:assembleRelease \
  -Pandroid.aapt2FromMavenOverride="$ANDROID_HOME/build-tools/34.0.0/aapt2"
```

The project preserves the existing `local.cm311.afwalltv` application ID for
configuration/update compatibility; it is not tied to a particular board.
`minSdk` is 24 (Android 7.0), and `targetSdk` is 34. This minimum is not proof of
correct kernel filtering or native-tool operation on every Android 7 device.
The ARM, ARM64, and x86 native tool resources are inherited from upstream;
there is no independently supplied x86_64-native helper set.

## Tests

`TvPolicyTest` exercises policy mapping, allowlist/blocklist round trips,
core/shared UID-related protection, and the custom-rule flag regression.
`TvLocalizationTest` uses actual Android resource resolution to check English,
Chinese region variants, non-Chinese fallback, formatted messages, and language
changes. The remaining tests are inherited upstream tests.

Static checks and JVM tests do not exercise Magisk dialogs, real D-pad hardware,
boot behavior, or actual IPv4/IPv6 network isolation. See
[TESTING.md](TESTING.md) for the manual procedure.

## Native helpers

The normal Gradle build uses the inherited helper resources. To investigate a
native rebuild, review `build-binaries.sh` and the sources/configuration in
`external/` first. It is a **Linux-only, opt-in** toolchain setup and downloads
an Android NDK; it is not run by Gradle. It may require additional fixes on a
given build host. This publication does not claim that native rebuilds were
executed or are byte-for-byte reproducible.

## 中文摘要

使用 JDK 17、Android SDK 34、Build Tools 34.0.0 和仓库内 Gradle wrapper。
执行上面的测试、lint 和 Release 构建命令。Release 默认不签名，私钥由构建者自行
保管，不应放进仓库。首次构建和 Robolectric 测试需要下载依赖。最低 Android 7
只是安装门槛，并不代替实际内核、原生工具、root 及网络拦截兼容性测试。
