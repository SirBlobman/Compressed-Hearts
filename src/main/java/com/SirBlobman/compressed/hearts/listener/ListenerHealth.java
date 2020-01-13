package com.SirBlobman.compressed.hearts.listener;

import java.text.DecimalFormat;
import java.util.Collection;

import com.SirBlobman.api.SirBlobmanAPI;
import com.SirBlobman.api.nms.EntityHandler;
import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.nms.PlayerHandler;
import com.SirBlobman.api.nms.boss.bar.BossBarHandler;
import com.SirBlobman.compressed.hearts.CompressedHearts;
import com.SirBlobman.compressed.hearts.configuration.DisplayType;
import com.SirBlobman.compressed.hearts.scoreboard.ScoreboardUtil;

import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitRunnable;

public class ListenerHealth extends BukkitRunnable implements Listener {
    private final CompressedHearts plugin;
    public ListenerHealth(CompressedHearts plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        Collection<? extends Player> playerList = Bukkit.getOnlinePlayers();
        playerList.forEach(this::checkTimer);
    }
    
    private boolean shouldNotScaleHealth() {
        FileConfiguration config = this.plugin.getConfig();
        return !config.getBoolean("default-options.scale-health", true);
    }
    
    private boolean shouldNotAlwaysShow() {
        FileConfiguration config = this.plugin.getConfig();
        return !config.getBoolean("default-options.always-show", false);
    }
    
    private boolean shouldShowHearts() {
        FileConfiguration config = this.plugin.getConfig();
        return config.getBoolean("default-options.show-hearts", true);
    }
    
    private DisplayType getDisplayType() {
        FileConfiguration config = this.plugin.getConfig();
        String displayTypeString = config.getString("default-options.display-type");
    
        try {
            return DisplayType.valueOf(displayTypeString);
        } catch(IllegalArgumentException ex) {
            return DisplayType.BOSS_BAR;
        }
    }
    
    private boolean shouldNotScaleHealth(Player player) {
        if(player == null) return true;
    
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        YamlConfiguration config = api.getDataFile(player);
        return !config.getBoolean("scale-health", !shouldNotScaleHealth());
    }
    
    private boolean shouldNotAlwaysShow(Player player) {
        if(player == null) return true;
    
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        YamlConfiguration config = api.getDataFile(player);
        return !config.getBoolean("always-show", !shouldNotAlwaysShow());
    }
    
    private boolean shouldShowHearts(Player player) {
        if(player == null) return true;
    
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        YamlConfiguration config = api.getDataFile(player);
        return config.getBoolean("show-hearts", shouldShowHearts());
    }
    
    private DisplayType getDisplayType(Player player) {
        if(player == null) return DisplayType.BOSS_BAR;
        
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        YamlConfiguration config = api.getDataFile(player);
        
        String displayTypeString = config.getString("display-type", getDisplayType().name());
    
        try {
            return DisplayType.valueOf(displayTypeString);
        } catch(IllegalArgumentException ex) {
            return getDisplayType();
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        check(player);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        check(player);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        check(player);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onHeal(EntityRegainHealthEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        check(player);
    }
    
    private void checkTimer(Player player) {
        if(player == null) return;
        if(shouldNotAlwaysShow(player)) return;
        check(player);
    }
    
    private void check(Player player) {
        if(player == null) return;
        checkScale(player);
        checkDisplay(player);
    }
    
    private void checkScale(Player player) {
        if(player == null) return;
        if(shouldNotScaleHealth(player)) return;
    
        player.setHealthScale(20.0D);
    }
    
    private String getMessage(Player player) {
        if(player == null) return "";
        FileConfiguration config = this.plugin.getConfig();
        DecimalFormat format = new DecimalFormat("0.00");
    
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        NMS_Handler nmsHandler = api.getVersionHandler();
        PlayerHandler playerHandler = nmsHandler.getPlayerHandler();
        EntityHandler entityHandler = nmsHandler.getEntityHandler();
        
        double health = player.getHealth();
        String healthString = format.format(health);
        
        double absorption = playerHandler.getAbsorptionHearts(player);
        String absString  = format.format(absorption);
        
        double maxHealth = entityHandler.getMaxHealth(player);
        String maxHealthString = format.format(maxHealth);
        
        if(shouldShowHearts(player)) {
            long hearts = Math.round(health / 2.0D);
            String heartsString = Long.toString(hearts);
            
            long maxHearts = Math.round(maxHealth / 2.0D);
            String maxHeartsString = Long.toString(maxHearts);
            
            long absHearts = Math.round(absorption / 2.0D);
            String absHeartsString = Long.toString(absHearts);
            
            String message = config.getString("messages.display-hearts");
            message = message.replace("{hearts}", heartsString).replace("{max_hearts}", maxHeartsString);
            
            if(absHearts > 0) {
                String absMessage = config.getString("messages.absorption-hearts");
                absMessage = absMessage.replace("{absorb_hearts}", absHeartsString);
                message += absMessage;
            }
            
            return message;
        }
    
        String message = config.getString("messages.display-health");
        message = message.replace("{health}", healthString).replace("{max_health}", maxHealthString);
    
        if(absorption > 0) {
            String absMessage = config.getString("messages.absorption-health");
            absMessage = absMessage.replace("{absorb_health}", absString);
            message += absMessage;
        }
    
        return message;
    }
    
    private void checkDisplay(Player player) {
        if(player == null) return;
        
        DisplayType displayType = getDisplayType(player);
        switch(displayType) {
            case BOSS_BAR:
                checkBossBar(player);
                break;
                
            case ACTION_BAR:
                checkActionBar(player);
                break;
                
            case BELOW_NAME:
                checkScoreBoard(player);
                break;
                
            default: break;
        }
    }
    
    private void checkBossBar(Player player) {
        if(player == null) return;
        
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        NMS_Handler nmsHandler = api.getVersionHandler();
        BossBarHandler bossBarHandler = nmsHandler.getBossBarHandler();
        
        String message = getMessage(player);
        bossBarHandler.updateBossBar(player, message, 1.0D, "BLUE", "SOLID");
    }
    
    private void checkActionBar(Player player) {
        if(player == null) return;
    
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        NMS_Handler nmsHandler = api.getVersionHandler();
        PlayerHandler playerHandler = nmsHandler.getPlayerHandler();
        
        String message = getMessage(player);
        playerHandler.sendActionBar(player, message);
    }
    
    private void checkScoreBoard(Player player) {
        double health = player.getHealth();
        int hearts = (int) (health / 2.0D);
        ScoreboardUtil.updateScoreboard(player, hearts);
    }
}