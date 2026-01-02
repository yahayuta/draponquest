package com.draponquest;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.util.HashMap;
import java.util.Map;

/**
 * Audio Manager for DraponQuest JavaFX.
 * Handles sound effects and background music using JavaFX MediaPlayer and AudioClip.
 *
 * @author Modern Migration
 * @author Yakkun (Original concept for sound management)
 */
public class AudioManager {

    // Audio types
    /** Sound effect for player movement. */
    public static final String SOUND_MOVE = "move";
    /** Sound effect for battle initiation. */
    public static final String SOUND_BATTLE_START = "battle_start";
    /** Sound effect for an attack. */
    public static final String SOUND_ATTACK = "attack";
    /** Sound effect for defending. */
    public static final String SOUND_DEFEND = "defend";
    /** Sound effect for escaping from battle. */
    public static final String SOUND_ESCAPE = "escape";
    /** Sound effect for winning a battle. */
    public static final String SOUND_VICTORY = "victory";
    /** Sound effect for player defeat. */
    public static final String SOUND_DEFEAT = "defeat";
    /** Sound effect for selecting a menu item. */
    public static final String SOUND_MENU_SELECT = "menu_select";
    /** Sound effect for opening a menu. */
    public static final String SOUND_MENU_OPEN = "menu_open";
    /** Sound effect for saving the game. */
    public static final String SOUND_SAVE = "save";
    /** Sound effect for loading the game. */
    public static final String SOUND_LOAD = "load";
    /** Sound effect for game over. */
    public static final String SOUND_GAME_OVER = "game_over";
    public static final String SOUND_HEAL = "heal";

    // Background music tracks
    /** Background music for the title screen. */
    public static final String MUSIC_TITLE = "title";
    /** Background music for the overworld field. */
    public static final String MUSIC_FIELD = "field";
    /** Background music for battle encounters. */
    public static final String MUSIC_BATTLE = "battle";
    /** Background music for battle victory. */
    public static final String MUSIC_VICTORY = "victory_music";
    /** Background music for towns. */
    public static final String MUSIC_TOWN = "town";
    /** Background music for castles. */
    public static final String MUSIC_CASTLE = "castle";
    /** Background music for caves. */
    public static final String MUSIC_CAVE = "cave";

    // Audio settings
    /**
     * The current volume level for sound effects (0.0 to 1.0).
     */
    private double soundVolume = 0.7;
    /**
     * The current volume level for background music (0.0 to 1.0).
     */
    private double musicVolume = 0.5;
    /**
     * Flag indicating whether sound effects are currently enabled.
     */
    private boolean soundEnabled = true;
    /**
     * Flag indicating whether background music is currently enabled.
     */
    private boolean musicEnabled = true;

    // Audio storage
    /**
     * A map storing loaded sound effect clips, accessible by their names.
     */
    private Map<String, AudioClip> soundEffects;
    /**
     * A map storing loaded background music tracks, accessible by their names.
     */
    private Map<String, MediaPlayer> backgroundMusic;
    /**
     * The MediaPlayer instance for the currently playing background music.
     */
    private MediaPlayer currentMusic;
    /**
     * The name of the currently playing background music track.
     */
    private String currentMusicTrack;

    // Singleton instance
    /**
     * The singleton instance of the AudioManager.
     */
    private static AudioManager instance;

    /**
     * Private constructor to enforce the singleton pattern.
     * Initializes the audio maps and loads all audio resources.
     */
    private AudioManager() {
        soundEffects = new HashMap<>();
        backgroundMusic = new HashMap<>();
        initializeAudio();
    }

    /**
     * Returns the singleton instance of the AudioManager.
     * If the instance does not exist, it is created.
     * @return The single instance of AudioManager.
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Initializes the audio system by loading all sound effects and background music tracks.
     */
    private void initializeAudio() {
        System.out.println("Initializing AudioManager...");

        // Load sound effects
        loadSoundEffects();

        // Load background music
        loadBackgroundMusic();

        System.out.println("AudioManager initialized successfully");
    }

    /**
     * Loads all predefined sound effects from their resource paths into the {@code soundEffects} map.
     */
    private void loadSoundEffects() {
        System.out.println("Loading sound effects...");

        try {
            // Load sound effects from resources
            loadSoundEffect(SOUND_MOVE, "/sounds/move.wav");
            loadSoundEffect(SOUND_BATTLE_START, "/sounds/battle_start.wav");
            loadSoundEffect(SOUND_ATTACK, "/sounds/attack.wav");
            loadSoundEffect(SOUND_DEFEND, "/sounds/defend.wav");
            loadSoundEffect(SOUND_ESCAPE, "/sounds/escape.wav");
            loadSoundEffect(SOUND_VICTORY, "/sounds/victory.wav");
            loadSoundEffect(SOUND_DEFEAT, "/sounds/defeat.wav");
            loadSoundEffect(SOUND_MENU_SELECT, "/sounds/menu_select.wav");
            loadSoundEffect(SOUND_SAVE, "/sounds/save.wav");
            loadSoundEffect(SOUND_LOAD, "/sounds/load.wav");
            // Add missing generated sound files
            loadSoundEffect(SOUND_GAME_OVER, "/sounds/game_over.wav");
            loadSoundEffect(SOUND_HEAL, "/sounds/heal.wav");

            System.out.println("Sound effects loaded: " + soundEffects.size() + " effects");
        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
        }
    }

    /**
     * Loads a single sound effect from the specified resource path and stores it in the {@code soundEffects} map.
     * @param name The unique name to associate with this sound effect.
     * @param resourcePath The path to the sound file within the project resources (e.g., "/sounds/move.wav").
     */
    private void loadSoundEffect(String name, String resourcePath) {
        try {
            String url = getClass().getResource(resourcePath).toExternalForm();
            AudioClip clip = new AudioClip(url);
            soundEffects.put(name, clip);
            System.out.println("Loaded sound effect: " + name);
        } catch (Exception e) {
            System.err.println("Failed to load sound effect " + name + ": " + e.getMessage());
        }
    }

    /**
     * Loads all predefined background music tracks from their resource paths into the {@code backgroundMusic} map.
     */
    private void loadBackgroundMusic() {
        System.out.println("Loading background music...");

        try {
            // Load background music from resources
            loadBackgroundMusicTrack(MUSIC_FIELD, "/sounds/bgm_field.wav");
            loadBackgroundMusicTrack(MUSIC_BATTLE, "/sounds/bgm_battle.wav");
            // Add missing generated music files
            loadBackgroundMusicTrack(MUSIC_TITLE, "/sounds/title.wav");
            loadBackgroundMusicTrack(MUSIC_VICTORY, "/sounds/victory_music.wav");
            loadBackgroundMusicTrack(MUSIC_TOWN, "/sounds/bgm_town.wav");
            loadBackgroundMusicTrack(MUSIC_CASTLE, "/sounds/bgm_castle.wav");
            loadBackgroundMusicTrack(MUSIC_CAVE, "/sounds/bgm_cave.wav");

            System.out.println("Background music loaded: " + backgroundMusic.size() + " tracks");
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    /**
     * Loads a single background music track from the specified resource path and stores it in the {@code backgroundMusic} map.
     * The track is configured to loop indefinitely.
     * @param name The unique name to associate with this music track.
     * @param resourcePath The path to the music file within the project resources (e.g., "/sounds/bgm_field.wav").
     */
    private void loadBackgroundMusicTrack(String name, String resourcePath) {
        try {
            String url = getClass().getResource(resourcePath).toExternalForm();
            Media media = new Media(url);
            MediaPlayer player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
            backgroundMusic.put(name, player);
            System.out.println("Loaded background music: " + name);
        } catch (Exception e) {
            System.err.println("Failed to load background music " + name + ": " + e.getMessage());
        }
    }

    /**
     * Plays a sound effect by its registered name.
     * The sound will only play if sound effects are enabled.
     * @param soundName The name of the sound effect to play.
     */
    public void playSound(String soundName) {
        if (!soundEnabled)
            return;

        AudioClip clip = soundEffects.get(soundName);
        if (clip != null) {
            clip.setVolume(soundVolume);
            clip.play();
            System.out.println("Playing sound: " + soundName);
        } else {
            System.out.println("Sound effect not found: " + soundName + " (placeholder)");
        }
    }

    /**
     * Plays a background music track by its registered name.
     * Stops any currently playing music before starting the new track.
     * Music will only play if background music is enabled.
     * @param musicName The name of the music track to play.
     */
    public void playMusic(String musicName) {
        if (!musicEnabled)
            return;

        // Stop current music if playing
        if (currentMusic != null && currentMusic.getStatus() == MediaPlayer.Status.PLAYING) {
            currentMusic.stop();
        }

        MediaPlayer player = backgroundMusic.get(musicName);
        if (player != null) {
            currentMusic = player;
            currentMusicTrack = musicName;
            currentMusic.setVolume(musicVolume);
            currentMusic.play();
            System.out.println("Playing music: " + musicName);
        } else {
            System.out.println("Background music not found: " + musicName + " (placeholder)");
        }
    }

    /**
     * Stops the currently playing background music track.
     */
    public void stopMusic() {
        if (currentMusic != null && currentMusic.getStatus() == MediaPlayer.Status.PLAYING) {
            currentMusic.stop();
            System.out.println("Stopped music: " + currentMusicTrack);
        }
    }

    /**
     * Sets the volume level for all sound effects. The volume is clamped between 0.0 and 1.0.
     * @param volume The desired volume level (0.0 to 1.0).
     */
    public void setSoundVolume(double volume) {
        this.soundVolume = Math.max(0.0, Math.min(1.0, volume));
        System.out.println("Sound volume set to: " + soundVolume);
    }

    /**
     * Sets the volume level for background music. The volume is clamped between 0.0 and 1.0.
     * If music is currently playing, its volume is updated immediately.
     * @param volume The desired volume level (0.0 to 1.0).
     */
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
        System.out.println("Music volume set to: " + musicVolume);
    }

    /**
     * Enables or disables sound effects globally.
     * @param enabled True to enable sound effects, false to disable.
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        System.out.println("Sound effects " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Enables or disables background music globally.
     * If music is disabled while playing, the current track will be stopped.
     * @param enabled True to enable background music, false to disable.
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled && currentMusic != null && currentMusic.getStatus() == MediaPlayer.Status.PLAYING) {
            currentMusic.stop();
        }
        System.out.println("Background music " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Returns the current volume level for sound effects.
     * @return The sound effects volume (0.0 to 1.0).
     */
    public double getSoundVolume() {
        return soundVolume;
    }

    /**
     * Returns the current volume level for background music.
     * @return The music volume (0.0 to 1.0).
     */
    public double getMusicVolume() {
        return musicVolume;
    }

    /**
     * Checks if sound effects are currently enabled.
     * @return True if sound effects are enabled, false otherwise.
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Checks if background music is currently enabled.
     * @return True if background music is enabled, false otherwise.
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Returns the name of the music track that is currently playing.
     * @return The name of the current music track, or {@code null} if no music is playing.
     */
    public String getCurrentMusicTrack() {
        return currentMusicTrack;
    }

    /**
     * Cleans up and disposes of all audio resources, stopping any playing media
     * and clearing internal collections. This should be called when the application is shutting down.
     */
    public void cleanup() {
        System.out.println("Cleaning up AudioManager...");

        // Stop and dispose of all media players
        for (MediaPlayer player : backgroundMusic.values()) {
            if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                player.stop();
            }
            player.dispose();
        }

        // Clear collections
        soundEffects.clear();
        backgroundMusic.clear();
        currentMusic = null;
        currentMusicTrack = null;

        System.out.println("AudioManager cleanup completed");
    }
}