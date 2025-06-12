package com.bosakon.monsterhunter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manages skill unlocks and dependencies.
 * Easily extensible: add more skills, chain prerequisites, or implement skill tiers.
 */
public class SkillTree {
    private Map<String, Skill> allSkills;
    private Map<String, List<String>> prerequisites;

    public SkillTree() {
        allSkills = new HashMap<>();
        prerequisites = new HashMap<>();
        initializeSkills();
    }

    /** Populates skill tree with available skills and their dependencies. */
    private void initializeSkills() {
        // Active skills
        addSkill("Stealth", true, 15, "Become invisible for 3 turns", null);
        addSkill("Intimidation", true, 10, "Reduce enemy attack by 30%", null);
        addSkill("Healing", true, 20, "Restore 30 HP", null);
        addSkill("Flux", true, 25, "Boost ATK/DEF by 40% for 4 turns", null);
        addSkill("Summon", true, 40, "Summon a monster ally", "Flux");
        
        // Passive skills
        addSkill("Will to Recover", false, 0, "Heal 5 HP per turn", null);
        addSkill("Tenacity", false, 0, "Reduce damage by 50% when HP < 30%", null);
        addSkill("Dungeon Sense", false, 0, "20% chance to detect hidden paths", "Will to Recover");
        addSkill("Fast Learner", false, 0, "Gain 20% more XP", null);
    }

    /**
     * Adds a skill to the tree.
     * @param name        Skill name
     * @param isActive    Whether the skill is active (true) or passive (false)
     * @param manaCost    MP cost (0 for passive)
     * @param effect      Description of skill effect
     * @param prerequisite Prerequisite skill name (null if none)
     */
    private void addSkill(String name, boolean isActive, int manaCost, String effect, String prerequisite) {
        allSkills.put(name, new Skill(name, isActive, manaCost, effect));
        if (prerequisite != null) {
            prerequisites.computeIfAbsent(name, k -> new LinkedList<>()).add(prerequisite);
        }
    }

    /**
     * Checks if skill can be unlocked by player (all prerequisites met).
     */
    public boolean canUnlock(String skillName, Hunter player) {
        if (!allSkills.containsKey(skillName)) return false;
        List<String> required = prerequisites.get(skillName);
        if (required == null) return true;
        for (String req : required) {
            if (!player.hasSkill(req)) return false;
        }
        return true;
    }

    /** Gets skill by name. Returns null if not found. */
    public Skill getSkill(String name) {
        return allSkills.get(name);
    }

    /**
     * Checks if a skill name is valid.
     * @param skillName Name to validate
     * @return true if skill exists in the tree
     */
    public boolean isValidSkill(String skillName) {
        return allSkills.containsKey(skillName);
    }

    /**
     * Displays all skills available for unlock by the player.
     */
    public void displayAvailableSkills(Hunter player) {
        System.out.println("\n--- AVAILABLE SKILLS ---");
        for (String skill : allSkills.keySet()) {
            if (!player.hasSkill(skill) && canUnlock(skill, player)) {
                Skill s = allSkills.get(skill);
                System.out.println("- " + s.getName() + ": " + s.getEffect() + 
                                 (s.isActive() ? " (" + s.getManaCost() + " MP)" : ""));
            }
        }
    }

    /**
     * Optionally, displays the full skill tree with unlock status.
     */
    public void displayFullTree(Hunter player) {
        System.out.println("\n--- SKILL TREE ---");
        for (String skill : allSkills.keySet()) {
            Skill s = allSkills.get(skill);
            String status = player.hasSkill(skill) ? "[UNLOCKED]" :
                            canUnlock(skill, player) ? "[AVAILABLE]" : "[LOCKED]";
            String req = prerequisites.get(skill) != null ? " (Req: " + String.join(", ", prerequisites.get(skill)) + ")" : "";
            System.out.println("- " + s.getName() + ": " + s.getEffect() + req + " " + status);
        }
    }
}
