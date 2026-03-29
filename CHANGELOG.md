## `1.3` - 2026-03-29 (29.03.2026)

### Additions
- Added two new **display sources**:
  - **Elevator Target Floor** (for **Elevator Contact**): oppositely to **Elevator Location with Extra Formats** displaying the floor the elevator _has just passed_, this one displays the floor the elevator was _called to_, and utilizes the same display formats:
    - `Floor Name`;
    - `Floor Description`;
    - `Floor Identifier & Description`;
    - `Floor Description & Identifier`;
  - **Elevator Movement Direction** (for **Elevator Pulley**): with the idea taken from the `@suggestions` channel of [Create's Discord server](https://discord.gg/AjRTh6B) (suggested twice, by [The Tiny Desk Engineer](https://discord.com/channels/620934202875183104/620937151915360268/1289411309347143764) and [Pfluck](https://discord.com/channels/620934202875183104/620937151915360268/1404243477952462869) respectively), it simply displays the elevator's movement direction one of the available formats:
    - `Arrows`: `×`, `↑` and `↓`;
    - `Triangles`: `■`, `▲` and `▼`;
    - `Words`: `standing still`, `heading up` and `heading down`;
- Added a new client config option:
  - **Show Tooltip For Single Option Selector**: `show the 'Type of Information' selector tooltip if it only has a single option` (should be useful if that single option is truncated, but you need to see it the whole for a moment);
- Added `fr_fr` (French) translation by [@JustArthur](https://github.com/JustArthur).
### Improvements
- Improved the Display Link Interface further by also **truncating** the overflowing Source Type strings even if they are not part of a selection, i.e. the single available option ([#7](https://github.com/VladisCrafter/Create-IDLX/issues/7));
- Improved the **Current Floor Extended** display source - now it features a simple `Show Empty Floor Descriptions as '????'` toggler to indicate whether the empty descriptions of floors that don't have it specified should be ignored or displayed as `????`;
- Improved the **Countdown** display source - now it features a simple `Overlap the Attached Label with the Finish Label` toggler to indicate whether the Finish Label, once the time is out, should be put in place of the placeholders or straight up overlap the whole string;
- Improved the **"Display Sources" Ponder Tag** from base Create - now it features the Elevator Contact (and, since now, the Elevator Pulley as well).
### Fixes
- Fixed the **Countdown** display source, specifically its Countdown Finish Label, not overriding the attached label if no placeholders are present (that eventually inspired the `Overlap the Attached Label with the Finish Label` toggler) ([#8](https://github.com/VladisCrafter/Create-IDLX/issues/8));
- Fixed the **Target** widget tooltip (`Set To`) being unrendered in the Display Link interface ([#9](https://github.com/VladisCrafter/Create-IDLX/issues/9));
- Fixed the **Current Floor Extended** display source displaying the description of the floor the elevator was _called to_, not the floor the elevator _has just passed_ (that eventually inspired the new **Elevator Target Floor** display source) ([#10](https://github.com/VladisCrafter/Create-IDLX/issues/10)).


---

## `1.3` - 2026-03-29 (29.03.2026)

### Additions
- Added two new **display sources**:
  - **Elevator Target Floor** (for **Elevator Contact**): oppositely to **Elevator Location with Extra Formats** displaying the floor the elevator _has just passed_, this one displays the floor the elevator was _called to_, and utilizes the same display formats:
    - `Floor Name`;
    - `Floor Description`;
    - `Floor Identifier & Description`;
    - `Floor Description & Identifier`;
  - **Elevator Movement Direction** (for **Elevator Pulley**): with the idea taken from the `@suggestions` channel of [Create's Discord server](https://discord.gg/AjRTh6B) (suggested twice, by [The Tiny Desk Engineer](https://discord.com/channels/620934202875183104/620937151915360268/1289411309347143764) and [Pfluck](https://discord.com/channels/620934202875183104/620937151915360268/1404243477952462869) respectively), it simply displays the elevator's movement direction one of the available formats:
    - `Arrows`: `×`, `↑` and `↓`;
    - `Triangles`: `■`, `▲` and `▼`;
    - `Words`: `standing still`, `heading up` and `heading down`;
- Added a new client config option:
  - **Show Tooltip For Single Option Selector**: `show the 'Type of Information' selector tooltip if it only has a single option` (should be useful if that single option is truncated, but you need to see it the whole for a moment);
- Added `fr_fr` (French) translation by [@JustArthur](https://github.com/JustArthur).
### Improvements
- Improved the Display Link Interface further by also **truncating** the overflowing Source Type strings even if they are not part of a selection, i.e. the single available option ([#7](https://github.com/VladisCrafter/Create-IDLX/issues/7));
- Improved the **Current Floor Extended** display source - now it features a simple `Show Empty Floor Descriptions as '????'` toggler to indicate whether the empty descriptions of floors that don't have it specified should be ignored or displayed as `????`;
- Improved the **Countdown** display source - now it features a simple `Overlap the Attached Label with the Finish Label` toggler to indicate whether the Finish Label, once the time is out, should be put in place of the placeholders or straight up overlap the whole string;
- Improved the **"Display Sources" Ponder Tag** from base Create - now it features the Elevator Contact (and, since now, the Elevator Pulley as well).
### Fixes
- Fixed the **Countdown** display source, specifically its Countdown Finish Label, not overriding the attached label if no placeholders are present (that eventually inspired the `Overlap the Attached Label with the Finish Label` toggler) ([#8](https://github.com/VladisCrafter/Create-IDLX/issues/8));
- Fixed the **Target** widget tooltip (`Set To`) being unrendered in the Display Link interface ([#9](https://github.com/VladisCrafter/Create-IDLX/issues/9));
- Fixed the **Current Floor Extended** display source displaying the description of the floor the elevator was _called to_, not the floor the elevator _has just passed_ (that eventually inspired the new **Elevator Target Floor** display source) ([#10](https://github.com/VladisCrafter/Create-IDLX/issues/10)).


---

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

