package com.SirBlobman.compressed.hearts.command;

import java.util.Arrays;
import java.util.List;

import com.SirBlobman.api.SirBlobmanAPI;
import com.SirBlobman.compressed.hearts.CompressedHearts;
import com.SirBlobman.compressed.hearts.configuration.DisplayType;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CommandCompressedHearts implements CommandExecutor {
    private final CompressedHearts plugin;
    public CommandCompressedHearts(CompressedHearts plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return helpCommand(sender);
        
        String sub = args[0].toLowerCase();
        String[] newArgs = (args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
        switch(sub) {
            case "help": return helpCommand(sender);
            case "compress": return compressCommand(sender);
            case "display": return displayCommand(sender, newArgs);
            case "reload": return reloadCommand(sender);
            
            default: return false;
        }
    }
    
    private String getConfigMessage(String path) {
        FileConfiguration config = this.plugin.getConfig();
        if(config == null) return path;
        
        if(config.isString(path)) {
            String message = config.getString(path, path);
            if(message == null) return path;
            
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        
        if(config.isList(path)) {
            List<String> messageList = config.getStringList(path);
            if(messageList == null || messageList.isEmpty()) return path;
            
            String message = String.join("\n", messageList);
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        
        return path;
    }
    
    private boolean doesNotHavePermission(CommandSender sender, String permission) {
        if(sender.hasPermission(permission)) return false;
        
        String message = getConfigMessage("messages.commands.no-permission").replace("{permission}", permission);
        sender.sendMessage(message);
        return true;
    }
    
    private boolean helpCommand(CommandSender sender) {
        if(doesNotHavePermission(sender, "compressed-hearts.command.compressed-hearts.help")) return true;
        
        String helpMessage = getConfigMessage("messages.commands.compressed-hearts.help");
        sender.sendMessage(helpMessage);
        return true;
    }
    
    private boolean compressCommand(CommandSender sender) {
        if(doesNotHavePermission(sender, "compressed-hearts.command.compressed-hearts.compress")) return true;
        
        if(!(sender instanceof Player)) {
            String message = getConfigMessage("messages.commands.not-player");
            sender.sendMessage(message);
            return true;
        }
    
        Player player = (Player) sender;
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        YamlConfiguration config = api.getDataFile(player);
        
        boolean scaleHealth = config.getBoolean("scale-health");
        if(scaleHealth) {
            config.set("scale-health", false);
            api.saveDataFile(player, config);
            player.setHealthScaled(false);
            
            String message = getConfigMessage("messages.commands.compressed-hearts.compress.disabled");
            player.sendMessage(message);
            return true;
        }
        
        config.set("scale-health", true);
        api.saveDataFile(player, config);
        player.setHealthScale(20.0D);
    
        String message = getConfigMessage("messages.commands.compressed-hearts.compress.enabled");
        player.sendMessage(message);
        return true;
    }
    
    private boolean displayCommand(CommandSender sender, String[] args) {
        if(doesNotHavePermission(sender, "compressed-hearts.command.compressed-hearts.display")) return true;
        if(args.length < 1) return false;
    
        if(!(sender instanceof Player)) {
            String message = getConfigMessage("messages.commands.not-player");
            sender.sendMessage(message);
            return true;
        }
    
        Player player = (Player) sender;
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        YamlConfiguration config = api.getDataFile(player);
        
        String sub = args[0].toLowerCase();
        String typeName;
        
        switch(sub) {
            case "boss":
            case "boss_bar":
            case "bossbar": {
                typeName = DisplayType.BOSS_BAR.name();
                config.set("display-type", typeName);
                break;
            }
            
            case "action":
            case "action_bar":
            case "actionbar": {
                typeName = DisplayType.ACTION_BAR.name();
                config.set("display-type", typeName);
                break;
            }
            
            case "below_name":
            case "belowname":
            case "scoreboard":
            case "score_board": {
                typeName = DisplayType.BELOW_NAME.name();
                config.set("display-type", typeName);
                break;
            }
            
            
            default: return false;
        }
    
        api.saveDataFile(player, config);
        String message = getConfigMessage("messages.commands.compressed-hearts.display").replace("{display-type}", typeName);
        player.sendMessage(message);
        return true;
    }
    
    private boolean reloadCommand(CommandSender sender) {
        if(doesNotHavePermission(sender, "compressed-hearts.command.compressed-hearts.reload")) return true;
        
        this.plugin.reloadConfig();
        String message = getConfigMessage("messages.commands.compressed-hearts.reload");
        
        sender.sendMessage(message);
        return true;
    }
}