import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a British soldier.
 * British soldiers age, move, eat enemies, reproduce, and die (predator).
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg and Yaal Luka Edrey Gatignol
 * @version 4.6
 */
public class British extends Empire
{
    // The age at which a British soldier can start reproducing.
    private static final int GIVING_BIRTH_AGE = 40;
    // The maximum age a British soldier can live.
    private static final int MAX_AGE = 800;
    // The probability a British solldier will give birth.
    private static final double GIVING_BIRTH_PROBABILITY = 0.2;
    // The maximum number of recruits a British soldier can produce in one step.
    private int maxRecruits = 3;
    // The maximum number of births.
    private static final int MAX_CHILDREN = 2;
    // The resource level restored after finding an enemy.
    private static final int FULL_RESOURCE_VALUE = 50;
    // A shared random number generator for controlling breeding and movement decisions.
    private static final Random rand = Randomizer.getRandom();
    

    /**
     * Create a British soldier.
     * A soldier may be created with either a random or zero age and resource level.
     * 
     * @param randomAge If true, the soldier will be given a random age and resource level.
     * @param location The initial location within the field.
     */
    public British(boolean randomAge, Location location, char sex)
    {
        super(location, sex);
        this.maxAge = MAX_AGE;
        this.givingBirthAge= GIVING_BIRTH_AGE;
        this.givingBirthProbability= GIVING_BIRTH_PROBABILITY;
        this.maxChildren= MAX_CHILDREN;
        if(randomAge) {
            this.age = rand.nextInt(maxAge);
        }
        else {
            this.age = 0;
        }
        this.resourceLevel = rand.nextInt(FULL_RESOURCE_VALUE);
    }
    
    /**
     * Simulate the British soldier actions for one step.
     * If alive, the soldier may move, recruit, reproduce, or die.
     * Behavior is influenced by the weather and time of day.
     * 
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field for the next step.
     */
    public void conquer(Field currentField, Field nextFieldState)
    {
        int executions = 1;
        if(getCurrentWeatherCondition() == Weather.Condition.RAINY) {
            executions++; // British soldiers move twice in rainy weather.
        }
        
        incrementAge(); // Increase age and check for death due to aging.
        
        if(isAlive()) {
            if(!isActiveTime()) {
                nextFieldState.placePerson(this, getLocation()); // Soldiers remain static during inactive times.
            }
            else{
                incrementResources(); // Decrease resources and check for death due to starvation.
                Location nextLocation = null;

                for(int i=0; i<executions; i++){
                    boolean recruit = false;
                    List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
                    // Recruiting civilians if nearby.
                    if((civilianNearby(currentField))) {
                        recruit(currentField, nextFieldState);
                        recruit = true;
                        nextLocation = getLocation();
                    }
                    // Reproducing if possible and didnt recruit.
                    else if((!freeLocations.isEmpty() && mateNearby(nextFieldState))) {
                        reproduce(nextFieldState, freeLocations);
                    }
                    // Move towards an enemy if found.
                    if(!recruit) {
                        nextLocation = findEnemy(currentField);
                        if(nextLocation == null && ! freeLocations.isEmpty()) {
                            nextLocation = freeLocations.remove(0);
                        }
                        if(nextLocation != null) {
                            setLocation(nextLocation);
                        }
                    }
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

    /**
     * Provide a string representation of the British soldier.
     */
    @Override
    public String toString() {
        return "British Solider{" +
                "age = " + age +
                ", alive = " + isAlive() +
                ", location = " + getLocation() +
                ", resourceLevel = " + resourceLevel +
                '}';
    }

    
    /**
     * Look for an enemy soldier near to the current location.
     * If an enemy is found, it is killed, and the British soldier regains full resources.
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
            if(soldier instanceof Persian || soldier instanceof Roman) {
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
     * New soliders are placed in adjacent free locations.
     * 
     * @param nextFieldState The updated field for the next step.
     * @param freeLocations The available free locations.
     */
    protected void reproduce(Field nextFieldState, List<Location> freeLocations)
    {
        int children = giveBirth();
        if(children > 0) {
            for (int b = 0; b < children && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                British youngRecruit = new British(false, loc, Randomizer.getRandomSex());
                nextFieldState.placePerson(youngRecruit, loc);
            }
        }   
    }

    /**
     * Attempt to recruit nearby civilians and convert them into British soldiers.
     * 
     * @param nextFieldState The updated field for the next step.
     * @param freeLocations The available free locations for recruits.
     */
    protected void recruit(Field currentFieldState, Field nextFieldState)
    {
        List<Location> adjacent = currentFieldState.getAdjacentLocations(getLocation());
        int recruitsMade = 0;
        
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext() && recruitsMade < maxRecruits) {
            Location loc = it.next();
            Person person = currentFieldState.getPersonAt(loc);
            if(person instanceof Civilian civilian) {
                if(civilian.isAlive()) {
                    civilian.setDead();
                    British recruit = new British(false, loc, civilian.getSex());
                    nextFieldState.placePerson(recruit, loc);
                    recruitsMade++;
                }
            }
        }
    }
      
    /**
     * Check if the soldier is active at the current time.
     * British soldiers are not active during "tea time" (3 PM to 5 PM).
     * 
     * @return True if the soldier is active, false otherwise.
     */
    private boolean isActiveTime() {
        int currentTime = Empire.getTime().getHour();
        return (6 <= currentTime && currentTime < 15) || (17 <= currentTime && currentTime < 19);
    }
}
