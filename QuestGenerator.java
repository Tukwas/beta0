package com.bosakon.monsterhunter;

import java.util.Random;

public class QuestGenerator {
    static String[] TITLES = {"Goblin Menace", "Spider Plague", "Dungeon Clear", "Orc Hunt"};
    static String[] OBJECTIVES = {"Defeat %d %s", "Clear %d dungeons", "Collect %d monster parts"};
    static String[] MONSTER_TYPES = {"Goblin", "Spider", "Orc", "Snake"};

    public static Quest generateRandomQuest(int playerLevel) {
        Random rand = new Random();
        String title = TITLES[rand.nextInt(TITLES.length)];
        String objectivePattern = OBJECTIVES[rand.nextInt(OBJECTIVES.length)];
        int count = rand.nextInt(3) + 2;
        String monster = MONSTER_TYPES[rand.nextInt(MONSTER_TYPES.length)];
        String objective = objectivePattern.replace("%d", ""+count).replace("%s", monster);
        int xp = 10 * count + playerLevel * 2;
        int gold = 5 * count;
        String unlockSkill = (rand.nextInt(100) < 20) ? "Tenacity" : null;
        return new Quest(title, objective, xp, gold, unlockSkill);
    }
}


