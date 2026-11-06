package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class DamageDone implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) { return; }
        if (event.getFinalDamage() < 1) { return; }
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if (e.getDamager() instanceof Player) {
                Player player = (Player) e.getDamager();
                if (Scroll.isScrollOff(player)) {

                    String entity = e.getEntity().getType().toString().replace("_", " ").toLowerCase();
                    String typedEntity = Strings.getEntityType(event.getEntity());
                    ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();

                    CreatureSpawnEvent.SpawnReason spawnReason = event.getEntity().getEntitySpawnReason();
                    for (int i = 0; i < lore.size(); i++) {
                        String line = Strings.cleanLore(lore.get(i), true, false);

                        // deal x damage
                        if ((line.split(" ", 0).length ==
                                4) && (line.contains("damage") &&
                                line.contains("deal"))) {
                            if (Completion.updateNumLine(lore, player, (int) event.getFinalDamage(), i)) {
                                return;
                            }
                        }

                        // deal x damage to wild y
                        if (
                                line.contains(" wild ")
                                &&
                                (
                                    (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ").length + 6) ||
                                    (line.contains(" " + typedEntity) && line.split(" ", 0).length == typedEntity.split(" ", 0).length + 6)
                                )
                                &&
                                (
                                    line.contains("damage") &&
                                    line.contains("deal")
                                )
                        ) {
                            if (Completion.isWild(spawnReason)) {
                                if (Completion.updateNumLine(lore, player, (int) event.getFinalDamage(), i)) {
                                    return;
                                }
                            }
                        }

                        // deal x damage
                        if (
                                (
                                    (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ").length + 5) ||
                                    (line.contains(" " + typedEntity) && line.split(" ", 0).length == typedEntity.split(" ", 0).length + 5)
                                )
                                &&
                                (
                                    line.contains("damage") &&
                                    line.contains("deal")
                                )
                        ) {
                            if (Completion.updateNumLine(lore, player, (int) event.getFinalDamage(), i)) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
