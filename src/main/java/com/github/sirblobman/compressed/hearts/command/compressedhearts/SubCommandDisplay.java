package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.utility.ConfigurationHelper;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;
import com.github.sirblobman.compressed.hearts.display.DisplayType;
import com.github.sirblobman.compressed.hearts.event.PlayerChangeDisplayTypeEvent;

public final class SubCommandDisplay extends PlayerCommand {
    private final HeartsPlugin plugin;

    public SubCommandDisplay(@NotNull HeartsPlugin plugin) {
        super(plugin, "display");
        setPermissionName("ch.command.compressed-hearts.display");
        this.plugin = plugin;
    }

    @NotNull
    @Override
    protected LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull Player player, String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> valueSet = getEnumNames(DisplayType.class);
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull Player player, String @NotNull [] args) {
        if (args.length < 1) {
            return false;
        }

        String sub = args[0];
        DisplayType newDisplayType = parseDisplayType(sub);
        if (newDisplayType == null) {
            Replacer replacer = new StringReplacer("{value}", sub);
            sendMessage(player, "error.invalid-display-type", replacer);
            return true;
        }

        DisplayType oldDisplayType = getDisplayType(player);
        if (oldDisplayType == newDisplayType) {
            Replacer replacer = new StringReplacer("{value}", newDisplayType.name());
            sendMessage(player, "command.compressed-hearts.display-already-matches", replacer);
            return true;
        }

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        playerData.set("display-type", newDisplayType.name());
        playerDataManager.save(player);

        Replacer replacer = new StringReplacer("{display-type}", newDisplayType.name());
        sendMessage(player, "command.compressed-hearts.change-display", replacer);

        Event event = new PlayerChangeDisplayTypeEvent(player, oldDisplayType, newDisplayType);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(event);
        return true;
    }

    private @NotNull HeartsPlugin getHeartsPlugin() {
        return this.plugin;
    }

    private @NotNull ConfigurationManager getConfigurationManager() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getConfigurationManager();
    }

    private @NotNull PlayerDataManager getPlayerDataManager() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getPlayerDataManager();
    }

    private @Nullable DisplayType parseDisplayType(String value) {
        if (value.equalsIgnoreCase("bossbar")) {
            value = "boss_bar";
        }

        if (value.equalsIgnoreCase("actionbar")) {
            value = "action_bar";
        }

        return ConfigurationHelper.parseEnum(DisplayType.class, value, null);
    }

    private @NotNull DisplayType getDisplayType(Player player) {
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
}
