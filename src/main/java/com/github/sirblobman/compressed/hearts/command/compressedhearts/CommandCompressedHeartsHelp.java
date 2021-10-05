package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

import org.jetbrains.annotations.NotNull;

public final class CommandCompressedHeartsHelp extends Command {
    private final HeartsPlugin plugin;
    
    public CommandCompressedHeartsHelp(HeartsPlugin plugin) {
        super(plugin, "help");
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
        if(!checkPermission(sender, "ch.command.compressed-hearts.help", true)) {
            return true;
        }
        
        sendMessage(sender, "command.compressed-hearts.help-message", null, true);
        return true;
    }
}
