package com.bosakon.monsterhunter;

/**
 * Represents a status effect (buff/debuff) applied to a Hunter or Monster.
 * Extensible for turn-based damage, stat modifiers, and custom logic.
 */
public class StatusEffect {
    private String name;
    private int duration;
    private int damagePerTurn;
    private double damageModifier;
    private double defenseModifier;

    public StatusEffect(String name, int duration, int damagePerTurn) {
        this(name, duration, damagePerTurn, 1.0, 1.0);
    }

    public StatusEffect(String name, int duration, int damagePerTurn, 
                       double damageModifier, double defenseModifier) {
        this.name = name;
        this.duration = duration;
        this.damagePerTurn = damagePerTurn;
        this.damageModifier = damageModifier;
        this.defenseModifier = defenseModifier;
    }

    /**
     * Applies the effect for one turn. Should be called at start/end of turn.
     * @return true if effect is still active after applying, false if expired
     */
    public boolean applyEffect() {
        if (duration > 0) {
            duration--;
            return true;
        }
        return false;
    }

    /** Reduces duration by 1 turn. */
    public void reduceDuration() {
        if (duration > 0) duration--;
    }

    /** @return true if effect is expired */
    public boolean isExpired() {
        return duration <= 0;
    }

    /** Returns a deep copy (for independent effect stacking). */
    public StatusEffect copy() {
        return new StatusEffect(name, duration, damagePerTurn, damageModifier, defenseModifier);
    }

    // --- Getters ---
    public String getName() { return name; }
    public int getDuration() { return duration; }
    public int getDamagePerTurn() { return damagePerTurn; }
    public double getDamageModifier() { return damageModifier; }
    public double getDefenseModifier() { return defenseModifier; }

    @Override
    public String toString() {
        return name + " (" + duration + " turns)";
    }
}