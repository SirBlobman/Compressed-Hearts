package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
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
        if(args.length == 0) {
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
        DisplayType displayType = parseDisplayType(sub);
        if(displayType == null) {
            return false;
        }
        
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        playerData.set("display-type", displayType.name());
        playerDataManager.save(player);
        
        Replacer replacer = message -> message.replace("{display-type}", displayType.name());
        sendMessage(player, "command.compressed-hearts.change-display", replacer, true);
        return true;
    }
    
    @Nullable
    private DisplayType parseDisplayType(String value) {
        if(value.equalsIgnoreCase("bossbar")) {
            value = "boss_bar";
        }
        
        if(value.equalsIgnoreCase("actiobar")) {
            value = "action_bar";
        }
        
        return DisplayType.parse(value);
    }
}
