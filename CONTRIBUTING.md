# Contributing

Please keep changes scoped and describe their effect on the TV interface and
the upstream firewall engine separately. Include the device/Android/root-manager
combination used for any runtime claim. Avoid unnecessary changes to upstream
policy-generation code.

Before proposing a change:

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleRelease --no-daemon
python3 scripts/verify_tv_resources.py
git diff --check
```

Add TV user-visible strings to both `values/tv_strings.xml` (English fallback)
and `values-zh/tv_strings.xml` (Simplified Chinese). Keep format placeholders in
sync, verify long English labels on a TV, and test D-pad focus and Back/Cancel
paths. Do not hardcode a launcher, device IP address, root approval, or a signing
key. Do not submit generated APKs, local SDK settings, private logs or credentials.

Respect existing per-file copyright/license notices. New application changes
must remain compatible with the project’s GPL terms. Report bugs in this
repository, not upstream, unless reproduced in an unmodified upstream build.

## 中文

请说明修改范围、测试设备及风险，优先保持上游规则引擎不变。新增界面文字必须
同时提供中英文资源，保持格式参数一致，并验证方向键、返回和取消路径。不得提交
APK、签名私钥、SDK 本机路径、未脱敏日志或凭据；问题先在本仓库报告。
