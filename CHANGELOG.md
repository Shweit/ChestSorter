# Changelog

All notable changes to ChestSorter will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2025-01-20

### Added
- **Per-Player Settings System**
  - Each player can now configure their own sorting preferences
  - Settings persist across server restarts in `players.yml`
  - New `/sortprefs` command to manage personal preferences
  - Players can enable/disable sorting individually (useful for client-side mod users)
  - Custom default sort type and order per player

- **Player Preferences Commands**
  - `/sortprefs` - View current settings
  - `/sortprefs toggle` - Enable/disable sorting for yourself
  - `/sortprefs type <NAME|QUANTITY|MATERIAL_TYPE|DURABILITY>` - Set default sort type
  - `/sortprefs order <ASC|DESC>` - Set default sort order

- **Welcome Message System**
  - First-time players receive a formatted welcome message
  - Explains plugin features and how to use them
  - Mentions `/sortprefs toggle` for players with client-side mods
  - Customizable in `config.yml`
  - Only shown once per player

- **Sound Effects**
  - Plays a configurable sound when sorting succeeds
  - Default: `ITEM_BOOK_PAGE_TURN` (subtle and non-annoying)
  - Configurable sound, volume, and pitch in `config.yml`
  - Can be disabled globally in config

- **Core Sorting Features**
  - Multiple sorting methods:
    - Middle-click in open containers to sort
    - Sneak + left-click containers from outside
    - Sort via commands with coordinates
    - Sort player inventories
  - Four sort types: NAME, QUANTITY, MATERIAL_TYPE, DURABILITY
  - Two sort orders: ASC (ascending), DESC (descending)

- **Brigadier Command System**
  - Modern Paper command API for better tab completion
  - `/sortcontainer` - Sort containers by raytrace or coordinates
  - `/sortplayer` - Sort your own or other players' inventories
  - Smart argument suggestions for sort types and orders
  - Player name suggestions for admin commands

- **Container Blacklist**
  - Location-based blacklist (prevent specific containers)
  - Container type blacklist (e.g., hoppers automatically excluded)
  - Fully configurable in `config.yml`

- **Quality of Life Features**
  - Action bar feedback messages for all operations
  - Colored messages with Adventure API
  - Permission-based access control
  - Configurable enable/disable for each sorting method

### Technical Details
- Built for Paper 1.21+ with modern APIs
- Uses Adventure API for text components
- Brigadier command system for modern command handling
- Full Java 21 support
- SpotBugs and Checkstyle compliant
- Comprehensive Javadoc documentation

### Configuration
- New `config.yml` with all settings
- New `players.yml` for per-player data storage
- All messages customizable
- Sound settings fully configurable

### Permissions
- `chestsorter.sort.container` - Use `/sortcontainer` command
- `chestsorter.sort.player.self` - Sort your own inventory
- `chestsorter.sort.player.others` - Sort other players' inventories
- `chestsorter.sneak.sort` - Use sneak + left-click sorting
- `chestsorter.middleclick.sort` - Use middle-click sorting

---

## Version History

### Legend
- **[Added]** - New features
- **[Changed]** - Changes in existing functionality
- **[Deprecated]** - Soon-to-be removed features
- **[Removed]** - Removed features
- **[Fixed]** - Bug fixes
- **[Security]** - Security improvements

---

## [2.0.1] - 2025-11-20

### Added
- **bStats Integration** ([#11](https://github.com/shweit/chestsorter/pull/11))
  - Server usage analytics to track plugin adoption
  - Anonymous statistics collection (can be disabled by server admins)
  - Metrics available at https://bstats.org/plugin/bukkit/chestsorter/28066
  - Helps understand plugin usage and guide future development

### Fixed
- Corrected welcome messages and project name references
- Fixed publish workflow configuration

### Dependencies
- Bumped `com.github.spotbugs:spotbugs-annotations` from 4.9.6 to 4.9.8 ([#7](https://github.com/shweit/chestsorter/pull/7))
- Bumped `com.github.spotbugs` from 6.4.2 to 6.4.5 ([#10](https://github.com/shweit/chestsorter/pull/10))
- Bumped `org.junit.jupiter:junit-jupiter` from 6.0.0 to 6.0.1 ([#9](https://github.com/shweit/chestsorter/pull/9))
- Bumped `org.junit.platform:junit-platform-launcher` to 6.0.1 ([#8](https://github.com/shweit/chestsorter/pull/8))

### Build
- Bumped `actions/checkout` from 5 to 6 ([#6](https://github.com/shweit/chestsorter/pull/6))
- Bumped `actions/upload-artifact` from 4 to 5 ([#5](https://github.com/shweit/chestsorter/pull/5))

---

## [Unreleased]

### Changed

- Updated the plugin to Minecraft 26.2 and Java 25
- Updated the Paper API and all build, test, runtime, and CI dependencies
- Added versioned release lines for Minecraft 26.2 and the maintained 1.21.x branch

---

## Release Notes

### 2.0.0 - Complete Rewrite
This is a complete rewrite of ChestSorter for modern Minecraft versions. The plugin has been rebuilt from the ground up using Paper 1.21+ APIs and Java 21.

**Major Highlights:**
- Per-player customization with persistent settings
- Welcome message system for new players
- Sound effects on successful sorting
- Modern Brigadier command system
- Multiple sorting methods (middle-click, sneak+left-click, commands)
- Flexible sorting options (by name, quantity, material, durability)
- Container blacklist system
- Full Adventure API integration

**Migration Notes:**
- This version requires Paper 1.21+ (will NOT work on Spigot or older versions)
- Requires Java 21+
- Configuration format may differ from older versions
- No backwards compatibility with ChestSorter v1.x

---

[2.0.1]: https://github.com/shweit/chestsorter/releases/tag/v2.0.1
[2.0.0]: https://github.com/shweit/chestsorter/releases/tag/v2.0.0
