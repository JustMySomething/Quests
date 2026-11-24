package com.jagsnet.minecraft.plugins.quests.commands;

import com.jagsnet.minecraft.plugins.quests.Quests;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Messaging;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Claim;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Configs;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class Admin implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;
            if (!player.hasPermission("quests.admin")) {
                Messaging.sendConfiguredMessage(player, "adminMessages.missingPermissions");
                return true;
            }
            if (args.length < 1) {
                Messaging.sendConfiguredMessage(player, "adminMessages.aquestHelpHeader", "helpHeaderMessage");
                Messaging.sendConfiguredMessage(player, "adminMessages.aquestHelp", "helpMessage");
                return true;
            }
            // ------------------------------------------------------
            // ------------------ List Rewards ----------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("rewardList")) {
                if (Scroll.isScrollMain(player) || Scroll.isScrollOff(player)) {
                    ItemMeta mainHandItem = null;
                    if (Scroll.isScrollMain(player)) {
                        mainHandItem = player.getInventory().getItemInMainHand().getItemMeta();
                    }
                    if (Scroll.isScrollOff(player)) {
                        mainHandItem = player.getInventory().getItemInOffHand().getItemMeta();
                    }
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "rewardLocation");
                    if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        String location = mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                        Configs.load(location.split("_")[0]);
                        List rewards = Configs.get().getStringList( location.split("_")[1] + ".rewards");
                        int rewardCount = rewards.size();
                        Messaging.sendConfiguredMessage(player, "adminMessages.rewardHeader", "standardMessage", String.valueOf(rewardCount));
                        for (int i = 0; i < rewardCount; i++) {
                            Messaging.sendMessage(player, "/" + rewards.get(i).toString(), "helpMessage", "Reward " + String.valueOf(i));
                        }
                    } else {
                        Messaging.sendConfiguredMessage(player, "adminMessages.rewardMissing");
                    }
                } else {
                    Messaging.sendConfiguredMessage(player, "adminMessages.invalidScroll");
                }
                return true;
            }
            // ------------------------------------------------------
            // ------------------ Set expiry ------------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("expiry")) {
                if (args.length < 2) {
                    Messaging.sendConfiguredMessage(player, "adminMessages.expiryNoMilli");
                    return true;
                }
                long expiry;
                try {
                    expiry = parseLong(args[1]);
                } catch (Exception e) {
                    Messaging.sendConfiguredMessage(player, "adminMessages.expiryInvalidMilli");
                    return true;
                }
                expiry = expiry + System.currentTimeMillis();
                if (Scroll.isScrollMain(player)) {
                    ItemMeta mainHandItem = player.getInventory().getItemInMainHand().getItemMeta();
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "expiry");
                    mainHandItem.getPersistentDataContainer().set(key, PersistentDataType.LONG, expiry);
                    Messaging.sendConfiguredMessage(player, "adminMessages.expirySet", "standardMessage", String.valueOf(expiry));
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    itemStack.setItemMeta(mainHandItem);
                }
                return true;
            }
            // ------------------------------------------------------
            // --------- Set config for scroll generation -----------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("monthly")) {
                if (args.length < 4) {
                    Messaging.sendMessage(player, "Insufficient arguments. Probably don't use this command anyway");
                    return true;
                }
                String out = "";
                for (int i = 4; i <= args.length; i++) {
                    if (args.length == i) {
                        out = out + args[i - 1];
                    } else {
                        out = out + args[i - 1] + " ";
                    }
                }
                Configs.get().set(args[1] + "." + args[2], out);
                Configs.save();
                return true;
            }
            // ------------------------------------------------------
            // ----- Claim a quest scroll without completing it -----
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("claim")) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("force")) {
                        if (Scroll.isScrollMain(player) || Scroll.isScrollOff(player)) {
                            return Claim.claim(player, false, ".rewards", true);
                        }
                    }
                }
                if (Scroll.isScrollMain(player) || Scroll.isScrollOff(player)) {
                    return Claim.claim(player, false, ".rewards", false);
                }
                Messaging.sendConfiguredMessage(player, "adminMessages.invalidScroll");
                return true;
            }
            // ------------------------------------------------------
            // --------------------- Who ----------------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("who")) {
                if (Scroll.isScrollMain(player)) {
                    ItemMeta mainHandItem = player.getInventory().getItemInMainHand().getItemMeta();
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "UUID");
                    if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        Messaging.sendMessage(player, Bukkit.getPlayer(UUID.fromString(mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING))).getDisplayName());
                    } else {
                        Messaging.sendConfiguredMessage(player, "adminMessages.whoAnyone");
                    }
                }
                return true;
            }
            // ------------------------------------------------------
            // --------------------- Set Who ------------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("setwho")) {
                if (Scroll.isScrollMain(player)) {
                    if (args.length < 2) {
                        Messaging.sendConfiguredMessage(player, "adminMessages.setWhoNoUUID");
                        return true;
                    }
                    ItemMeta mainHandItem = player.getInventory().getItemInMainHand().getItemMeta();
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "UUID");
                    mainHandItem.getPersistentDataContainer().set(key, PersistentDataType.STRING, args[1]);
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    itemStack.setItemMeta(mainHandItem);
                    if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        Messaging.sendMessage(player, Bukkit.getOfflinePlayer(UUID.fromString(mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING))).getName());
                    } else {
                        Messaging.sendConfiguredMessage(player, "adminMessages.whoAnyone");
                    }
                }
                return true;
            }
            // ------------------------------------------------------
            // --------------------- Reload Lists -------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("reloadlist")) {
                Configs.loadLists();
                Messaging.sendConfiguredMessage(player, "adminMessages.reloadComplete");
                return true;
            }
            // ------------------------------------------------------
            // ----------------- Quests Version Info ----------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("help")) {
                Messaging.sendMessage(player, "Quests - Created By MelonMarauda");
                Messaging.sendMessage(player, "https://github.com/JustMySomething/Quests", "helpMessage", Strings.questVersion());
                return true;
            }
            // ------------------------------------------------------
            // ------------------ Set Autocomplete ------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("auto")) {
                if (args.length < 2) {
                    Messaging.sendConfiguredMessage(player, "adminMessages.autoStatus", "standardMessage", String.valueOf(Quests.auto));
                    return true;
                }
                Configs.getGlobal().set("auto", Boolean.valueOf(args[1]));
                Configs.saveGlobal();
                Quests.setAuto();
                Messaging.sendConfiguredMessage(player, "adminMessages.autoStatus", "standardMessage", String.valueOf(Quests.auto));
                return true;
            }

            // ------------------------------------------------------
            // ------------------ Check scroll/list -----------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("check")) {
                if (args.length < 2) {
                    Messaging.sendMessage(player, "Something something");
                    return true;
                }
                List<String> lines = Configs.getLists().getStringList(args[1]);
                for (String s : lines) {
                    List<String> line = Arrays.asList(s.split(" "));
                    switch (line.get(0)) {

                        default: break;
                    }
                }

                return true;
            }

            // ------------------------------------------------------
            // --------------------- List scrolls -------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("list")) {
                if (args.length < 2) {
                    File[] files = new File(String.valueOf(Bukkit.getServer().getPluginManager().getPlugin("Quests").getDataFolder())).listFiles();
                    Messaging.sendConfiguredMessage(player, "adminMessages.listHeader", "helpHeaderMessage");
                    for (File f : files) {
                        if (f.getName().contains(".yml") && !f.getName().contains("Global.yml") && !f.getName().contains("Lists.yml")){
                            Messaging.sendMessage(player, f.getName().replace(".yml", ""), "helpMessage");
                        }
                    }
                    return true;
                }

                File file = new File(Bukkit.getServer().getPluginManager().getPlugin("Quests").getDataFolder(), args[1] + ".yml");
                FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
                Set<String> keys = fc.getKeys(false);
                Messaging.sendConfiguredMessage(player, "adminMessages.listQuestHeader", "helpHeaderMessage");
                for (String key : keys) {
                    Messaging.sendMessage(player, "/gquest " + args[1] + " " + key + " <playerName>", "helpMessage");
                }

                return true;
            }

            Messaging.sendConfiguredMessage(player, "adminMessages.invalidCommand");
        } else {
            Messaging.log("A player must run this command");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("rewardList");
            completions.add("expiry");
            completions.add("claim");
            completions.add("who");
            completions.add("setWho");
            completions.add("reloadList");
            completions.add("version");
            completions.add("auto");
            completions.add("list");
        }

        if (args.length == 2 && args[0].equals("expiry")) {
            completions.add("<expiryInMilliseconds>");
        }

        if (args.length == 2 && args[0].equals("setWho")) {
            completions.add("<UUID>");
        }

        if (args.length == 2 && args[0].equals("auto")) {
            completions.add("true");
            completions.add("false");
        }

        if (args.length == 2 && args[0].equals("list")) {
            for (String fileName : Configs.getQuestConfigsNames()) {
                completions.add(fileName);
            }
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}