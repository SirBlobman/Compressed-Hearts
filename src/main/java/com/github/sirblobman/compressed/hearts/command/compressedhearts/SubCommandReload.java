package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

import org.jetbrains.annotations.NotNull;

public final class SubCommandReload extends Command {
    private final HeartsPlugin plugin;

    public SubCommandReload(HeartsPlugin plugin) {
        super(plugin, "reload");
        setPermissionName("ch.command.compressed-hearts.reload");
        this.plugin = plugin;
    }

    @NotNull
    @Override
    protected LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        this.plugin.reloadConfig();
        sendMessage(sender, "command.compressed-hearts.reload-success");
        return true;
    }
}
