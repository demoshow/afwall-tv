# CI, signing and releases / 持续集成、签名与发布

This repository uses two GitHub Actions workflows:

| Workflow | Trigger | Output | Secrets |
| --- | --- | --- | --- |
| `.github/workflows/ci.yml` | push/PR to `main`, manual | tests, lint, debug APK artifact | none |
| `.github/workflows/release.yml` | `v*` tag, manual with a tag | signed release APK, source archive, `SHA256SUMS`, GitHub Release | four required signing secrets (+ optional keystore type) |

The CI workflow deliberately does **not** sign. Only a tag (or an explicit
manual run) uses the release keystore.

## 1. Signing secrets

Create these repository secrets under **Settings → Secrets and variables →
Actions** (or with `gh secret set`):

| Secret | Required | Meaning |
| --- | --- | --- |
| `ANDROID_KEYSTORE_BASE64` | yes | Base64 of the release keystore (`.p12`/`.jks`) |
| `ANDROID_KEYSTORE_PASSWORD` | yes | Keystore password |
| `ANDROID_KEY_ALIAS` | yes | Key alias inside the keystore |
| `ANDROID_KEY_PASSWORD` | yes | Key password (often equal to the keystore password) |
| `ANDROID_KEYSTORE_TYPE` | no | `PKCS12` (default) or `JKS` |

### Set them with the GitHub CLI

```sh
# Inspect the alias first; keytool prompts for the password.
keytool -list -v -keystore /secure/path/afwall-tv-release.p12 -storetype PKCS12

# macOS: base64 without line breaks. Linux: base64 -w0.
base64 -i /secure/path/afwall-tv-release.p12 | tr -d '\n' > /tmp/keystore.b64
gh secret set ANDROID_KEYSTORE_BASE64 --repo demoshow/afwall-tv < /tmp/keystore.b64
rm -f /tmp/keystore.b64

gh secret set ANDROID_KEYSTORE_PASSWORD --repo demoshow/afwall-tv
gh secret set ANDROID_KEY_ALIAS --repo demoshow/afwall-tv
gh secret set ANDROID_KEY_PASSWORD --repo demoshow/afwall-tv
gh secret set ANDROID_KEYSTORE_TYPE --repo demoshow/afwall-tv   # value: PKCS12
```

Never commit the keystore, `keystore.properties`, or the base64 blob. The
repository `.gitignore` already excludes `*.p12`, `*.jks`, `*.keystore`,
`keystore.properties` and `signing.properties`.

## 2. Release procedure

1. Review the tree and deliberately bump `tvVersionCode` / `tvVersionName` defaults
   in `app/build.gradle`. The tag must match `versionName`.
2. Run the local checks from [BUILDING.md](BUILDING.md) and the manual acceptance
   steps in [TESTING.md](TESTING.md); CI is not a device test.
3. Commit, tag and push:

```sh
git add -A
git commit -m "release: v1.0.0-tv"
git tag -a v1.0.0-tv -m "AFWall TV v1.0.0-tv"
git push origin main
git push origin v1.0.0-tv
```

The tag push starts `release.yml`. It will:

- check out the tag;
- run unit tests, lint and the unsigned release build;
- decode the keystore from the secret;
- zipalign and sign the APK with `apksigner`;
- verify the signature and print the public certificate digest;
- build `afwall-tv-<version>-source.zip` from the same tag;
- write `SHA256SUMS`;
- create (or update) the GitHub Release with the APK, the source archive and the
  checksums.

Manual run for an existing tag:

```sh
gh workflow run release.yml --repo demoshow/afwall-tv -f tag=v1.0.0-tv
gh run watch --repo demoshow/afwall-tv
```

## 3. Verify a release

```sh
gh release download v1.0.0-tv --repo demoshow/afwall-tv
sha256sum -c SHA256SUMS
"$ANDROID_HOME/build-tools/34.0.0/apksigner" verify --verbose --print-certs \
  afwall-tv-1.0.0-tv.apk
```

Publish the **public** certificate fingerprint from `apksigner verify
--print-certs`; never publish the private key or passwords.

## 4. Keystore and identity rules

- Back up the keystore and passwords offline. Losing them means you cannot update
  installations of this application ID; a new key is a new signing identity.
- The application ID is `io.github.demoshow.afwalltv`. A different ID is a separate
  installation, not an in-place update.
- The APK and the corresponding source must be published together. The release
  workflow attaches the source archive and uses the immutable tag.
- Do not claim byte-for-byte reproducibility. Reproducibility was not established;
  the signature and the tag are the trust anchors.
- If a key is exposed, rotate it, publish the new certificate and accept that
  existing installations cannot be updated by the new key.

## 5. Manual fallback

If Actions or secrets are unavailable, follow the offline procedure in
[RELEASING.md](RELEASING.md). It produces the same artifacts with local
`zipalign` and `apksigner`, and it requires the same source tag/corresponding
source discipline.

## 6. Scope and limitations

- CI uses Ubuntu, JDK 17, Android SDK 34 and Build Tools 34.0.0.
- CI does not exercise Magisk/root dialogs, real D-pad hardware, boot behavior or
  actual IPv4/IPv6 isolation. Record the devices and root managers you tested.
- The release workflow runs only for tags (or an explicit manual dispatch). It
  must not be used to publish an APK without the matching source tag.
- The release APK is signed with the maintainer key; it is not an official AFWall+
  release and is not endorsed by the upstream maintainers.
