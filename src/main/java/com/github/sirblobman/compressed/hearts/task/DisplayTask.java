package com.github.sirblobman.compressed.hearts.task;

import java.text.DecimalFormat;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.bossbar.BossBarHandler;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.compressed.hearts.object.DisplayType;

public final class DisplayTask extends BukkitRunnable {
    private final HeartsPlugin plugin;
    
    public DisplayTask(HeartsPlugin plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }
    
    @Override
    public void run() {
        Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
        for(Player player : onlinePlayerCollection) {
            checkDisplay(player);
        }
    }
    
    public void sendDisplay(Player player) {
        if(shouldUseHearts(player)) {
            sendHeartsDisplay(player);
            return;
        }
        
        sendHealthDisplay(player);
    }
    
    private HeartsPlugin getPlugin() {
        return this.plugin;
    }
    
    private ConfigurationManager getConfigurationManager() {
        HeartsPlugin plugin = getPlugin();
        return plugin.getConfigurationManager();
    }
    
    private LanguageManager getLanguageManager() {
        HeartsPlugin plugin = getPlugin();
        return plugin.getLanguageManager();
    }
    
    private PlayerDataManager getPlayerDataManager() {
        HeartsPlugin plugin = getPlugin();
        return plugin.getPlayerDataManager();
    }
    
    private MultiVersionHandler getMultiVersionHandler() {
        HeartsPlugin plugin = getPlugin();
        return plugin.getMultiVersionHandler();
    }
    
    private PlayerHandler getPlayerHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getPlayerHandler();
    }
    
    private EntityHandler getEntityHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getEntityHandler();
    }
    
    private BossBarHandler getBossBarHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getBossBarHandler();
    }
    
    private boolean shouldAlwaysShow(Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if(playerData.isSet("always-show")) {
            return playerData.getBoolean("always-show");
        }
        
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("always-show");
    }
    
    private boolean shouldUseHearts(Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if(playerData.isSet("show-hearts")) {
            return playerData.getBoolean("show-hearts");
        }
        
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("show-hearts");
    }
    
    private DisplayType getDisplayType(Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if(playerData.isSet("display-type")) {
            String displayTypeString = playerData.getString("display-type");
            return DisplayType.parse(displayTypeString);
        }
        
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        String displayTypeString = configuration.getString("display-type");
        return DisplayType.parse(displayTypeString);
    }
    
    private boolean hasWitherEffect(Player player) {
        return player.hasPotionEffect(PotionEffectType.WITHER);
    }
    
    private void checkDisplay(Player player) {
        if(shouldAlwaysShow(player)) {
            sendDisplay(player);
        }
    }
    
    private void sendHealthDisplay(Player player) {
        DisplayType displayType = getDisplayType(player);
        if(displayType == null || displayType == DisplayType.NONE) {
            return;
        }
        
        LanguageManager languageManager = getLanguageManager();
        PlayerHandler playerHandler = getPlayerHandler();
        EntityHandler entityHandler = getEntityHandler();
        
        String decimalFormatString = languageManager.getMessage(player, "display.decimal-format",
                null, false);
        DecimalFormat decimalFormat = new DecimalFormat(decimalFormatString);
        
        double normalHealth = player.getHealth();
        String normalHealthString = decimalFormat.format(normalHealth);
        
        double maxHealth = entityHandler.getMaxHealth(player);
        String maxHealthString = decimalFormat.format(maxHealth);
        
        String messagePath = (hasWitherEffect(player) ? "display.wither-health-format" : "display.health-format");
        String messageFormat = languageManager.getMessage(player, messagePath, null, true);
        String message = messageFormat.replace("{health}", normalHealthString)
                .replace("{max_health}", maxHealthString);
        
        double absorptionHealth = playerHandler.getAbsorptionHearts(player);
        if(absorptionHealth > 0.0D) {
            String absorptionHealthString = decimalFormat.format(absorptionHealth);
            String absorptionHealthFormat = languageManager.getMessage(player, "display.absorption-health-format",
                    null, true);
            String absorptionHealthMessage = absorptionHealthFormat.replace("{absorb_health}",
                    absorptionHealthString);
            message += absorptionHealthMessage;
        }
        
        if(displayType == DisplayType.ACTION_BAR) {
            playerHandler.sendActionBar(player, message);
            return;
        }
        
        if(displayType == DisplayType.BOSS_BAR) {
            BossBarHandler bossBarHandler = getBossBarHandler();
            bossBarHandler.updateBossBar(player, message, 1.0D, "BLUE", "SOLID");
        }
    }
    
    private void sendHeartsDisplay(Player player) {
        LanguageManager languageManager = this.plugin.getLanguageManager();
        MultiVersionHandler multiVersionHandler = this.plugin.getMultiVersionHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        BossBarHandler bossBarHandler = multiVersionHandler.getBossBarHandler();
        
        double normalHealth = player.getHealth();
        long normalHearts = Math.round(normalHealth / 2.0D);
        String normalHeartsString = Long.toString(normalHearts);
        
        double maxHealth = entityHandler.getMaxHealth(player);
        long maxHearts = Math.round(maxHealth / 2.0D);
        String maxHeartsString = Long.toString(maxHearts);
        
        String messagePath = (hasWitherEffect(player) ? "display.wither-hearts-format" : "display.hearts-format");
        String messageFormat = languageManager.getMessage(player, messagePath, null, true);
        String message = messageFormat.replace("{hearts}", normalHeartsString)
                .replace("{max_hearts}", maxHeartsString);
        
        double absorptionHealth = playerHandler.getAbsorptionHearts(player);
        long absorptionHearts = Math.round(absorptionHealth / 2.0D);
        if(absorptionHearts > 0) {
            String absorptionHeartsString = Long.toString(absorptionHearts);
            String absorptionHeartsFormat = languageManager.getMessage(player, "display.absorption-hearts-format",
                    null, true);
            String absorptionHeartsMessage = absorptionHeartsFormat
                    .replace("{absorb_hearts}", absorptionHeartsString);
            message += absorptionHeartsMessage;
        }
        
        DisplayType displayType = getDisplayType(player);
        if(displayType == null || displayType == DisplayType.NONE) return;
        
        if(displayType == DisplayType.ACTION_BAR) {
            playerHandler.sendActionBar(player, message);
        } else if(displayType == DisplayType.BOSS_BAR) {
            bossBarHandler.updateBossBar(player, message, 1.0D, "BLUE", "SOLID");
        }
    }
}
