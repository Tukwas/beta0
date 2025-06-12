package com.bosakon.monsterhunter;

/**
 * Represents hunter abilities (skills).
 * 
 * Easily extensible for active/passive skills, mana costs, and custom effects.
 * For more advanced effects, consider integrating with a SkillEffect interface or class.
 */
public class Skill {
    private String name;
    private boolean isActive; // true = active skill, false = passive
    private int manaCost;
    private String effect;    // Description or effect identifier

    public Skill(String name, boolean isActive, int manaCost, String effect) {
        this.name = name;
        this.isActive = isActive;
        this.manaCost = manaCost;
        this.effect = effect;
    }

    // --- Getters ---
    public String getName() { return name; }
    public boolean isActive() { return isActive; }
    public int getManaCost() { return manaCost; }
    public String getEffect() { return effect; }
}
