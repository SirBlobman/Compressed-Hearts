# Compressed Hearts

A plugin that allows you to change the heart scale of your players.

## Requirements

- [Spigot](https://www.spigotmc.org/) 1.8.8-1.21
- [SirBlobmanCore](https://www.spigotmc.org/resources/83189/) 2.9.6+

## Optional Requirements

- [PlaceholderAPI](https://www.spigotmc.org/resources/6245/) 2.11.6+

## Features

- Scaled player health to prevent screen overflow.
- Configurable display type for actual health information.
    - **Boss Bar:** Show the health as a bar on top of the player's screen
    - **Action Bar:** Show the health as a text line above the player's hotbar.
    - **No Display:** Don't show the health anywhere.
    - Boss Bar and Action Bar options support PlaceholderAPI.
- Configurable messages and optional per-player locale (requires manual translation).
- Option to show values as health (decimal) or hearts (integer) with custom formats.
- Option to hide health with magic formatting when player has wither effect applied.
- Command for players to choose whether they want their health to scale and their display type.

## Commmands

- **/hp**:
    - **Description:** Check your current health.
    - **Permission:** `ch.command.hp`
    - **Aliases:** `/hearts, /health`
    - **Sub-Commands:**
        - **/hp &lt;player&gt;**:
            - **Description:** Check the health of another player.

- **/compressed-hearts**:
    - **Description:** Administrator and settings command for Compressed Hearts.
    - **Permission:** `ch.command.compressed-hearts`
    - **Aliases:** `/ch, /compressedhearts, /chearts, /compressedh`
    - **Sub-Commands:**
        - **/compressed-hearts reload**:
            - **Description:** Reload the configuration files.
            - **Permission:** `ch.command.compressed-hearts.reload`
        - **/compressed-hearts help**:
            - **Description:** View a list of commands from Compressed Hearts.
            - **Permission:** `ch.command.compressed-hearts.help`
        - **/compressed-hearts compress**:
            - **Description:** Toggle whether your hearts are scaled or not.
            - **Permission:** `ch.command.compressed-hearts.compress`
        - **/compressed-hearts display &lt;bossbar &vert; actionbar &vert; none&gt;**:
            - **Description:** Change the display type for your health.
            - **Permission:** `ch.command.compressed-hearts.display`
