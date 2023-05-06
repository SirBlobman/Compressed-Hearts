package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

public final class SubCommandHelp extends Command {
    public SubCommandHelp(@NotNull HeartsPlugin plugin) {
        super(plugin, "help");
        setPermissionName("ch.command.compressed-hearts.help");
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        sendMessage(sender, "command.compressed-hearts.help-message");
        return true;
    }
}
