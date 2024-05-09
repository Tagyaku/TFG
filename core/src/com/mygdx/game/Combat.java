package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Combat implements Screen {
    private MyGdxGame game;
    private Stage stage;
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private TextureRegion borderTexture;
    private Rectangle lowerBorderArea;
    private Player player;
    private Enemies enemy;
    private Button attackButton, defendButton, useItemButton, escapeButton;
    private BitmapFont font;

    public Combat(MyGdxGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.player = Player.getInstance();
        this.enemy = new Enemies("images/Enemies/Enemies.atlas");

        initUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void initUI() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float borderHeight = screenHeight * 0.30f;
        lowerBorderArea = new Rectangle(10, 10, screenWidth - 20, borderHeight);

    // Configuración del estilo de los botones
    font = new BitmapFont();  // Se podría utilizar una fuente más grande o personalizada
    font.getData().setScale(3);  // Aumentar el tamaño de la fuente
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = font;

        attackButton = new TextButton("Attack", buttonStyle);
        defendButton = new TextButton("Defend", buttonStyle);
        useItemButton = new TextButton("Use Item", buttonStyle);
        escapeButton = new TextButton("Escape", buttonStyle);

    // Ancho de los botones y espacio entre ellos
    float buttonWidth = (lowerBorderArea.width - 80) / 4; // Distribuir el ancho del área entre los botones, dejando espacio
    float buttonHeight = 50; // Altura fija para todos los botones
    float spacing = 13; // Espacio entre botones

    // Posición de los botones en horizontal
    attackButton.setSize(buttonWidth, buttonHeight);
    defendButton.setSize(buttonWidth, buttonHeight);
    useItemButton.setSize(buttonWidth, buttonHeight);
    escapeButton.setSize(buttonWidth, buttonHeight);

    attackButton.setPosition(lowerBorderArea.x + 10, lowerBorderArea.y + (lowerBorderArea.height - buttonHeight) / 2);
    defendButton.setPosition(attackButton.getX() + buttonWidth + spacing, lowerBorderArea.y + (lowerBorderArea.height - buttonHeight) / 2);
    useItemButton.setPosition(defendButton.getX() + buttonWidth + spacing, lowerBorderArea.y + (lowerBorderArea.height - buttonHeight) / 2);
    escapeButton.setPosition(useItemButton.getX() + buttonWidth + spacing, lowerBorderArea.y + (lowerBorderArea.height - buttonHeight) / 2);

    // Agregar botones al escenario
        stage.addActor(attackButton);
        stage.addActor(defendButton);
        stage.addActor(useItemButton);
        stage.addActor(escapeButton);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(borderTexture, lowerBorderArea.x, lowerBorderArea.y, lowerBorderArea.width, lowerBorderArea.height);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        atlas.dispose();
    }
}
