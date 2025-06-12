package com.bosakon.monsterhunter;

/**
 * Represents game world locations (e.g., Market, Center, Gym, Home).
 * 
 * === Extension Guide ===
 * - To add a new location, instantiate a new Location and add to your locations map in HunterGame.
 * - To customize location behavior, extend this class or override enter().
 * 
 * Example:
 *   Location library = new Location("Library", "A quiet place full of ancient tomes.");
 *   locations.put("library", library);
 */
public class Location {
    private String name;
    private String description;
    
    /**
     * Constructs a location with a name and description.
     * @param name        Location name (e.g. "Market")
     * @param description Description or flavor text.
     */
    public Location(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /** Displays location information (called when player enters the location). */
    public void enter() {
        System.out.println("\n======================================");
        System.out.println("                " + name.toUpperCase());
        System.out.println("======================================");
        System.out.println(description);
    }
    
    /** Gets the name of the location. */
    public String getName() { return name; }
}