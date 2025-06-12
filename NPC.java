package com.bosakon.monsterhunter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class NPC {
    private String type;
    private String name;
    private Map<String, Integer> inventory;

    public NPC(String type, String name) {
        this.type = type;
        this.name = name;
        this.inventory = new HashMap<>();

        // Merchant default inventory (add more as needed)
        if ("merchant".equals(type)) {
            inventory.put("Health Potion", 10);
            inventory.put("Mana Potion", 8);
            inventory.put("Weapon Repair Kit", 5);
        }
    }

    /**
     * Handles interaction with player.
     * @param player Hunter interacting with NPC
     * @param scanner Shared Scanner instance
     */
    public void interact(Hunter player, Scanner scanner) {
        if (player == null) return;
        switch(type) {
            case "merchant":
                trade(player, scanner);
                break;
            case "friend":
                System.out.println(name + ": \"Hey " + player.getName() + ", how's the hunting?\"");
                break;
            case "reporter":
                if (player.getFame() > 50) {
                    System.out.println(name + ": \"Can I get an interview about your latest dungeon clear?\"");
                    player.addFame(10);
                } else {
                    System.out.println(name + ": \"Become more famous and I'll interview you!\"");
                }
                break;
            case "quest_giver":
                System.out.println(name + ": \"I have a task for you, Hunter!\"");
                // Add quest logic here if desired
                break;
            default:
                System.out.println(name + " has nothing to say right now.");
        }
    }

    /** Handles trading with merchant NPC */
    private void trade(Hunter player, Scanner scanner) {
        while (true) {
            System.out.println("\n" + name + "'s Shop:");
            int index = 1;
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println(index++ + ". " + entry.getKey() + " - " + getPrice(entry.getKey()) + " Gold (" + entry.getValue() + " in stock)");
            }
            System.out.println(index + ". Exit");

            System.out.print("Select item: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == index) return;

            String[] items = inventory.keySet().toArray(new String[0]);
            if (choice > 0 && choice <= items.length) {
                String itemName = items[choice-1];
                int price = getPrice(itemName);

                if (player.getGold() >= price) {
                    int stock = inventory.get(itemName);
                    if (stock > 0) {
                        player.addItem(itemName);
                        player.addGold(-price);
                        inventory.put(itemName, stock - 1);
                        System.out.println("Purchased " + itemName + "!");
                    } else {
                        System.out.println("Sorry, " + itemName + " is out of stock!");
                    }
                } else {
                    System.out.println("Not enough gold!");
                }
            } else {
                System.out.println("Invalid selection!");
            }
        }
    }

    /** Gets price for items (add more here) */
    private int getPrice(String item) {
        switch(item) {
            case "Health Potion": return 15;
            case "Mana Potion": return 20;
            case "Weapon Repair Kit": return 30;
            default: return 0;
        }
    }

    // Getters if needed
    public String getType() { return type; }
    public String getName() { return name; }
    public Map<String, Integer> getInventory() { return inventory; }
}