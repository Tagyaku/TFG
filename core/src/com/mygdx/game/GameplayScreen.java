package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class GameplayScreen implements Screen {
    private MyGdxGame game;
    private SpriteBatch batch;
    private TextureAtlas atlas, guiAtlas, bgAtlas;
    private TextureRegion borderTexture, boxTexture;
    private TextureRegion currentBackground;
    private Rectangle lowerBorderArea, pauseButtonArea;
    private Stage stage;
    private TextButton pauseButton;
    private BitmapFont font;
    private BitmapFont textFont;
    private List<String> gameTexts;
    private int currentTextIndex = 0;
    private boolean isInCombat = false;
    private int[] combatPoints = {2, 5}; // Puntos donde ocurren los combates
    private int currentCombatIndex = 0;




    public GameplayScreen(MyGdxGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.guiAtlas = new TextureAtlas("images/GUI/GUI.atlas");
        this.boxTexture = guiAtlas.findRegion("11 Border 01-0");
        this.bgAtlas= new TextureAtlas("images/Forest/Forest.atlas");
        // Establecer el fondo inicial del atlas
        this.currentBackground = bgAtlas.findRegion("bg_f", 1); // Comenzar con el fondo inicial
        this.stage = new Stage(new ScreenViewport(), batch);
        this.font = new BitmapFont();
        this.textFont = new BitmapFont();
        textFont.getData().setScale(2);

        Gdx.input.setInputProcessor(stage);
        initUI();
        initText();


    }

    private void initUI() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Configuración del borde inferior
        float borderHeight = screenHeight * 0.30f; //30% de la altura de la pantalla ajustable
        float borderWidth = screenWidth - 20; // Ancho del borde con margen ajustable
        lowerBorderArea = new Rectangle(10, 10, borderWidth, borderHeight);

        // Configuración del botón de pausa
        float pauseButtonWidth = 200; // Ancho fijo y ajustable para el botón de pausa
        float pauseButtonHeight = 60; // Altura fija y ajustable para el botón de pausa
        pauseButtonArea = new Rectangle(10, screenHeight - pauseButtonHeight - 10, pauseButtonWidth, pauseButtonHeight);

        // Estilo del botón de pausa con dimensiones independientes
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(boxTexture, 0, 0, 65, 65)); // Usa los límites exactos de la textura
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(boxTexture, 0, 0, 65, 65)); // Repite para el estado presionado
        buttonStyle.font = font;

        font.getData().setScale(3);  // Escala de fuente ajustable según necesidades

        pauseButton = new TextButton("Pausa", buttonStyle);
        pauseButton.setBounds(pauseButtonArea.x, pauseButtonArea.y, pauseButtonArea.width, pauseButtonArea.height);
        stage.addActor(pauseButton);
    }

    private void initText() {
        textFont = new BitmapFont();
        textFont.getData().setScale(2); // Escala del texto
        gameTexts = new ArrayList<>();
        gameTexts.add("Primer mensaje que aparecerá en el juego.");
        gameTexts.add("Segundo mensaje, continúa la historia.");
        gameTexts.add("Preparando para el primer combate.");
        gameTexts.add("Mensaje después del primer combate.");
        gameTexts.add("Mensaje previo al segundo combate.");
        gameTexts.add("Final del juego después del último combate.");
    }
    private void handleInput() {
        if (Gdx.input.justTouched() && !isInCombat) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (lowerBorderArea.contains(x, y)) {
                currentTextIndex++;
                if (currentTextIndex >= gameTexts.size()) {
                currentTextIndex = 0; // Reiniciar si se alcanza el final de los textos
                }

                if (currentCombatIndex < combatPoints.length && currentTextIndex == combatPoints[currentCombatIndex]) {
                isInCombat = true; // Simula entrar en combate
                    startCombat();
            } else {
                    // Cambiar fondo tras cada punto de combate
                    if (currentCombatIndex < combatPoints.length && currentTextIndex > combatPoints[currentCombatIndex]) {
                        currentCombatIndex++;
                        currentBackground = atlas.findRegion("bg_f", currentCombatIndex); // Cambiar fondo
                    }
                }
            }
        }
    }

    private void startCombat() {
        if (isInCombat) {
            game.setScreen(new Combat(game)); // Cambia a la pantalla de combate
        }
    }

    // Suponiendo que tienes un método para finalizar el combate y regresar
    public void endCombat() {
        isInCombat = false;
        currentTextIndex++; // Avanzar al siguiente texto después del combate
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Draw the lower border

        batch.draw(currentBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(borderTexture, lowerBorderArea.x, lowerBorderArea.y, lowerBorderArea.width, lowerBorderArea.height);
        if (!gameTexts.isEmpty() && !isInCombat) {
            String currentText = gameTexts.get(currentTextIndex);
            textFont.draw(batch, currentText, lowerBorderArea.x + 20, lowerBorderArea.y + lowerBorderArea.height / 2 + 10, lowerBorderArea.width - 40, Align.center, true);
        }
        // Draw the pause button area (background)
        batch.draw(boxTexture, pauseButtonArea.x, pauseButtonArea.y, pauseButtonArea.width, pauseButtonArea.height);
        batch.end();

        stage.act(delta);
        stage.draw();
        handleInput();
    }

    @Override
    public void show() {}

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
        atlas.dispose();
        bgAtlas.dispose(); // Asegúrate de disponer todos los atlas usados
        stage.dispose();
    }

}
