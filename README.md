# PollMaster Plugin
<img src="https://img.shields.io/github/actions/workflow/status/Shweit/ChestSorter/runtime.yml" /> <img src="https://img.shields.io/github/v/release/Shweit/ChestSorter" /> <img src="https://img.shields.io/github/license/Shweit/ChestSorter" />

## Overview
Short description of the project.

## Features
- **Sorting by Amount:** Organizes items in a container based on their total stack size, placing larger stacks first.
- **Sorting Alphabetically:** Sorts items in the container based on their names.
- **Sorting by Category:** Groups items based on their Minecraft creative categories.
  - **Random Shuffle:** Randomly shuffles all items in the container.

## Commands
`/sort <player/coordinates> [sortType]`
- **Description:** Sorts the items in the specified container or player's inventory based on the specified criteria.
- **Permission:** permission.node
- **Example:** 
  - `/sort` -> Sorts the Player's inventory alphabetically.
  - `/sort Shweit amount` -> Sorts the Player's inventory by amount.
  - `/sort 0 0 0 category` -> Sorts the container at coordinates (0, 0, 0) by category.

## Installation
### Prerequisites
- **Java:** JDK 20 or higher is required to build and run the project.
- **Gradle:** Make sure Gradle is installed on your system.

### Cloning the Repository
1. Clone the repository to your local machine.
```shell
git clone git@github.com:Shweit/ChestSorter.git
cd ChestSorter
```
### Building the Project
2. Build the project using Gradle.
```shell
gradle build
```
### Setting up the Minecraft Server
3. Copy the generated JAR file to the `plugins` directory of your Minecraft server.
```shell
cp build/libs/ChestSorter-*.jar /path/to/your/minecraft/server/plugins
```
4. Start or restart your Minecraft server.
```shell
java -Xmx1024M -Xms1024M -jar paper-1.21.jar nogui
```
5.  Once the server is running, the plugin will be loaded automatically. You can verify it by running:
```shell
/plugins
```

## Contributing
Contributions are welcome! Please read the [contributing guidelines](CONTRIBUTING.md) to get started.