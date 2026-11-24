package com.jagsnet.minecraft.plugins.quests.trackers.scheduledTasks;

import com.jagsnet.minecraft.plugins.quests.Quests;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Messaging;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Configs;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Perms;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;

public class ScoreboardDisplay {
    public static void showScoreboard() {
        String scoreBoardObjectiveName = "quest_log";
        for (Player p : Bukkit.getOnlinePlayers()) {

            if(!p.hasPermission("quest.display.off") && !p.hasPermission("quest.display.full") && !p.hasPermission("quest.display.compact")) {
                Perms.setDisplayPerm(p, "compact");
            }

            if (Scroll.isScrollOff(p)) {

                // get quest scroll meta
                ItemMeta questMeta = p.getInventory().getItemInOffHand().getItemMeta();

                if (p.hasPermission("quest.display.full") || p.hasPermission("quest.display.compact")) {

                    // initiate scoreboard
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    Scoreboard board = (Scoreboard) manager.getNewScoreboard();

                    // Set title
                    Objective objective = board.registerNewObjective(
                            scoreBoardObjectiveName,
                            Criteria.DUMMY,
                            questMeta.displayName()
                    );

                    // Set sidebar
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                    // Add lines of text
                    LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
                            .character('§')
                            .hexColors()
                            .useUnusualXRepeatedCharacterHexFormat()
                            .build();



                    ArrayList<Component> lore = (ArrayList<Component>) p.getInventory().getItemInOffHand().getItemMeta().lore();
                    int linePriority = 16;

                    String mode = "compact";

                    if (p.hasPermission("quest.display.full")) {
                        mode = "full";
                    }

                    String expiryString = "";
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "rewardLocation");
                    ItemStack mainHandItem = p.getInventory().getItemInOffHand();
                    String location = mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    Configs.load(location.split("_")[0]);
                    if (Configs.get().getString(location.split("_")[1] + ".expiry") != null) {
                        Long expiry = Long.valueOf(Configs.get().getString(location.split("_")[1] + ".expiry"));
                        key = new NamespacedKey(Quests.getInstance(), "expiry");
                        if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.LONG)) {
                            expiry += mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.LONG);
                            Long current = System.currentTimeMillis();
                            if (!(current > expiry)) {
                                expiryString = "§7Quest Expires in " + Messaging.roughTime(expiry - current);
                                TextComponent expiryComponent = Component.text().content(expiryString).build();
                                String expiryLine = serializer.serialize(expiryComponent);
                                Score expiryScore = objective.getScore(expiryLine);
                                expiryScore.setScore(0);
                                expiryScore.numberFormat(NumberFormat.blank());
                            }
                        }
                    }

//                    if (!expiryString.equals("")) {
//                        TextComponent empty = Component.text()
//                                .content(" ")
//                                .decorate(TextDecoration.BOLD)
//                                .build();
//                        String emptyText = serializer.serialize(empty);
//                        Score lineBreak = objective.getScore(emptyText);
//                        lineBreak.setScore(100);
//                        lineBreak.numberFormat(NumberFormat.blank());
//                    }

                    if (mode == "compact") {
                        for (int i = 0; i < lore.size() && i < 13; i++) {
                            String formattedLine = serializer.serialize(lore.get(i));
                            if (!formattedLine.contains(": ")) { continue; }
                            Team team = board.registerNewTeam(p.getName() + "_line_" + i);
                            String entryIdentifier = "§" + Integer.toHexString(i) + "§r";
                            team.addEntry(entryIdentifier);
                            team.setPrefix(formattedLine);

                            Score line = objective.getScore(entryIdentifier);
                            line.setScore(linePriority);
                            line.numberFormat(NumberFormat.blank());
                            linePriority--;
                        }
                        Team team = board.registerNewTeam(p.getName() + "_line_" + 50);
                        String entryIdentifier = "§" + Integer.toHexString(15) + "§r";
                        team.addEntry(entryIdentifier);
                        team.setPrefix("§7Use /quest help for quest info");

                        Score line = objective.getScore(entryIdentifier);
                        line.setScore(1);
                        line.numberFormat(NumberFormat.blank());
                    } else {
                        for (int i = 0; i < lore.size() && i < 13; i++) {
                            Team team = board.registerNewTeam(p.getName() + "_line_" + i);
                            String entryIdentifier = "§" + Integer.toHexString(i) + "§r";
                            team.addEntry(entryIdentifier);

                            String formattedLine = serializer.serialize(lore.get(i));
                            team.setPrefix(formattedLine);

                            Score line = objective.getScore(entryIdentifier);
                            line.setScore(linePriority);
                            line.numberFormat(NumberFormat.blank());
                            linePriority--;
                        }
                    }

                    // apply to player
                    p.setScoreboard(board);
                } else { hideScoreboard(p, scoreBoardObjectiveName); }
            } else { hideScoreboard(p, scoreBoardObjectiveName); }
        }
    }

    private static void hideScoreboard(Player p, String scoreBoardObjectiveName) {
        // If Player doesn't have a quest, fetch scoreboard and unregister
        Scoreboard board = p.getScoreboard();
        Objective objective = board.getObjective(scoreBoardObjectiveName);

        if (objective != null) {
            objective.unregister();
        }
    }
}
