package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.compressed.hearts.event.PlayerChangeHeartsDisplayTypeEvent;
import com.github.sirblobman.compressed.hearts.object.DisplayType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandCompressedHeartsDisplay extends PlayerCommand {
    private final HeartsPlugin plugin;
    
    public CommandCompressedHeartsDisplay(HeartsPlugin plugin) {
        super(plugin, "display");
        this.plugin = plugin;
    }
    
    @NotNull
    @Override
    protected LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }
    
    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if(args.length == 1) {
            return getMatching(args[0], "bossbar", "actionbar", "none");
        }
        
        return Collections.emptyList();
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        if(!checkPermission(player, "ch.command.compressed-hearts.display", true)) {
            return true;
        }
        
        if(args.length < 1) {
            return false;
        }
        
        String sub = args[0];
        DisplayType newDisplayType = parseDisplayType(sub);
        if(newDisplayType == null) {
            Replacer replacer = message -> message.replace("{value}", sub);
            sendMessage(player, "error.invalid-display-type", replacer, true);
            return true;
        }
        
        DisplayType oldDisplayType = getDisplayType(player);
        if(oldDisplayType == newDisplayType) {
            Replacer replacer = message -> message.replace("{value}", newDisplayType.name());
            sendMessage(player, "command.compressed-hearts.display-already-matches", replacer, true);
            return true;
        }
        
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        playerData.set("display-type", newDisplayType.name());
        playerDataManager.save(player);
    
        Replacer replacer = message -> message.replace("{display-type}", newDisplayType.name());
        sendMessage(player, "command.compressed-hearts.change-display", replacer, true);
    
        Event event = new PlayerChangeHeartsDisplayTypeEvent(player, oldDisplayType, newDisplayType);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(event);
        return true;
    }
    
    private HeartsPlugin getHeartsPlugin() {
        return this.plugin;
    }
    
    private ConfigurationManager getConfigurationManager() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getConfigurationManager();
    }
    
    private PlayerDataManager getPlayerDataManager() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getPlayerDataManager();
    }
    
    @Nullable
    private DisplayType parseDisplayType(String value) {
        if(value.equalsIgnoreCase("bossbar")) {
            value = "boss_bar";
        }
        
        if(value.equalsIgnoreCase("actionbar")) {
            value = "action_bar";
        }
        
        return DisplayType.parse(value);
    }
    
    @NotNull
    private DisplayType getDisplayType(Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if(playerData.isSet("display-type")) {
            String displayTypeString = playerData.getString("display-type");
            DisplayType displayType = DisplayType.parse(displayTypeString);
            return (displayType == null ? DisplayType.NONE : displayType);
        }
        
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        String displayTypeString = configuration.getString("display-type");
        DisplayType displayType = DisplayType.parse(displayTypeString);
        return (displayType == null ? DisplayType.NONE : displayType);
    }
}
