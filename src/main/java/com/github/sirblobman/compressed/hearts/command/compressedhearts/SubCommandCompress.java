package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

public final class SubCommandCompress extends PlayerCommand {
    private final HeartsPlugin plugin;

    public SubCommandCompress(@NotNull HeartsPlugin plugin) {
        super(plugin, "compress");
        setPermissionName("ch.command.compressed-hearts.compress");
        this.plugin = plugin;
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull Player player, String @NotNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull Player player, String @NotNull [] args) {
        HeartsPlugin plugin = getHeartsPlugin();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        boolean scaleHealth = playerData.getBoolean("scale-health");

        if (scaleHealth) {
            playerData.set("scale-health", false);
            playerDataManager.save(player);
            player.setHealthScaled(false);

            sendMessage(player, "command.compressed-hearts.compress.disabled");
            return true;
        }

        playerData.set("scale-health", true);
        playerDataManager.save(player);
        player.setHealthScaled(true);
        player.setHealthScale(20.0D);

        sendMessage(player, "command.compressed-hearts.compress.enabled");
        return true;
    }

    private @NotNull HeartsPlugin getHeartsPlugin() {
        return this.plugin;
    }
}
