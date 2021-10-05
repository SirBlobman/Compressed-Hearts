package com.github.sirblobman.compressed.hearts;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.compressed.hearts.command.CommandCompressedHearts;
import com.github.sirblobman.compressed.hearts.command.CommandHP;
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
        languageManager.saveDefaultLanguages();
    }
    
    @Override
    public void onEnable() {
        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguages();
        
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ListenerHealth(this), this);
        
        new CommandCompressedHearts(this).register();
        new CommandHP(this).register();
        
        DisplayTask displayTask = getDisplayTask();
        displayTask.runTaskTimer(this, 5L, 5L);
        
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        updateManager.addResource(this, 44024L);
    }
    
    @Override
    public void onDisable() {
        // Do Nothing
    }
    
    public DisplayTask getDisplayTask() {
        return this.displayTask;
    }
}
