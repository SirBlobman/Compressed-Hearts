package com.github.sirblobman.compressed.hearts.display;

import java.text.DecimalFormat;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.github.sirblobman.api.bossbar.BossBarHandler;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.DoubleReplacer;
import com.github.sirblobman.api.language.replacer.LongReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.utility.ConfigurationHelper;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.api.shaded.adventure.bossbar.BossBar.Color;
import com.github.sirblobman.api.shaded.adventure.bossbar.BossBar.Overlay;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextComponent;

public final class PlayerDisplayTask extends EntityTaskDetails<Player> {
    private final HeartsPlugin plugin;

    public PlayerDisplayTask(@NotNull HeartsPlugin plugin, @NotNull Player player) {
        super(plugin, player);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Player entity = getEntity();
        if (entity == null) {
            cancel();
            return;
        }

        checkDisplay(entity);
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

    private @NotNull EntityHandler getEntityHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getEntityHandler();
    }

    private @NotNull PlayerHandler getPlayerHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getPlayerHandler();
    }

    private @NotNull BossBarHandler getBossBarHandler() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        return multiVersionHandler.getBossBarHandler();
    }

    private void checkDisplay(@NotNull Player player) {
        if (isAlwaysShow(player)) {
            sendDisplay(player);
        }
    }

    private boolean isAlwaysShow(@NotNull Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        if (playerDataManager.hasData(player)) {
            YamlConfiguration configuration = playerDataManager.get(player);
            if (configuration.isSet("always-show")) {
                return configuration.getBoolean("always-show", true);
            }
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("always-show", true);
    }

    public void sendDisplay(@NotNull Player player) {
        LivingEntity display = getDisplay(player);
        boolean hearts = isUseHearts(player);

        DisplayType displayType = getDisplayType(player);
        if (displayType == DisplayType.NONE) {
            return;
        }

        Component message = (hearts ? getHeartsDisplay(player, display) : getHealthDisplay(player, display));
        if (Component.empty().equals(message)) {
            return;
        }

        switch (displayType) {
            case BOSS_BAR: sendBossBar(player, message); break;
            case ACTION_BAR: sendActionBar(player, message); break;
            default: break;
        }
    }

    private @NotNull LivingEntity getDisplay(@NotNull Player player) {
        GameMode gameMode = player.getGameMode();
        if (gameMode != GameMode.SPECTATOR) {
            return player;
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("spectate-health")) {
            return player;
        }

        Entity target = player.getSpectatorTarget();
        if (target instanceof LivingEntity) {
            return (LivingEntity) target;
        }

        return player;
    }

    private boolean isUseHearts(@NotNull Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        if (playerDataManager.hasData(player)) {
            YamlConfiguration configuration = playerDataManager.get(player);
            if (configuration.isSet("show-hearts")) {
                return configuration.getBoolean("show-hearts", true);
            }
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("show-hearts", true);
    }

    private @NotNull DisplayType getDisplayType(@NotNull Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        if (playerDataManager.hasData(player)) {
            YamlConfiguration configuration = playerDataManager.get(player);
            if (configuration.isSet("display-type")) {
                String displayTypeName = configuration.getString("display-type");
                return ConfigurationHelper.parseEnum(DisplayType.class, displayTypeName, DisplayType.NONE);
            }
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        String displayTypeName = configuration.getString("display-type");
        return ConfigurationHelper.parseEnum(DisplayType.class, displayTypeName, DisplayType.NONE);
    }

    private @NotNull Component getHealthDisplay(@NotNull Player viewer, @NotNull LivingEntity entity) {
        LanguageManager languageManager = getLanguageManager();
        DecimalFormat format = languageManager.getDecimalFormat(viewer);
        boolean witherEffect = hasWitherEffect(entity);

        TextComponent.Builder builder = Component.text();
        double health = getHealth(entity);
        if (health > 0.0D) {
            Replacer healthReplacer = new DoubleReplacer("{health}", health, format);
            Replacer maxHealthReplacer = new DoubleReplacer("{max_health}", getMaxHealth(entity), format);
            String messagePath = ("display." + (witherEffect ? "wither-" : "") + "health-format");
            builder.append(languageManager.getMessage(viewer, messagePath, healthReplacer, maxHealthReplacer));
        }

        double absorptionHealth = getAbsorptionHealth(entity);
        if (absorptionHealth > 0.0D) {
            Replacer replacer = new DoubleReplacer("{absorb_health}", absorptionHealth, format);
            String messagePath = ("display.absorption-health-format");
            builder.append(languageManager.getMessage(viewer, messagePath, replacer));
        }

        return builder.build();
    }

    private @NotNull Component getHeartsDisplay(@NotNull Player viewer, @NotNull LivingEntity entity) {
        LanguageManager languageManager = getLanguageManager();
        boolean witherEffect = hasWitherEffect(entity);

        TextComponent.Builder builder = Component.text();
        double health = getHealth(entity);
        if (health > 0.0D) {
            long hearts = ceil(health / 2.0D);
            long maxHearts = ceil(getMaxHealth(entity) / 2.0D);
            Replacer healthReplacer = new LongReplacer("{hearts}", hearts);
            Replacer maxHealthReplacer = new LongReplacer("{max_hearts}", maxHearts);
            String messagePath = ("display." + (witherEffect ? "wither-" : "") + "hearts-format");
            builder.append(languageManager.getMessage(viewer, messagePath, healthReplacer, maxHealthReplacer));
        }

        double absorptionHealth = getAbsorptionHealth(entity);
        if (absorptionHealth > 0.0D) {
            long absorptionHearts = ceil(absorptionHealth / 2.0D);
            Replacer replacer = new LongReplacer("{absorb_health}", absorptionHearts);
            String messagePath = ("display.absorption-hearts-format");
            builder.append(languageManager.getMessage(viewer, messagePath, replacer));
        }

        return builder.build();
    }

    private boolean hasWitherEffect(@NotNull LivingEntity entity) {
        return entity.hasPotionEffect(PotionEffectType.WITHER);
    }

    private double getHealth(@NotNull LivingEntity entity) {
        return entity.getHealth();
    }

    private double getMaxHealth(@NotNull LivingEntity entity) {
        EntityHandler entityHandler = getEntityHandler();
        return entityHandler.getMaxHealth(entity);
    }

    private double getAbsorptionHealth(@NotNull LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return 0.0D;
        }

        Player player = (Player) entity;
        PlayerHandler playerHandler = getPlayerHandler();
        return playerHandler.getAbsorptionHearts(player);
    }

    private long ceil(double value) {
        double ceil = Math.ceil(value);
        return Math.round(ceil);
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

    private void sendActionBar(@NotNull Player player, @NotNull Component message) {
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendActionBar(player, message);
    }
}
