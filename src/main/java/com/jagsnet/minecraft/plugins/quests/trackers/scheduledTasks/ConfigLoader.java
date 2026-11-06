package com.jagsnet.minecraft.plugins.quests.trackers.scheduledTasks;

import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Configs;

public class ConfigLoader {
    public static void reloadConfigs() {
        Configs.loadQuestConfigNames();
        Configs.loadQuestNames();
        Configs.loadLists();
        Configs.loadGlobal();
    }
}
