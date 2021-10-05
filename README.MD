# Compressed Hearts
A plugin that allows you to change the heart scale of your players.

## Requirements
- [Spigot](https://www.spigotmc.org/) 1.8.8-1.17.1
- [SirBlobmanCore](https://www.spigotmc.org/resources/83189/) 2.3.1+

## Optional Requirements
- [PlaceholderAPI](https://www.spigotmc.org/resources/6245/) 2.10.10+

## Features
- Scaled player health to prevent screen overflow.
- Configurable display type for actual health information.
  - *Boss Bar:* Show the health as a bar on top of the player's screen
  - *Action Bar:* Show the health as a text line above the player's hotbar.
  - *No Display:* Don't show the health anywhere.
  - Boss Bar and Action Bar options support PlaceholderAPI.
- Configurable messages and optional per-player locale (requires manual translation).
- Option to show values as health (decimal) or hearts (integer) with custom formats.
- Option to hide health with magic formatting when player has wither effect applied.
