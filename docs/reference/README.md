# Reference players: Accord and Flamingo

Documentation for two Android music players studied while working on ChoraLiqGla's
player and backgrounds. Every tracked file in both repositories is covered by an
inventory, and the important subsystems get a written architecture guide.

## Snapshots

| Project | Repository | Branch / commit | Tracked files | Licence |
|---|---|---|---|---|
| **Accord** | <https://github.com/emylfy/Accord> | `alpha` @ `653cb86` | 432 | GPL-3.0 |
| **Flamingo** | <https://github.com/shouryadixitisverycool/Flamingo> | `master` @ `316df89` | 273 | GPL-3.0 |

The Flamingo repository is a fork of [Yos-X/FlamingoSank](https://github.com/Yos-X/FlamingoSank).
Accord is a fork of [Gramophone](https://github.com/AkaneTan/Gramophone) /
[AccordLegacy](https://github.com/FoedusProgramme/AccordLegacy); its own readme says the
fork is no longer maintained and points at Accord 2.0.

These documents describe the code **as pinned above**. They are reference material,
not a claim that either project is current or that its behaviour is correct.

## Documents

| Document | What it covers |
|---|---|
| [background-methods.md](background-methods.md) | Accord's `BlendView` and Flamingo's `YosFloatingLight` in detail, compared with ChoraLiqGla's `ArtworkBlur.kt` |
| [accord/README.md](accord/README.md) | Accord's build, modules, playback service, library/database, UI, lyrics, settings, vendored code |
| [accord/inventory.md](accord/inventory.md) | **Every one of Accord's 432 tracked files**, grouped by directory, with a description |
| [flamingo/README.md](flamingo/README.md) | Flamingo's build, modules, playback controller, library, Compose UI, lyrics, effects and settings |
| [flamingo/inventory.md](flamingo/inventory.md) | **Every one of Flamingo's 273 tracked files**, grouped by directory, with a description |

## How “every file” is covered

`inventory.md` is not a summary of selected files. It is generated from
`git ls-files`, so it contains one row for each tracked path at the pinned commit:

- **Kotlin/Java** — package, KDoc summary when present, otherwise the top-level
  classes, objects, functions and properties declared in the file.
- **XML** — root element plus the meaningful contents: resource counts and keys,
  layout ids/custom views, menu items, preference keys, manifest component counts.
- **Images and binaries** — actual format and dimensions where available; bundled
  `.aar` archives are identified as libraries rather than reverse-engineered.
- **Gradle, ProGuard, properties, Markdown, CI files** — the role of the file and
  its notable entries.

To check coverage, compare the number of `git ls-files` paths at the pinned commit
with the row count in each inventory. They match: 432/432 and 273/273.

## Caveats

- Descriptions are intentionally factual and short. They explain what a file is for,
  not every line of it.
- `Accord/recyclerview` and `com.google.android.material.slider` are vendored
  third-party sources shipped inside the repository; they are documented as vendored
  modules rather than treated as Accord's own design.
- Translation `strings.xml` files and icon drawables are listed individually in the
  inventory, with their locale/type and key counts, not one prose page each.
- Code snippets are quoted only as needed to explain the reference implementations.
  Both upstream projects are GPL-3.0; keep that in mind before copying code.
