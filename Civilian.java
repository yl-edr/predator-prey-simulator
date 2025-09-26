import java.util.List;
import java.util.Random;

/**
 * Civilian class. This class is responsible for the behavior of the civilians in the simulation.
 * 
 * @author Rom Steinberg and Yaal Luka Edrey Gatignol
 * @version 7.1
 */
public class Civilian extends Person 
{   
    // The maximum age a Civilian can live.
    private static final int MAX_AGE = 100;
    // The age at which a Civilian can start reproducing.
    private static final int GIVING_BIRTH_AGE = 20;
    // The probability a Civilian will give birth.
    private static final double GIVING_BIRTH_PROBABILITY = 0.05;
    // The maximum number of births.
    private static final int MAX_CHILDREN = 4;
    // A shared random number generator for controlling breeding and movement decisions.
    private static final Random rand = Randomizer.getRandom();

    /**
    * Create a Civilian.
    * 
    * @param randomAge If true, the Civilian will be given a random age and resource level.
    * @param location The initial location within the field.
    */
    public Civilian(boolean randomAge, Location location, char sex) {
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
    }

    /**
     * Simulate the Civilian actions for one step.
     * If alive, the Civilian may move, reproduce, or die.
     * 
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field for the next step.
     */
    public void move(Field currentField, Field nextFieldState)
    {
        incrementAge(); // Increase age and check for death due to aging.

        if(isAlive()) {
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
            // Reproducing if possible.
            if(!freeLocations.isEmpty() && mateNearby(nextFieldState)) {
                reproduce(nextFieldState, freeLocations);
            }
            // Try to move into a free location, if not available die from overcrowding.                    
            if(!freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placePerson(this, nextLocation);
            }
            else {
                setDead();
            }
        }
    }

    /**
     * Provide a string representation of the Civilian.
     */
    @Override
    public String toString() {
        return "Civilian{" +
                "age = " + age +
                ", alive = " + isAlive() +
                ", location = " + getLocation() +
                '}';
    }

    /**
     * Try to reproduce.
     * New Civilians are placed in adjacent free locations.
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
                Civilian baby = new Civilian(false, loc, Randomizer.getRandomSex());
                nextFieldState.placePerson(baby, loc);
            }
        }
    }
        
}
