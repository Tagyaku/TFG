package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {
    private final MyGdxGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private TextureAtlas atlas, backgroundAtlas;
    private TextureRegion boxTextureRegion, backgroundTexture;
    private Rectangle opcionesButtonBounds, cargarPartidaButtonBounds, nuevaPartidaButtonBounds;
    private Viewport viewport;
    private boolean opcionesButtonPressed, cargarPartidaButtonPressed, nuevaPartidaButtonPressed;
    private float buttonPressDuration = 0.2f; // Duración de la iluminación en segundos
    private float elapsedTimeOpciones, elapsedTimeCargarPartida, elapsedTimeNuevaPartida;

    public MainMenuScreen(MyGdxGame game) {
        this.game = game;
        batch = new SpriteBatch();
        atlas = new TextureAtlas("images/GUI/GUI.atlas");
        backgroundAtlas = new TextureAtlas("images/intro_elden/Intro_BG.atlas");
        boxTextureRegion = atlas.findRegion("11 Border 01-0");
        backgroundTexture = backgroundAtlas.findRegion("lands_in_between");
        font = new BitmapFont();
        font.getData().setScale(1.5f); // Ajusta el tamaño del texto para coincidir con el original
        initButtons();
        viewport = new StretchViewport(800, 480); // Ajusta según el tamaño deseado de la ventana
    }

    private void initButtons() {
        float buttonWidth = 200; // Mantén el tamaño original
        float buttonHeight = 60;
        float screenWidth = 800; // Usa el ancho del viewport
        float screenHeight = 480; // Usa el alto del viewport

        float buttonX = (screenWidth - buttonWidth) / 2;
        float verticalSpacing = 20; // Espacio vertical entre botones

        float nuevaPartidaButtonY = (screenHeight + buttonHeight + verticalSpacing) / 2;
        float cargarPartidaButtonY = nuevaPartidaButtonY - (buttonHeight + verticalSpacing);
        float opcionesButtonY = cargarPartidaButtonY - (buttonHeight + verticalSpacing);

        nuevaPartidaButtonBounds = new Rectangle(buttonX, nuevaPartidaButtonY, buttonWidth, buttonHeight);
        cargarPartidaButtonBounds = new Rectangle(buttonX, cargarPartidaButtonY, buttonWidth, buttonHeight);
        opcionesButtonBounds = new Rectangle(buttonX, opcionesButtonY, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Dibujar el fondo
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        drawButton(nuevaPartidaButtonBounds, "Nueva Partida", nuevaPartidaButtonPressed);
        drawButton(cargarPartidaButtonBounds, "Cargar Partida", cargarPartidaButtonPressed);
        drawButton(opcionesButtonBounds, "Opciones", opcionesButtonPressed);

        batch.end();

    handleInput();
    updateButtonState(delta);  // Asegúrate de llamar esto continuamente, no solo cuando un botón es presionado
    }

    private void drawButton(Rectangle bounds, String text, boolean pressed) {
        if (pressed) {
            batch.setColor(1, 1, 1, 0.5f); // Iluminación al pulsar
        }
        batch.draw(boxTextureRegion, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(1, 1, 1, 1);

        GlyphLayout layout = new GlyphLayout(font, text);
        float textX = bounds.x + (bounds.width - layout.width) / 2;
        float textY = bounds.y + (bounds.height + layout.height) / 2;
        font.draw(batch, layout, textX, textY);
    }

private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPos);

            if (opcionesButtonBounds.contains(touchPos.x, touchPos.y)) {
                opcionesButtonPressed = true;
            } else if (cargarPartidaButtonBounds.contains(touchPos.x, touchPos.y)) {
                cargarPartidaButtonPressed = true;
            } else if (nuevaPartidaButtonBounds.contains(touchPos.x, touchPos.y)) {
                nuevaPartidaButtonPressed = true;
                game.setScreen(new InitialScreen(game));
            }
        }
    }

private void updateButtonState(float delta) {
    if (opcionesButtonPressed) {
                elapsedTimeOpciones += delta;
                if (elapsedTimeOpciones >= buttonPressDuration) {
                    opcionesButtonPressed = false;
            elapsedTimeOpciones = 0;
                }
    }
    if (cargarPartidaButtonPressed) {
                elapsedTimeCargarPartida += delta;
                if (elapsedTimeCargarPartida >= buttonPressDuration) {
                    cargarPartidaButtonPressed = false;
                    elapsedTimeCargarPartida = 0;
                }
    }
    if (nuevaPartidaButtonPressed) {
                elapsedTimeNuevaPartida += delta;
                if (elapsedTimeNuevaPartida >= buttonPressDuration) {
                    nuevaPartidaButtonPressed = false;
                    elapsedTimeNuevaPartida = 0;
                }
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        font.dispose();
    }
}
