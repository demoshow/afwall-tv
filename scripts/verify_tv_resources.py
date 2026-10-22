#!/usr/bin/env python3
"""Check the complete TV translation contract using only the Python standard library."""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"


def load(directory):
    tree = ET.parse(RES / directory / "tv_strings.xml").getroot()
    strings = {node.attrib["name"]: "".join(node.itertext()) for node in tree.findall("string")}
    arrays = {node.attrib["name"]: ["".join(item.itertext()) for item in node.findall("item")]
              for node in tree.findall("string-array")}
    assert len(strings) == len(tree.findall("string")), "Duplicate string resource"
    return strings, arrays


def placeholders(text):
    return sorted(re.findall(r"%\d+\$[ds]", text))


def main():
    english, english_arrays = load("values")
    chinese, chinese_arrays = load("values-zh")
    chinese_hant, hant_arrays = load("values-b+zh+Hant")
    assert chinese == chinese_hant and chinese_arrays == hant_arrays, "Chinese fallback sets differ"
    assert english.keys() == chinese.keys(), "TV translation key sets differ"
    for key in english:
        assert english[key].strip() and chinese[key].strip(), key
        assert placeholders(english[key]) == placeholders(chinese[key]), key
    assert english_arrays.keys() == chinese_arrays.keys()
    assert len(english_arrays["tv_modes"]) == len(chinese_arrays["tv_modes"]) == 4
    assert english_arrays["tv_modes"][:3] == ["Unrestricted", "LAN only", "Blocked"]
    source = (ROOT / "app/src/main/java/dev/ukanth/ufirewall/TvActivity.java").read_text()
    assert not re.search(r"[\u3400-\u9fff]", source), "Hardcoded Chinese TV text remains"
    references = set(re.findall(r"R\.string\.(tv_\w+)", source))
    assert references <= english.keys(), references - english.keys()
    assert 'new Locale(' not in source and 'Locale.setDefault' not in source
    manifest = (ROOT / "app/src/main/AndroidManifest.xml").read_text()
    assert "android.intent.category.HOME" not in manifest, "Unexpected launcher takeover"
    assert 'android:label="@string/tv_app_name"' in manifest
    print(f"Verified {len(english)} bilingual TV strings, 4 policy labels, format placeholders, and manifest scope.")


if __name__ == "__main__":
    main()
