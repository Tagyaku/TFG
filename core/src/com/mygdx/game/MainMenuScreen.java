package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Json;

import java.util.List;

public class MainMenuScreen implements Screen {
    private final MyGdxGame game;
    private SpriteBatch batch;
    private BitmapFont font, dialogFont;
    private TextureAtlas atlas, backgroundAtlas;
    private TextureRegion boxTextureRegion, backgroundTexture;
    private Rectangle opcionesButtonBounds, cargarPartidaButtonBounds, nuevaPartidaButtonBounds;
    private Viewport viewport;
    private boolean opcionesButtonPressed, cargarPartidaButtonPressed, nuevaPartidaButtonPressed;
    private boolean isOptionsDialogVisible, isLoadDialogVisible;
    private OptionsDialog optionsDialog;
    private Dialog loadDialog;
    private float buttonPressDuration = 0.2f;
    private float elapsedTimeOpciones, elapsedTimeCargarPartida, elapsedTimeNuevaPartida;

    private Stage stage;
    private Skin skin;

    public MainMenuScreen(MyGdxGame game) {
        this.game = game;
        batch = new SpriteBatch();
        atlas = new TextureAtlas("images/GUI/GUI.atlas");
        backgroundAtlas = new TextureAtlas("images/intro_elden/Intro_BG.atlas");
        boxTextureRegion = atlas.findRegion("11 Border 01-0");
        backgroundTexture = backgroundAtlas.findRegion("lands_in_between");
        font = new BitmapFont();
    font.getData().setScale(1.5f);
        dialogFont = new BitmapFont();
        dialogFont.getData().setScale(1.0f);
        initButtons();
        viewport = new StretchViewport(800, 480);


        stage = new Stage(viewport, batch);
        skin = new Skin();
    skin.add("default-font", font);
        skin.add("dialog-font", dialogFont);
    skin.addRegions(atlas);

    // Añadir WindowStyle al skin
    Window.WindowStyle windowStyle = new Window.WindowStyle();
    windowStyle.background = new TextureRegionDrawable(boxTextureRegion);
    windowStyle.titleFont = skin.getFont("default-font");
    windowStyle.titleFontColor = Color.WHITE;
    skin.add("default", windowStyle);

        // Añadir LabelStyle al skin
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("dialog-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Registrar la textura para el Slider
        TextureRegion sliderRegion = atlas.findRegion("UI_Flat_Scrollbar");
        skin.add("UI_Flat_Scrollbar", sliderRegion);

        // Añadir SliderStyle al skin
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(sliderRegion);
        sliderStyle.knob = new TextureRegionDrawable(sliderRegion);
        skin.add("default-horizontal", sliderStyle);

        // Añadir TextButtonStyle al skin
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("dialog-font");
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        Gdx.input.setInputProcessor(stage);

        AudioManager.getInstance().playMusic("audio/music/intro/Battle-Dawn_loop.m4a");
        AudioManager.getInstance().setMusicVolume(0.5f);
        AudioManager.getInstance().setSoundVolume(2f);

        // Inicializar el diálogo de opciones
        optionsDialog = new OptionsDialog("", skin);
        optionsDialog.setSize(400, 300);  // Aumentar el tamaño del diálogo
        optionsDialog.setPosition(viewport.getWorldWidth() - 400, 0);  // Posicionar a la derecha
        isOptionsDialogVisible = false;

        // Inicializar el diálogo de carga
        loadDialog = new Dialog("Cargar Partida", skin);
        loadDialog.setSize(400, 300);  // Ajustar el tamaño del diálogo
        loadDialog.setPosition(0, 0);  // Posicionar a la izquierda
        isLoadDialogVisible = false;
    }

    private void initButtons() {
        float buttonWidth = 200;
        float buttonHeight = 60;
        float screenWidth = 800;
        float screenHeight = 480;

        float buttonX = (screenWidth - buttonWidth) / 2;
        float verticalSpacing = 20;

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

        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        drawButton(nuevaPartidaButtonBounds, "Nueva Partida", nuevaPartidaButtonPressed);
        drawButton(cargarPartidaButtonBounds, "Cargar Partida", cargarPartidaButtonPressed);
        drawButton(opcionesButtonBounds, "Opciones", opcionesButtonPressed);

        batch.end();

        stage.act(delta);
        stage.draw();

    handleInput();
        updateButtonState(delta);
    }

    private void drawButton(Rectangle bounds, String text, boolean pressed) {
        if (pressed) {
            batch.setColor(1, 1, 1, 0.5f);
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
            AudioManager.getInstance().playSound("audio/sound effects/confirm_style_5_001.wav");

                if (isOptionsDialogVisible) {
                    optionsDialog.remove();
                } else {
                stage.addActor(optionsDialog);
                }
                isOptionsDialogVisible = !isOptionsDialogVisible;
            } else if (cargarPartidaButtonBounds.contains(touchPos.x, touchPos.y)) {
                cargarPartidaButtonPressed = true;
            AudioManager.getInstance().playSound("audio/sound effects/confirm_style_5_001.wav");

                if (isLoadDialogVisible) {
                    loadDialog.remove();
                } else {
                showLoadDialog();
                    stage.addActor(loadDialog);
                }
                isLoadDialogVisible = !isLoadDialogVisible;
            } else if (nuevaPartidaButtonBounds.contains(touchPos.x, touchPos.y)) {
                nuevaPartidaButtonPressed = true;
            AudioManager.getInstance().playSound("audio/sound effects/confirm_style_5_001.wav");
                Player.getInstance(game).reset();
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

    private void showLoadDialog() {
        Table contentTable = loadDialog.getContentTable();
        contentTable.clear(); // Clear the table to avoid duplicating buttons
        List<SavedGame> savedGames = game.getSaveGameService().loadAllSavedGames();
        for (SavedGame savedGame : savedGames) {
            TextButton loadSlotButton = new TextButton(savedGame.getPlayerName() + " - " + "Progreso: " + getProgress(savedGame), skin);
            loadSlotButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    loadGame(savedGame);
                    loadDialog.hide();
                }
            });
            contentTable.add(loadSlotButton).row();
        }
    }

    private int getProgress(SavedGame savedGame) {
        return savedGame.getCurrentTextIndex(); // Use the current text index as a measure of progress
    }

    private void loadGame(SavedGame savedGame) {
        Player player = Player.getInstance(game);
        deserializeGameData(savedGame.getSaveData(), player);
        player.initialize(game); // Initialize the player with the game reference and atlas

    // Pass the current text index and combat index to the GameplayScreen
    game.setScreen(new GameplayScreen(game, savedGame.getCurrentTextIndex(), savedGame.getCurrentCombatIndex()));
    }

    private void deserializeGameData(String saveData, Player player) {
        Json json = new Json();
        Player loadedPlayer = json.fromJson(Player.class, saveData);
        player.copyFrom(loadedPlayer);
        player.initialize(game); // Initialize the player with the game reference and atlas

        // Inicializar y normalizar después de deserializar
        player.getInventory().initializePotions();
        player.getInventory().normalizePotions();
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        font.dispose();
        dialogFont.dispose();
        backgroundAtlas.dispose();
        stage.dispose();
        skin.dispose();
    }
}
