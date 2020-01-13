package com.SirBlobman.compressed.hearts;

import java.util.logging.Logger;

import com.SirBlobman.api.SirBlobmanAPI;
import com.SirBlobman.compressed.hearts.command.CommandCompressedHearts;
import com.SirBlobman.compressed.hearts.command.CommandHP;
import com.SirBlobman.compressed.hearts.listener.ListenerHealth;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompressedHearts extends JavaPlugin {
    private final SirBlobmanAPI sirBlobmanAPI = SirBlobmanAPI.getInstance(this);
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ListenerHealth(this), this);
        
        registerCommands(manager);
    }
    
    @Override
    public void onDisable() {
        // Do Nothing
    }
    
    private void registerCommands(PluginManager manager) {
        registerCommand(manager, "compressed-hearts", new CommandCompressedHearts(this));
        registerCommand(manager, "hp", new CommandHP(this));
    }
    
    private void registerCommand(PluginManager manager, String commandName, CommandExecutor executor) {
        if(commandName == null || commandName.isEmpty() || executor == null) return;
        
        PluginCommand command = getCommand(commandName);
        if(command == null) {
            Logger logger = getLogger();
            logger.warning("Could not find command '" + commandName + "' in plugin.yml. Please contact SirBlobman!");
            return;
        }
        command.setExecutor(executor);
        
        if(executor instanceof TabCompleter) {
            TabCompleter completer = (TabCompleter) executor;
            command.setTabCompleter(completer);
        }
        
        if(executor instanceof Listener) {
            Listener listener = (Listener) executor;
            manager.registerEvents(listener, this);
        }
    }
    
    public SirBlobmanAPI getSirBlobmanAPI() {
        return this.sirBlobmanAPI;
    }
}