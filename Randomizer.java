import java.util.Random;

/**
 * Provide control over the randomization of the simulation. By using the shared, fixed-seed 
 * randomizer, repeated runs will perform exactly the same (which helps with testing). Set 
 * 'useShared' to false to get different random behaviour every time.
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg and Yaal Luka Edrey Gatignol
 * @version 7.0
 */
public class Randomizer
{
    // The default seed for control of randomization.
    private static final int SEED = 1111;
    // A shared Random object, if required.
    private static final Random rand = new Random(SEED);
    // Determine whether a shared random generator is to be provided.
    private static final boolean useShared = false;

    /**
     * Constructor for objects of class Randomizer
     */
    public Randomizer()
    {
    }

    /**
     * Provide a random generator.
     * @return A random object.
     */
    public static Random getRandom()
    {
        if(useShared) {
            return rand;
        }
        else {
            return new Random();
        }
    }
    
    /**
     * Reset the randomization.
     * This will have no effect if randomization is not 
     * through a shared Random generator.
     */
    public static void reset()
    {
        if(useShared) {
            rand.setSeed(SEED);
        }
    }

    /**
     * Assigns a random sex 'M' for Male 'F' for Female.
     * @return A char representing the sex.
     */
    public static char getRandomSex()
    {
        Random randSex = new Random();
        if(randSex.nextInt(2) == 0)
        {
            return 'M';
        }
        else
        {
            return 'F';
        }
    }
}
