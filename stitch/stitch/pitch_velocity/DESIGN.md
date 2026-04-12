# Design System Strategy: The Kinetic Pitch

## 1. Overview & Creative North Star
The Creative North Star for this design system is **"The Kinetic Pitch."** 

In football management, the atmosphere is a blend of high-stakes boardroom strategy and raw, explosive field energy. This system moves away from the "static table" look of traditional sports apps. Instead, we embrace a **High-End Editorial** aesthetic that treats data like a luxury sports magazine. We achieve this through **Intentional Asymmetry**—placing large-scale display typography off-center—and **Tonal Depth**, where the UI feels like a series of layered glass sheets floating over a deep, midnight stadium. This isn't just a tool; it's a command center.

---

## 2. Colors & Surface Architecture
The palette is rooted in the "After Hours" atmosphere of a lighted stadium: deep navies and charcoals contrasted by the electric "Pitch Green" and "Stadium Gold."

### The "No-Line" Rule
To maintain a premium, seamless feel, **1px solid borders are strictly prohibited** for sectioning. Contrast must be created through background shifts. For example, a global `surface` (`#060e20`) background should host modules using `surface-container-low` (`#091328`) to create a natural, sophisticated boundary without the "boxed-in" feel of lines.

### Surface Hierarchy & Nesting
Treat the interface as a physical stack.
- **Base Layer:** `surface` (`#060e20`) for the main app background.
- **Secondary Modules:** Use `surface-container` (`#0f1930`) for primary content blocks.
- **Nested Detail:** Use `surface-container-high` (`#141f38`) for inner elements like player stats within a team card.
- **The "Glass" Rule:** For floating navigation or action menus, use `surface-bright` (`#1f2b49`) at 80% opacity with a `20px` backdrop-blur. This creates "Glassmorphism" that lets the "Pitch Green" accents bleed through from behind.

### Signature Textures
Main CTAs should never be flat. Use a linear gradient (45°) transitioning from `primary` (`#6bff8f`) to `primary_container` (`#0abc56`) to give action buttons a "lit from within" glow.

---

## 3. Typography
We use a high-contrast scale to separate "The Strategy" (Headlines) from "The Data" (Body).

*   **Display & Headlines (Lexend):** We use Lexend for its geometric, authoritative weight. `display-lg` (3.5rem) should be used for hero stats or scorelines, often with tight letter-spacing (-2%) to feel "locked in."
*   **Body & Labels (Inter):** We use Inter for its technical precision. It provides the "clean, mobile-first" readability required for complex management tasks.
*   **Editorial Flair:** Pair a `headline-sm` title with a `label-sm` in `secondary` (`#ffd709`) all-caps for a "Player of the Month" feel.

---

## 4. Elevation & Depth
Depth is a tool for focus, not just decoration.

*   **The Layering Principle:** Avoid shadows for static cards. Instead, place a `surface-container-lowest` card on top of a `surface-container-low` section. The subtle shift from `#000000` to `#091328` creates a sophisticated "recessed" look.
*   **Ambient Shadows:** For high-energy elements like floating action buttons (FABs), use an extra-diffused shadow: `box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4)`. The shadow color should be a dark tint of the background, never pure black.
*   **The "Ghost Border" Fallback:** If a UI element (like a search bar) disappears into the background, use the `outline-variant` (`#40485d`) at **15% opacity**. This provides a "whisper" of a boundary that maintains the "No-Line" philosophy.

---

## 5. Components

### Buttons
*   **Primary:** Gradient of `primary` to `primary_container`. Bold `on_primary_container` text. `xl` (1.5rem) rounded corners.
*   **Secondary:** `surface-container-highest` background with `primary` text. No border.
*   **Tertiary:** Ghost style. Transparent background with `outline` text.

### Cards & Lists (The "No Divider" Rule)
*   **Cards:** Use `md` (0.75rem) or `lg` (1rem) roundedness.
*   **Lists:** Forbid divider lines. Separate list items using `spacing-4` (0.9rem) of vertical white space or by alternating background colors between `surface-container` and `surface-container-low`.

### The "Match Status" Chip
*   A specialized component for live games. Uses `error` (`#ff7351`) for "Live" indicators with a 10% opacity pulse animation.

### Data Inputs
*   **Input Fields:** Use `surface-container-highest` with a `Ghost Border`. On focus, the border opacity jumps to 100% using `primary`.

---

## 6. Do's and Don'ts

### Do
*   **Do** use `stadium_gold` (`secondary`) sparingly for "Achievement" moments or "Premium" features.
*   **Do** lean into the `spacing-10` to `spacing-16` range for section margins to let the bold typography "breathe."
*   **Do** use `rounded-full` for action chips to contrast against the `rounded-lg` cards.

### Don't
*   **Don't** use 100% opaque white text. Use `on_surface` (`#dee5ff`) for primary text and `on_surface_variant` (`#a3aac4`) for secondary text to reduce eye strain in dark mode.
*   **Don't** use standard "drop shadows" on every card. This clutters the "Kinetic Pitch" aesthetic. Rely on color-shifting layers first.
*   **Don't** use sharp corners. Everything in this system should feel fast and ergonomic (mobile-first), utilizing the `Roundedness Scale`.