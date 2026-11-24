package com.jagsnet.minecraft.plugins.quests.otherStuff.messages;

import com.jagsnet.minecraft.plugins.quests.Quests;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Configs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Messaging {
    public static boolean ddeve = false;

    public static void setDdeve(boolean b) {
        ddeve = b;
    }

    // Load new messaging formatting for use

    private static String pluginName;
    private static String messagePrefix;
    private static String messageColour;
    private static String commandColour;
    private static String helpColour;
    private static String standardMessage;
    private static String helpMessage;
    private static String helpHeaderMessage;

    public static void loadMessageFormattingStrings() {
        pluginName = Configs.getMessagingConfig().getString("main.pluginName");
        messagePrefix = Configs.getMessagingConfig().getString("main.messagePrefix").replace("<pluginName>", pluginName);
        messageColour = Configs.getMessagingConfig().getString("main.messageColour");
        commandColour = Configs.getMessagingConfig().getString("main.commandColour");
        helpColour = Configs.getMessagingConfig().getString("main.helpColour");
        standardMessage = Configs.getMessagingConfig().getString("main.standardMessage").replace("<messagePrefix>", messagePrefix).replace("<messageColour>", messageColour);
        helpMessage = Configs.getMessagingConfig().getString("main.helpMessage").replace("<commandColour>", commandColour).replace("<helpColour>", helpColour);
        helpHeaderMessage = Configs.getMessagingConfig().getString("main.helpHeaderMessage").replace("<messagePrefix>", messagePrefix).replace("<messageColour>", messageColour).replace("<helpColour>", helpColour);
    }

    // ------------- Send NEW Formatted Message To Player from Messaging.yml config file ------------------
    public static void sendConfiguredMessage(Player player, String location) {
        sendConfiguredMessage(player, location, "standardMessage", "", "", "", "", "");
    }
    public static void sendConfiguredMessage(Player player, String location, String messageType) {
        sendConfiguredMessage(player, location, messageType, "", "", "", "", "");
    }
    public static void sendConfiguredMessage(Player player, String location, String messageType, String input1) {
        sendConfiguredMessage(player, location, messageType, input1, "", "", "", "");
    }
    public static void sendConfiguredMessage(Player player, String location, String messageType, String input1, String input2) {
        sendConfiguredMessage(player, location, messageType, input1, input2, "", "", "");
    }
    public static void sendConfiguredMessage(Player player, String location, String messageType, String input1, String input2, String input3) {
        sendConfiguredMessage(player, location, messageType, input1, input2, input3, "", "");
    }
    public static void sendConfiguredMessage(Player player, String location, String messageType, String input1, String input2, String input3, String input4) {
        sendConfiguredMessage(player, location, messageType, input1, input2, input3, input4, "");
    }
    public static void sendConfiguredMessage(Player player, String location, String messageType, String input1, String input2, String input3, String input4, String input5) {
        if (Configs.getMessagingConfig().isSet(location)) {
            if (Configs.getMessagingConfig().isList(location)) {
                List<Map<?, ?>> mapList = Configs.getMessagingConfig().getMapList(location);
                for (Map<?, ?> entry : mapList) {
                    sendMessage(player, String.valueOf(entry.get("message")), messageType, String.valueOf(entry.get("prefix")), input2, input3, input4, input5);
                }
            } else {
                sendMessage(player, Configs.getMessagingConfig().getString(location), messageType, input1, input2, input3, input4, input5);
            }
        } else {
            sendMessage(player, "Mmm this regularly scheduled Quest message is broken. Please cite " + location + " to your nearest admin to have them resolve it. Have a nice day!", "standardMessage", input1, input2, input3, input4, input5);
        }
    }

    // ------------- Send NEW Formatted Message To Player ------------------
    public static void sendMessage(Player player, String msg){
        sendMessage(player, msg, "standardMessage", "", "", "", "", "");
    }
    public static void sendMessage(Player player, String msg, String messageType){
        sendMessage(player, msg, messageType, "", "", "", "", "");
    }
    public static void sendMessage(Player player, String msg, String messageType, String input1){
        sendMessage(player, msg, messageType, input1, "", "", "", "");
    }
    public static void sendMessage(Player player, String msg, String messageType, String input1, String input2){
        sendMessage(player, msg, messageType, input1, input2, "", "", "");
    }
    public static void sendMessage(Player player, String msg, String messageType, String input1, String input2, String input3){
        sendMessage(player, msg, messageType, input1, input2, input3, "", "");
    }
    public static void sendMessage(Player player, String msg, String messageType, String input1, String input2, String input3, String input4){
        sendMessage(player, msg, messageType, input1, input2, input3, input4, "");
    }
    public static void sendMessage(Player player, String msg, String messageType, String input1, String input2, String input3, String input4, String input5){
        String messageFormat;
        switch (messageType) {
            case "standardMessage":
                messageFormat = standardMessage;
                break;
            case "helpMessage":
                messageFormat = helpMessage;
                break;
            case "helpHeaderMessage":
                messageFormat = helpHeaderMessage;
                break;
            case "unformattedMessage":
            default:
                messageFormat = "<message>";
                break;
        }

        // Loop to resolve placeholders. Loops in case of nested placeholders.
        for (int i = 0; i < 3; i++) {
            // Add predefined placeholders
            messageFormat = messageFormat.replace("<message>", msg);
            messageFormat = messageFormat.replace("<messageColour>", messageColour);
            messageFormat = messageFormat.replace("<messagePrefix>", messagePrefix);
            messageFormat = messageFormat.replace("<helpColour>", helpColour);
            messageFormat = messageFormat.replace("<commandColour>", commandColour);
            messageFormat = messageFormat.replace("<pluginName>", pluginName);
            // Add custom inputs to message
            messageFormat = messageFormat.replace("<input1>", input1);
            messageFormat = messageFormat.replace("<input2>", input2);
            messageFormat = messageFormat.replace("<input3>", input3);
            messageFormat = messageFormat.replace("<input4>", input4);
            messageFormat = messageFormat.replace("<input5>", input5);
            // Addeve mode support
            if (ddeve) {
                messageFormat = messageFormat.replaceAll("quest", "addeve");
                messageFormat = messageFormat.replaceAll("Quest", "Addeve");
                messageFormat = messageFormat.replaceAll("QUEST", "ADDEVE");
            }
        }

        Component parsedMessage = MiniMessage.miniMessage().deserialize(messageFormat);

        player.sendMessage(parsedMessage);
    }

    // CLASSIC MESSAGING, DO NOT USE ANYMORE
    // ------------- Send Formatted Message To Player ------------------

//    public static void sendOldMessage(Player player, String msg){
//        sendOldMessage(player, msg, true);
//    }
//    public static void sendOldMessage(Player player, String msg, Boolean b){
//        if (ddeve) {
//            msg = msg.replaceAll("quest", "addeve");
//            msg = msg.replaceAll("Quest", "Addeve");
//            if (b) {
//                sendOldMessage(player, msg, ChatColor.GOLD, "ADDEVE");
//            } else {
//                sendOldUnformattedMessage(player, msg);
//            }
//        } else {
//            if (b) {
//                sendOldMessage(player, msg, ChatColor.GOLD, "QUESTS");
//            } else {
//                sendOldUnformattedMessage(player, msg);
//            }
//        }
//    }
//
//    public static void sendOldMessage(Player player, String msg, ChatColor color, String plugin){
//        String bold = ChatColor.BOLD + "";
//        String darkGray = ChatColor.DARK_GRAY + "";
//        Messaging.sendOldUnformattedMessage(player, darkGray + "[ " + color + bold + plugin + darkGray + " ] " + ChatColor.WHITE + msg);
//    }

    public static void sendClassicFormattedMessage(Player player, String msg){
        player.sendMessage(msg);
    }

    public static void log(String msg, String level){
        log(msg, Quests.getInstance(), level, "QUESTS");
    }

    public static void log(String msg){
        log(msg, Quests.getInstance(), "INFO", "QUESTS");
    }

    public static void log(String msg, JavaPlugin jp, String level, String pluginName) {
        jp.getLogger().log(Level.parse(level), msg);
    }

    public static String time(Long l){
        int hours = (int) Math.floor(l / 1000 / 60 / 60);
        int minutes = (int) Math.floor((l - (hours * 1000 * 60 * 60)) / 1000 / 60);
        int seconds = (int) Math.floor((l - (minutes * 1000 * 60) - (hours * 1000 * 60 * 60)) / 1000);
        String msg = ((hours>0) ? hours + ((hours>1) ? " Hours, " : " Hour, " ) : "" )
                + ((minutes>0) ? minutes + ((minutes>1) ? " Minutes And " : " Minute And " ) : "" )
                + seconds + " Seconds";
        return msg;
    }

    public static String roughTime(Long l) {
        if (l == null || l < 0) {
            return "0 Seconds";
        }

        long totalSeconds = l / 1000L;
        long totalMinutes = totalSeconds / 60L;
        long totalHours   = totalMinutes / 60L;
        long days         = totalHours / 24L;

        if (days > 2) {
            return days + " Days";
        }
        if (totalHours > 2) {
            return totalHours + " Hours";
        }
        if (totalMinutes > 2) {
            return totalMinutes + " Minutes";
        }

        return totalSeconds + (totalSeconds == 1 ? " Second" : " Seconds");
    }
}
