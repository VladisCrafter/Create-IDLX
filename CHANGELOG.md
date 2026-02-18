## `1.1` - 2026-02-18 (18.02.2026)

### Additions
- Added tooltip customization options to the client config:
  - **Enable Active Placeholders Tooltip**: `show the Active placeholders part of the Placeholders Usage Guide tooltip`;
  - **Enable Progress Bar Support State Tooltip**: `show the Progress Bar display format support part of the Placeholders Usage Guide tooltip`;
- Added `zh_cn` (Chinese Simplified) translation by [@KEricMinecraft](https://github.com/KEricMinecraft).
### Improvements
- Organized both configs - condensed some pairs of similar options into groups.

---

## `1.0` - 2026-02-08 (08.02.2026)

Initial public release.

---

## `0.2-dev1` - 2026-01-26 (26.01.2026)

### Additions
- Added a **Placeholders Usage Guide** hover-on button that displays a tooltip containing all the information about using placeholders (it was moved from the Attached Label tooltip which now doesn't get modified altogether);
- Added **client and server configs** with following parameters:
  - Client config:
    - **Enable Guide Buttons**: `show the hover-on Placeholders & String Slicing Usage Guides tooltip buttons in Display Link interface`;
  - Server config:
    - **Enable Dollar Sign Placeholder**: `treat $ (dollar sign) as a placeholder for the Attached Label`;
    - **Enable Curly Brackets Placeholder**: `treat {} (curly brackets) as a placeholder for the Attached Label`.
### Improvements
- The last string of the **Placeholders Usage Guide** tooltip now displays which placeholders are active (available to use) based on the server config.
### Removals
- Removed the **Overwrite** of the Attached Label's tooltip as the **Placeholders Usage Guide** strings were moved into their special hover-on button.

---

## `0.1-dev1` - 2026-01-22 (22.01.2026)

Initial **beta** release.

---

