# Security policy

This is experimental root-privileged software, not a security audit or a promise
of complete network isolation. Root-capable applications can change firewall
rules. Shared UIDs cannot be isolated per package. DNS, loopback, proxy/VPN,
kernel/OEM and boot-time behavior must be considered separately.
The exported TV entry is not an access-control boundary; do not rely on the
advanced interface’s app-lock options to secure every TV entry path.

Do not enable this derivative and upstream AFWall+ together: they use the same
iptables chain names. Do not remotely test restrictive system rules without a
local recovery route. Disable this app’s firewall before uninstalling it.

For vulnerabilities, use GitHub private vulnerability reporting if available,
or agree on a private reporting channel with the maintainer before disclosing
sensitive details. Do not publish tokens, credentials, unredacted device logs,
package inventories or personal network information in public issues.

This repository does not provide signing keys, automatic updates, or a support
guarantee. Review third-party sources and build dependencies before distributing
a binary. User-triggered upstream help/donation/lookup integrations may access
external services; see the README’s dependency and privacy notes.

## 中文

本项目需要 root，不构成安全审计或绝对隔离承诺。共享 UID、DNS、代理、内核与
开机窗口都会影响效果。不要同时启用官方 AFWall+，卸载前先停用规则，并保留本地
恢复手段；不要将高级界面中的应用锁视为电视入口的完整访问保护。敏感漏洞优先
私下报告，公开问题中不得提交密钥、凭据或未脱敏设备信息。
