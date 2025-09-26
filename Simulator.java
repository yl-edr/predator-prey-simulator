import java.util.*;

/**
 * A simulation of an empire-based ecosystem with multiple characters (British, Amazonians, Persians, Romans, Spanish, and Civilians).
 * The simulation is run on a rectangular field with dynamic weather and time changes.
 * Entities can move, reproduce, and interact based on specific conditions.
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg, and Yaal Luka Edrey Gatignol
 * @version 7.1
 */
public class Simulator
{
    // Default width of the simulation field.
    private static final int DEFAULT_WIDTH = 120;
    // Default depth of the simulation field.
    private static final int DEFAULT_DEPTH = 80;
    // Creation probabilities for different characters in the simulation.
    private static final double BRITISH_CREATION_PROBABILITY = 0.01;
    private static final double AMAZONIAN_CREATION_PROBABILITY = 0.03;    
    private static final double ROMAN_CREATION_PROBABILITY = 0.01;
    private static final double PERSIAN_CREATION_PROBABILITY = 0.04; 
    private static final double SPANISH_CREATION_PROBABILITY = 0.01;
    private static final double CIVILIAN_CREATION_PROBABILITY = 0.1;
    private static final double PROBABILITY_TO_MOVE_WHEN_SNOWY = 0.5;

    // The current state of the field.
    private Field field;
    // The current step in the simulation.
    private int step;
    // The graphical view of the simulation.
    private final SimulatorView view;
    // The current time of the simulation.
    private Time time;
    // The current weather of the simulation.
    private static Weather weather = new Weather();
    // The music player to change the soundtrack dynamically during the simulation.
    private MusicPlayer musicPlayer;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        time = Empire.getTime();
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);
        musicPlayer = new MusicPlayer();
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (2000 steps).
     */
    public void runLongSimulation()
    {
        simulate(2000);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each soldier and civilan.
     */
    public void simulateOneStep() {
        step++;
    
        // Change the weather every 15 steps.
        if (step % 15 == 0) {
            weather.changeWeather();
        }
    
        // Create the next field state.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());
        Random rand = Randomizer.getRandom();
        List<Person> people = field.getPeople();
    
        // Check if the weather is snowy to reduce redundant checks.
        boolean isSnowy = weather.getCurrentCondition() == Weather.Condition.SNOWY;
    
        // Process each person in the field.
        for (Person aPerson : people) {
            boolean shouldMove = !isSnowy || rand.nextDouble() <= PROBABILITY_TO_MOVE_WHEN_SNOWY;
            if (shouldMove) {
                movePerson(aPerson, field, nextFieldState);
            }
            else {
                nextFieldState.placePerson(aPerson, aPerson.getLocation());
            }
        }
    
        // Repopulate civilians if none exist in the field.
        if (!field.existCivilian()) {
            field.repopulateCivilians(nextFieldState);
        }
    
        // Update the field state.
        field = nextFieldState;
    
        // Update stats and music.
        reportStats();
        updateMusic();
        view.showStatus(step, weather, time, field);
    
        // Display time and weather.
        System.out.println(time);
        System.out.println(weather);
    
        // Increase the time in the simulation.
        time.incrementTime();
    }

    /**
     * Move a person on the field to their next position, handling their respective actions.
     * 
     * @param person The person to be moved or updated.
     * @param currentField The current field state.
     * @param nextFieldState The next state of the field.
     */
    private void movePerson(Person person, Field currentField, Field nextFieldState) {
        if (person instanceof Empire empire) {
            empire.conquer(currentField, nextFieldState);
        } else if (person instanceof Civilian civilian) {
            civilian.move(currentField, nextFieldState);
        }
    }

    /**
     * Update the music based on the largest empire and play a "Win" track if the simulation ends.
     */
    public void updateMusic(){
        HashMap<String, Integer> stats = field.getFieldStats();
        stats.remove("Civilian");
        String biggestEmpire = "";
        int biggestEmpireSize = 0;
        // Determine the largest empire by population size.
        for (String empire : stats.keySet()) {
            if (stats.get(empire) > biggestEmpireSize) {
                biggestEmpire = empire;
                biggestEmpireSize = stats.get(empire);
            }
        }
        musicPlayer.updateMusic(biggestEmpire);
        // Play "Win" music if the field is no longer viable.
        if(!field.isViable()){
            musicPlayer.updateMusic("Win");
        }
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, weather, time, field);
    }
    
    /**
     * Randomly populate the field with soldiers and civilians.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= BRITISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    British british = new British(true, location, Randomizer.getRandomSex());
                    field.placePerson(british, location);
                }
                else if(rand.nextDouble() <= ROMAN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Roman roman = new Roman(true, location);
                    field.placePerson(roman, location);
                }
                else if(rand.nextDouble() <= PERSIAN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Persian persian = new Persian(true, location, Randomizer.getRandomSex());
                    field.placePerson(persian, location);
                }
                else if(rand.nextDouble() <= SPANISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Spanish spanish = new Spanish(true, location, Randomizer.getRandomSex());
                    field.placePerson(spanish, location);
                }
                else if(rand.nextDouble() <= CIVILIAN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Civilian civilian = new Civilian(true, location, Randomizer.getRandomSex());
                    field.placePerson(civilian, location);
                }
                else if(rand.nextDouble() <= AMAZONIAN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Amazonian amazonian = new Amazonian(true, location);
                    field.placePerson(amazonian, location);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Report on the number of each type of entity in the field.
     */
    public void reportStats()
    {
        field.printFieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }

    /**
     * Retrieve the current weather condition in the simulation.
     * 
     * @return The current Weather object.
     */
    public static Weather getWeather() {
        return weather;
    }
}
