package com.SirBlobman.compressed.hearts.scoreboard;

import com.SirBlobman.api.nms.AbstractNMS;
import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.api.nms.ScoreboardHandler;
import com.SirBlobman.compressed.hearts.CompressedHearts;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

public final class ScoreboardUtil {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private static final Scoreboard scoreboard = manager.getNewScoreboard();
    private static final Objective objective = createObjective();
    
    private static Objective createObjective() {
        CompressedHearts plugin = JavaPlugin.getPlugin(CompressedHearts.class);
        MultiVersionHandler<CompressedHearts> nmsHandler = plugin.getMultiVersionHandler();
        
        AbstractNMS nmsInterface = nmsHandler.getInterface();
        ScoreboardHandler scoreboardHandler = nmsInterface.getScoreboardHandler();
        
        Objective objective = scoreboardHandler.createObjective(scoreboard, "comp_hearts", "dummy", "Hearts");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        return objective;
    }
    
    public static void updateScoreboard(Player player, int hearts) {
        String playerName = player.getName();
        Score score = objective.getScore(playerName);
        score.setScore(hearts);
        
        player.setScoreboard(scoreboard);
    }
}