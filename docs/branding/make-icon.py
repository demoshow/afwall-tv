#!/usr/bin/env python3
"""Generate the original AFWall+ TV icon, adaptive foreground and TV banner.

Design: a mint shield (firewall) holding a blue TV screen with a check mark
(allowed), on a dark navy TV-UI background. Original artwork for this project.
"""
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import math, os

BG_TOP = (0x18, 0x28, 0x33)
BG_BOT = (0x0C, 0x12, 0x18)
DARK = (0x10, 0x18, 0x20)
SHIELD_TOP = (0x8B, 0xF0, 0xC6)
SHIELD_BOT = (0x2F, 0xB9, 0x8A)
SCREEN = (0x75, 0xBD, 0xFF)
INK = (0x0A, 0x12, 0x18)
FONT = "/System/Library/Fonts/Supplemental/Arial Bold.ttf"


def gradient(size, top, bot):
    im = Image.new("RGB", (1, size), top)
    px = im.load()
    for y in range(size):
        t = y / max(1, size - 1)
        px[0, y] = tuple(int(top[i] * (1 - t) + bot[i] * t) for i in range(3))
    return im.resize((size, size))


def shield_mask(S, w, h, inset=0.0):
    cx, cy = S / 2, S / 2
    x0, x1 = cx - w / 2 + inset, cx + w / 2 - inset
    y0, y1 = cy - h / 2 + inset, cy + h / 2 - inset * 1.1
    m = Image.new("L", (S, S), 0)
    d = ImageDraw.Draw(m)
    top_h = (y0 + (cy - h / 2) + 0.56 * h) - y0
    r = max(2, int(0.075 * w))
    d.rounded_rectangle([x0, y0, x1, y0 + 0.56 * h], radius=r, fill=255)
    d.polygon([(x0, y0 + 0.52 * h), (x1, y0 + 0.52 * h), (cx, y1)], fill=255)
    return m


def draw_mark(S, w_ratio=0.60, h_ratio=0.68, glow=True):
    """RGBA mark on transparent canvas S (supersampled)."""
    w, h = S * w_ratio, S * h_ratio
    cx, cy = S / 2, S / 2
    x0 = cx - w / 2
    y0 = cy - h / 2

    # soft glow behind the shield
    if glow:
        g = Image.new("RGBA", (S, S), (0, 0, 0, 0))
        gd = ImageDraw.Draw(g)
        gd.ellipse([cx - w * 0.62, cy - h * 0.55, cx + w * 0.62, cy + h * 0.55],
                   fill=(0x67, 0xDB, 0xA0, 46))
        g = g.filter(ImageFilter.GaussianBlur(S * 0.06))
    else:
        g = None

    out = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    if g:
        out.alpha_composite(g)

    # shield shadow
    sh = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    sh.paste((0, 0, 0, 110), (0, 0), shield_mask(S, w, h))
    sh = sh.filter(ImageFilter.GaussianBlur(S * 0.018))
    out.alpha_composite(sh)

    # outer shield: vertical mint gradient
    grad = gradient(S, SHIELD_TOP, SHIELD_BOT).convert("RGBA")
    layer = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    layer.paste(grad, (0, 0), shield_mask(S, w, h))
    out.alpha_composite(layer)

    # inner shield: dark, leaves a mint rim
    inset = 0.072 * w
    inner = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    inner.paste(DARK + (255,), (0, 0), shield_mask(S, w, h, inset))
    out.alpha_composite(inner)

    # TV screen
    sw, shgt = 0.44 * w, 0.30 * h
    ys = y0 + 0.46 * h
    screen = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    ImageDraw.Draw(screen).rounded_rectangle(
        [cx - sw / 2, ys - shgt / 2, cx + sw / 2, ys + shgt / 2],
        radius=0.045 * w, fill=SCREEN + (255,))
    out.alpha_composite(screen)

    # check mark on the screen
    chk = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    cd = ImageDraw.Draw(chk)
    pts = [(cx - 0.135 * w, ys + 0.005 * h),
           (cx - 0.035 * w, ys + 0.085 * h),
           (cx + 0.145 * w, ys - 0.105 * h)]
    cd.line(pts, fill=INK + (255,), width=max(3, int(0.05 * w)), joint="curve")
    out.alpha_composite(chk)

    # TV stand + base
    st = 0.022 * h
    stand = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    sd = ImageDraw.Draw(stand)
    sd.rectangle([cx - 0.052 * w, ys + shgt / 2, cx + 0.052 * w, ys + shgt / 2 + st],
                 fill=SCREEN + (255,))
    sd.rectangle([cx - 0.125 * w, ys + shgt / 2 + st * 1.55, cx + 0.125 * w,
                  ys + shgt / 2 + st * 2.35], fill=SCREEN + (255,))
    out.alpha_composite(stand)
    return out


def icon_canvas(size, rounded=True, border=0.0):
    S = size * 3
    bg = gradient(S, BG_TOP, BG_BOT).convert("RGBA")
    # radial glow on the background
    glow = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    ImageDraw.Draw(glow).ellipse([S * 0.16, S * 0.12, S * 0.84, S * 0.88],
                                 fill=(0x67, 0xDB, 0xA0, 26))
    glow = glow.filter(ImageFilter.GaussianBlur(S * 0.10))
    bg.alpha_composite(glow)
    mask = Image.new("L", (S, S), 0)
    if rounded:
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, S - 1, S - 1],
                                               radius=int(S * 0.22), fill=255)
    else:
        ImageDraw.Draw(mask).ellipse([0, 0, S - 1, S - 1], fill=255)
    canvas = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    canvas.paste(bg, (0, 0), mask)
    mark = draw_mark(S, 0.60, 0.68)
    canvas.alpha_composite(mark)
    return canvas.resize((size, size), Image.LANCZOS)


def foreground(size):
    S = size * 4
    out = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    out.alpha_composite(draw_mark(S, 0.54, 0.60))
    return out.resize((size, size), Image.LANCZOS)


def banner(W=1280, H=720):
    bg = gradient(H, BG_TOP, BG_BOT).convert("RGBA").resize((W, H))
    glow = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(glow).ellipse([W * 0.02, H * 0.08, W * 0.42, H * 0.92],
                                 fill=(0x67, 0xDB, 0xA0, 38))
    glow = glow.filter(ImageFilter.GaussianBlur(H * 0.10))
    bg.alpha_composite(glow)
    d = ImageDraw.Draw(bg)
    # thin mint frame line
    d.rectangle([0, 0, W - 1, H - 1], outline=(0x67, 0xDB, 0xA0, 70), width=2)
    # icon at left
    mark = draw_mark(int(H * 0.92), 0.60, 0.68)
    bg.alpha_composite(mark, (int(W * 0.045), int(H * 0.14)))
    # title
    f_title = ImageFont.truetype(FONT, int(H * 0.185))
    f_sub = ImageFont.truetype(FONT, int(H * 0.056))
    f_small = ImageFont.truetype(FONT, int(H * 0.036))
    x = int(W * 0.405)
    y = int(H * 0.245)
    parts = [("AFWall", (255, 255, 255, 255)), ("+", (0x67, 0xDB, 0xA0, 255)),
             (" TV", (255, 255, 255, 255))]
    for text, color in parts:
        d.text((x, y), text, font=f_title, fill=color)
        x += d.textlength(text, font=f_title)
    # mint underline
    d.rectangle([int(W * 0.405), int(H * 0.475), int(W * 0.92), int(H * 0.482)],
                fill=(0x67, 0xDB, 0xA0, 230))
    d.text((int(W * 0.407), int(H * 0.515)), "FIREWALL FOR ANDROID TV",
           font=f_sub, fill=(0x75, 0xBD, 0xFF, 255))
    d.text((int(W * 0.407), int(H * 0.635)), "GPL-3.0  ·  UNOFFICIAL TV PORT",
           font=f_small, fill=(0x9F, 0xB3, 0xC2, 255))
    return bg.resize((640, 360), Image.LANCZOS)


from pathlib import Path


def write_assets():
    """Write the Android resource densities and the docs/branding exports."""
    root = Path(__file__).resolve().parents[2]
    res = root / "app/src/main/res"
    for density, size, fg in [("mdpi", 48, 108), ("hdpi", 72, 162),
                              ("xhdpi", 96, 216), ("xxhdpi", 144, 324),
                              ("xxxhdpi", 192, 432)]:
        icon_canvas(size).save(res / f"mipmap-{density}/ic_launcher.png")
        icon_canvas(size, rounded=False).save(res / f"mipmap-{density}/ic_launcher_round.png")
        foreground(fg).save(res / f"mipmap-{density}/ic_launcher_foreground.png")
    for density, size in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144)]:
        icon_canvas(size).save(res / f"drawable-{density}/ic_launcher.png")
    icon_canvas(512).save(root / "app/src/main/ic_launcher-web.png")
    banner().save(res / "drawable-xhdpi/tv_banner.png")
    icon_canvas(1024).save(root / "docs/branding/app-icon-1024.png")
    icon_canvas(512).save(root / "docs/branding/app-icon-web-512.png")
    banner().save(root / "docs/branding/tv-banner-640x360.png")
    (res / "values/ic_launcher_background.xml").write_text(
        '<?xml version="1.0" encoding="utf-8"?>\n'
        '<resources>\n    <color name="ic_launcher_background">#101820</color>\n'
        '</resources>\n')
    old = res / "drawable/tv_banner.xml"
    if old.exists():
        old.unlink()
    print("wrote icon, adaptive foreground and TV banner")


if __name__ == "__main__":
    write_assets()
