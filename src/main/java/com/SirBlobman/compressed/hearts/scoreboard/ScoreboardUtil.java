package com.SirBlobman.compressed.hearts.scoreboard;

import com.SirBlobman.api.SirBlobmanAPI;
import com.SirBlobman.api.nms.NMS_Handler;
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
        JavaPlugin plugin = JavaPlugin.getPlugin(CompressedHearts.class);
        SirBlobmanAPI api = SirBlobmanAPI.getInstance(plugin);
    
        NMS_Handler nmsHandler = api.getVersionHandler();
        ScoreboardHandler scoreboardHandler = nmsHandler.getScoreboardHandler();
        
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