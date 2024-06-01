package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class TextureManager implements Disposable {
    private AssetManager assetManager;
    public static final String ATLAS_MAIN = "images/TFG_Atlas_1.atlas";
    public static final String ATLAS_GUI = "images/GUI/GUI.atlas";
    public static final String ATLAS_BG = "images/Forest/Forest.atlas";
    public static final String ATLAS_ITEMS = "images/items/items.atlas";
    public static final String ATLAS_CAVERN = "images/cavern/cavern_BG.atlas";
    public static final String ATLAS_ENEMIES = "images/Enemies/Enemies.atlas";

    public TextureManager() {
        assetManager = new AssetManager();
        loadAssets();
    }

    private void loadAssets() {
        assetManager.load(ATLAS_MAIN, TextureAtlas.class);
        assetManager.load(ATLAS_GUI, TextureAtlas.class);
        assetManager.load(ATLAS_BG, TextureAtlas.class);
        assetManager.load(ATLAS_ITEMS, TextureAtlas.class);
        assetManager.load(ATLAS_CAVERN, TextureAtlas.class);
        assetManager.load(ATLAS_ENEMIES, TextureAtlas.class);
    }

    public void finishLoading() {
        assetManager.finishLoading();
    }

    public TextureAtlas getAtlas(String atlasName) {
        return assetManager.get(atlasName, TextureAtlas.class);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
