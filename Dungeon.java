package com.bosakon.monsterhunter;

import java.util.Random;

/**
 * Represents a dungeon with rank, random name, difficulty, floor progression,
 * and special properties (Red Gate, Double Dungeon).
 *
 * <b>How to add new dungeon names:</b>
 *   - Add to the relevant static array (e.g. E_RANK_NAMES).
 *
 * <b>How to instantiate:</b>
 *   Dungeon d = new Dungeon("A");
 *   d.setAsDoubleDungeon();
 *
 * <b>Example: Add new rank or floor logic:</b>
 *   // Add "NewArea" to D-rank:
 *   private static final String[] D_RANK_NAMES = {"Mati", ..., "NewArea"};
 */
public class Dungeon {
    private String rank;
    private String name;
    private int difficulty;
    private boolean isRedGate;
    private boolean isDoubleDungeon;
    private int currentFloor;

    // Dungeon name lists (edit here to add new names)
    private static final String[] E_RANK_NAMES = {"Buhangin", "Milan", "Tigatto", "Bajada", "SPMC", "Cabagiuo"};
    private static final String[] D_RANK_NAMES = {"Mati", "Apokon", "Panabo", "Carmen", "Mawab"};
    private static final String[] C_RANK_NAMES = {"Lasang", "Sirawan", "Bunawan", "Donia Pilar", "LandMark"};
    private static final String[] A_RANK_NAMES = {"Lanang", "Sasa", "Magsaysay", "Roxas", "Uyangureen"};
    private static final String[] S_RANK_NAMES = {"Boulevard", "Matina Aplaya", "San Pedro", "Ubos Bangkerohan"};

    /**
     * Constructs a dungeon of a given rank.
     * Automatically sets Red Gate for C-Rank.
     * Starts on floor 1.
     */
    public Dungeon(String rank) {
        this.rank = rank;
        this.name = generateRandomName(rank);
        this.difficulty = getDifficultyValue(rank);
        this.isRedGate = "C".equals(rank);
        this.isDoubleDungeon = false;
        this.currentFloor = 1;
    }

    /**
     * Generates a random dungeon name based on the rank.
     * @param rank - Dungeon rank ("E"..."S")
     * @return Name string
     */
    private String generateRandomName(String rank) {
        Random rand = new Random();
        switch(rank) {
            case "E": return E_RANK_NAMES[rand.nextInt(E_RANK_NAMES.length)];
            case "D": return D_RANK_NAMES[rand.nextInt(D_RANK_NAMES.length)];
            case "C": return C_RANK_NAMES[rand.nextInt(C_RANK_NAMES.length)];
            case "A": return A_RANK_NAMES[rand.nextInt(A_RANK_NAMES.length)];
            case "S": return S_RANK_NAMES[rand.nextInt(S_RANK_NAMES.length)];
            default: return "Unknown";
        }
    }

    /**
     * Assigns a numeric difficulty to each rank.
     */
    private int getDifficultyValue(String rank) {
        switch(rank) {
            case "E": return 1;
            case "D": return 2;
            case "C": return 3;
            case "B": return 4;
            case "A": return 5;
            case "S": return 8;
            default: return 1;
        }
    }

    /** Advances to the next floor of the dungeon. */
    public void nextFloor() {
        currentFloor++;
    }

    /** Marks this dungeon as a Red Gate. */
    public void setAsRedGate() {
        this.isRedGate = true;
    }

    /** Marks this as a Double Dungeon. */
    public void setAsDoubleDungeon() {
        this.isDoubleDungeon = true;
    }

    // -------- Getters ---------
    /** 
     * Returns the full formatted dungeon name, e.g.
     * "Bajada Dungeon (C-Rank - RED GATE)"
     */
    public String getFullName() { 
        return name + " Dungeon (" + rank + "-Rank" 
            + (isRedGate ? " - RED GATE" : "") 
            + (isDoubleDungeon ? " - DOUBLE DUNGEON" : "")
            + ")";
    }

    public int getDifficulty() { return difficulty; }
    public String getRank() { return rank; }
    public boolean isRedGate() { return isRedGate; }
    public int getCurrentFloor() { return currentFloor; }
    public String getName() { return name; }
    public boolean isDoubleDungeon() { return isDoubleDungeon; }
}