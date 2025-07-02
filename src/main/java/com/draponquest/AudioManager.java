package com.draponquest;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.util.HashMap;
import java.util.Map;

/**
 * Audio Manager for DraponQuest JavaFX
 * Handles sound effects and background music using JavaFX MediaPlayer and AudioClip
 * 
 * @author Modern Migration
 */
public class AudioManager {
    
    // Audio types
    public static final String SOUND_MOVE = "move";
    public static final String SOUND_BATTLE_START = "battle_start";
    public static final String SOUND_ATTACK = "attack";
    public static final String SOUND_DEFEND = "defend";
    public static final String SOUND_ESCAPE = "escape";
    public static final String SOUND_VICTORY = "victory";
    public static final String SOUND_DEFEAT = "defeat";
    public static final String SOUND_MENU_SELECT = "menu_select";
    public static final String SOUND_MENU_OPEN = "menu_open";
    public static final String SOUND_SAVE = "save";
    public static final String SOUND_LOAD = "load";
    public static final String SOUND_GAME_OVER = "game_over";
    
    // Background music tracks
    public static final String MUSIC_TITLE = "title";
    public static final String MUSIC_FIELD = "field";
    public static final String MUSIC_BATTLE = "battle";
    public static final String MUSIC_VICTORY = "victory_music";
    
    // Audio settings
    private double soundVolume = 0.7;
    private double musicVolume = 0.5;
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    
    // Audio storage
    private Map<String, AudioClip> soundEffects;
    private Map<String, MediaPlayer> backgroundMusic;
    private MediaPlayer currentMusic;
    private String currentMusicTrack;
    
    // Singleton instance
    private static AudioManager instance;
    
    /**
     * Private constructor for singleton pattern
     */
    private AudioManager() {
        soundEffects = new HashMap<>();
        backgroundMusic = new HashMap<>();
        initializeAudio();
    }
    
    /**
     * Get the singleton instance of AudioManager
     * @return AudioManager instance
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Initialize audio system and load audio resources
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
     * Load all sound effects
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
            
            System.out.println("Sound effects loaded: " + soundEffects.size() + " effects");
        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
        }
    }
    
    /**
     * Load a single sound effect
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
     * Load background music tracks
     */
    private void loadBackgroundMusic() {
        System.out.println("Loading background music...");
        
        try {
            // Load background music from resources
            loadBackgroundMusicTrack(MUSIC_FIELD, "/sounds/bgm_field.wav");
            loadBackgroundMusicTrack(MUSIC_BATTLE, "/sounds/bgm_battle.wav");
            
            System.out.println("Background music loaded: " + backgroundMusic.size() + " tracks");
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }
    
    /**
     * Load a single background music track
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
     * Play a sound effect
     * @param soundName Name of the sound effect
     */
    public void playSound(String soundName) {
        if (!soundEnabled) return;
        
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
     * Play background music
     * @param musicName Name of the music track
     */
    public void playMusic(String musicName) {
        if (!musicEnabled) return;
        
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
     * Stop current background music
     */
    public void stopMusic() {
        if (currentMusic != null && currentMusic.getStatus() == MediaPlayer.Status.PLAYING) {
            currentMusic.stop();
            System.out.println("Stopped music: " + currentMusicTrack);
        }
    }
    
    /**
     * Set sound effects volume (0.0 to 1.0)
     * @param volume Volume level
     */
    public void setSoundVolume(double volume) {
        this.soundVolume = Math.max(0.0, Math.min(1.0, volume));
        System.out.println("Sound volume set to: " + soundVolume);
    }
    
    /**
     * Set background music volume (0.0 to 1.0)
     * @param volume Volume level
     */
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
        System.out.println("Music volume set to: " + musicVolume);
    }
    
    /**
     * Enable or disable sound effects
     * @param enabled True to enable, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        System.out.println("Sound effects " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Enable or disable background music
     * @param enabled True to enable, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled && currentMusic != null && currentMusic.getStatus() == MediaPlayer.Status.PLAYING) {
            currentMusic.stop();
        }
        System.out.println("Background music " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Get current sound volume
     * @return Sound volume (0.0 to 1.0)
     */
    public double getSoundVolume() {
        return soundVolume;
    }
    
    /**
     * Get current music volume
     * @return Music volume (0.0 to 1.0)
     */
    public double getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Check if sound effects are enabled
     * @return True if enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Check if background music is enabled
     * @return True if enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    /**
     * Get current music track name
     * @return Current music track name or null if no music is playing
     */
    public String getCurrentMusicTrack() {
        return currentMusicTrack;
    }
    
    /**
     * Clean up audio resources
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