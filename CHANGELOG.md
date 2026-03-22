## `1.2` - 2026-03-22 (22.03.2026)

### Additions
- Added a **ponder** about **using Display Links' Attached Label and Placeholders** (can be found after Create's ponders for the **Display Link**);
- Added two new **display sources**:
  - **Elevator Location with Extra Formats** (for **Elevator Contact**): a format-richer alternative to the existing **Elevator Location** display source - instead of just the `Floor Identifier`, the new version allows to also display:
    - `Floor Description`;
    - `Floor Identifier & Description`;
    - `Floor Description & Identifier`;
  - **Countdown** (for **Cuckoo Clock**): a counterpart to the Create's **Stopwatch** display source, has the following input fields:
    - `Countdown Timer` (Scroll to select seconds, Scroll with Ctrl to select minutes, hold Shift to multiply the step value by 10);
    - `Countdown Finish Label` (the text that will be displayed once the time is out);
- Added `nl_nl` (Dutch) translation by [@Siepert123](https://github.com/Siepert123).
### Improvements
- Improved the Display Link interface by **truncating** the overflowing strings in selection input fields and giving them the **"Marquee" (AKA "Running Line") effect**, which can be turned off or thoroughly customized in the client config - the changeable values are:
  - **Fixed Chat Travel Time**: `the fixed time for the truncated string to scroll through, per overflowing character.`;
  - **Fixed String Travel Time** (disabled by default): `the fixed time for the truncated string to scroll through, independently of its length.`;
  - **Maximal String Travel Time** (disabled by default): `the maximal time for the truncated string to scroll through, independently of its length.`;
  - **Minimal String Travel Time** (disabled by default): `the minimal time for the truncated string to scroll through, independently of its length.`;
  - **String Pause Time**: `the fixed time for the truncated string to stay still once at the start or the end of scrolling.`;
- Improved the **Current Floor** and **Train Status** display sources from base Create - now both have Attached Label functionality (can be reverted in the server config).

---

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

