package com.github.sirblobman.compressed.hearts.command;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.SirBlobman.api.command.PlayerCommand;
import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.nms.EntityHandler;
import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.api.nms.PlayerHandler;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

public class CommandHP extends PlayerCommand {
    private final HeartsPlugin plugin;
    public CommandHP(HeartsPlugin plugin) {
        super(plugin, "hp");
        this.plugin = plugin;
    }

    @Override
    public LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        if(args.length == 1) {
            Set<String> onlinePlayerNameSet = getOnlinePlayerNames();
            return getMatching(onlinePlayerNameSet, args[0]);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if(args.length < 1) {
            showSelf(player);
            return true;
        }

        Player target = findTarget(player, args[0]);
        if(target == null) return true;

        showOther(player, target);
        return true;
    }

    private void showSelf(Player player) {
        LanguageManager languageManager = getLanguageManager();
        String message = languageManager.getMessageColored(player, "command.hp.self-information");
        String realMessage = replaceVariables(player, player, message);
        player.sendMessage(realMessage);
    }

    private void showOther(Player player, Player target) {
        LanguageManager languageManager = getLanguageManager();
        String message = languageManager.getMessageColored(player, "command.hp.other-information");
        String realMessage = replaceVariables(player, target, message);
        player.sendMessage(realMessage);
    }

    private String replaceVariables(Player player, Player target, String message) {
        LanguageManager languageManager = getLanguageManager();
        MultiVersionHandler multiVersionHandler = this.plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();

        String decimalFormatString = languageManager.getMessage(player, "display.decimal-format");
        DecimalFormat decimalFormat = new DecimalFormat(decimalFormatString);

        double health = target.getHealth();
        String healthString = decimalFormat.format(health);

        long hearts = Math.round(health / 2.0D);
        String heartsString = Long.toString(hearts);

        double maxHealth = entityHandler.getMaxHealth(player);
        String maxHealthString = decimalFormat.format(maxHealth);

        long maxHearts =  Math.round(maxHealth / 2.0D);
        String maxHeartsString = Long.toString(maxHearts);

        double absorptionHealth = playerHandler.getAbsorptionHearts(player);
        String absorptionHealthString = decimalFormat.format(absorptionHealth);

        long absorptionHearts = Math.round(absorptionHealth / 2.0D);
        String absorptionHeartsString = Long.toString(absorptionHearts);

        return message.replace("{health}", healthString).replace("{hearts}", heartsString)
                .replace("{max_health}", maxHealthString).replace("{max_hearts}", maxHeartsString)
                .replace("{absorb_health}", absorptionHealthString).replace("{absorb_hearts}", absorptionHeartsString);
    }
}