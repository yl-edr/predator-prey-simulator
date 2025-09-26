import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of an Amazonian soldier.
 * Amazonian soldiers age, move, reproduce, and die (Prey).
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg and Yaal Luka Edrey Gatignol
 * @version 4.6
 */
public class Amazonian extends Empire
{
    // The age at which an Amazonian soldier can start reproducing.
    private static final int GIVING_BIRTH_AGE = 10;
    // The maximum age an Amazonian soldier can live.
    private static final int MAX_AGE = 200;
    // The probability an Amazonian soldier will give birth.
    private static final double GIVING_BIRTH_PROBABILITY = 0.02;
    // The maximum number of births.
    private static final int MAX_CHILDREN = 1;
    // The maximum number of recruits an Amazonian soldier can produce in one step.
    private static final int MAX_RECRUITS = 2;
    // A shared random number generator for controlling breeding and movement decisions.
    private static final Random rand = Randomizer.getRandom();
    

    /**
     * Create an Amazonian soldier.
     * A soldier may be created with either a random or zero age and resource level.
     * 
     * @param randomAge If true, the soldier will be given a random age and resource level.
     * @param location The initial location within the field.
     */
    public Amazonian(boolean randomAge, Location location)
    {
        super(location, 'F');
        this.maxAge = MAX_AGE;
        this.givingBirthAge= GIVING_BIRTH_AGE;
        this.givingBirthProbability= GIVING_BIRTH_PROBABILITY;
        this.maxChildren= MAX_CHILDREN;
        if(randomAge) {
            this.age = rand.nextInt(maxAge);
        }
        else{
            this.age = 0;
        }
    }
    
    /**
     * Simulate the Amazonian soldier actions for one step.
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
            else{ 
                boolean recruit = false;
                List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
                // Recruiting civilians if nearby.
                if(civilianNearby(currentField)) {
                    recruit(currentField,nextFieldState);
                    recruit = true;
                    nextFieldState.placePerson(this, getLocation());
                }
                // Reproducing if possible and didnt recruit.
                else if(!freeLocations.isEmpty()) {
                    reproduce(nextFieldState, freeLocations);
                }
                if(!recruit) {
                    // Try to move into a free location, if not available die from overcrowding.                    
                    if(! freeLocations.isEmpty()) {
                        Location nextLocation = freeLocations.get(0);
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
     * Provide a string representation of the Amazonian soldier.
     */
    @Override
    public String toString() {
        return "Amazonian Soldier{" +
                "age = " + age +
                ", alive = " + isAlive() +
                ", location = " + getLocation() +
                '}';
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
        int births = giveBirth();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Amazonian youngRecruit = new Amazonian(false, loc);
                nextFieldState.placePerson(youngRecruit, loc);
            }
        }
    }

    /**
     * Attempt to recruit nearby civilians and convert them into Amazonian soldiers.
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
            if(person instanceof Civilian civilian && person.getSex() == 'F') {
                if(civilian.isAlive()) {
                    civilian.setDead();
                    Amazonian recruit = new Amazonian(false, loc);
                    nextFieldState.placePerson(recruit, loc);
                    recruitsMade++;
                }
            }
        }
    }

    /**
     * Check if the soldier is active at the current time.   
     *
     * @return True if the soldier is active, false otherwise.
     */
    private boolean isActiveTime(){
        int timeInEmpire = Empire.getTime().getHour();
        return timeInEmpire >= 6 && timeInEmpire < 23;
    }
        
}
