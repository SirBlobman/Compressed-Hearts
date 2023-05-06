package com.github.sirblobman.compressed.hearts.display;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.github.sirblobman.api.bossbar.BossBarHandler;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.folia.details.TaskDetails;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.utility.ConfigurationHelper;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.compressed.hearts.hook.HookPlaceholderAPI;
import com.github.sirblobman.api.shaded.adventure.bossbar.BossBar.Color;
import com.github.sirblobman.api.shaded.adventure.bossbar.BossBar.Overlay;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.minimessage.MiniMessage;

public final class DisplayTask extends TaskDetails {
    private final HeartsPlugin plugin;

    public DisplayTask(@NotNull HeartsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayerCollection) {
            checkDisplay(player);
        }
    }

    public void sendDisplay(Player player) {
        Player displayPlayer = getDisplayPlayer(player);
        boolean hearts = shouldUseHearts(player);

        Component message = (hearts ? getHeartsDisplayMessage(displayPlayer) : getHealthDisplayMessage(displayPlayer));
        if (Component.empty().equals(message)) {
            return;
        }

        DisplayType displayType = getDisplayType(player);
        if (displayType == DisplayType.NONE) {
            return;
        }

        switch (displayType) {
            case BOSS_BAR:
                sendBossBar(player, message);
                break;
            case ACTION_BAR:
                sendActionBar(player, message);
                break;
            default:
                break;
        }
    }

    private @NotNull HeartsPlugin getHeartsPlugin() {
        return this.plugin;
    }

    private @NotNull ConfigurationManager getConfigurationManager() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getConfigurationManager();
    }

    private @NotNull LanguageManager getLanguageManager() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getLanguageManager();
    }

    private @NotNull PlayerDataManager getPlayerDataManager() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getPlayerDataManager();
    }

    private @NotNull MultiVersionHandler getMultiVersionHandler() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getMultiVersionHandler();
    }

    private @NotNull PlayerHandler getPlayerHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getPlayerHandler();
    }

    private @NotNull EntityHandler getEntityHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getEntityHandler();
    }

    private @NotNull BossBarHandler getBossBarHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getBossBarHandler();
    }

    private boolean shouldAlwaysShow(@NotNull Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if (playerData.isSet("always-show")) {
            return playerData.getBoolean("always-show");
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("always-show");
    }

    private boolean shouldUseHearts(@NotNull Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if (playerData.isSet("show-hearts")) {
            return playerData.getBoolean("show-hearts");
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("show-hearts");
    }

    private @NotNull DisplayType getDisplayType(@NotNull Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if (playerData.isSet("display-type")) {
            String displayTypeString = playerData.getString("display-type");
            return ConfigurationHelper.parseEnum(DisplayType.class, displayTypeString, DisplayType.NONE);
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        String displayTypeString = configuration.getString("display-type");
        return ConfigurationHelper.parseEnum(DisplayType.class, displayTypeString, DisplayType.NONE);
    }

    private @NotNull DecimalFormat getDecimalFormat(@NotNull Player player) {
        LanguageManager languageManager = getLanguageManager();
        return languageManager.getDecimalFormat(player);
    }

    private @NotNull DecimalFormat getIntegerFormat(@NotNull Player player) {
        return getDecimalFormat(player);
    }

    private boolean hasWitherEffect(@NotNull Player player) {
        return player.hasPotionEffect(PotionEffectType.WITHER);
    }

    private double getHealth(@NotNull Player player) {
        return player.getHealth();
    }

    private boolean hasHealth(@NotNull Player player) {
        double health = getHealth(player);
        return (health > 0.0D);
    }

    private @NotNull String getHealthString(@NotNull Player player) {
        double health = getHealth(player);
        DecimalFormat decimalFormat = getDecimalFormat(player);
        return decimalFormat.format(health);
    }

    private double getMaxHealth(@NotNull Player player) {
        EntityHandler entityHandler = getEntityHandler();
        return entityHandler.getMaxHealth(player);
    }

    private @NotNull String getMaxHealthString(@NotNull Player player) {
        double maxHealth = getMaxHealth(player);
        DecimalFormat decimalFormat = getDecimalFormat(player);
        return decimalFormat.format(maxHealth);
    }

    private double getAbsorptionHealth(@NotNull Player player) {
        PlayerHandler playerHandler = getPlayerHandler();
        return playerHandler.getAbsorptionHearts(player);
    }

    private boolean hasAbsorptionHealth(@NotNull Player player) {
        double absorptionHealth = getAbsorptionHealth(player);
        return (absorptionHealth > 0.0D);
    }

    private @NotNull String getAbsorptionHealthString(@NotNull Player player) {
        double absorptionHealth = getAbsorptionHealth(player);
        DecimalFormat decimalFormat = getDecimalFormat(player);
        return decimalFormat.format(absorptionHealth);
    }

    private long ceil(double value) {
        double ceil = Math.ceil(value);
        return Math.round(ceil);
    }

    private long getHearts(@NotNull Player player) {
        double health = getHealth(player);
        return ceil(health / 2.0D);
    }

    private long getMaxHearts(@NotNull Player player) {
        double maxHealth = getMaxHealth(player);
        return ceil(maxHealth / 2.0D);
    }

    private long getAbsorptionHearts(@NotNull Player player) {
        double absorptionHealth = getAbsorptionHealth(player);
        return ceil(absorptionHealth / 2.0D);
    }

    private @NotNull String getHeartsString(@NotNull Player player) {
        long hearts = getHearts(player);
        DecimalFormat integerFormat = getIntegerFormat(player);
        return integerFormat.format(hearts);
    }

    private @NotNull String getMaxHeartsString(@NotNull Player player) {
        long maxHearts = getMaxHearts(player);
        DecimalFormat integerFormat = getIntegerFormat(player);
        return integerFormat.format(maxHearts);
    }

    private @NotNull String getAbsorptionHeartsString(@NotNull Player player) {
        long absorptionHearts = getAbsorptionHearts(player);
        DecimalFormat integerFormat = getIntegerFormat(player);
        return integerFormat.format(absorptionHearts);
    }

    private void checkDisplay(@NotNull Player player) {
        if (shouldAlwaysShow(player)) {
            sendDisplay(player);
        }
    }

    private @NotNull Component getHealthDisplayMessage(@NotNull Player player) {
        LanguageManager languageManager = getLanguageManager();
        boolean witherEffect = hasWitherEffect(player);

        StringBuilder messageBuilder = new StringBuilder();
        if (hasHealth(player)) {
            String health = getHealthString(player);
            String maxHealth = getMaxHealthString(player);

            Replacer healthReplacer = new StringReplacer("{health}", health);
            Replacer maxHealthReplacer = new StringReplacer("{max_health}", maxHealth);

            String messagePath = (witherEffect ? "display.wither-health-format" : "display.health-format");
            String message = languageManager.getMessageString(player, messagePath, healthReplacer, maxHealthReplacer);
            messageBuilder.append(message);
        }

        if (hasAbsorptionHealth(player)) {
            String absorptionHealthString = getAbsorptionHealthString(player);
            Replacer replacer = new StringReplacer("{absorb_health}", absorptionHealthString);

            String messagePath = ("display.absorption-health-format");
            String message = languageManager.getMessageString(player, messagePath, replacer);
            messageBuilder.append(message);
        }

        String messageString = messageBuilder.toString();
        String messageReplaced = HookPlaceholderAPI.replace(player, messageString);
        MiniMessage miniMessage = languageManager.getMiniMessage();
        return miniMessage.deserialize(messageReplaced);
    }

    private @NotNull Component getHeartsDisplayMessage(@NotNull Player player) {
        LanguageManager languageManager = getLanguageManager();
        boolean witherEffect = hasWitherEffect(player);

        StringBuilder messageBuilder = new StringBuilder();
        if (hasHealth(player)) {
            String hearts = getHeartsString(player);
            String maxHearts = getMaxHeartsString(player);

            Replacer heartsReplacer = new StringReplacer("{hearts}", hearts);
            Replacer maxHeartsReplacer = new StringReplacer("{max_hearts}", maxHearts);

            String messagePath = (witherEffect ? "display.wither-hearts-format" : "display.hearts-format");
            String message = languageManager.getMessageString(player, messagePath, heartsReplacer, maxHeartsReplacer);
            messageBuilder.append(message);
        }

        if (hasAbsorptionHealth(player)) {
            String absorptionHeartsString = getAbsorptionHeartsString(player);
            Replacer replacer = new StringReplacer("{absorb_hearts}", absorptionHeartsString);

            String messagePath = ("display.absorption-hearts-format");
            String message = languageManager.getMessageString(player, messagePath, replacer);
            messageBuilder.append(message);
        }

        String messageString = messageBuilder.toString();
        String messageReplaced = HookPlaceholderAPI.replace(player, messageString);
        MiniMessage miniMessage = languageManager.getMiniMessage();
        return miniMessage.deserialize(messageReplaced);
    }

    private void sendActionBar(@NotNull Player player, @NotNull Component message) {
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendActionBar(player, message);
    }

    private void sendBossBar(@NotNull Player player, @NotNull Component message) {
        UUID playerId = player.getUniqueId();
        String bossBarKey = ("ch-display-" + playerId);
        BossBarHandler bossBarHandler = getBossBarHandler();
        bossBarHandler.updateBossBar(bossBarKey, message, 1.0F, Color.BLUE, Overlay.PROGRESS);
        bossBarHandler.showBossBar(player, bossBarKey);
    }

    public void removeBossBar(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        String bossBarKey = ("ch-display-" + playerId);
        BossBarHandler bossBarHandler = getBossBarHandler();
        bossBarHandler.removeBossBar(bossBarKey);
    }

    private @NotNull Player getDisplayPlayer(@NotNull Player player) {
        GameMode gameMode = player.getGameMode();
        if (gameMode != GameMode.SPECTATOR) {
            return player;
        }

        HeartsPlugin plugin = getHeartsPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("spectate-health")) {
            return player;
        }

        Entity spectatorTarget = player.getSpectatorTarget();
        if (spectatorTarget instanceof Player) {
            return (Player) spectatorTarget;
        }

        return player;
    }
}
