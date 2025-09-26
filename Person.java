import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * The Person class represents all individuals in the simulation.
 * It serves as a superclass for all different types of people in the game.
 * 
 * Each Person has a location, a sex, and a status indicating whether they are alive.
 * Subclasses of Person must implement reproduction behavior.
 * 
 * @author Rom Steinberg and Yaal Luka Edrey Gatignol
 * @version 2024.11.28
 */
public abstract class Person 
{
    // Indicates whether the person is alive.
    private boolean alive;
    // The current location of the person in the field.
    private Location location;
    // The sex of the person ('M' or 'F').
    private char sex;
    // A shared random number generator for controlling breeding and movement decisions.
    private static final Random rand = Randomizer.getRandom();

    protected int age; // The person's current age.
    protected int maxAge; // The maximum age a person can live.
    protected int maxChildren; // The maximum number of children a person can have.
    protected double givingBirthProbability; // The probability a person will give birth.
    protected int givingBirthAge; // The age at which a person can start reproducing.

    /**
     * Constructor for a Person.
     * Each person starts alive and is placed at a given location with a specified sex.
     * 
     * @param location The initial location of the person.
     * @param sex The sex of the person ('M' or 'F').
     */
    public Person(Location location, char sex) 
    {
        this.alive = true;
        this.location = location;
        this.sex = sex;
    }

    /**
     * Check if the person is alive.
     * 
     * @return true if the person is alive, false otherwise.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Mark the person as dead.
     * Once dead, the person's location is set to null.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Get the person's current location.
     * 
     * @return The location of the person.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Update the persons location.
     * 
     * @param location The new location to set.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * Get the persons sex.
     * 
     * @return The sex of the person ('M' or 'F').
     */
    protected char getSex() {
        return sex;
    }

    /**
     * Increment the soldiers age. If the soldier exceeds the maximum age, they die.
     */
    protected void incrementAge()
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * Determine the number of children born if reproduction conditions are met.
     * 
     * @return The number of children (can be zero).
     */
    protected int giveBirth()
    {
        int births = 0;
        if(canGiveBirth() && rand.nextDouble() <= givingBirthProbability) {
            births = rand.nextInt(maxChildren) + 1;
        }
        return births;
    }

    /**
     * Check if the Amazonian soldier is old enough to reproduce..
     * @return true if the Amazonian can breed, false otherwise.
     */
    private boolean canGiveBirth()
    {
        return age >= givingBirthAge;
    }

    /**
     * Check if there is a potential mate nearby.
     * A mate is another living person of the same type and of the opposite sex.
     * 
     * @param field The field to check.
     * @return True if a mate is nearby, false otherwise.
     */
    protected boolean mateNearby(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(this.getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location loc = it.next();
            Person person = field.getPersonAt(loc);
            if(person != null && person.getClass() == this.getClass()) {
                if(person.isAlive() && person.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Abstract method for reproduction.
     * Must be implemented by subclasses to define specific reproduction behavior.
     * 
     * @param nextFieldState The updated field for the next simulation step.
     * @param freeLocations The list of available locations where reproduction can occur.
     */
    protected abstract void reproduce(Field nextFieldState, List<Location> freeLocations);
}
