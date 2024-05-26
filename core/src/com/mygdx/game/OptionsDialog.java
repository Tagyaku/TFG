package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class OptionsDialog extends Dialog {
    public OptionsDialog(String title, Skin skin) {
        super(title, skin);

        Table contentTable = getContentTable();
        Label musicLabel = new Label("Volumen Musica", skin);
        Slider musicSlider = new Slider(0, 1, 0.1f, false, skin);
        musicSlider.setValue(AudioManager.getInstance().getMusicVolume());
        musicSlider.addListener(event -> {
            AudioManager.getInstance().setMusicVolume(musicSlider.getValue());
            return false;
        });

        Label soundLabel = new Label("Volumen Efectos", skin);
        Slider soundSlider = new Slider(0, 1, 0.1f, false, skin);
        soundSlider.setValue(AudioManager.getInstance().getSoundVolume());
        soundSlider.addListener(event -> {
            AudioManager.getInstance().setSoundVolume(soundSlider.getValue());
            return false;
        });

        contentTable.add(musicLabel).pad(10);
        contentTable.row();
        contentTable.add(musicSlider).width(200).pad(10);
        contentTable.row();
        contentTable.add(soundLabel).pad(10);
        contentTable.row();
        contentTable.add(soundSlider).width(200).pad(10);


    }

    @Override
    protected void result(Object object) {
        // Opciones adicionales cuando el diálogo se cierra
    }
}
