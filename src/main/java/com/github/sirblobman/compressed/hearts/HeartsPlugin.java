package com.github.sirblobman.compressed.hearts;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.Language;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.shaded.bstats.bukkit.Metrics;
import com.github.sirblobman.api.shaded.bstats.charts.SimplePie;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.compressed.hearts.command.CommandHP;
import com.github.sirblobman.compressed.hearts.command.compressedhearts.CommandCompressedHearts;
import com.github.sirblobman.compressed.hearts.listener.ListenerDisplayType;
import com.github.sirblobman.compressed.hearts.listener.ListenerHealth;
import com.github.sirblobman.compressed.hearts.task.DisplayTask;

public final class HeartsPlugin extends ConfigurablePlugin {
    private final DisplayTask displayTask;

    public HeartsPlugin() {
        this.displayTask = new DisplayTask(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");

        LanguageManager languageManager = getLanguageManager();
        languageManager.saveDefaultLanguageFiles();
    }

    @Override
    public void onEnable() {
        reloadConfiguration();

        LanguageManager languageManager = getLanguageManager();
        languageManager.onPluginEnable();

        registerCommands();
        registerListeners();
        registerTasks();
        registerUpdateChecker();
        register_bStats();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    protected void reloadConfiguration() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguages();
    }

    public DisplayTask getDisplayTask() {
        return this.displayTask;
    }

    private void registerCommands() {
        new CommandCompressedHearts(this).register();
        new CommandHP(this).register();
    }

    private void registerListeners() {
        new ListenerHealth(this).register();
        new ListenerDisplayType(this).register();
    }

    private void registerTasks() {
        DisplayTask displayTask = getDisplayTask();
        displayTask.runTaskTimer(this, 5L, 5L);
    }

    private void registerUpdateChecker() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        updateManager.addResource(this, 44024L);
    }

    private void register_bStats() {
        Metrics metrics = new Metrics(this, 16177);
        metrics.addCustomChart(new SimplePie("selected_language", this::getDefaultLanguageCode));
    }

    private String getDefaultLanguageCode() {
        LanguageManager languageManager = getLanguageManager();
        Language defaultLanguage = languageManager.getDefaultLanguage();
        return (defaultLanguage == null ? "none" : defaultLanguage.getLanguageName());
    }
}
