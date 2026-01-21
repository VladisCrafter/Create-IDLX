<div align="center">
  <img src="icon.png" width="192" height="192" alt="C: IDLX mod icon">
  <h1>Create: Improved Display Link Experience</h1>
	<a href=""><img src="https://img.shields.io/badge/Avalable_For-1.21.1-blue" alt="Supported Versions"></a>
	<a href="https://github.com/VladisCrafter/Create-IDLX/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-MIT-red" alt="License"></a>
</div>

___
## Create: Improved Display Link Experience (C: IDLX) is a little Quality-of-Life add-on for Create mod aimed at sprucing up some of Display Link features, mainly the underrated Attached Label option.

---
<div align="center">
    <h2>Currently added features</h2>
1. Placeholders for the Attached Label.
    
###### Yeah, that's basically it for now.
</div>

---
## 1. Placeholders for the Attached Label:
The way Create mod currently handles displaying information (aka data) with the attached label is by simply concatenating them with a space:
It's a simple way, but thus a severely limiting one in terms of customization.

<details open>

<summary>[Collapsible]</summary>

![image](README-images/IDLX_showcase1.png)

</details>

This add-on pushes the boundaries apart by introducing the `$` and `{}` placeholders (aka specifiers).
Placing one (or more) marks the place where the information string should be inserted.
In short, the Attached Label can now act like a formatted string!

<details open>

<summary>[Collapsible]</summary>

![image](README-images/IDLX_showcase2.png)

</details>

You can utilize as many placeholders as you want in one string. Mixing both types of them doesn't cause any issues as well!

<details open>

<summary>[Collapsible]</summary>

![image](README-images/IDLX_showcase3.png)

</details>

If you need to insert a `$` character literally (same applies to `{}`), without it turning into a placeholder, use backslash-escaping on it.

<details open>

<summary>[Collapsible]</summary>

![image](README-images/IDLX_showcase4.png)

</details>

In case of no unescaped placeholders present in the label altogether, default (base Create's) concatenation will be applied.

<details open>

<summary>[Collapsible]</summary>

![image](README-images/IDLX_showcase5.png)

</details>

Lastly, there's a new tooltip linked to the Attached Label input field, that briefly repeats the above.

<details open>

<summary>[Collapsible]</summary>

![image](README-images/IDLX_showcase_label.png)

</details>

---
# The README is incomplete and will be getting updates through further development.