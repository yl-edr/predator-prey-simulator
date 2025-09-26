import java.util.Iterator;
import java.util.List;

/**
 * The Empire class represents a general empire in the simulation.
 * It extends the Person class and serves as a superclass for the soldier classes.
 *
 * @author David J. Barnes, Michael Kölling, Rom Steinberg, and Yaal Luka Edrey Gatignol
 * @version 7.0
 */
public abstract class Empire extends Person
{
    // The current time. Visible to everyone.
    private static Time time = new Time();
    // The current weather. Visible to everyone.
    private static Weather weather = Simulator.getWeather();
    protected int resourceLevel; // The British soldier's current resource level.

    /**
     * Constructor for an Empire.
     * An Empire has multiple soldiers (initiated in the subclasses).
     * 
     * @param location The initial location of the soldier.
     * @param sex The sex of the soldier within the empire ('M' or 'F').
     */
    public Empire(Location location, char sex)
    {
        super(location, sex);
    }
    
    /**
     * Defines the empire's main behavior for conquering.
     * This method is abstract and must be implemented in the subclasses.
     * 
     * @param currentField The current state of the field.
     * @param nextFieldState The updated state of the field for the next step.
     */
    abstract public void conquer(Field currentField, Field nextFieldState);
    
    /**
     * Get the current time instance for the simulation.
     * 
     * @return The current time in the simulation.
     */
    public static Time getTime() {
        return time;
    }

    /**
     * Get the current weather condition in the simulation.
     * 
     * @return The current weather condition as an enum value.
     */
    public Weather.Condition getCurrentWeatherCondition() {
        return weather.getCurrentCondition();
    }
    
    /**
     * Check if there is a civilian nearby.
     * This method scans adjacent locations to find any living civilians.
     * 
     * @param field The field currently occupied.
     * @return true if a living civilian is found nearby, false otherwise.
     */
    protected boolean civilianNearby(Field field)
    {
        // Get adjacent locations
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        // Check for civilians in adjacent locations
        while(it.hasNext()) {
            Location loc = it.next();
            Person person = field.getPersonAt(loc);
            if(person instanceof Civilian) {
                if(person.isAlive()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Decrease the soldiers resource level by one. If it reaches zero, the soldier dies.
     */
    protected void incrementResources()
    {
        resourceLevel--;
        if(resourceLevel <= 0) {
            setDead();
        }
    }

    /**
     * Try to reproduce.
     * New soldiers are placed in adjacent free locations.
     * 
     * @param nextFieldState The updated field for the next step.
     * @param freeLocations The available free locations.
     */
    abstract protected void reproduce(Field nextFieldState, List<Location> freeLocations);

    /**
     * Attempt to recruit nearby civilians and convert them into Roman soldiers.
     * 
     * @param nextFieldState The updated field for the next step.
     * @param freeLocations The available free locations for recruits.
     */
    abstract protected void recruit(Field currentFieldState, Field nextFieldState);
}
