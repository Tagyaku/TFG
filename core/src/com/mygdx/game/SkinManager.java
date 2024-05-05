package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SkinManager {
    private static TextureAtlas atlas;

    public static void loadResources() {
        atlas = new TextureAtlas(Gdx.files.internal("images/TFG_Atlas_1.atlas"));
    }

    public static TextureAtlas getAtlas() {
        return atlas;
    }
}
