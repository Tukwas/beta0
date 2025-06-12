package com.bosakon.monsterhunter;

/**
 * Represents quests with objectives and rewards.
 * Extensible for more quest types, multi-stage objectives, and narrative links.
 */
public class Quest {
    private String title;
    private String objective;
    private int rewardXP;
    private int rewardGold;
    private String unlockSkill;
    private boolean completed;

    // Optionally, add quest type (main, side, repeatable), prerequisites, or quest giver for extensibility

    public Quest(String title, String objective, int rewardXP, int rewardGold, String unlockSkill) {
        this.title = title;
        this.objective = objective;
        this.rewardXP = rewardXP;
        this.rewardGold = rewardGold;
        this.unlockSkill = unlockSkill;
        this.completed = false;
    }

    /**
     * Completes quest and grants rewards.
     * @param player Hunter to receive rewards
     */
    public void complete(Hunter player) {
        if (completed) {
            System.out.println("> Quest '" + title + "' is already completed!");
            return;
        }
        completed = true;
        System.out.println("\n> QUEST COMPLETE: " + title);
        System.out.println("> Rewards: " + rewardXP + " XP, " + rewardGold + " Gold");
        player.addExperience(rewardXP);
        player.addGold(rewardGold);

        if (unlockSkill != null && !unlockSkill.isEmpty()) {
            System.out.println("> Unlocked Skill: " + unlockSkill);
            player.unlockSkill(unlockSkill);
        }
    }

    // --- Getters ---
    public String getTitle() { return title; }
    public String getObjective() { return objective; }
    public int getRewardXP() { return rewardXP; }
    public int getRewardGold() { return rewardGold; }
    public String getUnlockSkill() { return unlockSkill; }
    public boolean isCompleted() { return completed; }
}