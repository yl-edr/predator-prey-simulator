import java.util.Random;

/**
 * The Weather class represents the weather conditions in the simulation.
 * The weather changes randomly and affects (or not) each empire differently.
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg, and Yaal Luka Edrey Gatignol
 * @version 4.6
 */
public class Weather {
    // The possible weather conditions.
    public enum Condition { SUNNY, RAINY, FOGGY, SNOWY, MODERATE }
    
    // The current weather condition.
    private Condition currentCondition;
    // Shared random number generator to determine weather changes.
    private static final Random rand = new Random();

    /**
     * Constructor for the Weather class.
     * Initializes the weather to a moderate condition at the start of the simulation.
     */
    public Weather() {
        currentCondition = Condition.MODERATE;
    }

    /**
     * Get the current weather condition.
     * 
     * @return The current weather condition.
     */
    public Condition getCurrentCondition() {
        return currentCondition;
    }

    /**
     * Randomly change the weather to a new condition.
     * The new condition is selected randomly from the available weather types.
     */
    public void changeWeather() {
        Condition newCondition = Condition.values()[rand.nextInt(Condition.values().length)];
        currentCondition = newCondition;
    }
    
    /**
     * Provide a string representation of the current weather condition.
     * 
     * @return A string describing the current weather.
     */
    @Override
    public String toString() {
        return "Current weather: " + currentCondition;
    }
}
