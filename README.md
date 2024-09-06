# PollMaster Plugin
<img src="https://img.shields.io/github/actions/workflow/status/Shweit/REPO/runtime.yml" /> <img src="https://img.shields.io/github/v/release/Shweit/REPO" /> <img src="https://img.shields.io/github/license/Shweit/REPO" />

## Overview
Short description of the project.

## Features
- **Feature 1:** Description of feature 1.
- **Feature 2:** Description of feature 2.

## Commands
`/command1 <arg1> <arg2> <arg3> ... [arg4]`
- **Description:** Description of the command.
- **Permission:** permission.node
- **Example:** `/command1 arg1 arg2 arg3`

## Installation
### Prerequisites
- **Java:** JDK 20 or higher is required to build and run the project.
- **Gradle:** Make sure Gradle is installed on your system.

### Cloning the Repository
1. Clone the repository to your local machine.
```shell
git clone git@github.com:Shweit/REPO.git
cd REPO
```
### Building the Project
2. Build the project using Gradle.
```shell
gradle build
```
### Setting up the Minecraft Server
3. Copy the generated JAR file to the `plugins` directory of your Minecraft server.
```shell
cp build/lib/PLUGIN-*.jar /path/to/your/minecraft/server/plugins
```
4. Start or restart your Minecraft server.
```shell
java -Xmx1024M -Xms1024M -jar paper-1.21.jar nogui
```
5.  Once the server is running, the plugin will be loaded automatically. You can verify it by running:
```shell
/plugins
```

## Future Features
- **Feature 3:** Description of feature 3.
- **Feature 4:** Description of feature 4.

## Contributing
Contributions are welcome! Please read the [contributing guidelines](CONTRIBUTING.md) to get started.