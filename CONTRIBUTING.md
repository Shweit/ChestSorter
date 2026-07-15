# How to Contribute

## General workflow
0. (External contributors only) Create a fork of the repository
1. Pull any changes from `master` to make sure you're up-to-date
2. Create a branch from `master`
    * Give your branch a name that describes your change (e.g. add-scoreboard)
    * Focus on one change per branch
3. Commit your changes
    * Keep your commits small and focused
    * Write descriptive commit messages in [Conventional Commit](https://www.conventionalcommits.org/en/v1.0.0/) format
4. When you're ready, create a pull request to `master`
   * Keep your PRs small (preferably <300 LOC)
   * Format your title in [Conventional Commit](https://www.conventionalcommits.org/en/v1.0.0/) format
   * List any changes made in your description
   * Link any issues that your pull request is related to as well

### Example:
```text
Create scoreboard for total points

ADDED - Scoreboard displayed in-game at game end  
CHANGED - Updated `StorageManager` class to persist scoreboard data
```

After the pull request has been reviewed, approved, and passes all automated checks, it will be merged into master.

## Minecraft version support

The repository uses one active development line and maintenance branches for older Minecraft
versions:

- `master` targets the latest supported Minecraft and Paper release.
- `<version>.x` branches, such as `1.21.x`, receive compatible bug and security fixes only.
- New features are developed on `master` and are backported selectively with `git cherry-pick`.
- Maintenance branches are not merged back into `master`.

Create a maintenance branch from the final compatible commit before updating `master` to a new
Minecraft compatibility line. A separate branch is not needed for Paper build updates or Minecraft
patch releases that do not require different source code, API metadata, or a different Java runtime.

Release tags must include both the plugin version and Minecraft target:

```text
v2.1.0-mc26.2
v2.0.4-mc1.21
```

The publishing workflow uses this suffix to select the correct Minecraft range and Java version.
The Paper dependency, Java toolchain, and `plugin.yml` API version remain branch-specific.
