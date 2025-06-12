package com.bosakon.monsterhunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;



/**
 * Represents enemies in dungeons, with support for abilities and status effects.
 * 
 * Features:
 * - Bosses can have multiple abilities and stronger stats.
 * - Each monster can inflict status effects (e.g., Burn, Poison) on the Hunter.
 * - Cooldowns for abilities, and active effects on monster (affecting its attack).
 * - Easily extensible: Add new abilities/statuses by editing ABILITIES array.
 */
public class Monster {
    private String name;
    private int level;
    private int maxHP;
    private int hp;
    private int baseDamage;
    private boolean isBoss;
    private String[] abilities;
    private int[] cooldowns;
    private StatusEffect[] abilityEffects;
    private List<StatusEffect> activeEffects;

    private static final String[] PREFIXES = {"Shadow", "Frost", "Vengeful", "Crimson", "Rotting"};
    private static final String[] SUFFIXES = {"Goblin", "Orc", "Spider", "Snake", "Wraith"};
    // {AbilityName, StatusName, duration, damage, dmgModifier, defModifier}
    private static final String[][] ABILITIES = {
        {"Fire Breath", "Burning", "3", "10", "1.2", "0"},
        {"Poison Sting", "Tetanus", "2", "7", "1.0", "0"},
        {"Frost Aura", "Freeze", "2", "0", "0.8", "1.2"},
        {"Life Drain", "Leech", "3", "5", "1.0", "0"},
        {"Enrage", "Enrage", "4", "0", "2.0", "0"},
        {"Venom Cloud", "Venom", "3", "6", "1.0", "0"}
    };

    public Monster(int playerRank, boolean boss) {
        Random rand = new Random();
        this.name = PREFIXES[rand.nextInt(PREFIXES.length)] + " " + SUFFIXES[rand.nextInt(SUFFIXES.length)];
        this.level = playerRank + rand.nextInt(3) + (boss ? 2 : 0);
        this.maxHP = 20 + level * 10 + (boss ? 30 : 0);
        this.hp = maxHP;
        this.baseDamage = 5 + level * 2 + (boss ? 8 : 0);
        this.isBoss = boss;

        int nAbilities = boss ? 3 : 1 + rand.nextInt(2);
        abilities = new String[nAbilities];
        cooldowns = new int[nAbilities];
        abilityEffects = new StatusEffect[nAbilities];
        Set<Integer> used = new HashSet<>();
        for (int i = 0; i < nAbilities; i++) {
            int ab;
            do { ab = rand.nextInt(ABILITIES.length); } while (used.contains(ab));
            used.add(ab);
            abilities[i] = ABILITIES[ab][0];
            cooldowns[i] = 0;
            abilityEffects[i] = new StatusEffect(
                ABILITIES[ab][1],
                Integer.parseInt(ABILITIES[ab][2]),
                Integer.parseInt(ABILITIES[ab][3]),
                Double.parseDouble(ABILITIES[ab][4]),
                Double.parseDouble(ABILITIES[ab][5])
            );
        }
        activeEffects = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public int getHP() { return hp; }
    public int getLevel() { return level; }
    public boolean isBoss() { return isBoss; }
    public int getMaxHP() { return maxHP; }

    /** Uses first available ability, or attacks */
    public void useAbility(Hunter hunter) {
        for (int i = 0; i < abilities.length; i++) {
            if (cooldowns[i] == 0) {
                StatusEffect se = abilityEffects[i].copy();
                hunter.applyStatusEffect(se);
                System.out.println(name + " uses " + abilities[i] + "! Applied " + se);
                cooldowns[i] = 2 + new Random().nextInt(2);
                return;
            }
        }
        attack(hunter);
    }


    /** Reduces all ability cooldowns by 1 (minimum 0) */
    public void reduceCooldowns() {
        for (int i = 0; i < cooldowns.length; i++)
            if (cooldowns[i] > 0) cooldowns[i]--;
    }

    /** Apply a status effect to the monster itself */
    public void applyStatusEffect(StatusEffect se) {
        activeEffects.add(se);
    }

    /** Apply all end-of-turn effects, and remove expired ones */
    public void applyEndOfTurnEffects() {
        Iterator<StatusEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            StatusEffect se = it.next();
            se.applyEffect(null); // Placeholder: add monster effect logic as needed
            se.reduceDuration();
            if (se.isExpired()) it.remove();
        }
    }

    public void takeDamage(int dmg) {
        this.hp -= Math.max(0, dmg);
        if (hp < 0) hp = 0;
    }

    /** Monster basic attack: applies status effect damage modifiers */
    public void attack(Hunter hunter) {
        int dmg = baseDamage;
        for (StatusEffect se : activeEffects)
            dmg *= se.getDamageModifier();
        System.out.println(name + " attacks for " + dmg + "!");
        hunter.reduceHP(dmg);
    }

    public boolean isDead() { return hp <= 0; }

    public String toString() {
        return name + " | Lvl: " + level + " | HP: " + hp + "/" + maxHP + " | Abilities: " + Arrays.toString(abilities);
    }
}