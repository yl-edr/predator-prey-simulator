/**
 * Time class. This class represents the time in the game.
 * 
 * @author David J. Barnes, Michael Kölling, Rom Steinberg and Yaal Luka Edrey Gatignol
 * @version 4.6
 */
public class Time {
    private int hour;
    private int minute;
    
    public Time() {
        hour = 10;
        minute = 30;
    }

    /*
     * Increments the time. Every "step" in the simulation is equivelent to 5 minutes.
     */
    public void incrementTime() {
        minute = (minute + 5) % 60;
        if (minute == 0) {
            hour= (hour + 1) % 24;
        }
    }

    /**
     * @return An int hours.
     */
    public int getHour() {
        return hour;
    }
    
    /**
     * @return An int minutes.
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Provide a string representation of the current weather condition.
     * 
     * @return A string describing the current weather.
     */
    @Override
    public String toString() {
        return String.format(" %02d:%02d ", hour, minute);
    }
}
