# Attribution and modification notice / 来源与修改声明

AFWall+ TV is an independent, unofficial derivative of
[AFWall+](https://github.com/ukanth/afwall), not a replacement for or an official
release by the upstream maintainers.

- Upstream snapshot: `acfebf0f04c807acd7fe80b27d09c04e9b581e74` (branch: `beta`).
- Original work includes copyright by Rodrigo Zechin Rosauro, Umakanthan
  Chandran, the Android Open Source Project, and other contributors identified
  in the source files. Their notices are retained.
- TV modifications: 2026-10-18; public localization and distribution preparation:
  2026-10-22, by the AFWall+ TV contributors.
- The application is distributed under GNU GPL version 3 or, where permitted by
  the original file notices, any later version. The unmodified license text is
  in [LICENSE](LICENSE). Separately licensed components retain their own terms;
  see [THIRD_PARTY.md](THIRD_PARTY.md).

Changes include a TV-first three-policy interface, D-pad focus and click support
in the advanced interface, shared/core UID handling, custom-rule warning fixes,
English/Chinese Android resources, additional tests, compatible build settings,
and public-facing documentation. The firewall engine and its limitations remain
derived from upstream.

This repository intentionally starts with a new initial commit. It does not
carry the local development history and is not a GitHub-network fork. A fresh
history does **not** imply original authorship of the inherited code or remove
copyright, copyleft, attribution, or source-distribution obligations. The
upstream README and changelog are retained under `docs/upstream/` for provenance;
they describe upstream, not the support or release status of this derivative.

## 中文

AFWall+ TV 是 AFWall+ 的独立、非官方派生版本。基线为上述准确提交；原文件中的
版权、许可证和免责声明保持不变。2026 年 10 月 18 日增加电视三档界面、遥控器
适配和规则映射；2026 年 10 月 22 日整理双语资源、测试、构建与公开文档。

应用继续遵循 GPLv3 及原文件允许的后续版本条款；独立第三方组件分别遵循其原有
许可。新建仓库只清除本地开发的 Git 历史，不取消原作者署名或 GPL 义务。仓库未
建立 GitHub fork 网络关系，并不代表所继承的代码由本项目原创。
