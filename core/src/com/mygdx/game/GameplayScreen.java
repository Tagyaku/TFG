package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class GameplayScreen implements Screen {
    private MyGdxGame game;
    private SpriteBatch batch;
    private TextureAtlas atlas, guiAtlas, bgAtlas, itemAtlas; // Agregar itemAtlas
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
    private Dialog pauseDialog, inventoryDialog, statusDialog;
    private boolean isPaused = false;
    private Skin skin;

    public GameplayScreen(MyGdxGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.guiAtlas = new TextureAtlas("images/GUI/GUI.atlas");
        this.bgAtlas = new TextureAtlas("images/Forest/Forest.atlas");
        this.itemAtlas = new TextureAtlas("images/items/items.atlas"); // Inicializar itemAtlas
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.boxTexture = guiAtlas.findRegion("11 Border 01-0");
        // Establecer el fondo inicial del atlas
        this.currentBackground = bgAtlas.findRegion("bg_f", 1); // Comenzar con el fondo inicial
        this.stage = new Stage(new ScreenViewport(), batch);
        this.font = new BitmapFont();
        this.textFont = new BitmapFont();
        textFont.getData().setScale(2);

        Gdx.input.setInputProcessor(stage);
        initUI();
        initText();
        initSkin();
    }

    private void initSkin() {
        skin = new Skin();
        skin.add("default-font", font);
        skin.addRegions(atlas);

        // Create the TextButton style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = new TextureRegionDrawable(borderTexture);
        textButtonStyle.down = new TextureRegionDrawable(borderTexture);
        textButtonStyle.font = skin.getFont("default-font");
        skin.add("default", textButtonStyle);

        // Create the Label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        skin.add("default", labelStyle);

        // Create the Window style
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(borderTexture);
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.stageBackground = new TextureRegionDrawable(borderTexture);
        windowStyle.titleFontColor = Color.WHITE; // Proporcionar un color por defecto
        skin.add("default", windowStyle);
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
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isPaused) {
                    isPaused = true;
                    showPauseDialog();
                }
            }
        });
        stage.addActor(pauseButton);
    }

    private void showPauseDialog() {
        if (pauseDialog == null) {
            // Create the dialog
            pauseDialog = new Dialog("Pausa", skin) {
                @Override
                protected void result(Object object) {
                    isPaused = false;
                }
            };

            // Add buttons to the dialog
            TextButton inventoryButton = new TextButton("Inventario", skin);
            TextButton equipmentButton = new TextButton("Equipo", skin);
            TextButton statusButton = new TextButton("Estado", skin);
            TextButton saveButton = new TextButton("Guardar", skin);
            TextButton exitButton = new TextButton("Salir", skin);

            // Set button sizes
            float buttonWidth = 200;
            float buttonHeight = 50;
            inventoryButton.setSize(buttonWidth, buttonHeight);
            equipmentButton.setSize(buttonWidth, buttonHeight);
            statusButton.setSize(buttonWidth, buttonHeight);
            saveButton.setSize(buttonWidth, buttonHeight);
            exitButton.setSize(buttonWidth, buttonHeight);

            // Add buttons to the dialog
            pauseDialog.getContentTable().add(inventoryButton).width(buttonWidth).height(buttonHeight).pad(5);
            pauseDialog.getContentTable().row();
            pauseDialog.getContentTable().add(equipmentButton).width(buttonWidth).height(buttonHeight).pad(5);
            pauseDialog.getContentTable().row();
            pauseDialog.getContentTable().add(statusButton).width(buttonWidth).height(buttonHeight).pad(5);
            pauseDialog.getContentTable().row();
            pauseDialog.getContentTable().add(saveButton).width(buttonWidth).height(buttonHeight).pad(5);
            pauseDialog.getContentTable().row();
            pauseDialog.getContentTable().add(exitButton).width(buttonWidth).height(buttonHeight).pad(5);

            pauseDialog.pack();

            // Set the position of the pause dialog
            pauseDialog.setPosition(10, Gdx.graphics.getHeight() - pauseDialog.getHeight() - 50);

            // Add listeners for inventory button
            inventoryButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showInventoryDialog();
                }
            });

            statusButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showStatusDialog();
                }
            });
        }
        pauseDialog.show(stage);

        // Set a listener to detect clicks outside the dialog
        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isPaused) {
                    float dialogX = pauseDialog.getX();
                    float dialogY = pauseDialog.getY();
                    float dialogWidth = pauseDialog.getWidth();
                    float dialogHeight = pauseDialog.getHeight();
                    if (x < dialogX || x > dialogX + dialogWidth || y < dialogY || y > dialogY + dialogHeight) {
                        pauseDialog.hide();
                        isPaused = false;
                        stage.removeListener(this); // Remove the listener when the dialog is closed
                    }
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    private void showInventoryDialog() {
        if (inventoryDialog == null) {
            inventoryDialog = new Dialog("Inventario", skin) {
                @Override
                protected void result(Object object) {
                    // Handle the result here
                }
            };

            Table inventoryTable = new Table(skin);
            Inventory playerInventory = Player.getInstance().getInventory();

            // Create inventory grid
            int itemsPerRow = 4;
            int currentColumn = 0;
            for (Equipment item : playerInventory.getEquipment()) {
                TextureRegion itemTexture = item.getTexture();
                if (itemTexture != null) {
                    TextButton itemButton = new TextButton("", new TextButton.TextButtonStyle(new TextureRegionDrawable(itemTexture), null, null, font));
                    inventoryTable.add(itemButton).size(50).pad(5);

                    currentColumn++;
                    if (currentColumn >= itemsPerRow) {
                        inventoryTable.row();
                        currentColumn = 0;
                    }
                }
            }

            for (Potion.PotionType potionType : Potion.PotionType.values()) {
                int quantity = playerInventory.getPotionQuantity(potionType);
                if (quantity > 0) {
                    TextureRegion potionTexture = null;
                    switch (potionType) {
                        case HEAL_30:
                            potionTexture = itemAtlas.findRegion("potion30");
                            break;
                        case HEAL_100:
                            potionTexture = itemAtlas.findRegion("potion100");
                            break;
                    }
                    if (potionTexture != null) {
                        TextButton potionButton = new TextButton(quantity + "x", new TextButton.TextButtonStyle(new TextureRegionDrawable(potionTexture), null, null, font));
                        inventoryTable.add(potionButton).size(50).pad(5);

                        currentColumn++;
                        if (currentColumn >= itemsPerRow) {
                            inventoryTable.row();
                            currentColumn = 0;
                        }
                    }
                }
            }

            inventoryDialog.getContentTable().add(inventoryTable).pad(10);
            inventoryDialog.pack();

            // Set the position of the inventory dialog
            inventoryDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());
        }
        inventoryDialog.show(stage);

        // Set a listener to detect clicks outside the inventory dialog
        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float dialogX = inventoryDialog.getX();
                float dialogY = inventoryDialog.getY();
                float dialogWidth = inventoryDialog.getWidth();
                float dialogHeight = inventoryDialog.getHeight();
                if (x < dialogX || x > dialogX + dialogWidth || y < dialogY || y > dialogY + dialogHeight) {
                    inventoryDialog.hide();
                    stage.removeListener(this); // Remove the listener when the dialog is closed
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }
    private void showStatusDialog() {
        if (statusDialog == null) {
            statusDialog = new Dialog("Estado del Jugador", skin) {
                @Override
                protected void result(Object object) {
                }
            };

            Table statusTable = new Table(skin);
            Player player = Player.getInstance();

            statusTable.add(new Label("Nombre: " + player.getPlayerName(), skin)).pad(5).row();
            statusTable.add(new Label("Nivel: " + player.getLevel(), skin)).pad(5).row();
            statusTable.add(new Label("Vida: " + player.getHitPoints() + " / " + player.getMaxHitPoints(), skin)).pad(5).row();
            statusTable.add(new Label("Experiencia: " + player.getExperience() + " / " + (100 * player.getLevel()), skin)).pad(5).row();
            statusTable.add(new Label("Vitalidad: " + player.getVitality(), skin)).pad(5).row();
            statusTable.add(new Label("Fuerza: " + player.getStrength(), skin)).pad(5).row();
            statusTable.add(new Label("Resistencia: " + player.getEndurance(), skin)).pad(5).row();
            statusTable.add(new Label("Destreza: " + player.getDexterity(), skin)).pad(5).row();
            statusTable.add(new Label("Suerte: " + player.getLuck(), skin)).pad(5).row();

            statusDialog.getContentTable().add(statusTable).pad(10);
            statusDialog.pack();
            statusDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());
        }
        statusDialog.show(stage);

        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float dialogX = statusDialog.getX();
                float dialogY = statusDialog.getY();
                float dialogWidth = statusDialog.getWidth();
                float dialogHeight = statusDialog.getHeight();
                if (x < dialogX || x > dialogX + dialogWidth || y < dialogY || y > dialogY + dialogHeight) {
                    statusDialog.hide();
                    stage.removeListener(this);
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
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
        if (Gdx.input.justTouched() && !isInCombat && !isPaused) {
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
                        currentBackground = bgAtlas.findRegion("bg_f", currentCombatIndex); // Cambiar fondo
                    }
                }
            }
        }
    }

    private void startCombat() {
        if (isInCombat) {
            game.setScreen(new Combat(game, currentBackground)); // Pasar el fondo actual al constructor de Combat
        }
    }


    // Suponiendo que tienes un método para finalizar el combate y regresar
    public void endCombat() {
        isInCombat = false;
        currentTextIndex++; // Avanzar al siguiente texto después del combate
    }

    @Override
    public void render(float delta) {
        // Asegurémonos de que la limpieza de la pantalla no cause un parpadeo negro.
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
