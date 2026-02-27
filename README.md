<div align="center">
<img src="https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/icon.png" width="192" height="192" alt="C: IDLX mod icon">
<h1>Create: Improved Display Link Experience</h1>
<a href="https://modrinth.com/mod/create-idlx"><img height="56" alt="Available on Modrinth" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/create-idlx"><img height="56" alt="Available on CurseForge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg"></a>

<a href="https://github.com/VladisCrafter/Create-IDLX"><img height="56" alt="Available on GitHub" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg"></a>
<a href="https://github.com/VladisCrafter/Create-IDLX/tree/main/src/main/resources/assets/createidlx/lang"><img height="56" alt="Help me Translate" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/translate/generic-singular_vector.svg"></a>

<a href="https://neoforged.net/"><img height="56" alt="Available For Neoforge" src="https://github.com/VladisCrafter/Create-IDLX/raw/main/README-images/neoforge_vector.svg"></a>
<a href="https://fabricmc.net/"><img height="56" alt="Won't support Fabric" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/fabric_vector.svg"></a>

<a href="https://github.com/VladisCrafter/Create-IDLX/releases"><img height="30" alt="Available For 1.21.1" src="https://forthebadge.com/api/badges/generate?panels=2&primaryLabel=Available+For&secondaryLabel=1.21.1&primaryBGColor=%23222427&primaryTextColor=%23ffffff&secondaryBGColor=%23d7742f&secondaryTextColor=%23ffffff&primaryFontSize=12&primaryFontWeight=600&primaryLetterSpacing=2&primaryFontFamily=Roboto&primaryTextTransform=uppercase&secondaryFontSize=16&secondaryFontWeight=700&secondaryLetterSpacing=2&secondaryFontFamily=Montserrat&secondaryTextTransform=uppercase"></a>
<a href="https://github.com/VladisCrafter/Create-IDLX/blob/main/LICENSE"><img height="30" alt="License MIT" src="https://forthebadge.com/api/badges/generate?panels=2&primaryLabel=License&secondaryLabel=MIT&primaryBGColor=%23222427&primaryTextColor=%23FFFFFF&secondaryBGColor=%23389AD5&secondaryTextColor=%23FFFFFF&primaryFontSize=12&primaryFontWeight=600&primaryLetterSpacing=2&primaryFontFamily=Roboto&primaryTextTransform=uppercase&secondaryFontSize=16&secondaryFontWeight=700&secondaryLetterSpacing=2&secondaryFontFamily=Montserrat&secondaryTextTransform=uppercase"></a>
</div>

---
**Create: Improved Display Link Experience (C: IDLX) is a little Quality-of-Life add-on for Create mod aimed at sprucing up some of Display Link features, mainly the underrated Attached Label option.**

---
## Features

### Placeholders for the Attached Label
<details open> <summary></summary>

The way Create mod currently handles displaying information (let's call one an input string) with the attached label is by simply concatenating both with a space in between -
it's a simple way, but thus, in terms of customization, a severely limiting one:

<details open> <summary></summary>

![IDLX showcase 1](https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/README-images/IDLX_showcase1.png)

</details>

This add-on pushes the boundaries apart by introducing the `$` and `{}` placeholders.
Placing one (or more) marks the place where the input string should be inserted.
In short, the Attached Label can now act like a formatted string!

<details open> <summary></summary>

![IDLX showcase 2](https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/README-images/IDLX_showcase2.png)

</details>

You can utilize as many placeholders as you want in one string. Mixing both types of them doesn't cause any issues as well!

<details open> <summary></summary>

![IDLX showcase 3](https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/README-images/IDLX_showcase3.png)

</details>

If you need to insert a `$` character literally (same applies to `{}`), without it turning into a placeholder, use backslash-escaping on it.

<details open> <summary></summary>

![IDLX showcase 4](https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/README-images/IDLX_showcase4.png)

</details>

In case of no unescaped placeholders present in the label altogether, default (base Create's) concatenation will be applied.

<details open> <summary></summary>

![IDLX showcase 5](https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/README-images/IDLX_showcase5.png)

</details>

Lastly, there's a new hover-on button that displays a Placeholders Usage Guide tooltip containing all the abovementioned.

<details open> <summary></summary>

![IDLX showcase (tooltip)](https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/README-images/IDLX_tooltip_showcase1.png)

</details>

Its last string displays which placeholders are active (available to use in this world/server, which is specified by the server config of the mod).

<details open> <summary></summary>

![IDLX showcase (tooltip)](https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/README-images/IDLX_tooltip_showcase.png)

<details> <summary>(A .webp version if the one above doesn't play properly)</summary>

![IDLX showcase (tooltip)](https://raw.githubusercontent.com/VladisCrafter/Create-IDLX/main/README-images/IDLX_tooltip_showcase.webp)

</details>

</details>

</details>

---
## Config
C: IDLX has a proper Create-styled config for both the client (visuals) and the server (per-world).

<details open> <summary></summary>

### Client settings
- `Enable Guide Buttons`: toggling that off will hide the abovementioned hover-on button in the Display Link interface.
- **Tooltip Customization**:
    - `Enable (Active Placeholders / Progress Bar Support) Tooltip`: those two add their additional strings into the Placeholders Usage Guide tooltip, that change the text, reflecting the states of their respective config options in **Server settings**.
### Server settings
- `Hide Escaping Of Disabled Placeholders`: by default, when a disabled placeholder is escaped, the backslash stays visible as if placed before a regular character; enable to make it get hidden like if it did its job;
- `Enable Crude Progress Bar Support`: currently the progress bar characters are being rendered incorrectly (appear squashed together) if used with placeholders, so the support for this display format is disabled altogether by default (see **Known Issues**).
- **Placeholders Availability**:
    - `Enable (Dollar Sign / Curly Brackets) Placeholder`: with those two it is possible to limit the placeholder functionality to only one option - the Dollar Sign or the Curly Brackets respectively - or disable the feature altogether by ticking both as off (affects the last string of the Placeholders Usage Guide tooltip);

</details>

---
## Known Issues
Essentially C: IDLX provides a single flap display section with default formatting options,
unlike Create that makes two for the label (default formatting) and the value (formatting specific to its display format) respectively.
The consequence of the first method is that the progress bar characters are being rendered as literal characters (non-wide), and thus appear squashed together.
Fixing that would involve rewriting the code to instead generate an array of sections. I started working on it but didn't have time to finish after running into numerous issues,
and decided to postpone it for when I get more time on my hands. Currently, placeholders support for the progress bar is put behind config (off by default).

If anybody has the will to help me with this issue, I would appreciate the contribution very much.

---
## Porting Plans
C: IDLX is planned to be backported to **Forge 1.20.1**. However I currently can not say exactly when that will happen.

---
## Credits

### Mod Development
- [@LIUKRAST](https://github.com/LIUKRAST) and [@serverside-swzo](https://github.com/serverside-swzo) for valuable Mixin advices at the early stage of C: IDLX development;
- [@realRobotix](https://github.com/realRobotix) for help with making my mod config always open through Catnip (be Create-styled);
- GG_Gnom (`gg_gnom` on Discord) for testing the mod (literally the only case of testing this mod I know).

### Mod Page Decoration
- [Devin's Badges](https://intergrav.github.io/devins-badges-docs/) for most badges used in this README;
- [@flikrheist](https://github.com/flikrheist) for the [NeoForge badge](https://github.com/intergrav/devins-badges/pull/92) specifically;
- [FOR THE BADGE](https://forthebadge.com) for the "Available For: 1.21.1" and "License: MIT" badges.

### Translation
- [@KEricMinecraft](https://github.com/KEricMinecraft) for Chinese Simplified (`zh_cn`).

_My first Minecraft mod, by the way!_