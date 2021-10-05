package com.github.sirblobman.compressed.hearts.task;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.Language;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.bossbar.BossBarHandler;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.compressed.hearts.hook.HookPlaceholderAPI;
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
        boolean hearts = shouldUseHearts(player);
        String message = (hearts ? getHeartsDisplayMessage(player) : getHealthDisplayMessage(player));
        if(message == null || message.isEmpty()) {
            return;
        }
        
        DisplayType displayType = getDisplayType(player);
        if(displayType == null || displayType == DisplayType.NONE) {
            return;
        }
        
        switch(displayType) {
            case BOSS_BAR: sendBossBar(player, message);
            case ACTION_BAR: sendActionBar(player, message);
            default: break;
        }
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
    
    private DecimalFormat getDecimalFormat(Player player) {
        LanguageManager languageManager = getLanguageManager();
        Language language = languageManager.getLanguage(player);
        Optional<Locale> optionalLocale = language.getJavaLocale();
        
        Locale locale = optionalLocale.orElse(Locale.US);
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
        
        String path = ("display.decimal-format");
        String decimalFormatString = languageManager.getMessage(player, path, null, false);
        return new DecimalFormat(decimalFormatString, decimalFormatSymbols);
    }
    
    private DecimalFormat getIntegerFormat(Player player) {
        LanguageManager languageManager = getLanguageManager();
        Language language = languageManager.getLanguage(player);
        Optional<Locale> optionalLocale = language.getJavaLocale();
        
        Locale locale = optionalLocale.orElse(Locale.US);
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
        
        String path = ("display.integer-format");
        String decimalFormatString = languageManager.getMessage(player, path, null, false);
        return new DecimalFormat(decimalFormatString, decimalFormatSymbols);
    }
    
    private boolean hasWitherEffect(Player player) {
        if(player == null) {
            return false;
        }
        
        return player.hasPotionEffect(PotionEffectType.WITHER);
    }
    
    private double getHealth(Player player) {
        if(player == null) {
            return 0.0D;
        }
        
        return player.getHealth();
    }
    
    private boolean hasHealth(Player player) {
        double health = getHealth(player);
        return (health > 0.0D);
    }
    
    private String getHealthString(Player player) {
        double health = getHealth(player);
        DecimalFormat decimalFormat = getDecimalFormat(player);
        return decimalFormat.format(health);
    }
    
    private double getMaxHealth(Player player) {
        EntityHandler entityHandler = getEntityHandler();
        return entityHandler.getMaxHealth(player);
    }
    
    private String getMaxHealthString(Player player) {
        double maxHealth = getMaxHealth(player);
        DecimalFormat decimalFormat = getDecimalFormat(player);
        return decimalFormat.format(maxHealth);
    }
    
    private double getAbsorptionHealth(Player player) {
        PlayerHandler playerHandler = getPlayerHandler();
        return playerHandler.getAbsorptionHearts(player);
    }
    
    private boolean hasAbsorptionHealth(Player player) {
        double absorptionHealth = getAbsorptionHealth(player);
        return (absorptionHealth > 0.0D);
    }
    
    private String getAbsorptionHealthString(Player player) {
        double absorptionHealth = getAbsorptionHealth(player);
        DecimalFormat decimalFormat = getDecimalFormat(player);
        return decimalFormat.format(absorptionHealth);
    }
    
    private long ceil(double value) {
        double ceil = Math.ceil(value);
        return Math.round(ceil);
    }
    
    private long getHearts(Player player) {
        double health = getHealth(player);
        return ceil(health / 2.0D);
    }
    
    private long getMaxHearts(Player player) {
        double maxHealth = getMaxHealth(player);
        return ceil(maxHealth / 2.0D);
    }
    
    private long getAbsorptionHearts(Player player) {
        double absorptionHealth = getAbsorptionHealth(player);
        return ceil(absorptionHealth / 2.0D);
    }
    
    private String getHeartsString(Player player) {
        long hearts = getHearts(player);
        DecimalFormat integerFormat = getIntegerFormat(player);
        return integerFormat.format(hearts);
    }
    
    private String getMaxHeartsString(Player player) {
        long maxHearts = getMaxHearts(player);
        DecimalFormat integerFormat = getIntegerFormat(player);
        return integerFormat.format(maxHearts);
    }
    
    private String getAbsorptionHeartsString(Player player) {
        long absorptionHearts = getAbsorptionHearts(player);
        DecimalFormat integerFormat = getIntegerFormat(player);
        return integerFormat.format(absorptionHearts);
    }
    
    private void checkDisplay(Player player) {
        if(shouldAlwaysShow(player)) {
            sendDisplay(player);
        }
    }
    
    private String getHealthDisplayMessage(Player player) {
        LanguageManager languageManager = getLanguageManager();
        boolean witherEffect = hasWitherEffect(player);
        
        StringBuilder messageBuilder = new StringBuilder();
        if(hasHealth(player)) {
            String health = getHealthString(player);
            String maxHealth = getMaxHealthString(player);
            Replacer replacer = message -> message.replace("{health}", health)
                    .replace("{max_health}", maxHealth);
    
            String messagePath = (witherEffect ? "display.wither-health-format" : "display.health-format");
            String message = languageManager.getMessage(player, messagePath, replacer, true);
            messageBuilder.append(message);
        }
        
        if(hasAbsorptionHealth(player)) {
            String absorptionHealthString = getAbsorptionHealthString(player);
            Replacer replacer = message -> message.replace("{absorb_health}", absorptionHealthString);
            
            String messagePath = ("display.absorption-health-format");
            String message = languageManager.getMessage(player, messagePath, replacer, true);
            messageBuilder.append(message);
        }
        
        String message = messageBuilder.toString();
        return HookPlaceholderAPI.replace(player, message);
    }
    
    private String getHeartsDisplayMessage(Player player) {
        LanguageManager languageManager = getLanguageManager();
        boolean witherEffect = hasWitherEffect(player);
        
        StringBuilder messageBuilder = new StringBuilder();
        if(hasHealth(player)) {
            String hearts = getHeartsString(player);
            String maxHearts = getMaxHeartsString(player);
            Replacer replacer = message -> message.replace("{hearts}", hearts)
                    .replace("{max_hearts}", maxHearts);
    
            String messagePath = (witherEffect ? "display.wither-hearts-format" : "display.hearts-format");
            String message = languageManager.getMessage(player, messagePath, replacer, true);
            messageBuilder.append(message);
        }
        
        if(hasAbsorptionHealth(player)) {
            String absorptionHeartsString = getAbsorptionHeartsString(player);
            Replacer replacer = message -> message.replace("{absorb_hearts}", absorptionHeartsString);
    
            String messagePath = ("display.absorption-hearts-format");
            String message = languageManager.getMessage(player, messagePath, replacer, true);
            messageBuilder.append(message);
        }
        
        String message = messageBuilder.toString();
        return HookPlaceholderAPI.replace(player, message);
    }
    
    private void sendActionBar(Player player, String message) {
        PlayerHandler playerHandler = getPlayerHandler();
        playerHandler.sendActionBar(player, message);
    }
    
    private void sendBossBar(Player player, String message) {
        BossBarHandler bossBarHandler = getBossBarHandler();
        bossBarHandler.updateBossBar(player, message, 1.0D, "BLUE", "SOLID");
    }
}
