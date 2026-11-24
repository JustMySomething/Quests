package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Messaging;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;

public class Repairing implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnvilRepair(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory anvil)) return;
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.isCancelled()) return;

        ItemStack input = anvil.getItem(0);
        ItemStack result = event.getCurrentItem();

        if (input == null || result == null || result.getType().isAir()) return;
        if (input.getType() != result.getType()) return;

        if (player.getGameMode() != GameMode.CREATIVE && player.getLevel() < anvil.getRepairCost()) {
            return;
        }

        if (input.getItemMeta() instanceof Damageable oldMeta
                && result.getItemMeta() instanceof Damageable newMeta) {

            int repairedAmount = oldMeta.getDamage() - newMeta.getDamage();
            if (repairedAmount > 0) {

                if (Scroll.isScrollOff(player)) {

                    String entity = result.getType().toString().replace("_", " ").toLowerCase();
                    ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();

                    for (int i = 0; i < lore.size(); i++) {
                        String line = Strings.cleanLore(lore.get(i), false, true);
                        if (
                                (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 3)
                                        &&
                                        line.contains("repair")
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
}
