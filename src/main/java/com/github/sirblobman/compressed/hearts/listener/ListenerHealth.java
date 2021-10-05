package com.github.sirblobman.compressed.hearts.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.compressed.hearts.task.DisplayTask;

public class ListenerHealth implements Listener {
    private final HeartsPlugin plugin;
    
    public ListenerHealth(HeartsPlugin plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        check(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        check(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        check(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeal(EntityRegainHealthEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        check(player);
    }
    
    private boolean shouldScaleHealth(Player player) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if(playerData.isSet("scale-health")) return playerData.getBoolean("scale-health");
        
        FileConfiguration configuration = this.plugin.getConfig();
        return configuration.getBoolean("scale-health");
    }
    
    private void check(Player player) {
        checkScale(player);
        checkDisplay(player);
    }
    
    private void checkScale(Player player) {
        if(shouldScaleHealth(player)) {
            player.setHealthScaled(true);
            player.setHealthScale(20.0D);
            return;
        }
        
        player.setHealthScaled(false);
    }
    
    private void checkDisplay(Player player) {
        DisplayTask displayTask = this.plugin.getDisplayTask();
        displayTask.sendDisplay(player);
    }
}
