# Native source archives

Retrieved from the component maintainers on 2026-10-22. Archives are unmodified;
component license texts are inside them and copied under `LICENSES/`.

| Archive | Official download | SHA-256 |
| --- | --- | --- |
| `busybox-1.36.1.tar.bz2` | https://busybox.net/downloads/busybox-1.36.1.tar.bz2 | `b8cc24c9574d809e7279c3be349795c5d5ceb6fdf19ca709f80cde50e47de314` |
| `iptables-1.8.10.tar.xz` | https://www.netfilter.org/projects/iptables/files/iptables-1.8.10.tar.xz | `5cc255c189356e317d070755ce9371eb63a1b783c34498fb8c30264f3cc59c9c` |

The inherited helper resources identify these versions. The retained
`build-binaries.sh` contains their Android configuration and source modifications;
`external/` contains additional configuration, patches and NFLOG/libmnl source.
These files are source-availability material, not an assertion that rebuilding
them reproduces the inherited binaries byte-for-byte. Normal Gradle builds use
the inherited resources. Review the build script before invoking a native build.

中文：以上是对应版本的官方原始源码，未修改归档内容。Android 构建配置和修改位于
保留的构建脚本及 `external/`。本次未重新编译原生工具，不承诺位级可复现。
