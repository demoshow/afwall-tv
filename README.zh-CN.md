# AFWall+ TV

[English](README.md) · [简体中文](README.zh-CN.md)

**AFWall+ TV 是 [AFWall+](https://github.com/ukanth/afwall) 的电视适配版本，
不是从零开发的防火墙。**

> **本项目的防火墙核心及绝大部分应用实现均来自 AFWall+。** 原有应用、规则引擎
> 和成熟功能的主要贡献应归于 Umakanthan Chandran 及 AFWall+ 贡献者，以及
> Rodrigo Zechin Rosauro 等前序作者；具体署名保留在源码中。本项目的新增工作
> 主要集中在电视界面、遥控器操作、三档策略呈现与本地化。

**原项目：** [ukanth/afwall](https://github.com/ukanth/afwall) ·
**上游文档：** [AFWall+ Wiki](https://github.com/ukanth/afwall/wiki)

**非官方派生版本 · Android 7.0 及以上 · 需要 root · GPLv3**

感谢 AFWall+ 维护者及所有贡献者提供的核心实现与持续维护，本项目建立在他们的
工作之上。如果这个电视适配版本对你有帮助，也请关注和支持
[AFWall+ 原项目](https://github.com/ukanth/afwall#support-afwall-development)。
本仓库是独立的非官方源码发行，不代表对所继承应用的原创声明，也不构成绝对
网络隔离承诺。

## 哪些能力来自 AFWall+，哪些是本项目新增

### 继承自 AFWall+ 的核心能力

应用架构、iptables/ip6tables 规则生成与应用、基于 UID 的网络过滤、IPv4/IPv6
处理、各网络类型配置、多配置方案、日志、开机恢复、高级设置，以及大量原有
界面、翻译和测试，均来自 AFWall+。内置原生工具也来自上游及各自作者。
**这些核心能力不是 AFWall+ TV 重新实现的功能，不应归作本项目原创。**

### 本项目新增或适配的 TV 层

- **明确的三档策略：** 正常联网、仅内网、禁止联网，按应用 UID 分组显示，避免
  直接面对含义随模式变化的复选框。
- **遥控器操作：** 方向键切换应用和工具栏，确认键选档，返回键确认放弃草稿；
  高级界面保留方向键焦点和确认键勾选适配。
- **共享与核心 UID 保护：** 同 UID 的包统一管理；电视界面保护 Android 系统、
  维护功能和防火墙自身 UID，减少误封风险。
- **中英文自动切换：** 电视界面跟随系统语言，中文区域使用简体中文，其他语言
  默认显示英文；高级界面保留上游已有的多语言翻译。
- **明确的生效边界：** 修改选档只是草稿；必须确认应用并通过 root 管理器授权，
  才会写入实际规则。
- **保留高级配置：** 不再将默认开启的标志误报为已有自定义规则；保留现有白名单/
  黑名单模式、脚本、直接规则及 Tor 设置。这些能力本身来自上游，本项目负责
  三档策略与原有配置之间的映射和界面衔接。

三档界面最终调用 AFWall+ 执行规则，并未替换或独立重写其防火墙引擎。新增的
策略回归和本地化测试用于验证适配层，同时保留上游原有测试。

## 三档策略及边界

| 策略 | 对应用直接流量的预期处理 |
| --- | --- |
| 正常联网 | 不额外限制该应用 |
| 仅内网 | 允许引擎定义的局域网、私有网段和发现地址；限制 Wi-Fi/有线外网、移动数据和 VPN 路径 |
| 禁止联网 | 限制直接 LAN/WAN 流量，但仍受上游引擎例外影响 |

以上是规则意图，不是绝对隔离保证。回环、DNS、系统代理、root 权限、共享 UID、
VPN 路由、内核行为、高级规则及开机时序都可能影响实际效果。“仅内网”不是仅允许
某一台 NAS 或单一 IP 的精确白名单；同 UID 的多个应用不能分别隔离。

新增应用沿用当前上游模式的默认策略：黑名单默认放行，白名单默认阻止。高级
界面中的复选框也保持上游含义：白名单勾选表示允许，黑名单勾选表示阻止。电视
三档界面负责转换，因此三档标签不随模式反转。

## 使用条件与首次操作

1. 设备需为 Android 7.0/API 24 及以上，具备可用的 root 管理器，以及支持
   iptables/ip6tables 的内核。最低 SDK 只是安装门槛，并非所有盒子的兼容认证。
2. 按照[构建说明](docs/BUILDING.md)自行构建和签名。本次 GitHub 仅发布源码，
   不上传 APK 或签名私钥。
3. 打开 **AFWall+ TV**，先选一个非关键普通应用，用确认键选择联网档位。
4. 选择“应用规则”，阅读确认框，并自行在 root 管理器中授权。应用时会启用
   LAN、IPv6、VPN、漫游和共享列来表达三档；保留既有 Tor 设置及高级模式。
5. 在本机验证内网/外网、IPv4/IPv6 的实际效果。“停用防火墙”清理本应用规则，
   保存的应用选择仍保留。

不要同时启用官方 AFWall+ 与本项目：两者使用同名 iptables 链。保留本地恢复
手段，不要从系统、root、ADB 等维护 UID 开始限制；卸载前先停用规则。
参见[安全说明](SECURITY.md)。

## 构建与验证

使用 JDK 17、Android SDK 34、Build Tools 34.0.0、Gradle 8.7 和 AGP 8.5.0。
仓库包含带下载校验值的 Gradle wrapper。

```sh
git clone https://github.com/demoshow/afwall-tv.git
cd afwall-tv
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleRelease --no-daemon
python3 scripts/verify_tv_resources.py
```

Release 默认生成未签名 APK，签名凭据应保存在仓库之外。为兼容已有安装，保留
应用 ID `local.cm311.afwalltv`，但实现并不绑定某款电视盒子。首次构建需要下载
依赖；常规构建使用上游原生工具，相关源码、修改和授权见
[第三方说明](THIRD_PARTY.md)，不声称原生工具已实现位级可复现。

测试覆盖三档与白名单/黑名单转换、核心 UID 保护、默认自定义规则标志误报，
以及真实 Android 资源解析中的中英文切换、区域回退和格式参数。单元测试不能
代替遥控器、root 授权、开机恢复和真实网络拦截验证；请执行
[人工验收清单](docs/TESTING.md)。

## 项目结构

| 位置 | 职责 |
| --- | --- |
| `TvActivity.java` | 电视三档界面及确认后应用流程 |
| `tv/TvPolicy.java` | 与上游模式无关的策略映射 |
| `util/TvRemoteNavigation.java` | 高级界面方向键焦点支持 |
| `Api.java` | 继承的 AFWall+ 防火墙引擎 |
| `app/src/main/res/values*/` | 上游翻译与电视语言资源 |
| `app/src/test/` | 上游、策略及本地化回归测试 |
| `external/`、`third_party/sources/` | 原生源码、构建配置、官方源码归档与校验值 |
| `docs/` | 构建、验收与上游来源记录 |

## 隐私与外部依赖

电视层没有新增统计服务、远程控制端点或云账号要求。继承的应用仍包含可选外链
及 DNS/主机名查询功能；这不代表已经审计并证明全部上游行为离线。root 授权由
设备上的管理器处理，不由本仓库提供远程服务。

构建会访问 Gradle 中声明的依赖仓库，Robolectric 首次测试还可能下载 Android
框架资源。公开问题中不要提交未脱敏的应用清单、日志、凭据或个人网络信息。
TV 改版问题请先提交到[本仓库](https://github.com/demoshow/afwall-tv/issues)，
除非已在未修改的上游版本独立复现。

## 授权、来源与贡献

基线为上游提交
[`acfebf0f04c807acd7fe80b27d09c04e9b581e74`](https://github.com/ukanth/afwall/tree/acfebf0f04c807acd7fe80b27d09c04e9b581e74)。
原作者版权和许可声明保持不变。应用继续按 GNU GPLv3 及原文件允许的后续版本
条款发布；独立第三方组件保持各自授权。

本仓库从干净的源码快照建立历史，未导入此前 Git 记录，也未建立 GitHub fork
网络关系。**这种仓库组织方式不代表所继承代码由本项目原创**，不会取消原作者
署名、版权、copyleft 或完整对应源码义务。详见[许可证](LICENSE)、[修改声明](NOTICE.md)、
[第三方组件](THIRD_PARTY.md)及保留的[上游 README](docs/upstream/README.md)。
贡献流程参见 [CONTRIBUTING.md](CONTRIBUTING.md)。
