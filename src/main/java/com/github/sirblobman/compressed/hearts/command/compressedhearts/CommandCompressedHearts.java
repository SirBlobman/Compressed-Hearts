package com.github.sirblobman.compressed.hearts.command.compressedhearts;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.compressed.hearts.HeartsPlugin;

public class CommandCompressedHearts extends Command {
    public CommandCompressedHearts(HeartsPlugin plugin) {
        super(plugin, "compressed-hearts");
        addSubCommand(new CommandCompressedHeartsCompress(plugin));
        addSubCommand(new CommandCompressedHeartsDisplay(plugin));
        addSubCommand(new CommandCompressedHeartsHelp(plugin));
        addSubCommand(new CommandCompressedHeartsReload(plugin));
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
    
    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        return false;
    }
}
