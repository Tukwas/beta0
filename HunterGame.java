package com.bosakon.monsterhunter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;



/**
 * Main game controller with game loop and systems.
 * 
 * === Extension/Customization Guide ===
 * - Add new weapons: Add new Item fields (see THUNDER_BLADE example).
 * - Add new locations/NPCs: Add to locations and npcs maps in initializeWorld().
 * - Add new skills/items/quests: Update SkillTree, merchant inventories, and quest lists.
 * - Adjust monster generation: Integrate with Monster class as needed.
 */
public class HunterGame {

    private Hunter player;
    private final Scanner scanner;
    private boolean inGame;
    private boolean atHome;

    // --- Starting weapons (edit/add here) ---
    private static final Item LAPIS = new Item("Lapis", 8, 0.2, "20% critical hit chance", 50);
    private static final Item TIRUNGAN = new Item("Tirungan", 6, 0.1, "25% chance to inflict Tetanus", 40);
    private static final Item FLAT_SCREW = new Item("Flat Screw", 5, 0.6, "Duslak ability (60% crit)", 35);
    private static final Item SANGGA = new Item("Sanga sa Kamunggay", 7, 0.15, "20% chance to heal on hit", 45);

    // --- Special weapons (boss drops) ---
    private static final Item DOS_PURDOS = new Item("Dos Purdos", 12, 0.3, "High critical hit chance", 60);
    // Example: Add a new weapon
    // private static final Item THUNDER_BLADE = new Item("Thunder Blade", 15, 0.5, "Stuns on crit", 55);

    // --- Locations and NPCs ---
    private Map<String, Location> locations;
    private Map<String, NPC> npcs;
    private Stack<Dungeon> dungeonStack = new Stack<>();

    public HunterGame() {
        scanner = new Scanner(System.in);
        inGame = true;
        atHome = false;
    }

    public static void main(String[] args) {
        HunterGame game = new HunterGame();
        game.startGame();
    }

    private int getIntInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    /**
     * Starts the game and initializes player.
     */
    public void startGame() {
        System.out.println("=====================================");
        System.out.println("     WELCOME TO THE HUNTER GAME      ");
        System.out.println("=====================================");
        System.out.print("Enter your Hunter's name: ");
        String name = scanner.nextLine();

        System.out.print("\nChoose your starting weapon (Lapis, Tirungan, Flat Screw): ");
        String weaponChoice = scanner.nextLine().toLowerCase();

        Item weapon;
        if (weaponChoice.contains("lapis")) {
            weapon = LAPIS;
        } else if (weaponChoice.contains("tirungan")) {
            weapon = TIRUNGAN;
        } else if (weaponChoice.contains("flat") || weaponChoice.contains("screw")) {
            weapon = FLAT_SCREW;
        } else {
            System.out.println("Invalid choice, defaulting to Lapis");
            weapon = LAPIS;
        }

        player = new Hunter(name, weapon);
        System.out.println("\nWelcome Hunter " + name + "!");

        initializeWorld();
        mainMenu();
    }

    /**
     * Initializes locations, NPCs, and initial quests.
     * Extend here to add new content.
     */
    private void initializeWorld() {
        // --- Create locations (add more here) ---
        locations = new HashMap<>();
        locations.put("market", new Location("Market", "A bustling marketplace filled with merchants"));
        locations.put("gym", new Location("Gym", "Training facility to improve your skills"));
        locations.put("center", new Location("Hunter Center",
                "Hub for hunter analysis and System information\n"
                + " - 2D Echo: Visual heart health scan\n"
                + " - Mana Crystal: Rank assessment technology\n"
                + " - System Terminal: Access game information"));
        locations.put("home", new Location("Home", "Your safe haven to rest and recover"));
        // Example: locations.put("library", new Location("Library", "A quiet place of ancient secrets."));

        // --- Create NPCs (add more here) ---
        npcs = new HashMap<>();
        npcs.put("market_merchant", new NPC("merchant", "Old Man Jenkins"));
        npcs.put("center_clerk", new NPC("friend", "Clerk Sarah"));
        npcs.put("reporter", new NPC("reporter", "Lois Lane"));
        npcs.put("quest_giver", new NPC("quest_giver", "Master Hunter Rodriguez"));
        // Example: npcs.put("sage", new NPC("sage", "Old Sage Merlin"));

        // --- Initial quests (add more as desired) ---
        player.addQuest(new Quest("First Hunt", "Clear any E-Rank dungeon", 100, 50, "Will to Recover"));
        player.addQuest(new Quest("Red Gate Challenge", "Clear a C-Rank dungeon", 300, 150, "Tenacity"));
    }

    /**
     * Main menu loop.
     */
    private void mainMenu() {
        while (inGame && player.isAlive()) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Enter Dungeon");
            System.out.println("2. Check Stats");
            System.out.println("3. Check Inventory");
            System.out.println("4. Go Home");
            System.out.println("0. Exit Game");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: enterDungeon(); break;
                case 2: player.displayStats(); break;
                case 3: player.displayInventory(); break;
                case 4: goHome(); break;
                case 0: inGame = false; break;
                default: System.out.println("Invalid choice");
            }
        }

        if (!player.isAlive()) {
            System.out.println("\nGAME OVER - You have been defeated");
        }

        System.out.println("Thanks for playing!");
    }

    /**
     * Home location menu.
     */
    private void goHome() {
        atHome = true;
        while (atHome && inGame) {
            locations.get("home").enter();

            System.out.println("1. Save Game");
            System.out.println("2. Load Game");
            System.out.println("3. Sleep (Recover Health)");
            System.out.println("4. Go Outside");
            System.out.println("5. Enter Dungeon");
            System.out.println("6. Train Skills");
            System.out.println("7. View Quests");
            System.out.println("0. Exit Game");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: saveGame(); break;
                case 2: loadGame(); break;
                case 3: player.heal(player.getMaxHP()); System.out.println("You sleep and recover all health!"); break;
                case 4: goOutside(); break;
                case 5: enterDungeon(); break;
                case 6: trainSkills(); break;
                case 7: player.displayQuests(); break;
                case 0: inGame = false; atHome = false; break;
                default: System.out.println("Invalid choice");
            }
        }
    }

    /**
     * Saves game state (expand for more detail if needed).
     */
    private void saveGame() {
        try (FileWriter writer = new FileWriter("hunter_save.txt")) {
            writer.write(player.getName() + "\n");
            writer.write(player.getRank() + "\n");
            writer.write(player.getHP() + "\n");
            writer.write(player.getGold() + "\n");
            writer.write(player.getFame() + "\n");
            writer.write(player.getWeapon().getName() + "\n");
            System.out.println("> Game saved successfully!");
        } catch (IOException e) {
            System.out.println("> ERROR: Failed to save game - " + e.getMessage());
        }
    }

    /**
     * Loads game state (expand for more detail if needed).
     */
    private void loadGame() {
        try (BufferedReader reader = new BufferedReader(new FileReader("hunter_save.txt"))) {
            String name = reader.readLine();
            String rank = reader.readLine();
            int hp = Integer.parseInt(reader.readLine());
            int gold = Integer.parseInt(reader.readLine());
            int fame = Integer.parseInt(reader.readLine());
            String weaponName = reader.readLine();

            Item weapon;
            switch (weaponName) {
                case "Lapis": weapon = LAPIS; break;
                case "Tirungan": weapon = TIRUNGAN; break;
                case "Flat Screw": weapon = FLAT_SCREW; break;
                case "Sanga sa Kamunggay": weapon = SANGGA; break;
                case "Dos Purdos": weapon = DOS_PURDOS; break;
                default: weapon = LAPIS;
            }

            player = new Hunter(name, weapon);
            player.addGold(gold);
            player.addFame(fame);
            // Note: This simplified load doesn't restore all state
            System.out.println("> Game loaded successfully!");
        } catch (IOException e) {
            System.out.println("> ERROR: Failed to load game - " + e.getMessage());
        }
    }

    /**
     * Skill training menu.
     */
    private void trainSkills() {
        player.displayAvailableSkills();
        System.out.print("\nEnter skill name to train (or 'back'): ");
        String skillName = scanner.nextLine();

        if ("back".equalsIgnoreCase(skillName)) return;

        // Validate skill exists
        if (!player.skillTree.isValidSkill(skillName)) {
            System.out.println("> Invalid skill name! Available skills:");
            player.displayAvailableSkills();
            return;
        }

        // Validate funds
        if (player.getGold() < 100) {
            System.out.println("> Not enough gold! (Cost: 100 gold)");
            return;
        }

        // Attempt unlock
        if (player.canUnlock(skillName)) {
            player.unlockSkill(skillName);
            player.addGold(-100);
        } else {
            System.out.println("> Requirements not met for " + skillName);
        }
    }

    /**
     * Outside location menu.
     */
    private void goOutside() {
        boolean outside = true;
        while (outside && inGame) {
            System.out.println("\nWhere would you like to go?");
            System.out.println("1. Visit Market");
            System.out.println("2. Go to Gym");
            System.out.println("3. Visit Center");
            System.out.println("0. Go Back Inside");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: locations.get("market").enter(); npcs.get("market_merchant").interact(player); break;
                case 2: locations.get("gym").enter(); System.out.println("You spend an hour training..."); player.addExperience(15); break;
                case 3: visitCenter(); break;
                case 0: outside = false; break;
                default: System.out.println("Invalid choice");
            }
        }
    }

    /**
     * Displays the 2D Heart Echo (ASCII heart visualization).
     */
    private void displayHeartEcho() {
        int heartSize = 10;
        int currentHP = player.getHP();
        int maxHP = player.getMaxHP();

        if (maxHP <= 0) maxHP = 1; // Prevent division by zero
        if (currentHP < 0) currentHP = 0;
        if (currentHP > maxHP) currentHP = maxHP;
        int healthPercent = (int) Math.round((double) currentHP / maxHP * 100);

        System.out.println("\n--- 2D HEART ECHO SCAN ---");
        System.out.println("Cardiovascular Status: " + healthPercent + "% functionality");
        System.out.println("Current HP: " + currentHP + "/" + maxHP);

        // ASCII heart visualization
        for (int y = -heartSize; y <= heartSize; y++) {
            for (int x = -2 * heartSize; x <= 2 * heartSize; x++) {
                double formula = Math.pow(x * 0.04, 2) + Math.pow(y * 0.1, 2) - Math.pow(heartSize * 0.1, 2);
                if (Math.abs(formula) < heartSize * 0.3) {
                    // Color based on health status
                    if (healthPercent > 70) {
                        System.out.print("\u001B[32m♥\u001B[0m");
                    } else if (healthPercent > 30) {
                        System.out.print("\u001B[33m♥\u001B[0m");
                    } else {
                        System.out.print("\u001B[31m♥\u001B[0m");
                    }
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

        // Health status interpretation
        System.out.println("\nHealth Analysis:");
        if (healthPercent > 90) {
            System.out.println("Cardiovascular system: Optimal");
        } else if (healthPercent > 70) {
            System.out.println("Cardiovascular system: Strong");
        } else if (healthPercent > 50) {
            System.out.println("Cardiovascular system: Stable");
        } else if (healthPercent > 30) {
            System.out.println("Cardiovascular system: Weakened");
        } else if (healthPercent > 10) {
            System.out.println("Cardiovascular system: Critical");
        } else {
            System.out.println("Cardiovascular system: Failure imminent");
        }
    }

    /**
     * Displays rank visualization (Mana Crystal).
     */
    private void displayManaCrystal() {
        String[] rankColors = {"\u001B[37m", "\u001B[34m", "\u001B[36m", "\u001B[32m", "\u001B[33m", "\u001B[31m"};
        String[] rankNames = {"E-Rank", "D-Rank", "C-Rank", "B-Rank", "A-Rank", "S-Rank"};
        int rankIndex = Arrays.asList("E", "D", "C", "B", "A", "S").indexOf(player.getRank());

        System.out.println("\n--- MANA CRYSTAL ANALYSIS ---");
        System.out.println("Current Rank: " + rankNames[rankIndex]);

        // ASCII crystal visualization
        String color = rankColors[rankIndex];
        System.out.println(color + "    /\\");
        System.out.println("   /  \\");
        System.out.println("  /    \\");
        System.out.println(" /      \\");
        System.out.println(" \\      /");
        System.out.println("  \\    /");
        System.out.println("   \\  /");
        System.out.println("    \\/\u001B[0m");

        // Rank progression
        System.out.println("\nRank Progression:");
        for (int i = 0; i <= 5; i++) {
            String indicator = (i <= rankIndex) ? "◉" : "○";
            System.out.println(rankColors[i] + indicator + " " + rankNames[i] + "\u001B[0m");
        }
    }

    /**
     * Enhanced Center menu with stat visualization and System info.
     */
    private void visitCenter() {
        boolean inCenter = true;
        while (inCenter && inGame) {
            locations.get("center").enter();

            System.out.println("\nCenter Services:");
            System.out.println("1. 2D Heart Echo Scan");
            System.out.println("2. Mana Crystal Rank Assessment");
            System.out.println("3. Access System Terminal");
            System.out.println("4. Speak with Clerk Sarah");
            System.out.println("5. Speak with Master Hunter");
            System.out.println("0. Exit Center");
            System.out.print("Select service: ");

            int choice = getIntInput();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1: displayHeartEcho(); break;
                case 2: displayManaCrystal(); break;
                case 3: displaySystemInfo(); break;
                case 4: npcs.get("center_clerk").interact(player); break;
                case 5: npcs.get("quest_giver").interact(player); break;
                case 0: inCenter = false; break;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * Displays system information.
     */
    private void displaySystemInfo() {
        System.out.println("\n--- SYSTEM TERMINAL ---");
        System.out.println("Core Functions:");
        System.out.println("- Gate Stabilization: Operational");
        System.out.println("- Mana Regulation: " + (player.isAwakened() ? "Enhanced" : "Normal"));
        System.out.println("- Dungeon Synchronization: Active");

        System.out.println("\nHunter Analysis:");
        System.out.println("- Awakening Status: " + (player.isAwakened() ? "Confirmed" : "Not Detected"));
        System.out.println("- Skill Manifestation: " + player.getSkills().size() + " abilities");

        System.out.println("\nRecent Notifications:");
        System.out.println("- Red Gate activity: Increased");
        System.out.println("- Double Dungeon probability: 5%");
    }

    /**
     * Dungeon entry menu. Add new ranks or adjust as needed.
     */
    private void enterDungeon() {
        System.out.println("\n===== DUNGEON SELECTION =====");
        System.out.println("1. E-Rank Dungeon (Beginner)");
        System.out.println("2. D-Rank Dungeon (Easy)");
        System.out.println("3. C-Rank Dungeon (Red Gate)");
        System.out.println("4. B-Rank Dungeon (Medium)");
        System.out.println("5. A-Rank Dungeon (Hard)");
        System.out.println("6. S-Rank Dungeon (Deadly)");
        System.out.println("7. Return to Main Menu");
        System.out.print("Select: ");

        String rankChoice;
        int choice = getIntInput();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1: rankChoice = "E"; break;
            case 2: rankChoice = "D"; break;
            case 3: rankChoice = "C"; break;
            case 4: rankChoice = "B"; break;
            case 5: rankChoice = "A"; break;
            case 6: rankChoice = "S"; break;
            case 7: return;
            default: System.out.println("Invalid choice, defaulting to E-Rank"); rankChoice = "E";
        }

        // Check rank requirements
        String playerRank = player.getRank();
        if (getRankValue(rankChoice) > getRankValue(playerRank)) {
            System.out.println("\n> Access denied! You need at least " + rankChoice + "-Rank");
            return;
        }

        Dungeon dungeon = new Dungeon(rankChoice);

        // Red gate mechanics
        if (dungeon.isRedGate()) {
            System.out.println("\n> WARNING: RED GATE DETECTED!");
            System.out.println("> You cannot leave until you clear all floors!");
        }

        // Double dungeon chance
        Random rand = new Random();
        if (rand.nextDouble() < 0.06) { // 6% chance
            Dungeon doubleDungeon = new Dungeon("S");
            doubleDungeon.setAsDoubleDungeon();
            System.out.println("\n> WARNING: Hidden Double Dungeon detected!");
            System.out.println("> You've been transported to " + doubleDungeon.getName() + " Dungeon!");
            dungeon = doubleDungeon;
        }

        System.out.println("\n=================================================");
        System.out.println("You are entering the " + dungeon.getName() + " Dungeon.");
        System.out.println("===================================================");

        dungeonStack.push(dungeon);
        exploreDungeon(dungeon);
    }

    /**
     * Converts rank string to numerical value.
     */
    private int getRankValue(String rank) {
        switch (rank) {
            case "E": return 1;
            case "D": return 2;
            case "C": return 3;
            case "B": return 4;
            case "A": return 5;
            case "S": return 6;
            default: return 0;
        }
    }

    /**
     * Dungeon exploration system. Adjust monster generation as needed.
     */
    private void exploreDungeon(Dungeon dungeon) {
        boolean inDungeon = true;
        while (inDungeon && player.isAlive()) {
            System.out.println("\n=============================================");
            System.out.println("You are on Floor " + dungeon.getCurrentFloor());
            System.out.println("===============================================");
            System.out.println("Your Health: " + player.getHP());

            // Generate monsters (1-3 per floor)
            Random rand = new Random();
            int monsterCount = 1 + rand.nextInt(3);
            Monster[] monsters = new Monster[monsterCount];
            for (int i = 0; i < monsterCount; i++) {
                monsters[i] = new Monster(dungeon.getCurrentFloor(), dungeon.getRank());
                // To customize monster creation, edit Monster.java as needed.
            }

            System.out.println("Monsters: " + monsterCount);
            for (int i = 0; i < monsterCount; i++) {
                System.out.println((i + 1) + ". " + monsters[i].getName()
                        + " | Level: " + monsters[i].getLevel()
                        + " | Health: " + monsters[i].getHealth());
            }

            int choice = -1;
            while (choice < 0 || choice > monsterCount) {
                try {
                    System.out.print("Choose a monster to attack (1-" + monsterCount + " or 0 to leave): ");
                    choice = scanner.nextInt();
                    scanner.nextLine();

                    if (choice < 0 || choice > monsterCount) {
                        System.out.println("Invalid choice! Please enter between 0 and " + monsterCount);
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input! Please enter a number.");
                    scanner.nextLine();
                }
                if (choice == 0) {
                    if (dungeon.isRedGate()) {
                        System.out.println("Cannot leave Red Gate dungeon!");
                    } else {
                        System.out.println("You leave the dungeon");
                        return;
                    }
                }
                if (choice < 1 || choice > monsterCount) {
                    System.out.println("Invalid choice!");
                    continue;
                }

                Monster target = monsters[choice - 1];
                System.out.println("You attack the " + target.getName() + "!");

                // Combat with selected monster
                boolean monsterDefeated = combat(target);

                if (!player.isAlive()) {
                    System.out.println("You have been defeated!");
                    if (dungeon.isDoubleDungeon() && !player.isAwakened()) {
                        player.awaken();
                        player.heal(player.getMaxHP()); // Full heal
                        System.out.println("> SYSTEM: You have been revived by the System!");
                    }
                    return;
                }

                if (monsterDefeated) {
                    // Check if all monsters are defeated
                    boolean allDefeated = true;
                    for (Monster m : monsters) {
                        if (m.isAlive()) {
                            allDefeated = false;
                            break;
                        }
                    }
                    if (allDefeated) {
                        System.out.println("\n> All monsters on this floor defeated!");
                        System.out.println("> Advancing to next floor...");
                        dungeon.nextFloor();
                        // Chance to find healing
                        if (rand.nextDouble() < 0.4) {
                            System.out.println("> You found a Health Potion!");
                            player.addItem("Health Potion");
                        }
                    }
                }
                // Check quest completion
                player.checkQuestCompletion(dungeon.getName(), dungeon.getRank());
            }
        }
    }

    /**
     * Combat system. Integrate with Monster and Hunter logic as needed.
     * Add new skills/items here.
     */
    private boolean combat(Monster monster) {
        boolean inCombat = true;
        boolean usedFlux = false;
        int fluxTurns = 0;

        while (inCombat && player.isAlive() && monster.isAlive()) {
            player.applyEndOfTurnEffects();

            System.out.println("\n" + monster.getName() + " | Level: " + monster.getLevel()
                    + " | Health: " + monster.getHealth());
            System.out.println("Your HP: " + player.getHP() + " | Mana: " + player.getMana());
            System.out.println("1. Attack");
            System.out.println("2. Use Skill");
            System.out.println("3. Use Item");
            System.out.println("4. Attempt Flee");
            System.out.print("Select: ");

            int action = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            int damage = 0;

            switch (action) {
                case 1: // Attack
                    int attackDamage = player.getWeapon().use();
                    if (attackDamage > 1) {
                        damage = player.calculateDamage();
                        System.out.println("You deal " + damage + " damage!");
                        monster.takeDamage(damage);
                    } else {
                        System.out.println("Your weapon broke! Attack failed.");
                    }
                    break;

                case 2: // Use Skill
                    if (player.isAwakened()) {
                        Map<String, Skill> skills = player.getSkills();
                        System.out.println("Available Skills:");
                        int index = 1;
                        for (Skill skill : skills.values()) {
                            System.out.println(index++ + ". " + skill.getName()
                                    + " (" + skill.getManaCost() + " MP)");
                        }
                        System.out.println(index + ". Cancel");

                        System.out.print("Select skill: ");
                        int skillChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        if (skillChoice == index) continue;

                        Skill[] skillArray = skills.values().toArray(new Skill[0]);
                        if (skillChoice > 0 && skillChoice <= skillArray.length) {
                            Skill selectedSkill = skillArray[skillChoice - 1];

                            if (player.getMana() >= selectedSkill.getManaCost()) {
                                switch (selectedSkill.getName()) {
                                    case "Healing": player.heal(30); System.out.println("> Healed 30 HP!"); break;
                                    case "Flux": usedFlux = true; fluxTurns = 4; System.out.println("> ATK/DEF boosted for 4 turns!"); break;
                                    case "Stealth": System.out.println("> You vanish from sight!"); break;
                                }
                                // Properly reduce mana (should be setMana, not addGold)
                                // player.setMana(player.getMana() - selectedSkill.getManaCost());
                            } else {
                                System.out.println("Not enough mana!");
                            }
                        }
                    } else {
                        System.out.println("Skills not unlocked yet!");
                    }
                    break;

                case 3: // Use Item
                    System.out.println("1. Health Potion");
                    System.out.println("2. Mana Potion");
                    System.out.println("3. Weapon Repair Kit");
                    System.out.println("4. Cancel");
                    System.out.print("Select: ");

                    int itemChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    switch (itemChoice) {
                        case 1: player.useItem("Health Potion"); break;
                        case 2: player.useItem("Mana Potion"); break;
                        case 3: player.useItem("Weapon Repair Kit"); break;
                    }
                    break;

                case 4: // Flee
                    if (new Random().nextDouble() < 0.4) {
                        System.out.println("You escaped successfully!");
                        return false;
                    } else {
                        System.out.println("Escape failed!");
                    }
                    break;

                default: System.out.println("Invalid choice, you hesitate...");
            }

            // Apply flux damage boost if active
            if (usedFlux && action == 1) {
                int fluxDamage = (int) (damage * 0.4);
                System.out.println("Flux adds " + fluxDamage + " bonus damage!");
                monster.takeDamage(fluxDamage);
                if (--fluxTurns <= 0) {
                    usedFlux = false;
                    System.out.println("> Flux effect wore off");
                }
            }

            // Monster attack if still alive
            if (monster.isAlive()) {
                int monsterDamage = monster.getDamage();
                if (usedFlux) monsterDamage *= 0.6;
                System.out.println(monster.getName() + " attacks for " + monsterDamage + " damage!");
                player.takeDamage(monsterDamage);
            }
        }

        if (!player.isAlive()) return false;

        if (!monster.isAlive()) {
            // Victory rewards
            System.out.println("\n> Victory! You defeated the " + monster.getName());
            System.out.println("> Rewards: " + monster.getExpReward() + " XP, "
                    + monster.getGoldReward() + " Gold");
            player.addExperience(monster.getExpReward());
            player.addGold(monster.getGoldReward());
            player.addFame(monster.isBoss() ? 15 : 5);

            // Boss drops
            if (monster.isBoss() && new Random().nextDouble() < 0.3) {
                System.out.println("> The boss dropped a Dos Purdos!");
                player.setWeapon(DOS_PURDOS);
            }
            return true;
        }
        return false;
    }
}