# Branding / 品牌资源

`afw-tv-icon.svg` is the project's original app icon: a TV behind a firewall
shield with a green check, on a transparent background. It is rendered to the
launcher, adaptive and TV-banner assets below and distributed under
GPL-3.0-or-later with the application.

| File | Content |
| --- | --- |
| `afw-tv-icon.svg` | Vector source of the icon |
| `app-icon-1024.png` | Launcher icon (dark `#101820` rounded background) |
| `app-icon-web-512.png` | Web/store icon |
| `tv-banner-640x360.png` | Android TV banner (xhdpi; 320x180 dp) |
| `make-icon.py` | Renders the SVG and writes the Android resource densities |

Regenerate from the repository root:

```sh
python3 docs/branding/make-icon.py
```

Requires Pillow and `sips` (macOS). The script composes the transparent SVG
over the app's `#101820` background and writes every density into
`app/src/main/res/`; the vector source here is the preferred form for edits.
