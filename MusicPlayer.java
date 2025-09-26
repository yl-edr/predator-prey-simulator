import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * MusicPlayer class for playing audio tracks.
 * 
 * @author Rom Steinberg and Yaal Luka Edrey Gatignol
 *         --> This class was imported from the internet and modified by us.
 * @version 4.6
 */
public class MusicPlayer {
    private Clip clip; // The audio clip for the currently playing song.
    private String currentSong; // The name of the currently playing song.
    private HashMap<String, String> empireMusic = new HashMap<>(); // Map linking empires to their respective songs.
    private URL audioFileUrl; // The URL of the audio file to be loaded.
    
    /**
     * Constructor for the MusicPlayer class.
     * Initializes the empire-to-music mapping for all relevant empires.
     */
    public MusicPlayer() {
        // Map each empire to its corresponding music file.
        empireMusic.put("Amazonian", "AmazonianMusic.wav");
        empireMusic.put("British", "BritishMusic.wav");
        empireMusic.put("Spanish", "SpanishMusic.wav");
        empireMusic.put("Persian", "PersianMusic.wav");
        empireMusic.put("Roman", "RomanMusic.wav");
        empireMusic.put("Win", "WinMusic.wav");
        currentSong = ""; // No song is playing initially.
    }

    /**
     * Play the music based on the loop mode.
     */
    public void play() {
        if (clip != null) {
            clip.setFramePosition(0); // Start from the beginning
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop indefinitely
        } 
        else {
            System.out.println("Music clip is not available to play.");
        }
    }

    /**
     * Stop playing the music.
     */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        } else {
            System.out.println("No music is currently playing.");
        }
    }

    /**
     * Update the currently playing music based on the given empire.
     * If the requested music is already playing, no action is taken.
     * Otherwise, the current music is stopped, and the new track is loaded and played.
     * 
     * @param empire The name of the empire whose music should be played.
     */
    public void updateMusic(String empire) {
        // Avoid unnecessary updates if the requested song is already playing.
        if(currentSong.equals(empire)) {
            return;
        }

        stop(); // Stop any currently playing music.

        String fileName = empireMusic.get(empire); // Retrieve the music file for the given empire.
        if (fileName != null) {
            updateFile(fileName); // Load the new audio file.
            play(); // Start playing the new track in loop mode.
            currentSong = empire; // Update the current song to reflect the newly loaded track.
        } else {
            System.out.println("Empire music not found: " + empire);
        }
    }

    /**
     * Load a new audio file based on the provided file name.
     * This method replaces the currently loaded clip with the new clip.
     * 
     * @param songName The name of the song file to be loaded.
     */
    public void updateFile(String songName) {
        try {
            if (clip != null) {
                clip.close(); // Close existing clip
            }
            // Load the audio file as a resource
            audioFileUrl = MusicPlayer.class.getResource("/resources/" + songName);
            if (audioFileUrl == null) {
                System.out.println("Audio file not found!");
                return;
            }
            // Open the audio stream and prepare the clip
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFileUrl);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            System.out.println("Error loading audio file.");
        }
    }
    
}
