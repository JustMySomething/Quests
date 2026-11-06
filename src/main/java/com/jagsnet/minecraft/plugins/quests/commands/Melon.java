package com.jagsnet.minecraft.plugins.quests.commands;

import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Messaging;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Configs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Melon implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.getName().equals("MelonMarauda")) {
                Messaging.sendMessage(player, "What if... No");
                return true;
            }

            Configs.loadQuestConfigNames();

            boolean ddeve = false;

            try {
                ddeve = Boolean.parseBoolean(args[0]);
            } catch (Exception e) {}

            Messaging.setDdeve(ddeve);
            Messaging.sendMessage(player, "Addeve mode set to " + String.valueOf(Messaging.ddeve));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("true");
            completions.add("false");
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}