package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

import org.jetbrains.annotations.NotNull;

public final class SubCommandCompress extends PlayerCommand {
    private final HeartsPlugin plugin;
    
    public SubCommandCompress(HeartsPlugin plugin) {
        super(plugin, "compress");
        this.plugin = plugin;

        setPermissionName("ch.command.compressed-hearts.compress");
    }
    
    @NotNull
    @Override
    protected LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }
    
    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
    
    @Override
    protected boolean execute(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        boolean scaleHealth = playerData.getBoolean("scale-health");
        
        if(scaleHealth) {
            playerData.set("scale-health", false);
            playerDataManager.save(player);
            player.setHealthScaled(false);
            
            sendMessage(player, "command.compressed-hearts.compress.disabled", null);
            return true;
        }
        
        playerData.set("scale-health", true);
        playerDataManager.save(player);
        player.setHealthScaled(true);
        player.setHealthScale(20.0D);
        
        sendMessage(player, "command.compressed-hearts.compress.enabled", null);
        return true;
    }
}
