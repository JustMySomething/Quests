package com.jagsnet.minecraft.plugins.quests.otherStuff.messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;

public class Strings {
    public static String colourIt(String s) {
        while (s.contains("&0")) s = s.replace("&0", ChatColor.BLACK + "");
        while (s.contains("&1")) s = s.replace("&1", ChatColor.DARK_BLUE + "");
        while (s.contains("&2")) s = s.replace("&2", ChatColor.DARK_GREEN + "");
        while (s.contains("&3")) s = s.replace("&3", ChatColor.DARK_AQUA + "");
        while (s.contains("&4")) s = s.replace("&4", ChatColor.DARK_RED + "");
        while (s.contains("&5")) s = s.replace("&5", ChatColor.DARK_PURPLE + "");
        while (s.contains("&6")) s = s.replace("&6", ChatColor.GOLD + "");
        while (s.contains("&7")) s = s.replace("&7", ChatColor.GRAY + "");
        while (s.contains("&8")) s = s.replace("&8", ChatColor.DARK_GRAY + "");
        while (s.contains("&9")) s = s.replace("&9", ChatColor.BLUE + "");
        while (s.contains("&a")) s = s.replace("&a", ChatColor.GREEN + "");
        while (s.contains("&b")) s = s.replace("&b", ChatColor.AQUA + "");
        while (s.contains("&c")) s = s.replace("&c", ChatColor.RED + "");
        while (s.contains("&d")) s = s.replace("&d", ChatColor.LIGHT_PURPLE + "");
        while (s.contains("&e")) s = s.replace("&e", ChatColor.YELLOW + "");
        while (s.contains("&f")) s = s.replace("&f", ChatColor.WHITE + "");
        while (s.contains("&k")) s = s.replace("&k", ChatColor.MAGIC + "");
        while (s.contains("&l")) s = s.replace("&l", ChatColor.BOLD + "");
        while (s.contains("&m")) s = s.replace("&m", ChatColor.STRIKETHROUGH + "");
        while (s.contains("&n")) s = s.replace("&n", ChatColor.UNDERLINE + "");
        return s;
    }

    public static String cleanLore(String s, boolean fixMobs, boolean fixBlocks){
        s = ChatColor.stripColor(s);
        s = s.toLowerCase();
        if (fixMobs) {
            s = s.replace("wolves", "wolf");
            s = s.replace("endermen", "enderman");
        }
        if (fixBlocks) {
            s = s.replace("shelves", "shelf");
        }
        return s;
    }

    public static String getEntityType(Entity entity) {
        String entityString = entity.getType().toString().replace("_", " ").toLowerCase();

        String entityType = entity.getType().name();
        switch (entityType) {
            case "SHEEP":
                Sheep sheep = (Sheep) entity;
                entityString = sheep.getColor().name().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "COPPER_GOLEM":
                CopperGolem copperGolem = (CopperGolem) entity;
                entityString = copperGolem.getWeatheringState().name().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "PIG":
                Pig pig = (Pig) entity;
                entityString = pig.getVariant().toString().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "CAT":
                Cat cat = (Cat) entity;
                entityString = cat.getCatType().toString().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "FROG":
                Frog frog = (Frog) entity;
                entityString = frog.getVariant().toString().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "WOLF":
                Wolf wolf = (Wolf) entity;
                entityString = wolf.getVariant().toString().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "HORSE":
                Horse horse = (Horse) entity;
                entityString = horse.getStyle().toString().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "CHICKEN":
                Chicken chicken = (Chicken) entity;
                entityString = chicken.getVariant().toString().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "AXOLOTL":
                Axolotl axolotl = (Axolotl) entity;
                entityString = axolotl.getVariant().toString().replace("_", " ").toLowerCase() + " " + entityString;
                break;
            case "SLIME":
            case "MAGMA_CUBE":
                Slime slime = (Slime) entity;
                int size = slime.getSize();
                switch (size) {
                    case 1:
                        entityString = "small " + entityString;
                        break;
                    case 2:
                        entityString = "medium " + entityString;
                        break;
                    case 4:
                        entityString = "large " + entityString;
                        break;
                }
                break;
        }

        return entityString;
    }

    public static String cleanEntity(String s) {
        return " " + s.replace("_", " ").toLowerCase();
    }

    public static String questVersion(){
        return "1.11";
    }
}
