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