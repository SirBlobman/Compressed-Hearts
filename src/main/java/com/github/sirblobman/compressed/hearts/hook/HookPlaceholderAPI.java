package com.github.sirblobman.compressed.hearts.hook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;

import me.clip.placeholderapi.PlaceholderAPI;

public final class HookPlaceholderAPI {
    public static String replace(OfflinePlayer player, String message) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (!pluginManager.isPluginEnabled("PlaceholderAPI")) {
            return message;
        }

        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
