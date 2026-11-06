package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Messaging;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import io.papermc.paper.event.entity.EntityCompostItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ShearingBlocks implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onShearBlock(PlayerShearBlockEvent event) {
        if (event.isCancelled()) {return;}
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (Scroll.isScrollOff(player)) {

                String entity = event.getBlock().getType().toString().replace("_", " ").toLowerCase();
                ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();

                for (int i = 0; i < lore.size(); i++) {
                    String line = Strings.cleanLore(lore.get(i), false, true);
                    if (
                            (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 3)
                                    &&
                                    line.contains("shear")
                    ) {
                        if (Completion.updateNumLine(lore, player, 1, i)) {
                            return;
                        }
                    }
                }
            }
        }
    }
}