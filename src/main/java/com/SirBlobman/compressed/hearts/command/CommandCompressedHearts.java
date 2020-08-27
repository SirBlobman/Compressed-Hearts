package com.SirBlobman.compressed.hearts.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.SirBlobman.api.command.Command;
import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.api.configuration.PlayerDataManager;
import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.language.Replacer;
import com.SirBlobman.compressed.hearts.HeartsPlugin;
import com.SirBlobman.compressed.hearts.task.DisplayTask.DisplayType;

public class CommandCompressedHearts extends Command {
    private final HeartsPlugin plugin;
    public CommandCompressedHearts(HeartsPlugin plugin) {
        super(plugin, "compressed-hearts");
        this.plugin = plugin;
    }

    @Override
    public LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> valueList = Arrays.asList("compress", "display", "help", "reload");
            return getMatching(valueList, args[0]);
        }

        if(args.length == 2 && args[0].toLowerCase().equals("display")) {
            List<String> valueList = Arrays.asList("bossbar", "actionbar");
            return getMatching(valueList, args[1]);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length < 1) return false;

        String sub = args[0].toLowerCase();
        String[] newArgs = (args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
        switch(sub) {
            case "compress": return compressCommand(sender);
            case "display": return displayCommand(sender, newArgs);
            case "help": return helpCommand(sender);
            case "reload": return reloadCommand(sender);
            default: break;
        }

        return false;
    }

    private boolean compressCommand(CommandSender sender) {
        if(!checkPermission(sender, "ch.command.compressed-hearts.compress", true)) return true;
        if(!(sender instanceof Player)) {
            sendMessageOrDefault(sender, "error.player-only", "You are not a player.", null, true);
            return true;
        }

        Player player = (Player) sender;
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        boolean scaleHealth = configuration.getBoolean("scale-health");

        if(scaleHealth) {
            configuration.set("scale-health", false);
            playerDataManager.save(player);
            player.setHealthScaled(false);

            sendMessageOrDefault(player, "command.compressed-hearts.compress.disabled", "", null, true);
            return true;
        }

        configuration.set("scale-health", true);
        playerDataManager.save(player);
        player.setHealthScaled(true);
        player.setHealthScale(20.0D);

        sendMessageOrDefault(player, "command.compressed-hearts.compress.enabled", "", null, true);
        return true;
    }

    private boolean displayCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        if(!checkPermission(sender, "ch.command.compressed-hearts.display", true)) return true;
        if(!(sender instanceof Player)) {
            sendMessageOrDefault(sender, "error.player-only", "You are not a player.", null, true);
            return true;
        }

        Player player = (Player) sender;
        String sub = args[0].toLowerCase();
        DisplayType displayType;
        if(sub.equals("bossbar")) {
            displayType = DisplayType.BOSS_BAR;
        } else if(sub.equals("actionbar")) {
            displayType = DisplayType.ACTION_BAR;
        } else return false;

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        configuration.set("display-type", displayType.name());
        playerDataManager.save(player);

        Replacer replacer = message -> message.replace("{display-type}", displayType.name());
        sendMessageOrDefault(player, "command.compressed-hearts.change-display", "", replacer, true);
        return true;
    }

    private boolean helpCommand(CommandSender sender) {
        if(!checkPermission(sender, "ch.command.compressed-hearts.help", true)) return true;
        sendMessageOrDefault(sender, "command.compressed-hearts.help-message", "", null, true);
        return true;
    }

    private boolean reloadCommand(CommandSender sender) {
        if(!checkPermission(sender, "ch.command.compressed-hearts.reload", true)) return true;
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();

        configurationManager.reload("config.yml");
        configurationManager.reload("language.yml");
        configurationManager.reload("language/en_us.lang.yml");

        sendMessageOrDefault(sender, "command.compressed-hearts.reload-success", "", null, true);
        return true;
    }
}