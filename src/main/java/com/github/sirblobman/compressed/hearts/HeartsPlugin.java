package com.github.sirblobman.compressed.hearts;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.update.UpdateChecker;
import com.github.sirblobman.compressed.hearts.command.CommandCompressedHearts;
import com.github.sirblobman.compressed.hearts.command.CommandHP;
import com.github.sirblobman.compressed.hearts.listener.ListenerHealth;
import com.github.sirblobman.compressed.hearts.task.DisplayTask;

public class HeartsPlugin extends JavaPlugin {
    private final ConfigurationManager configurationManager;
    private final LanguageManager languageManager;
    private final PlayerDataManager playerDataManager;
    private final DisplayTask displayTask;
    public HeartsPlugin() {
        this.configurationManager = new ConfigurationManager(this);
        this.languageManager = new LanguageManager(this, this.configurationManager);
        this.playerDataManager = new PlayerDataManager(this);
        this.displayTask = new DisplayTask(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("language.yml");
        configurationManager.saveDefault("language/en_us.lang.yml");
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ListenerHealth(this), this);

        new CommandCompressedHearts(this).register();
        new CommandHP(this).register();

        DisplayTask displayTask = getDisplayTask();
        displayTask.runTaskTimer(this, 5L, 5L);

        UpdateChecker updateChecker = new UpdateChecker(this, 44024L);
        updateChecker.runCheck();
    }

    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public MultiVersionHandler getMultiVersionHandler() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        return corePlugin.getMultiVersionHandler();
    }

    public DisplayTask getDisplayTask() {
        return this.displayTask;
    }
}