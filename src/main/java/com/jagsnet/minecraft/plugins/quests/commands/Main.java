package com.jagsnet.minecraft.plugins.quests.commands;

import com.jagsnet.minecraft.plugins.quests.Quests;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.*;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Messaging;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.parseInt;
import static org.bukkit.Bukkit.getServer;

public class Main implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String white = ChatColor.WHITE + "";

            if (!Messaging.ddeve) {
                if (cmd.getName().equals("addeve")){
                    Messaging.sendMessage(player, "<white>Unknown command. Type \"/help\" for help.</white>", "unformattedMessage");
                    return true;
                }
            }

            if (Messaging.ddeve) {
                if (cmd.getName().equals("quest")){
                    Messaging.sendMessage(player, "<white>Unknown command. Type \"/addeve\" for help.</white>", "unformattedMessage");
                    return true;
                }
            }
            // ------------------------------------------------------
            // ------------------ See help info ---------------------
            // ------------------------------------------------------
            if (args.length < 1 || args[0].equalsIgnoreCase("help")) {

                Messaging.sendConfiguredMessage((Player) sender, "mainMessages.questHelpHeader", "helpHeaderMessage");
                Messaging.sendConfiguredMessage((Player) sender, "mainMessages.questHelp", "helpMessage");


                ItemMeta mainHandItem = null;
                if (Scroll.isScrollMain(player)) {
                    mainHandItem = player.getInventory().getItemInMainHand().getItemMeta();
                }
                if (Scroll.isScrollOff(player)) {
                    mainHandItem = player.getInventory().getItemInOffHand().getItemMeta();
                }
                if (mainHandItem != null) {
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "rewardLocation");
                    if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        String[] location = mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING).split("_");
                        Configs.load(location[0]);
                        List helps = Configs.get().getStringList( location[1] + ".help");
                        if (helps != null && helps.size() != 0) {
                            Messaging.sendConfiguredMessage((Player) sender, "mainMessages.questSpecificHelp", "helpHeaderMessage");
                            for (int i = 0; i < helps.size(); i++) {
                                Messaging.sendMessage((Player) sender, String.valueOf(helps.get(i)), "helpMessage", (i+1) + "");
                            }
                        }
                    }
                }
                return true;
            }
            // ------------------------------------------------------
            // ------------------ Claim completed quest -------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("claim")) {
                if (Scroll.isScrollMain(player) || Scroll.isScrollOff(player)) {
                    ItemMeta mainHandItem = null;
                    if (Scroll.isScrollMain(player)) {
                        mainHandItem = player.getInventory().getItemInMainHand().getItemMeta();
                    }
                    if (Scroll.isScrollOff(player)) {
                        mainHandItem = player.getInventory().getItemInOffHand().getItemMeta();
                    }
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "rewardLocation");
                    String location = mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    key = new NamespacedKey(Quests.getInstance(), "expiry");
                    if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.LONG)) {
                        key = new NamespacedKey(Quests.getInstance(), "rewardLocation");
                        if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                            Configs.load(location.split("_")[0]);
                            if (Configs.get().getString(location.split("_")[1] + ".expiry") != null) {
                                long expiry = Long.valueOf(Configs.get().getString(location.split("_")[1] + ".expiry"));
                                key = new NamespacedKey(Quests.getInstance(), "expiry");
                                expiry += mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.LONG);
                                if (expiry <= System.currentTimeMillis()) {
                                    Messaging.sendConfiguredMessage(player, "mainMessages.questExpired");
                                    return true;
                                }
                            }
                        }
                    }

                    if (Completion.isComplete(mainHandItem)) {
                        if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                            Messaging.sendConfiguredMessage(player, "mainMessages.questDouble");
                            return true;
                        }
                        return Claim.claim(player, true, ".rewards", false);
                    } else {
                        Messaging.sendConfiguredMessage(player, "mainMessages.questIncomplete");
                        return true;
                    }
                }
                Messaging.sendConfiguredMessage(player, "mainMessages.invalidScroll");
                return true;
            }
            // ------------------------------------------------------
            // ------------------ See expiry ------------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("expiry")) {
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
                        if (Configs.get().getString(location.split("_")[1] + ".expiry") != null) {
                            Long expiry = Long.valueOf(Configs.get().getString(location.split("_")[1] + ".expiry"));
                            key = new NamespacedKey(Quests.getInstance(), "expiry");
                            if (!mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.LONG)) {
                                Messaging.sendConfiguredMessage(player, "mainMessages.questNoExpire");
                                return true;
                            }
                            expiry += mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.LONG);
                            Long current = System.currentTimeMillis();
                            if (current > expiry) {
                                Messaging.sendConfiguredMessage(player, "mainMessages.questExpired");
                                return true;
                            }
                            Long hours = (expiry / 60 / 60 / 1000) - (current / 60 / 60 / 1000);
                            if (hours < 6) {
                                Long minutes = (expiry / 60 / 1000) - (current / 60 / 1000);
                                Messaging.sendConfiguredMessage(player, "mainMessages.questExpiry", "standardMessage", minutes + " minutes");
                            } else {
                                Messaging.sendConfiguredMessage(player, "mainMessages.questExpiry", "standardMessage", hours + " hours");
                            }
                            return true;
                        }
                        Messaging.sendConfiguredMessage(player, "mainMessages.questNoExpire");
                        return true;
                    }

                    key = new NamespacedKey(Quests.getInstance(), "expiry");
                    if (!mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.LONG)) {
                        Messaging.sendConfiguredMessage(player, "mainMessages.questNoExpire");
                        return true;
                    }
                    Long expiry = mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.LONG);
                    Long current = System.currentTimeMillis();
                    if (current > expiry) {
                        Messaging.sendConfiguredMessage(player, "mainMessages.questExpired");
                        return true;
                    }
                    Long hours = (expiry / 60 / 60 / 1000) - (current / 60 / 60 / 1000);
                    if (hours < 6) {
                        Long minutes = (expiry / 60 / 1000) - (current / 60 / 1000);
                        Messaging.sendConfiguredMessage(player, "mainMessages.questExpiry", "standardMessage", minutes + " minutes");
                    } else {
                        Messaging.sendConfiguredMessage(player, "mainMessages.questExpiry", "standardMessage", hours + " hours");
                    }
                    return true;
                }
                Messaging.sendConfiguredMessage(player, "mainMessages.invalidScroll");
                return true;
            }
            // ------------------------------------------------------
            // ----------------- Complete a secret ------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("secret")) {
                if (Scroll.isScrollMain(player) || Scroll.isScrollOff(player)) {
                    ItemMeta mainHandItem = null;
                    Boolean isOff = false;
                    if (Scroll.isScrollMain(player)) {
                        mainHandItem = player.getInventory().getItemInMainHand().getItemMeta();
                    } else if (Scroll.isScrollOff(player)) {
                        mainHandItem = player.getInventory().getItemInOffHand().getItemMeta();
                        isOff = true;
                    }
                    if (args.length < 2) {
                        Messaging.sendConfiguredMessage(player, "mainMessages.noCode");
                        return true;
                    }
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "secretCooldown");
                    if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.LONG)) {
                        if (mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.LONG) > System.currentTimeMillis()) {
                            long t = mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.LONG) - System.currentTimeMillis();

                            Messaging.sendConfiguredMessage(player, "mainMessages.codeCooldown", "standardMessage", Messaging.time(t));
                            return true;
                        }
                    }

                    key = new NamespacedKey(Quests.getInstance(), "ultraSecret");
                    if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        if (mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING).equals("Claimed")) {
                            Messaging.sendConfiguredMessage(player, "mainMessages.codeAlreadyCracked");
                            return true;
                        }
                    }

                    key = new NamespacedKey(Quests.getInstance(), "rewardLocation");
                    String secret;
                    List ultraSecret = null;
                    String[] location = mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING).split("_");
                    if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        Configs.load(location[0]);
                        if (Configs.get().getStringList(location[1] + ".secret") == null) {
                            Messaging.sendConfiguredMessage(player, "mainMessages.noSecret");
                            return true;
                        }
                        secret = Configs.get().getString(location[1] + ".secret");
                        if (Configs.get().getStringList(location[1] + ".ultraSecret") != null) {
                            ultraSecret = Configs.get().getStringList(location[1] + ".ultraSecret");
                        }
                    } else {
                        Messaging.sendConfiguredMessage(player, "mainMessages.noSecret");
                        return true;
                    }

                    key = new NamespacedKey(Quests.getInstance(), "secretCooldown");
                    ItemStack is;
                    if (isOff) {
                        is = player.getInventory().getItemInOffHand();
                    } else {
                        is = player.getInventory().getItemInMainHand();
                    }
                    ItemMeta m = is.getItemMeta();

                    if (Configs.get().getString(location[1] + ".secretCooldown") != null) {
                        m.getPersistentDataContainer().set(key, PersistentDataType.LONG, System.currentTimeMillis() + Configs.get().getLong(location[1] + ".secretCooldown"));
                    } else {
                        m.getPersistentDataContainer().set(key, PersistentDataType.LONG, System.currentTimeMillis() + 60000);
                    }

                    is.setItemMeta(m);

                    String suppliedCode = "";
                    for (int i = 1; i < args.length; i++) {
                        if (i == args.length - 1) {
                            suppliedCode = suppliedCode + args[i];
                        } else {
                            suppliedCode = suppliedCode + args[i] + " ";
                        }
                    }

                    if (!secret.equals(suppliedCode)) {
                        if (Configs.get().getString(location[1] + ".secretCooldown") != null) {
                            Messaging.sendConfiguredMessage(player, "mainMessages.codeIncorrect", "standardMessage", Messaging.time(Configs.get().getLong(location[1] + ".secretCooldown")));
                        } else {
                            Messaging.sendConfiguredMessage(player, "mainMessages.codeIncorrect", "standardMessage", "60 seconds");
                        }
                        return true;
                    }

                    ArrayList<String> lore;
                    if (isOff) {
                        lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();
                    } else {
                        lore = (ArrayList<String>) player.getInventory().getItemInMainHand().getItemMeta().getLore();
                    }
                    for (int i = 0; i < lore.size(); i++) {
                        if (lore.get(i).contains("Guess The Secret Code") && lore.get(i).contains("Incomplete")) {
                            Completion.updateTxtLine(lore, player, i, isOff);
                        }
                    }

                    ItemStack itemStack;
                    if (isOff) {
                        itemStack = player.getInventory().getItemInOffHand();
                    } else {
                        itemStack = player.getInventory().getItemInMainHand();
                    }
                    ItemMeta meta = itemStack.getItemMeta();

                    if (ultraSecret != null) {
                        int rewardCount = ultraSecret.size();
                        int count = 0;
                        for (int slot = 0; slot < 36; slot++) {
                            ItemStack is2 = player.getInventory().getItem(slot);
                            if (is2 == null) {
                                count++;
                            }
                        }
                        if (count < rewardCount) {
                            Messaging.sendConfiguredMessage(player, "mainMessages.codeSpace");
                            return true;
                        }
                        key = new NamespacedKey(Quests.getInstance(), "ultraSecret");
                        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                            for (int i = 0; ultraSecret.size() > i; i++) {
                                String rewardName = (String) ultraSecret.get(i);
                                if (rewardName.contains("<player>")) {
                                    rewardName = rewardName.replace("<player>", ChatColor.stripColor(player.getName()));
                                }
                                Configs.loadLists();
                                while (rewardName.contains("<") && rewardName.contains(">")) {
                                    String listName = rewardName.substring(rewardName.indexOf("<") + 1, rewardName.indexOf(">"));
                                    List list = Configs.getLists().getStringList(listName);
                                    int randomNum = ThreadLocalRandom.current().nextInt(0, list.size());
                                    rewardName = rewardName.replace("<" + listName + ">", (CharSequence) list.get(randomNum));
                                }
                                String finalRewardName = rewardName;
                                Bukkit.getScheduler().runTask(Quests.getInstance(), () -> {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalRewardName);
                                });
                                Messaging.log("Quests ran: " + rewardName);
                            }
                        }
                    }
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "Claimed");
                    meta.setLore(lore);
                    itemStack.setItemMeta(meta);
                    Messaging.sendConfiguredMessage(player, "mainMessages.codeCorrect");
                    return true;
                }
                Messaging.sendConfiguredMessage(player, "mainMessages.invalidScroll");
                return true;
            }

            // ------------------------------------------------------
            // ------------------ Claim Quest Path ------------------
            // ------------------------------------------------------
            if (args[0].equalsIgnoreCase("path")) {
                if (args.length > 1) {
                    if (Scroll.isScrollMain(player) || Scroll.isScrollOff(player)) {
                        ItemMeta mainHandItem = null;
                        if (Scroll.isScrollMain(player)) {
                            mainHandItem = player.getInventory().getItemInMainHand().getItemMeta();
                        }
                        if (Scroll.isScrollOff(player)) {
                            mainHandItem = player.getInventory().getItemInOffHand().getItemMeta();
                        }
                        String location;
                        NamespacedKey key = new NamespacedKey(Quests.getInstance(), "rewardLocation");
                        if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                            location = mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                            Configs.load(location.split("_")[0]);
                        } else {
                            Messaging.sendConfiguredMessage(player, "mainMessages.pathNotConfigured");
                            return true;
                        }
                        key = new NamespacedKey(Quests.getInstance(), "expiry");
                        if (mainHandItem.getPersistentDataContainer().has(key, PersistentDataType.LONG)) {
                            if (Configs.get().getString(location.split("_")[1] + ".expiry") != null) {
                                long expiry = Long.valueOf(Configs.get().getString(location.split("_")[1] + ".expiry"));
                                key = new NamespacedKey(Quests.getInstance(), "expiry");
                                expiry += mainHandItem.getPersistentDataContainer().get(key, PersistentDataType.LONG);
                                if (expiry <= System.currentTimeMillis()) {
                                    Messaging.sendConfiguredMessage(player, "mainMessages.questExpired");
                                    return true;
                                }
                            }
                        }
                        if (Configs.get().getStringList(location.split("_")[1] + ".path." + args[1]).size() < 1) {
                            Messaging.sendConfiguredMessage(player, "mainMessages.pathInvalid");
                            return true;
                        }
                        return Claim.claim(player, true, ".path." +args[1], false);
                    }
                } else {
                    Messaging.sendConfiguredMessage(player, "mainMessages.pathInvalid");
                    return true;
                }
            }

            // ------------------------------------------------------
            // -------------------- List Quests ---------------------
            // ------------------------------------------------------
            Configs.loadGlobal();
            if (args[0].equalsIgnoreCase("list")) {
                String boldGold = ChatColor.GOLD + "" + ChatColor.BOLD + "";
                String boldRed = ChatColor.RED + "" + ChatColor.BOLD + "";
                String boldGreen = ChatColor.GREEN + "" + ChatColor.BOLD + "";

                ArrayList<ItemStack> items = new ArrayList<>();

                for (String s : Configs.getGlobal().getKeys(true)) {
                    if (s.split("\\.").length == 2 && s.split("\\.")[0].equals("commands")) {
                        String command = s.split("\\.")[1];
                        String dateFormat = Configs.getGlobal().getString("commands." + command + ".dateFormat");
                        String permissionName = Configs.getGlobal().getString("commands." + command + ".permissionName");
                        String description = Configs.getGlobal().getString("commands." + command + ".description");

                        SimpleDateFormat df;
                        String date;

                        if (dateFormat.equals("infinite")) {
                            date = dateFormat;
                        } else {
                            df = new SimpleDateFormat(dateFormat);
                            date = df.format(new Date());
                        }

                        if (!player.hasPermission("quest." + permissionName + "." + date)) {
                            items.add(Gui.createGuiItem(Quests.getInstance(), Material.PAPER, boldGreen + "/quest " + command, "quest " + command + "&quest list", white + description, white + "Click to collect the scroll!"));
                        } else {
                            items.add(Gui.createGuiItem(Quests.getInstance(), Material.RED_STAINED_GLASS_PANE, boldRed + "/quest " + command, "", white + description, white + "Scroll is unavailable, check back later!"));
                        }
                    }
                }
                Gui inv = new Gui(items, boldGold + "Quests!", Quests.getInstance());
                getServer().getPluginManager().registerEvents(inv, Quests.getInstance());
                inv.openInventory(player);
                return true;
            }

            // ------------------------------------------------------
            // ------------------ Custom Commands -------------------
            // ------------------------------------------------------
            String command = args[0].toLowerCase();
            if (Configs.getGlobal().getString("commands." + command + ".dateFormat") != null) {
                String dateFormat = Configs.getGlobal().getString("commands." + command + ".dateFormat");
                String permissionName = Configs.getGlobal().getString("commands." + command + ".permissionName");
                String permissionCooldown = Configs.getGlobal().getString("commands." + command + ".permissionCooldown");
                String quest = Configs.getGlobal().getString("commands." + command + ".quest");
                String denyMessage = Configs.getGlobal().getString("commands." + command + ".denyMessage");

                SimpleDateFormat df;
                String date;

                if (dateFormat.equals("infinite")) {
                    date = dateFormat;
                } else {
                    df = new SimpleDateFormat(dateFormat);
                    date = df.format(new Date());
                }

                if (player.hasPermission("quest." + permissionName + "." + date)) {
                    Messaging.sendMessage(player, denyMessage);
                    return true;
                }

                String[] genStuff = {quest.split("_")[0], quest.split("_")[1], player.getName()};

                if (!dateFormat.equals("infinite") && Generators.genScroll(genStuff, player)) {
                    Perms.setPerm(player.getName(), "quest." + permissionName + "." + date, permissionCooldown);
                    //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission settemp quest." + permissionName + "." + date + " true " + permissionCooldown);
                    return true;
                } else if (dateFormat.equals("infinite") && Generators.genScroll(genStuff, player)) {
                    Perms.setPermPerm(player.getName(), "quest." + permissionName + "." + date);
                }

                return true;
            }

            // ------------------------------------------------------
            // ------------------ Set Scoreboard --------------------
            // ------------------------------------------------------
            if (args.length >= 1 && args[0].equalsIgnoreCase("display")) {
                if (Scroll.isScrollOff(player)) {

                    // set display mode
                    if (args.length > 1) {
                        switch (args[1]) {
                            case "compact", "on":
                                Perms.setDisplayPerm(player, "compact");
                                Messaging.sendConfiguredMessage(player, "mainMessages.displayCompact");
                                break;
                            case "full":
                                Perms.setDisplayPerm(player, "full");
                                Messaging.sendConfiguredMessage(player, "mainMessages.displayFull");
                                break;
                            case "off":
                                Perms.setDisplayPerm(player, "off");
                                Messaging.sendConfiguredMessage(player, "mainMessages.displayOff");
                                break;
                            default:
                                // if invalid mode specified, do nothing
                                Messaging.sendConfiguredMessage(player, "mainMessages.displayIncorrect");
                                return true;
                        }
                        return true;
                    }

                    // If no mode specified, toggle instead
                    if (player.hasPermission("quest.display.compact") || player.hasPermission("quest.display.compact")) {
                        Perms.setDisplayPerm(player, "off");
                        Messaging.sendConfiguredMessage(player, "mainMessages.displayOff");
                    } else {
                        Perms.setDisplayPerm(player, "compact");
                        Messaging.sendConfiguredMessage(player, "mainMessages.displayCompact");
                    }

                }
                return true;
            }

            Messaging.sendConfiguredMessage(player, "mainMessages.invalidCommand");
        } else {
            Messaging.log("A player must run this command");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("claim");
            completions.add("display");
            completions.add("expiry");
            completions.add("list");
            completions.add("secret");
            completions.add("path");
        }

        if (args.length == 2 && args[0].equals("display")) {
            completions.add("full");
            completions.add("compact");
            completions.add("off");
        }

        if (args.length == 2 && args[0].equals("secret")) {
            completions.add("<code>");
        }
        if (args.length == 2 && args[0].equals("path")) {
            completions.add("<path>");
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}
