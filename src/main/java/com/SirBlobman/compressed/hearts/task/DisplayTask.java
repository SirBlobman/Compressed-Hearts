package com.SirBlobman.compressed.hearts.task;

import java.text.DecimalFormat;
import java.util.Collection;

import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.api.configuration.PlayerDataManager;
import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.nms.EntityHandler;
import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.api.nms.PlayerHandler;
import com.SirBlobman.api.nms.bossbar.BossBarHandler;
import com.SirBlobman.api.utility.Validate;
import com.SirBlobman.compressed.hearts.HeartsPlugin;
import com.SirBlobman.compressed.hearts.object.DisplayType;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DisplayTask extends BukkitRunnable {

    private final HeartsPlugin plugin;
    public DisplayTask(HeartsPlugin plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    @Override
    public void run() {
        Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
        onlinePlayerCollection.forEach(this::checkDisplay);
    }

    public void sendDisplay(Player player) {
        if(shouldUseHearts(player)) {
            sendHeartsDisplay(player);
            return;
        }

        sendHealthDisplay(player);
    }

    private boolean shouldAlwaysShow(Player player) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        if(configuration.isSet("always-show")) return configuration.getBoolean("always-show");

        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration pluginConfiguration = configurationManager.get("config.yml");
        return pluginConfiguration.getBoolean("always-show");
    }

    private boolean shouldUseHearts(Player player) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        if(configuration.isSet("show-hearts")) return configuration.getBoolean("show-hearts");

        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration pluginConfiguration = configurationManager.get("config.yml");
        return pluginConfiguration.getBoolean("show-hearts");
    }

    private DisplayType getDisplayType(Player player) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        if(configuration.isSet("display-type")) {
            String displayTypeString = configuration.getString("display-type");
            return DisplayType.parse(displayTypeString);
        }

        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration pluginConfiguration = configurationManager.get("config.yml");
        String displayTypeString = pluginConfiguration.getString("display-type");
        return DisplayType.parse(displayTypeString);
    }

    private boolean hasWitherEffect(Player player) {
        return player.hasPotionEffect(PotionEffectType.WITHER);
    }

    private void checkDisplay(Player player) {
        if(!shouldAlwaysShow(player)) return;
        sendDisplay(player);
    }

    private void sendHealthDisplay(Player player) {
        LanguageManager languageManager = this.plugin.getLanguageManager();
        MultiVersionHandler multiVersionHandler = this.plugin.getMultiVersionHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        String decimalFormatString = languageManager.getMessage(player, "display.decimal-format");
        DecimalFormat decimalFormat = new DecimalFormat(decimalFormatString);

        double normalHealth = player.getHealth();
        String normalHealthString = decimalFormat.format(normalHealth);

        double maxHealth = entityHandler.getMaxHealth(player);
        String maxHealthString = decimalFormat.format(maxHealth);

        String messagePath = (hasWitherEffect(player) ? "display.wither-health-format" : "display.health-format");
        String messageFormat = languageManager.getMessageColored(player, messagePath);
        String message = messageFormat.replace("{health}", normalHealthString).replace("{max_health}", maxHealthString);

        double absorptionHealth = playerHandler.getAbsorptionHearts(player);
        if(absorptionHealth > 0.0D) {
            String absorptionHealthString = decimalFormat.format(absorptionHealth);
            String absorptionHealthFormat = languageManager.getMessageColored(player,"display.absorption-health-format");
            String absorptionHealthMessage = absorptionHealthFormat.replace("{absorb_health}", absorptionHealthString);
            message += absorptionHealthMessage;
        }

        DisplayType displayType = getDisplayType(player);
        if(displayType == null || displayType == DisplayType.NONE) return;

        if(displayType == DisplayType.ACTION_BAR) {
            playerHandler.sendActionBar(player, message);
            return;
        }

        if(displayType == DisplayType.BOSS_BAR) {
            BossBarHandler bossBarHandler = multiVersionHandler.getBossBarHandler();
            bossBarHandler.updateBossBar(player, message, 1.0D, "BLUE", "SOLID");
        }
    }

    private void sendHeartsDisplay(Player player) {
        LanguageManager languageManager = this.plugin.getLanguageManager();
        MultiVersionHandler multiVersionHandler = this.plugin.getMultiVersionHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        double normalHealth = player.getHealth();
        long normalHearts = Math.round(normalHealth / 2.0D);
        String normalHeartsString = Long.toString(normalHearts);

        double maxHealth = entityHandler.getMaxHealth(player);
        long maxHearts = Math.round(maxHealth / 2.0D);
        String maxHeartsString = Long.toString(maxHearts);

        String messagePath = (hasWitherEffect(player) ? "display.wither-hearts-format" : "display.hearts-format");
        String messageFormat = languageManager.getMessageColored(player, messagePath);
        String message = messageFormat.replace("{hearts}", normalHeartsString).replace("{max_hearts}", maxHeartsString);

        double absorptionHealth = playerHandler.getAbsorptionHearts(player);
        long absorptionHearts = Math.round(absorptionHealth / 2.0D);
        if(absorptionHearts > 0) {
            String absorptionHeartsString = Long.toString(absorptionHearts);
            String absorptionHeartsFormat = languageManager.getMessageColored(player,"display.absorption-hearts-format");
            String absorptionHeartsMessage = absorptionHeartsFormat.replace("{absorb_hearts}", absorptionHeartsString);
            message += absorptionHeartsMessage;
        }

        DisplayType displayType = getDisplayType(player);
        if(displayType == null || displayType == DisplayType.NONE) return;

        if(displayType == DisplayType.ACTION_BAR) {
            playerHandler.sendActionBar(player, message);
            return;
        }

        if(displayType == DisplayType.BOSS_BAR) {
            BossBarHandler bossBarHandler = multiVersionHandler.getBossBarHandler();
            bossBarHandler.updateBossBar(player, message, 1.0D, "BLUE", "SOLID");
        }
    }
}