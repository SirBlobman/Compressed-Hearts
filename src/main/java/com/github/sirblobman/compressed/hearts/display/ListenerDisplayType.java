package com.github.sirblobman.compressed.hearts.display;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.plugin.listener.PluginListener;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.compressed.hearts.event.PlayerChangeDisplayTypeEvent;

public final class ListenerDisplayType extends PluginListener<HeartsPlugin> {
    private final Map<UUID, PlayerDisplayTask> taskMap;

    public ListenerDisplayType(HeartsPlugin plugin) {
        super(plugin);
        this.taskMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        addTask(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        removeTask(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        removeTask(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangeDisplayType(PlayerChangeDisplayTypeEvent e) {
        DisplayType oldType = e.getOldType();
        if (oldType != DisplayType.BOSS_BAR) {
            return;
        }

        DisplayType newType = e.getNewType();
        if (newType == DisplayType.BOSS_BAR) {
            return;
        }

        Player player = e.getPlayer();
        removeBossBar(player);
    }

    private void addTask(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        if (this.taskMap.containsKey(playerId)) {
            removeTask(player);
        }

        HeartsPlugin plugin = getPlugin();
        PlayerDisplayTask task = new PlayerDisplayTask(plugin, player);
        task.setPeriod(20L);

        TaskScheduler scheduler = plugin.getFoliaHelper().getScheduler();
        scheduler.scheduleEntityTask(task);
        this.taskMap.put(playerId, task);
    }

    private void removeTask(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        PlayerDisplayTask task = this.taskMap.remove(playerId);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    public @Nullable PlayerDisplayTask getDisplayTask(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        return this.taskMap.get(playerId);
    }

    private void removeBossBar(Player player) {
        PlayerDisplayTask task = getDisplayTask(player);
        if (task != null) {
            task.removeBossBar(player);
        }
    }
}
