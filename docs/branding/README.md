# Branding / 品牌资源

The AFWall+ TV app icon and 16:9 TV banner are original assets for this
unofficial TV port. They are distributed under GPL-3.0-or-later with the
application and do not reuse upstream AFWall+ artwork.

| File | Content |
| --- | --- |
| `app-icon-1024.png` | Launcher icon (same mark used for the adaptive/legacy densities) |
| `app-icon-web-512.png` | Web/store icon |
| `tv-banner-640x360.png` | Android TV banner (xhdpi; 320x180 dp) |
| `make-icon.py` | Pillow generator for the icon, banner and Android resource densities |

Colors: `#101820` background, `#67DBA0` shield, `#75BDFF` TV screen, mint/blue
accents. The TV UI uses the same palette. Regenerate from the repository root:

```sh
python3 docs/branding/make-icon.py
```

It needs Pillow and a bold system font (Arial Bold on macOS). The generated
Android resources are written into `app/src/main/res/`; the source in this
directory is the preferred form for modifications.
