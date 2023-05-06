package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

public final class CommandCompressedHearts extends Command {
    public CommandCompressedHearts(HeartsPlugin plugin) {
        super(plugin, "compressed-hearts");
        setPermissionName("ch.command.compressed-hearts");
        addSubCommand(new SubCommandCompress(plugin));
        addSubCommand(new SubCommandDisplay(plugin));
        addSubCommand(new SubCommandHelp(plugin));
        addSubCommand(new SubCommandReload(plugin));
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        return false;
    }
}
