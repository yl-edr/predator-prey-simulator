import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Roman soldier.
 * Roman soldiers age, move, eat enemies, reproduce, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg and Yaal Luka Edrey Gatignol
 * @version 4.6
 */
public class Roman extends Empire
{
    // The age at which a Roman soldier can start reproducing.
    private static final int GIVING_BIRTH_AGE= 30;
    // The maximum age a Roman soldier can live.
    private static final int MAX_AGE = 800;
    // The probability a Roman soldier will give birth.
    private static final double GIVING_BIRTH_PROBABILITY = 0.75;
    // The maximum number of births.
    private static final int MAX_CHILDREN = 4;
    // The maximum number of recruits a Roman soldier can produce in one step.
    private static final int MAX_RECRUITS = 4;
    // The resource level restored after finding an enemy.
    private static final int FULL_RESOURCE_VALUE = 20;
    // A shared random number generator for controlling breeding and movement decisions.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a Roman soldier.
     * A soldier may be created with either a random or zero age and resource level.
     * 
     * @param randomAge If true, the soldier will be given a random age and resource level.
     * @param location The initial location within the field.
     */
    public Roman(boolean randomAge, Location location)
    {
        super(location, 'M');
        this.maxAge = MAX_AGE;
        this.givingBirthAge= GIVING_BIRTH_AGE;
        this.givingBirthProbability= GIVING_BIRTH_PROBABILITY;
        this.maxChildren= MAX_CHILDREN;
        if(randomAge) {
            this.age = rand.nextInt(MAX_AGE);
        }
        else {
            this.age = 0;
        }
        this.resourceLevel = rand.nextInt(FULL_RESOURCE_VALUE);
    }
    
    /**
     * Simulate the Roman soldier actions for one step.
     * If alive, the soldier may move, recruit, reproduce, or die.
     * Behavior is influenced by the weather and time of day.
     * 
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field for the next step.
     */
    public void conquer(Field currentField, Field nextFieldState)
    {
        incrementAge(); // Increase age and check for death due to aging.
        
        if(isAlive()) {
            if(!isActiveTime()) {
                nextFieldState.placePerson(this, getLocation());
            }
            else {
                incrementResources(); // Decrease resources and check for death due to starvation.
                boolean recruit = false;
                List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation()); 
                // Recruiting civilians if nearby.
                if(civilianNearby(currentField)) {
                    recruit(currentField,nextFieldState);
                    recruit = true;
                    nextFieldState.placePerson(this, getLocation());
                }
                // Reproducing if possible and didn't recruit.
                else if(!freeLocations.isEmpty()) {
                    reproduce(nextFieldState, freeLocations);
                }
                // Move towards an enemy if found.
                if(!recruit) {
                    Location nextLocation = findEnemy(currentField);
                    if(nextLocation == null && ! freeLocations.isEmpty()) {
                        // No enemy found - try to move to a free location.
                        nextLocation = freeLocations.remove(0);
                    }
                    // Move to the determined location, or die if overcrowded.
                    if(nextLocation != null) {
                        setLocation(nextLocation);
                        nextFieldState.placePerson(this, nextLocation);
                    }
                    else {
                        setDead();
                    }
                } 
            } 
        }
    }

    /**
     * Provide a string representation of the Roman soldier.
     */
    @Override
    public String toString() {
        return "Roman Soldier{" +
                "age = " + age +
                ", alive = " + isAlive() +
                ", location = " + getLocation() +
                ", resourceLevel = " + resourceLevel +
                '}';
    }
    
    
    /**
     * Look for an enemy soldier near to the current location.
     * If an enemy is found, it is killed, and the Roman soldier regains full resources.
     * 
     * @param field The field currently occupied.
     * @return The location of the enemy if found, or null if not found.
     */
    private Location findEnemy(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location enemyLocation = null;
        while(enemyLocation == null && it.hasNext()) {
            Location loc = it.next();
            Person soldier = field.getPersonAt(loc);
            if(soldier instanceof Amazonian || soldier instanceof Persian) {
                if(soldier.isAlive()) {
                    soldier.setDead();
                    resourceLevel = FULL_RESOURCE_VALUE;
                    enemyLocation = loc;
                }
            }
        }
        return enemyLocation;
    }
    
    /**
     * Try to reproduce.
     * New soldiers are placed in adjacent free locations.
     * 
     * @param nextFieldState The updated field for the next step.
     * @param freeLocations The available free locations.
     */
    protected void reproduce(Field nextFieldState, List<Location> freeLocations)
    {
        int recruits = giveBirth();
        if(recruits > 0) {
            for (int b = 0; b < recruits && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Roman youngRecruit = new Roman(false, loc);
                nextFieldState.placePerson(youngRecruit, loc);
            }
        }
    }

    /**
     * Attempt to recruit nearby civilians and convert them into Roman soldiers.
     * 
     * @param nextFieldState The updated field for the next step.
     * @param freeLocations The available free locations for recruits.
     */
    protected void recruit(Field currentFieldState, Field nextFieldState)
    {
        List<Location> adjacent = currentFieldState.getAdjacentLocations(getLocation());
        int recruitsMade = 0;

        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext() && recruitsMade < MAX_RECRUITS) {
            Location loc = it.next();
            Person person = currentFieldState.getPersonAt(loc);
            if(person instanceof Civilian civilian && person.getSex() == 'M') {
                if(civilian.isAlive()) {
                    civilian.setDead();
                    Roman recruit = new Roman(false, loc);
                    nextFieldState.placePerson(recruit, loc);
                    recruitsMade++;
                }
            }
        }
    }
        
    /**
     * Check if the soldier is active at the current time.
     * Roman soldiers are not active during "bath time" (11 AM to 1 PM).
     * 
     * @return True if the soldier is active, false otherwise.
     */
    private boolean isActiveTime() {
        int currentTime = Empire.getTime().getHour();
        return (7 <= currentTime && currentTime < 11) || (13 <= currentTime && currentTime < 20);
    }


}
