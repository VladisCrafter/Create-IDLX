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
