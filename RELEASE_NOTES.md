## `1.5` - 2026-05-26 (26.05.2026)

### Additions
- Added **Visualization Settings** to the Display Link Interface:
  - **Center Text**: `the displayed text will be center-aligned on the Board`;
  - **Mark Truncation with Ellipsis**: `in case of the displayed text not fitting onto the Board and getting truncated, its last visible character will be replaced with ellipsis (…)`;

Currently, those options are available only for **single line** display sources and only for **Display Boards** as Target Displays (expanded support for both the sources and the targets is in the works).
### Improvements
- Massively improved - revamped from the ground-up, to be precise - the core architecture of the **flap display sections assembly**, thanks to which all the flap cycling animations now look how they were supposed to.
### Fixes
- Fixed **progress bars rendering incorrectly** when used with placeholders (being the mod's oldest issue) thanks to the abovementioned rework ([#1](https://github.com/VladisCrafter/Create-IDLX/issues/1));
- Fixed crashing on **dedicated server** ([#14](https://github.com/VladisCrafter/Create-IDLX/issues/14)) thanks to a Pull Request ([#19](https://github.com/VladisCrafter/Create-IDLX/pull/19)) by [@Flinkpfote](https://github.com/Flinkpfote);
- Fixed crashing on opening the **Types of Copiable Display Link Properties** ponder scene ([#15](https://github.com/VladisCrafter/Create-IDLX/issues/15));
- Fixed some formats of **Mechanical Piston Extension State** display source being incorrectly transposed ([#16](https://github.com/VladisCrafter/Create-IDLX/issues/16));
- Fixed visual glitching of **Guide Buttons** in the Display Link Interface when scrolling through available sources.
