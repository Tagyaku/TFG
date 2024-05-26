package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

public class AudioManager {
    private static AudioManager instance;


    private Music currentMusic;
    private String currentMusicFilePath;
    private float musicVolume = 0.5f;
    private float soundVolume = 1.0f;
    private ObjectMap<String, Sound> soundCache; // Cache para almacenar los sonidos cargados

    private AudioManager() {
        soundCache = new ObjectMap<>();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playMusic(String filePath) {
        if (currentMusic != null && currentMusic.isPlaying() && filePath.equals(currentMusicFilePath)) {
            Gdx.app.log("AudioManager", "Music already playing: " + filePath);
            return; // Do not change the music if it's already playing
        }
        if (currentMusic != null) {
            Gdx.app.log("AudioManager", "Stopping and disposing current music.");
            currentMusic.stop();
            currentMusic.dispose();
        }
        Gdx.app.log("AudioManager", "Loading music: " + filePath);
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        currentMusic.setLooping(true);
        currentMusic.setVolume(musicVolume);
        Gdx.app.log("AudioManager", "Playing music: " + filePath);
        currentMusic.play();
        currentMusicFilePath = filePath;
    }

    public void stopMusic() {
        if (currentMusic != null) {
            Gdx.app.log("AudioManager", "Stopping music.");
            currentMusic.stop();
        }
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
    }


    public void playSound(String filePath) {
        Gdx.app.log("AudioManager", "Playing sound: " + filePath);
        Sound sound = soundCache.get(filePath);

        if (sound == null) {
            Gdx.app.log("AudioManager", "Loading sound: " + filePath);
            sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            soundCache.put(filePath, sound);
        }

        sound.play(soundVolume);
    }

    public void setSoundVolume(float volume) {
        soundVolume = volume;
    }

    public void loadSound(String filePath) {
        if (!soundCache.containsKey(filePath)) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            soundCache.put(filePath, sound);
        }
    }
    public Music getCurrentMusic() {
        return currentMusic;
    }

    public String getCurrentMusicFilePath() {
        return currentMusicFilePath;
    }
    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

}
