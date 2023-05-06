package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

public final class SubCommandReload extends Command {
    public SubCommandReload(@NotNull HeartsPlugin plugin) {
        super(plugin, "reload");
        setPermissionName("ch.command.compressed-hearts.reload");
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        JavaPlugin plugin = getPlugin();
        plugin.reloadConfig();

        sendMessage(sender, "command.compressed-hearts.reload-success");
        return true;
    }
}
