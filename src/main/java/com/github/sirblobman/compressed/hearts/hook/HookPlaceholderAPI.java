package com.github.sirblobman.compressed.hearts.hook;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;

import me.clip.placeholderapi.PlaceholderAPI;

public final class HookPlaceholderAPI {
    public static @NotNull String replace(@NotNull OfflinePlayer player, @NotNull String message) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (!pluginManager.isPluginEnabled("PlaceholderAPI")) {
            return message;
        }

        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
