package com.SirBlobman.compressed.hearts.command;

import java.text.DecimalFormat;
import java.util.List;

import com.SirBlobman.api.SirBlobmanAPI;
import com.SirBlobman.api.nms.EntityHandler;
import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.nms.PlayerHandler;
import com.SirBlobman.compressed.hearts.CompressedHearts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandHP implements CommandExecutor {
    private final CompressedHearts plugin;
    public CommandHP(CompressedHearts plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player) && args.length < 1) {
            String message = getConfigMessage("messages.commands.not-player");
            sender.sendMessage(message);
            return true;
        }
        
        String targetName = args.length > 0 ? args[0] : sender.getName();
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            String message = getConfigMessage("messages.commands.hp.invalid-target").replace("{target}", targetName);
            sender.sendMessage(message);
            return true;
        }
        
        if(target.equals(sender)) return checkSelf(target);
        return checkOther(sender, target);
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
    
    private boolean checkSelf(Player player) {
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        NMS_Handler nmsHandler = api.getVersionHandler();
        PlayerHandler playerHandler = nmsHandler.getPlayerHandler();
        EntityHandler entityHandler = nmsHandler.getEntityHandler();
        DecimalFormat format = new DecimalFormat("0.00");
    
        double health = player.getHealth();
        String healthString = format.format(health);
        
        double maxHealth = entityHandler.getMaxHealth(player);
        String maxHealthString = format.format(maxHealth);
        
        double absorptionHealth = playerHandler.getAbsorptionHearts(player);
        String absorptionHealthString = format.format(absorptionHealth);
        
        long hearts = Math.round(health / 2.0D);
        String heartsString = Long.toString(hearts);
        
        long maxHearts = Math.round(maxHealth / 2.0D);
        String maxHeartsString = Long.toString(maxHearts);
        
        long absorptionHearts = Math.round(absorptionHealth / 2.0D);
        String absorptionHeartsString = Long.toString(absorptionHearts);
        
        String message = getConfigMessage("messages.commands.hp.self");
        message = message.replace("{health}", healthString).replace("{max_health}", maxHealthString);
        message = message.replace("{absorption_health}", absorptionHealthString).replace("{absorption_hearts}", absorptionHeartsString);
        message = message.replace("{hearts}", heartsString).replace("{max_hearts}", maxHeartsString);
        
        player.sendMessage(message);
        return true;
    }
    
    private boolean checkOther(CommandSender sender, Player target) {
        SirBlobmanAPI api = this.plugin.getSirBlobmanAPI();
        NMS_Handler nmsHandler = api.getVersionHandler();
        PlayerHandler playerHandler = nmsHandler.getPlayerHandler();
        EntityHandler entityHandler = nmsHandler.getEntityHandler();
        DecimalFormat format = new DecimalFormat("0.00");
    
        double health = target.getHealth();
        String healthString = format.format(health);
        
        double maxHealth = entityHandler.getMaxHealth(target);
        String maxHealthString = format.format(maxHealth);
        
        double absorptionHealth = playerHandler.getAbsorptionHearts(target);
        String absorptionHealthString = format.format(absorptionHealth);
        
        long hearts = Math.round(health / 2.0D);
        String heartsString = Long.toString(hearts);
        
        long maxHearts = Math.round(maxHealth / 2.0D);
        String maxHeartsString = Long.toString(maxHearts);
        
        long absorptionHearts = Math.round(absorptionHealth / 2.0D);
        String absorptionHeartsString = Long.toString(absorptionHearts);
        
        String message = getConfigMessage("messages.commands.hp.other");
        message = message.replace("{health}", healthString).replace("{max_health}", maxHealthString);
        message = message.replace("{absorption_health}", absorptionHealthString).replace("{absorption_hearts}", absorptionHeartsString);
        message = message.replace("{hearts}", heartsString).replace("{max_hearts}", maxHeartsString);
        message = message.replace("{target}", target.getName());
        
        sender.sendMessage(message);
        return true;
    }
}
