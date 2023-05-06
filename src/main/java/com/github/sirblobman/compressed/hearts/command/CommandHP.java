package com.github.sirblobman.compressed.hearts.command;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.DoubleReplacer;
import com.github.sirblobman.api.language.replacer.LongReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

public class CommandHP extends PlayerCommand {
    private final HeartsPlugin plugin;

    public CommandHP(@NotNull HeartsPlugin plugin) {
        super(plugin, "hp");
        setPermissionName("ch.command.hp");
        this.plugin = plugin;
    }

    @Override
    public @NotNull LanguageManager getLanguageManager() {
        HeartsPlugin plugin = getHeartsPlugin();
        return plugin.getLanguageManager();
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull Player player, String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull Player player, String @NotNull [] args) {
        if (args.length < 1) {
            showSelf(player);
            return true;
        }

        Player target = findTarget(player, args[0]);
        if (target == null) {
            return true;
        }

        showOther(player, target);
        return true;
    }

    private @NotNull HeartsPlugin getHeartsPlugin() {
        return this.plugin;
    }

    private void showSelf(@NotNull Player player) {
        Replacer[] replacerArray = getReplacerArray(player, player);
        sendMessage(player, "command.hp.self-information", replacerArray);
    }

    private void showOther(@NotNull Player player, @NotNull Player target) {
        Replacer[] replacerArray = getReplacerArray(player, target);
        sendMessage(player, "command.hp.other-information", replacerArray);
    }

    private Replacer @NotNull [] getReplacerArray(@NotNull Player player, @NotNull Player target) {
        HeartsPlugin plugin = getHeartsPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();

        LanguageManager languageManager = getLanguageManager();
        DecimalFormat decimalFormat = languageManager.getDecimalFormat(player);

        double health = target.getHealth();
        long hearts = Math.round(health / 2.0D);
        Replacer healthReplacer = new DoubleReplacer("{health}", health, decimalFormat);
        Replacer heartsReplacer = new LongReplacer("{hearts}", hearts);

        double maxHealth = entityHandler.getMaxHealth(target);
        long maxHearts = Math.round(maxHealth / 2.0D);
        Replacer maxHealthReplacer = new DoubleReplacer("{max_health}", maxHealth, decimalFormat);
        Replacer maxHeartsReplacer = new LongReplacer("{max_hearts}", maxHearts);

        double absorbHealth = playerHandler.getAbsorptionHearts(target);
        long absorbHearts = Math.round(absorbHealth / 2.0D);
        Replacer absorbHealthReplacer = new DoubleReplacer("{absorb_health}", absorbHealth, decimalFormat);
        Replacer absorbHeartsReplacer = new LongReplacer("{absort_hearts}", absorbHearts);

        String targetName = target.getName();
        Replacer targetReplacer = new StringReplacer("{target}", targetName);
        return new Replacer[]{healthReplacer, heartsReplacer, maxHealthReplacer, maxHeartsReplacer,
                absorbHealthReplacer, absorbHeartsReplacer, targetReplacer};
    }
}
