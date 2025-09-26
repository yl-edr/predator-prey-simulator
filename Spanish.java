import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Spanish soldier.
 * Spanish soldiers age, move, eat enemies, reproduce, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg and Yaal Luka Edrey Gatignol
 * @version 7.1
 */
public class Spanish extends Empire
{
    // The age at which a Spanish soldier can start reproducing.
    private static final int GIVING_BIRTH_AGE = 50;
    // The maximum age a Spanish soldier can live.
    private static final int MAX_AGE = 500;
    // The probability a Spanish soldier will give birth.
    private static final double GIVING_BIRTH_PROBABILITY = 0.7;
    // The maximum number of births.
    private static final int MAX_CHILDREN = 4;
    // The maximum number of recruits a Spanish soldier can produce in one step.
    private static final int MAX_RECRUITS = 3;
    // The resource level restored after finding an enemy.
    private static final int FULL_RESOURCE_VALUE = 30;
    // A shared random number generator for controlling breeding and movement decisions.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a Spanish soldier.
     * A soldier may be created with either a random or zero age and resource level.
     * 
     * @param randomAge If true, the soldier will be given a random age and resource level.
     * @param location The initial location within the field.
     */
    public Spanish(boolean randomAge, Location location, char sex)
    {
        super(location, sex);
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
     * Simulate the Spanish soldier actions for one step.
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
                nextFieldState.placePerson(this, getLocation());;
            }
            else{
                incrementResources(); // Decrease resources and check for death due to starvation.
                boolean recruit = false;
                List<Location> freeLocations =nextFieldState.getFreeAdjacentLocations(getLocation());
                // Recruiting civilians if nearby.
                if(civilianNearby(currentField)) {
                    recruit(currentField, nextFieldState);
                    recruit = true;
                    nextFieldState.placePerson(this, getLocation());
                }
                // Reproducing if possible and didn't recruit.
                else if((!freeLocations.isEmpty() && mateNearby(nextFieldState))) {
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
     * Provide a string representation of the Spanish soldier.
     */
    @Override
    public String toString() {
        return "Spanish Soldier{" +
                "age = " + age +
                ", alive = " + isAlive() +
                ", location = " + getLocation() +
                ", resourceLevel = " + resourceLevel +
                '}';
    }
    
    
    /**
     * Look for an enemy soldier near to the current location.
     * If an enemy is found, it is killed, and the Spanish soldier regains full resources.
     * 
     * @param field The field currently occupied.
     * @return The location of the enemy if found, or null if not found.
     */
    private Location findEnemy(Field field)
    {   
        List<Location> adjacent;
        if(getCurrentWeatherCondition() == Weather.Condition.SUNNY) {    
            adjacent = field.getAdjacentLocations(getLocation(), 2);  
        }
        else {
            adjacent = field.getAdjacentLocations(getLocation());
        }
        Iterator<Location> it = adjacent.iterator();
        Location enemyLocation = null;
        while(enemyLocation == null && it.hasNext()) {
            Location loc = it.next();
            Person soldier = field.getPersonAt(loc);
            if(soldier instanceof Amazonian || soldier instanceof Roman) {
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
        // New spanish are born into adjacent locations.
        // Get a list of adjacent free locations.
        int children = giveBirth();
        if(children > 0) {
            for (int b = 0; b < children && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Spanish youngRecruit = new Spanish(false, loc, Randomizer.getRandomSex());
                nextFieldState.placePerson(youngRecruit, loc);
            }
        }
    }

    /**
     * Attempt to recruit nearby civilians and convert them into Spanish soldiers.
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
            if(person instanceof Civilian civilian) {
                if(civilian.isAlive()) {
                    civilian.setDead();
                    Spanish recruit = new Spanish(false, loc, civilian.getSex());
                    nextFieldState.placePerson(recruit, loc);
                    recruitsMade++;
                }
            }
        }
    }

    /**
     * Check if the soldier is active at the current time.
     * Spanish soldiers are not active during "siesta time" (2 PM to 4 PM).
     * 
     * @return True if the soldier is active, false otherwise.
     */
    private boolean isActiveTime() {
        int currentTime = Empire.getTime().getHour();
        return (7 <= currentTime && currentTime < 14) || (16 <= currentTime && currentTime < 20);
    } 
}
