package com.bosakon.monsterhunter;

/**
 * Represents weapons, potions, and equipment.
 * 
 * === Extension Guide ===
 * - To add new item types, just create new Item instances with desired stats.
 * - Special effect is a string; if you want logic, handle it in the Hunter or combat logic.
 * 
 * Example:
 *   Item thunderSword = new Item("Thunder Sword", 15, 0.33, "Stun on crit", 60);
 */
public class Item {
    private String name;
    private int baseDamage;
    private double critChance;
    private String specialEffect;
    private int durability;
    private int maxDurability;

    /**
     * Constructs an item (weapon, potion, equipment, etc.).
     * 
     * @param name           Item name (e.g. "Lapis")
     * @param baseDamage     Damage stat (0 for non-weapons)
     * @param critChance     Critical hit chance (0-1.0)
     * @param specialEffect  Effect description (for display and game logic)
     * @param durability     Max durability (potions: set to 1)
     */
    public Item(String name, int baseDamage, double critChance, 
                String specialEffect, int durability) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.critChance = critChance;
        this.specialEffect = specialEffect;
        this.durability = durability;
        this.maxDurability = durability;
    }

    /**
     * Uses the item, reducing durability by 1.
     * For weapons, returns true if still usable, false if broken.
     * For consumables, set durability to 0 after use.
     * @return true if item still usable after use, false if broken
     * 
     * Example usage in combat:
     *   if (item.use()) { // attack }
     *   else { // weapon broke }
     */
    public boolean use() {
        if (durability > 0) {
            durability--;
            return true;
        }
        return false;
    }

    /** Repairs the item to full durability. */
    public void repair() {
        durability = maxDurability;
    }

    // === Getters ===
    public String getName()            { return name; }
    public int getBaseDamage()         { return baseDamage; }
    public double getCritChance()      { return critChance; }
    public String getSpecialEffect()   { return specialEffect; }
    public int getDurability()         { return durability; }
    public int getMaxDurability()      { return maxDurability; }
}