package com.bosakon.monsterhunter;

import java.util.*;

/**
 * Represents the player character with stats, inventory, progression, skills, and quest log.
 */
public class Hunter {
    // Core identity and progression
    private String name;
    private String rank;
    private int level;
    private int experience;
    private int gold;
    private int fame;
    private boolean isAwakened;

    // Stats
    private int strength;
    private int dexterity;
    private int intelligence;
    private int currentHP, maxHP;
    private int currentMana, maxMana;

    // Inventory and progression systems
    private Map<String, Integer> inventory; // e.g. "Health Potion" -> 2
    private Item equippedWeapon;
    private Map<String, Skill> skills;
    private StatusEffect currentStatus;
    private List<Quest> activeQuests;
    public SkillTree skillTree;

    // Rank thresholds and possible ranks
    private static final int[] RANK_THRESHOLDS = {100, 300, 700, 1500, 3000};
    private static final String[] RANKS = {"E", "D", "C", "B", "A", "S"};

    /**
     * Constructs a new Hunter with a name and starting weapon.
     */
    public Hunter(String name, Item startingWeapon) {
        this.name = name;
        this.rank = "E";
        this.level = 1;
        this.experience = 0;
        this.gold = 100;
        this.fame = 0;
        this.isAwakened = false;

        this.strength = 5;
        this.dexterity = 5;
        this.intelligence = 5;
        this.maxHP = 100;
        this.currentHP = maxHP;
        this.maxMana = 10;
        this.currentMana = maxMana;

        this.inventory = new HashMap<>();
        this.equippedWeapon = startingWeapon;
        this.skills = new HashMap<>();
        this.activeQuests = new LinkedList<>();
        this.skillTree = new SkillTree();
    }

    /** Triggers awakening after double dungeon death; unlocks special skills. */
    public void awaken() {
        if (isAwakened) {
            System.out.println("> SYSTEM: Awakening already achieved.");
            return;
        }
        isAwakened = true;
        System.out.println("\n===============================================");
        System.out.println("      SYSTEM INTERVENTION: AWAKENING EVENT     ");
        System.out.println("===============================================");
        System.out.println("A surge of energy floods your body...");
        System.out.println("You have been revived and AWAKENED by the System!");
        unlockSkill("Will to Recover");
        unlockSkill("Tenacity");
        System.out.println("> Advanced skills are now available!");
        System.out.println("===============================================");
    }

    /** Unlocks a new skill if requirements are met. */
    public void unlockSkill(String skillName) {
        if (skills.containsKey(skillName)) {
            System.out.println("> Skill already unlocked: " + skillName);
            return;
        }
        if (skillTree.canUnlock(skillName, this)) {
            Skill skill = skillTree.getSkill(skillName);
            skills.put(skillName, skill);
            System.out.println("> Learned new skill: " + skillName);
        } else {
            System.out.println("> Requirements not met for " + skillName);
        }
    }

    /** Adds experience and checks for level up and rank up. */
    public void addExperience(int exp) {
        experience += exp;
        System.out.println("> Gained " + exp + " XP");
        if (experience >= level * 100) {
            levelUp();
        }
        checkRankUp();
    }

    /** Checks if player is eligible for a rank up based on experience. */
    private void checkRankUp() {
        for (int i = 0; i < RANK_THRESHOLDS.length; i++) {
            if (experience >= RANK_THRESHOLDS[i] && rank.equals(RANKS[i])) {
                rank = RANKS[i+1];
                System.out.println("\n> Rank up! You're now " + rank + "-Rank Hunter!");
                break;
            }
        }
    }

    /** Increases level and stats, restores health and mana. */
    private void levelUp() {
        level++;
        experience -= (level - 1) * 100; // Carry over extra XP
        strength += 2;
        dexterity += 2;
        intelligence += 2;
        maxHP += 10;
        maxMana += 5;
        currentHP = maxHP;
        currentMana = maxMana;
        System.out.println("\n> Level up! You're now level " + level);
    }

    /** Spends gold if possible, returns true if successful. */
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        System.out.println("> Not enough gold!");
        return false;
    }

    /** Restores mana up to maxMana. */
    public void restoreMana(int amount) {
        if (amount < 0) amount = 0;
        currentMana = Math.min(maxMana, currentMana + amount);
    }

    /** Adds a stackable item to inventory. */
    public void addItem(String item) {
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
    }

    /** Uses an item from inventory (e.g. potion, repair kit). */
    public boolean useItem(String item) {
        if (inventory.containsKey(item) && inventory.get(item) > 0) {
            inventory.put(item, inventory.get(item) - 1);

            switch(item) {
                case "Health Potion":
                    heal(25);
                    System.out.println("> Healed 25 HP!");
                    return true;
                case "Mana Potion":
                    restoreMana(20);
                    System.out.println("> Restored 20 Mana!");
                    return true;
                case "Weapon Repair Kit":
                    if (equippedWeapon != null) {
                        equippedWeapon.repair();
                        System.out.println("> Weapon repaired!");
                        return true;
                    } else {
                        System.out.println("> No weapon equipped to repair!");
                        return false;
                    }
            }
        }
        return false;
    }

    /** Checks if a skill can be unlocked based on requirements. */
    public boolean canUnlock(String skillName) {
        return skillTree.canUnlock(skillName, this);
    }

    /** Applies damage to hunter, factoring in skills like Tenacity. */
    public void takeDamage(int damage) {
        if (damage < 0) damage = 0; // Prevent negative damage
        // Apply damage reduction from Tenacity (if HP < 30%)
        if (currentHP < maxHP * 0.3 && skills.containsKey("Tenacity")) {
            damage *= 0.5;
        }
        currentHP -= damage;
        if (currentHP < 0) currentHP = 0;
    }

    /** Reduces HP directly (for monster attacks). */
    public void reduceHP(int amount) {
        currentHP -= amount;
        if (currentHP < 0) currentHP = 0;
    }

    /** Heals hunter, up to max HP. */
    public void heal(int amount) {
        if (amount < 0) amount = 0; // Prevent negative healing
        currentHP = Math.min(maxHP, currentHP + amount);
    }

    /** Returns true if hunter is alive. */
    public boolean isAlive() {
        return currentHP > 0;
    }

    /** Calculates attack damage, applying crit and weapon effects. */
    public int calculateDamage() {
        int baseDamage = equippedWeapon.getBaseDamage();
        double critChance = equippedWeapon.getCritChance();

        // Apply weapon special effects (add more cases here)
        Random rand = new Random();
        switch(equippedWeapon.getName()) {
            case "Tirungan":
                if (rand.nextDouble() < 0.25) {
                    currentStatus = new StatusEffect("Tetanus", 3, 3);
                }
                break;
            case "Sanga sa Kamunggay":
                if (rand.nextDouble() < 0.2) {
                    heal(5);
                }
                break;
        }
        // Critical hit calculation
        if (rand.nextDouble() < critChance) {
            return baseDamage * 2;
        }
        return baseDamage;
    }

    /** Applies end-of-turn effects (passive healing, status damage, etc.) */
    public void applyEndOfTurnEffects() {
        // Passive healing from Will to Recover
        if (skills.containsKey("Will to Recover")) {
            heal(5);
        }
        // Status effects (e.g. poison)
        if (currentStatus != null) {
            if (currentStatus.applyEffect()) {
                takeDamage(currentStatus.getDamagePerTurn());
                System.out.println("> " + currentStatus.getName() + " deals " +
                                  currentStatus.getDamagePerTurn() + " damage!");
            } else {
                currentStatus = null;
            }
        }
    }

    /** Allows monsters to inflict status effects on the hunter. */
    public void applyStatusEffect(StatusEffect se) {
        this.currentStatus = se;
        System.out.println("> Status effect applied: " + se.getName() + " (" + se.getDuration() + " turns)");
    }

    /** Displays all hunter stats, equipped weapon, and skills. */
    public void displayStats() {
        System.out.println("\n--- HUNTER STATS ---");
        System.out.println(name + " | Rank: " + rank + " | Level: " + level);
        System.out.println("HP: " + currentHP + "/" + maxHP + " | Mana: " + currentMana + "/" + maxMana);
        System.out.println("Gold: " + gold + " | Fame: " + fame);
        System.out.println("Weapon: " + equippedWeapon.getName() +
                          " (DMG: " + equippedWeapon.getBaseDamage() +
                          ", DUR: " + equippedWeapon.getDurability() + "/" +
                          equippedWeapon.getMaxDurability() + ")");
        if (!skills.isEmpty()) {
            System.out.println("\nSkills:");
            for (Skill skill : skills.values()) {
                System.out.println("- " + skill.getName() + ": " + skill.getEffect());
            }
        }
    }

    /** Displays all inventory contents. */
    public void displayInventory() {
        System.out.println("\n--- INVENTORY ---");
        System.out.println("Gold: " + gold);
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty");
        } else {
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    /** Displays all active quests and completion status. */
    public void displayQuests() {
        if (activeQuests.isEmpty()) {
            System.out.println("No active quests");
            return;
        }
        System.out.println("\n--- ACTIVE QUESTS ---");
        int index = 1;
        for (Quest quest : activeQuests) {
            System.out.println(index++ + ". " + quest.getTitle() +
                              ": " + quest.getObjective() +
                              (quest.isCompleted() ? " (Completed)" : ""));
        }
    }

    /** Adds a new quest to the active quest log. */
    public void addQuest(Quest quest) {
        activeQuests.add(quest);
        System.out.println("> New Quest: " + quest.getTitle());
    }

    /** Checks quest completion conditions (e.g. after dungeon clear). */
    public void checkQuestCompletion(String dungeonName, String rank) {
        for (Quest quest : activeQuests) {
            if (!quest.isCompleted()) {
                if (quest.getObjective().contains(dungeonName) ||
                    quest.getObjective().contains(rank + "-Rank")) {
                    quest.complete(this);
                }
            }
        }
    }

    /** Displays all available skills for training. */
    public void displayAvailableSkills() {
        skillTree.displayAvailableSkills(this);
    }

    // === Getters and Setters ===
    public int getHP() { return currentHP; }
    public int getMaxHP() { return maxHP; }
    public int getMana() { return currentMana; }
    public int getMaxMana() { return maxMana; }
    public int getGold() { return gold; }
    public int getFame() { return fame; }
    public String getRank() { return rank; }
    public String getName() { return name; }
    public Item getWeapon() { return equippedWeapon; }
    public boolean isAwakened() {
        return isAwakened;
    }
    public Map<String, Skill> getSkills() {
        return skills;
    }
    public boolean hasSkill(String skillName) { return skills.containsKey(skillName); }
    public void addGold(int amount) { gold += amount; }
    public void addFame(int amount) { fame += amount; }
    public void setWeapon(Item weapon) { equippedWeapon = weapon; }
    public void setMana(int amount) { currentMana = Math.max(0, Math.min(amount, maxMana)); }
}