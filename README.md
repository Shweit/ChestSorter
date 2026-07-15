# ChestSorter

A powerful and customizable chest sorting plugin for Minecraft Paper 26.2 servers.

## Features

- **Multiple Sorting Methods**
  - Middle-click inside open containers to sort
  - Sneak + left-click containers from outside to sort
  - Sort via commands with coordinates
  - Sort player inventories

- **Flexible Sorting Options**
  - Sort by item name (alphabetically)
  - Sort by quantity (stack size)
  - Sort by material type
  - Sort by durability
  - Ascending or descending order

- **Per-Player Settings**
  - Each player can configure their own default sort preferences
  - Enable/disable the plugin per player (useful for players with client-side sorting mods)
  - Persistent settings stored in `players.yml`

- **Smart Features**
  - Location-based blacklist (prevent specific containers from being sorted)
  - Container type blacklist (e.g., hoppers)
  - Customizable sound effects on successful sort
  - Action bar feedback messages
  - First-join welcome message explaining features

- **Modern API Usage**
  - Built with modern Paper 26.2 APIs
  - Uses Brigadier command system for better tab completion
  - Adventure API for text components
  - Full Java 25 support

## Installation

1. Download the latest release from the [Releases](https://github.com/shweit/chestsorter/releases) page
2. Place the JAR file in your server's `plugins/` folder
3. Restart or reload your server
4. Configure the plugin via `plugins/ChestSorter/config.yml`

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/sortcontainer` | Sort the container you're looking at | `chestsorter.sort.container` |
| `/sortcontainer <x> <y> <z>` | Sort a container at specific coordinates | `chestsorter.sort.container` |
| `/sortcontainer <x> <y> <z> <type> <order>` | Sort with specific settings | `chestsorter.sort.container` |
| `/sortplayer` | Sort your own inventory | `chestsorter.sort.player.self` |
| `/sortplayer <player>` | Sort another player's inventory | `chestsorter.sort.player.others` |
| `/sortprefs` | View your current sort preferences | (none) |
| `/sortprefs toggle` | Enable/disable sorting for yourself | (none) |
| `/sortprefs type <type>` | Set your default sort type | (none) |
| `/sortprefs order <order>` | Set your default sort order | (none) |

### Sort Types
- `NAME` - Sort alphabetically by item name
- `QUANTITY` - Sort by stack size
- `MATERIAL_TYPE` - Sort by material/block type
- `DURABILITY` - Sort by item durability/damage

### Sort Orders
- `ASC` - Ascending (A-Z, low to high)
- `DESC` - Descending (Z-A, high to low)

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `chestsorter.sort.container` | Use `/sortcontainer` command | op |
| `chestsorter.sort.player.self` | Use `/sortplayer` for yourself | op |
| `chestsorter.sort.player.others` | Use `/sortplayer` on others | op |
| `chestsorter.sneak.sort` | Use sneak + left-click sorting | op |
| `chestsorter.middleclick.sort` | Use middle-click sorting | op |

## Configuration

```yaml
# Default sorting settings (used if player has no custom preferences)
default-sort-type: NAME  # Options: NAME, QUANTITY, MATERIAL_TYPE, DURABILITY
default-sort-order: ASC  # Options: ASC, DESC

# Sound settings
sound:
  enabled: true
  success: ITEM_BOOK_PAGE_TURN  # Sound to play on successful sort
  volume: 0.5
  pitch: 1.0

# Sneak+Left-Click sorting
sneak-sort:
  enabled: true

# Middle-Click sorting (in open containers)
middle-click:
  enabled: true

# Blacklist - Containers that should never be sorted
blacklist:
  # Specific locations (format: "world:x:y:z")
  locations: []
  # Example:
  # - "world:100:64:200"

  # Container types to never sort
  container-types:
    - "minecraft:hopper"
```

## Usage Examples

### Basic Usage
1. **Open a chest and middle-click** - The chest will be sorted using your preferences
2. **Look at a chest, sneak, and left-click** - Sorts without opening
3. **Use `/sortprefs type QUANTITY`** - All your future sorts will use quantity sorting

### Setting Up Per-Player Preferences
```
/sortprefs                          # View current settings
/sortprefs type QUANTITY            # Sort by stack size
/sortprefs order DESC               # Sort descending (largest first)
/sortprefs toggle                   # Disable/enable the plugin
```

### Sorting Specific Containers
```
/sortcontainer                      # Sort the container you're looking at
/sortcontainer ~ ~ ~                # Sort container at your position
/sortcontainer 100 64 200           # Sort container at coordinates
/sortcontainer ~ ~ ~ NAME ASC       # Sort with specific type and order
```

### Admin Commands
```
/sortplayer Steve                   # Sort Steve's inventory
/sortplayer Steve QUANTITY DESC     # Sort with specific settings
```

## Compatibility

### Client-Side Mods
If you or your players use client-side chest sorting mods (like Inventory Profiles Next), you can disable the plugin per-player:
```
/sortprefs toggle
```

This prevents conflicts while other players can still use the server-side sorting.

### Server Requirements
- **Paper 26.2** (or a compatible Folia 26.2 build)
- **Java 25+**

### Plugin Dependencies
- PaperLib (shaded, no need to install separately)

## Development

### Building from Source
```bash
git clone https://github.com/shweit/chestsorter.git
cd chestsorter
./gradlew build
```

The compiled JAR will be in `build/libs/ChestSorter-<version>.jar`

### Project Structure
```
src/
├── main/
│   ├── java/com/shweit/chestsorter/
│   │   ├── ChestSorter.java           # Main plugin class
│   │   ├── PlayerData.java            # Per-player settings manager
│   │   ├── commands/
│   │   │   └── SortCommands.java      # Brigadier command handlers
│   │   ├── listeners/
│   │   │   ├── MiddleClickSortListener.java
│   │   │   ├── SneakSortListener.java
│   │   │   └── PlayerJoinListener.java
│   │   └── sorting/
│   │       ├── InventorySorter.java   # Core sorting logic
│   │       ├── SortHelper.java        # Helper methods
│   │       ├── SortType.java          # Sort type enum
│   │       └── SortOrder.java         # Sort order enum
│   └── resources/
│       ├── config.yml                 # Default configuration
│       ├── players.yml                # Player settings template
│       └── paper-plugin.yml           # Plugin metadata
```

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before submitting pull requests.

## Code of Conduct

This project follows the [Code of Conduct](CODE_OF_CONDUCT.md). Please be respectful and inclusive.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Issues:** [GitHub Issues](https://github.com/shweit/chestsorter/issues)
- **Discussions:** [GitHub Discussions](https://github.com/shweit/chestsorter/discussions)

## Credits

Created by Dennis van den Brock (@shweit)

Built with:
- [Paper API](https://papermc.io/)
- [Adventure API](https://docs.adventure.kyori.net/)
- [PaperLib](https://github.com/PaperMC/PaperLib)

---

**Note:** The current `master` release line requires Paper 26.2 and Java 25. Minecraft 1.21 maintenance releases are built from the `1.21.x` branch.
