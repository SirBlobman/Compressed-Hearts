package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

import org.jetbrains.annotations.NotNull;

public final class CommandCompressedHeartsReload extends Command {
    private final HeartsPlugin plugin;
    
    public CommandCompressedHeartsReload(HeartsPlugin plugin) {
        super(plugin, "reload");
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
        if(!checkPermission(sender, "ch.command.compressed-hearts.reload", true)) {
            return true;
        }
        
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("language.yml");
        
        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguages();
    
        sendMessage(sender, "command.compressed-hearts.reload-success", null, true);
        return true;
    }
}
