package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Messaging;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;

import java.util.ArrayList;

public class DyeSheep implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDye(SheepDyeWoolEvent event) {
        if (event.isCancelled()) { return; }
        Player player = (Player) event.getPlayer();
        if (Scroll.isScrollOff(player)) {

            String entity = event.getEntityType().toString().replace("_", " ").toLowerCase();
            String typedEntity = Strings.getEntityType(event.getEntity());
            ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();
            CreatureSpawnEvent.SpawnReason spawnReason = event.getEntity().getEntitySpawnReason();
            String colour = event.getColor().name().replace("_", " ").toLowerCase();

            for (int i = 0; i < lore.size(); i++) {
                String line = Strings.cleanLore(lore.get(i), true, false);

                // wild mobs
                if (
                        line.contains(" wild ")
                                &&
                                (
                                        (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 5) ||
                                        (line.contains(" " + typedEntity) && line.split(" ", 0).length == typedEntity.split(" ", 0).length + 5)
                                )
                                &&
                                line.contains("dye")
                                &&
                                line.contains(colour)
                ) {
                    if (Completion.isWild(spawnReason)) {
                        if (Completion.updateNumLine(lore, player, 1, i)) {
                            return;
                        }
                    }
                }

                // all mobs
                if (
                        (
                            (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 4) ||
                            (line.contains(" " + typedEntity) && line.split(" ", 0).length == typedEntity.split(" ", 0).length + 4)
                        )
                        &&
                        line.contains("dye")
                        &&
                        line.contains(colour)
                ) {
                    if (Completion.updateNumLine(lore, player, 1, i)) {
                        return;
                    }
                }
            }
        }
    }
}

