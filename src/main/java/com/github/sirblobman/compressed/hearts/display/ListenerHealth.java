package com.github.sirblobman.compressed.hearts.display;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.plugin.listener.PluginListener;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

public final class ListenerHealth extends PluginListener<HeartsPlugin> {
    private final ListenerDisplayType listener;

    public ListenerHealth(@NotNull HeartsPlugin plugin, @NotNull ListenerDisplayType listener) {
        super(plugin);
        this.listener = listener;
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
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        check(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeal(EntityRegainHealthEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        check(player);
    }

    private boolean shouldScaleHealth(@NotNull Player player) {
        HeartsPlugin plugin = getPlugin();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);

        if (playerData.isSet("scale-health")) {
            return playerData.getBoolean("scale-health");
        }

        YamlConfiguration configuration = plugin.getConfig();
        return configuration.getBoolean("scale-health");
    }

    private void check(@NotNull Player player) {
        checkScale(player);
        checkDisplay(player);
    }

    private void checkScale(@NotNull Player player) {
        if (shouldScaleHealth(player)) {
            player.setHealthScaled(true);
            player.setHealthScale(20.0D);
            return;
        }

        player.setHealthScaled(false);
    }

    private @NotNull ListenerDisplayType getListener() {
        return this.listener;
    }

    private void checkDisplay(@NotNull Player player) {
        ListenerDisplayType listener = getListener();
        PlayerDisplayTask task = listener.getDisplayTask(player);
        if (task != null) {
            task.sendDisplay(player);
        }
    }
}
