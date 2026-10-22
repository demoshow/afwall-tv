# Manual acceptance checklist / 人工验收

Perform these checks on a test device with local access and a recovery route.
Never start by restricting the system UID, root, the firewall itself, your
launcher, or remote-maintenance components.

1. Open the TV entry. Confirm that app labels and shared UID grouping are shown.
2. Use Up/Down to navigate rows, OK to open the policy dialog, and Back to cancel.
   Check toolbar focus and the advanced interface checkboxes with a real D-pad.
3. Choose a policy and leave the page without applying: a discard prompt should
   appear. Cancel it to continue editing; no firewall should be enabled merely
   by viewing the page or editing a draft.
4. With no configured scripts/direct rules, the default enabled customization
   flag must not prevent the apply confirmation. If actual customizations exist,
   inspect the warning and the advanced interface before proceeding.
5. Switch the device language between English and Chinese, then reopen the app.
   Verify policy names, toolbar, dialogs, errors and status. The TV layer has
   English and Simplified Chinese resources; other locales fall back to English.
   Upstream advanced screens retain their existing translations.
6. On an isolated test setup, grant root yourself and apply one policy to a
   nonessential app. Test unrestricted access, LAN-only access and blocking
   against known LAN and internet endpoints, for both IPv4 and IPv6.
7. Repeat after reopening and rebooting. Boot restoration is inherited and is
   not a guarantee of zero traffic before rules are restored.
8. Disable the firewall and confirm connectivity is restored. Saved policies
   should remain, and other firewall managers must not run concurrently.

Report the device model, Android version, root manager, active advanced mode,
exact actions, expected behavior and actual behavior. Redact package inventories,
IP addresses, logs, credentials and personally identifying information.

## 中文

先验证方向键、确认键、返回键、工具栏和高级界面复选框；修改草稿不应自动启用。
在没有实际脚本时，不应再出现“必须关闭自定义规则”的错误阻止。切换系统中英文
后重新打开，检查三档、提示、状态和错误信息。真实拦截效果须在可恢复的测试设备
上，用普通应用验证内网/外网、IPv4/IPv6、重启与停用；不要从系统或维护 UID 开始。
仅内网和禁止档仍存在上游引擎的 DNS、回环和系统代理例外，不应解释为绝对隔离。
