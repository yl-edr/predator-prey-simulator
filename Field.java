import java.util.*;

/**
 * Represents a rectangular grid of field positions.
 * Each position can store a single person (soldier or civilian).
 * The field supports placement, retrieval, and adjacency calculations for people.
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg, and Yaal Luka Edrey Gatignol
 * @version 7.0
 */
public class Field
{
    // The probability that a civilian will be created in any given grid position.
    private static final double CIVILIAN_CREATION_PROBABILITY = 0.01;
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    // A map that stores the location of each person on the field.
    private final Map<Location, Person> field = new HashMap<>();
    // A list containing all people (soldiers and civilians) in the field.
    private final List<Person> people = new ArrayList<>();

    //private HashMap<String, Integer> stats = new HashMap<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place a person at the given location.
     * If there is already a person at the location, they are replaced.
     * 
     * @param aPerson The person to be placed.
     * @param location The location where the person is placed.
     */
    public void placePerson(Person aPerson, Location location)
    {
        Object other = field.get(location);
        if(other != null) {
            people.remove(other); // Remove the person previously at this location.
        }
        field.put(location, aPerson); // Update the field map with the new person.
        people.add(aPerson); // Add the person to the list of all people.
        
    }
    
    /**
     * Get the person at the specified location, if any.
     * @param location The location to check.
     * @return The person at the location, or null if the location is empty.
     */
    public Person getPersonAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            Person aPerson = field.get(next);
            if(aPerson == null) {
                free.add(next);
            }
            else if(!aPerson.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Get all locations within the specified radius of a given location.
     * @param location The reference location.
     * @param radius The radius within which to find locations.
     * @return A list of adjacent locations within the specified radius.
     */
    public List<Location> getAdjacentLocations(Location location, int radius)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = radius*-1; roffset <= radius; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = radius*-1; coffset <= radius; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Print statistics about the number of each type of person in the field.
     * 
     * @return A map of each type of person and their respective counts.
     */
    public HashMap<String,Integer> getFieldStats()
    {
        int numAmazonians = 0, numBritish = 0, numPersians = 0, numRomans = 0, numSpanish = 0, numCivilians = 0;
        for(Person aPerson : field.values()) {
            if(aPerson instanceof Persian persian) {
                if(persian.isAlive()) {
                    numPersians++;
                }
            }
            else if(aPerson instanceof British british) {
                if(british.isAlive()) {
                    numBritish++;
                }
            }
            else if(aPerson instanceof Amazonian amazonian) {
                if(amazonian.isAlive()) {
                    numAmazonians++;
                }
            }
            
            else if(aPerson instanceof Roman roman) {
                if(roman.isAlive()) {
                    numRomans++;
                }
            }
            else if(aPerson instanceof Spanish spanish) {
                if(spanish.isAlive()) {
                    numSpanish++;
                }

            }
            else if(aPerson instanceof Civilian civilian) {
                if(civilian.isAlive()) {
                    numCivilians++;
                }
            }
        }
        
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("Amazonian", numAmazonians);
        stats.put("British", numBritish);
        stats.put("Persian", numPersians);
        stats.put("Roman", numRomans);
        stats.put("Spanish", numSpanish);
        stats.put("Civilian", numCivilians);
        return stats;
    }

    /**
     * Print statistics about the number of each type of person in the field.
     */
    public void printFieldStats()
    {
        HashMap<String, Integer> stats = getFieldStats();
        System.out.println("Civilians: " + stats.get("Civilian") +
                        " Amazonians: " + stats.get("Amazonian") +
                        " British: " + stats.get("British") +
                        " Persians: " + stats.get("Persian") +
                        " Romans: " + stats.get("Roman") +
                        " Spanish: " + stats.get("Spanish"));
    }
    

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Determine if there are at least two surviving empires on the field.
     * 
     * @return true if at least two empires are alive; false otherwise.
     */
    public boolean isViable()
    {
        int survivingEmpiresCount = 0;
        boolean britishFound = false;
        boolean amazonianFound = false;
        boolean persianFound = false;
        boolean romanFound = false;
        boolean spanishFound = false;
        Iterator<Person> it = people.iterator();
        while(it.hasNext() && survivingEmpiresCount < 2) {
            Person aPerson = it.next();
            if(aPerson instanceof British british) {
                if(british.isAlive() && !britishFound) {
                    britishFound = true;
                    survivingEmpiresCount++;
                }
            }
            else if(aPerson instanceof Amazonian amazonian) {
                if(amazonian.isAlive() && !amazonianFound) {
                    amazonianFound = true;
                    survivingEmpiresCount++;
                }
            }
            else if(aPerson instanceof Persian persian) {
                if(persian.isAlive() && !persianFound) {
                    persianFound = true;
                    survivingEmpiresCount++;
                }
            }
            else if(aPerson instanceof Roman roman) {
                if(roman.isAlive() && !romanFound) {
                    romanFound = true;
                    survivingEmpiresCount++;
                }
            }
            else if(aPerson instanceof Spanish spanish) {
                if(spanish.isAlive() && !spanishFound) {
                    spanishFound = true;
                    survivingEmpiresCount++;
                }
            }
        }
        return survivingEmpiresCount > 1;
    }
    
    /**
     * Check if there is at least one civilian present and alive in the field.
     * @return true if at least one civilian is found; false otherwise.
     */
    public boolean existCivilian() {
        boolean civilianFound = false;
        Iterator<Person> it = people.iterator();
        while(it.hasNext() && !civilianFound) {
            Person aPerson = it.next();
            if(aPerson instanceof Civilian civilian) {
                if(civilian.isAlive()) {
                    civilianFound = true;
                }
            }
        }
        return civilianFound;
    }

    /**
     * Repopulate the field with civilians in random free locations.
     * 
     * @param nextFieldState The field state to update with new civilians.
     */
    public void repopulateCivilians(Field nextFieldState) {
        List<Location> freeLocations = getFreeLocations(nextFieldState);
        //Random rand = Randomizer.getRandom();
        for(Location freeLocation : freeLocations) {
            if(rand.nextDouble() <= CIVILIAN_CREATION_PROBABILITY) {
                Civilian civilian = new Civilian(true, freeLocation, Randomizer.getRandomSex());
                nextFieldState.placePerson(civilian, freeLocation);
            }
        }
    }

    /**
     * Get a list of all free locations in the field.
     * 
     * @param nextFieldState The field to check for free locations.
     * @return A list of free locations.
     */
    public List<Location> getFreeLocations(Field nextFieldState)
    {
        List<Location> free = new LinkedList<>();
        for(int row = 0; row < getDepth(); row++) {
            for(int col = 0; col < getWidth(); col++) {
            Location loc = new Location(row, col);
            if(getPersonAt(loc) == null) {  
                free.add(loc);
            }
            }
        }
        return free;
    }

    /**
     * Get the list of peoplr.
     */
    public List<Person> getPeople()
    {
        return people;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
