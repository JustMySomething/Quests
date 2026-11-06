package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.jagsnet.minecraft.plugins.quests.Quests;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Messaging;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Strange implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
//    public void onRight(PlayerInteractEntityEvent event) {
    public void onRight(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (Scroll.isScrollOff(player)) {
            NamespacedKey key = new NamespacedKey(Quests.getInstance(), "strangeCooldown");
            if (event.getRightClicked().getPersistentDataContainer().has(key, PersistentDataType.LONG)) {
                if (event.getRightClicked().getPersistentDataContainer().get(key, PersistentDataType.LONG) > System.currentTimeMillis()) {
                    return;
                }
            }

            String mainHandItem = event.getPlayer().getInventory().getItemInMainHand().getType().toString().toLowerCase();
            String entity = event.getRightClicked().getType().toString().toLowerCase();
            String typedEntity = Strings.getEntityType(event.getRightClicked());

            if (mainHandItem.equalsIgnoreCase("bucket")) mainHandItem = "milk";
            if (mainHandItem.equals("milk") && player.getInventory().getItemInMainHand().getAmount() > 1) {
                Messaging.sendMessage(player, "You can't use more than one bucket at a time. I know, a pain, sorry.");
                return;
            }
            if (mainHandItem.equals("milk") && player.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                Messaging.sendMessage(player, "You can't use special buckets for this task, sorry! :)");
                return;
            }
            if (mainHandItem.equalsIgnoreCase("shears")) mainHandItem = "shear";

            ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();
            int length = 2 + mainHandItem.split(" ", 0).length + entity.split(" ", 0).length;

            entity = entity.toLowerCase().replace("_", " ");

            for (int i = 0; i < lore.size(); i++) {
                String line = Strings.cleanLore(lore.get(i), true, false);
                if (    // all mobs
                        (
                            line.contains(mainHandItem) &&
                            (
                                (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 3) ||
                                (line.contains(" " + typedEntity) && line.split(" ", 0).length == typedEntity.split(" ", 0).length + 3)
                            )
                        )
                        ||
                        // wild mobs
                        (
                            Completion.isWild(event.getRightClicked().getEntitySpawnReason()) &&
                            line.contains(" wild ") &&
                            line.contains(mainHandItem) &&
                            (
                                (line.contains(" " + entity) && line.split(" ", 0).length == entity.split(" ", 0).length + 4) ||
                                (line.contains(" " + typedEntity) && line.split(" ", 0).length == typedEntity.split(" ", 0).length + 4)
                            )
                        )
                ) {
                    if (Completion.updateNumLine(lore, player, 1, i)) {
                        if (mainHandItem.equalsIgnoreCase("milk") && entity.equalsIgnoreCase("cow")) return;
                        event.getRightClicked().getPersistentDataContainer().set(key, PersistentDataType.LONG, System.currentTimeMillis() + 30000);

                        String b = ChatColor.BOLD + "";

                        ItemStack is;
                        ItemMeta im;
                        switch (mainHandItem) {
                            case "shear":
                                is  = new ItemStack(Material.WHITE_WOOL);
                                im = is.getItemMeta();
                                im.setDisplayName(b + WordUtils.capitalize(entity) + " Wool");
                                is.setItemMeta(im);
                                event.getRightClicked().getWorld().dropItem(event.getRightClicked().getLocation(), is);
                                player.playSound(player.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1F, 1F);
                                break;
                            case "milk":
                                event.setCancelled(true);
                                is  = new ItemStack(Material.MILK_BUCKET);
                                im = is.getItemMeta();
                                im.setDisplayName(b + WordUtils.capitalize(entity) + " Milk");
                                is.setItemMeta(im);
                                player.getInventory().setItemInMainHand(is);
                                player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 1F, 1F);
                                break;
                            default: break;
                        }
                        return;
                    }
                }
            }
        }
    }
}
