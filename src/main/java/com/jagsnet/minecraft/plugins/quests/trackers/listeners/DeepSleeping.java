package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.ArrayList;

public class DeepSleeping implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSleep(PlayerDeepSleepEvent event) {
        if (event.isCancelled()) {return;}

        Player player = event.getPlayer();
        if (Scroll.isScrollOff(player)) {

            Location bedLocation = player.getBedLocation();
            Block bedBlock = bedLocation.getBlock();

            String entity = bedBlock.getType().toString().replace("_", " ").toLowerCase();
            ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();

            for (int i = 0; i < lore.size(); i++) {
                String line = Strings.cleanLore(lore.get(i), false, true);

                if (
                        (
                                (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 5) ||
                                        (line.contains(" bed") && line.split(" ", 0).length == 6)
                        )
                                &&
                                line.contains("deepsleep")
                ) {
                    if (Completion.updateNumLine(lore, player, 1, i)) {
                        return;
                    }
                }
            }
        }

    }
}