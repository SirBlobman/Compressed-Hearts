package com.github.sirblobman.compressed.hearts.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.plugin.listener.PluginListener;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.compressed.hearts.event.PlayerChangeHeartsDisplayTypeEvent;
import com.github.sirblobman.compressed.hearts.object.DisplayType;
import com.github.sirblobman.compressed.hearts.task.DisplayTask;

public final class ListenerDisplayType extends PluginListener<HeartsPlugin> {
    public ListenerDisplayType(HeartsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeType(PlayerChangeHeartsDisplayTypeEvent e) {
        DisplayType oldType = e.getOldType();
        if(oldType != DisplayType.BOSS_BAR) {
            return;
        }
        
        DisplayType newType = e.getNewType();
        if(newType == DisplayType.BOSS_BAR) {
            return;
        }
        
        Player player = e.getPlayer();
        removeBossBar(player);
    }
    
    private void removeBossBar(Player player) {
        HeartsPlugin plugin = getPlugin();
        DisplayTask displayTask = plugin.getDisplayTask();
        displayTask.removeBossBar(player);
    }
}
