package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;

import java.util.ArrayList;

public class Breeding implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreed(EntityBreedEvent event) {
        if (event.isCancelled()) { return; }
        if (event.getBreeder() instanceof Player) {
            Player player = (Player) event.getBreeder();
            if (Scroll.isScrollOff(player)) {

                String entity = event.getEntityType().toString().replace("_", " ").toLowerCase();
                String typedEntity = Strings.getEntityType(event.getEntity());
                ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();

                CreatureSpawnEvent.SpawnReason mother = event.getMother().getEntitySpawnReason();
                CreatureSpawnEvent.SpawnReason father = event.getFather().getEntitySpawnReason();
                for (int i = 0; i < lore.size(); i++) {
                    String line = Strings.cleanLore(lore.get(i), true, false);

                    // wild mobs
                    if (
                            line.contains(" wild ")
                            &&
                            (
                                (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 4) ||
                                (line.contains(" " + typedEntity) && line.split(" ", 0).length == typedEntity.split(" ", 0).length + 4)
                            )
                            &&
                            line.contains("breed")
                ) {
                        if (Completion.isWild(mother) && Completion.isWild(father)) {
                            if (Completion.updateNumLine(lore, player, 1, i)) {
                                return;
                            }
                        }
                    }

                    // all mobs
                    if (
                            (
                                (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 3) ||
                                (line.contains(" " + typedEntity) && line.split(" ", 0).length == typedEntity.split(" ", 0).length + 3)
                            )
                            &&
                            line.contains("breed")
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
