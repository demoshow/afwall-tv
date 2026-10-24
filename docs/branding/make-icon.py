#!/usr/bin/env python3
"""Render docs/branding/afw-tv-icon.svg into Android icon/banner resources."""
from pathlib import Path
import subprocess, tempfile
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[2]
SVG = Path(__file__).with_name("afw-tv-icon.svg")
FONT = "/System/Library/Fonts/Supplemental/Arial Bold.ttf"
BG = (0x10, 0x18, 0x20, 255)
MINT = (0x67, 0xDB, 0xA0, 255)
BLUE = (0x75, 0xBD, 0xFF, 255)
GREY = (0x9F, 0xB3, 0xC2, 255)


def render_svg():
    with tempfile.TemporaryDirectory() as d:
        out = Path(d) / "icon.png"
        subprocess.run(["sips", "-s", "format", "png", str(SVG), "--out", str(out)],
                       check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        im = Image.open(out).convert("RGBA")
    return im.crop(im.getbbox())


MARK = render_svg()


def icon(size, ratio=0.82, circle=False, transparent=False):
    S = size
    canvas = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    if not transparent:
        mask = Image.new("L", (S, S), 0)
        d = ImageDraw.Draw(mask)
        if circle:
            d.ellipse([0, 0, S - 1, S - 1], fill=255)
        else:
            d.rounded_rectangle([0, 0, S - 1, S - 1], radius=int(S * 0.22), fill=255)
        canvas.paste(Image.new("RGBA", (S, S), BG), (0, 0), mask)
    m = MARK.copy()
    m.thumbnail((int(ratio * S), int(ratio * S)), Image.LANCZOS)
    canvas.alpha_composite(m, ((S - m.width) // 2, (S - m.height) // 2))
    return canvas


def foreground(size):
    return icon(size, ratio=0.58, transparent=True)


def banner(W=640, H=360):
    im = Image.new("RGBA", (W, H), BG)
    d = ImageDraw.Draw(im)
    m = MARK.copy()
    m.thumbnail((int(H * 0.64), int(H * 0.64)), Image.LANCZOS)
    im.alpha_composite(m, (int(W * 0.045), (H - m.height) // 2))
    f_title = ImageFont.truetype(FONT, 44)
    f_sub = ImageFont.truetype(FONT, 18)
    f_small = ImageFont.truetype(FONT, 14)
    x = int(W * 0.405)
    y = int(H * 0.20)
    for text, color in [("AFWall", (255, 255, 255, 255)), ("+", MINT), (" TV", (255, 255, 255, 255))]:
        d.text((x, y), text, font=f_title, fill=color)
        x += d.textlength(text, font=f_title)
    d.rectangle([int(W * 0.405), int(H * 0.405), int(W * 0.92), int(H * 0.412)], fill=MINT)
    d.text((int(W * 0.407), int(H * 0.445)), "FIREWALL FOR ANDROID TV", font=f_sub, fill=BLUE)
    d.text((int(W * 0.407), int(H * 0.60)), "GPL-3.0 · UNOFFICIAL TV PORT", font=f_small, fill=GREY)
    return im


def write_assets():
    res = ROOT / "app/src/main/res"
    for density, size, fg in [("mdpi", 48, 108), ("hdpi", 72, 162),
                              ("xhdpi", 96, 216), ("xxhdpi", 144, 324),
                              ("xxxhdpi", 192, 432)]:
        icon(size).save(res / f"mipmap-{density}/ic_launcher.png")
        icon(size, circle=True).save(res / f"mipmap-{density}/ic_launcher_round.png")
        foreground(fg).save(res / f"mipmap-{density}/ic_launcher_foreground.png")
    for density, size in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144)]:
        icon(size).save(res / f"drawable-{density}/ic_launcher.png")
    icon(512).save(ROOT / "app/src/main/ic_launcher-web.png")
    banner().save(res / "drawable-xhdpi/tv_banner.png")
    icon(1024).save(ROOT / "docs/branding/app-icon-1024.png")
    icon(512).save(ROOT / "docs/branding/app-icon-web-512.png")
    banner().save(ROOT / "docs/branding/tv-banner-640x360.png")
    (res / "values/ic_launcher_background.xml").write_text(
        '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n'
        '    <color name="ic_launcher_background">#101820</color>\n</resources>\n')
    old = res / "drawable/tv_banner.xml"
    if old.exists():
        old.unlink()
    print("wrote SVG-based icon, adaptive foreground and TV banner")


if __name__ == "__main__":
    write_assets()
