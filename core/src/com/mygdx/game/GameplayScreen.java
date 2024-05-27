package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class GameplayScreen implements Screen {
    private MyGdxGame game;
    private SpriteBatch batch;
    private TextureAtlas atlas, guiAtlas, bgAtlas, itemAtlas, cavernAtlas;
    private TextureRegion borderTexture, boxTexture, slideBarTexture;
    TextureRegion currentBackground;
    private Rectangle lowerBorderArea, pauseButtonArea;
    private Stage stage;
    private TextButton pauseButton;
    private BitmapFont font;
    private BitmapFont textFont;
    private List<String> gameTexts;
    private int currentTextIndex = 0;
    private boolean isInCombat = false;
    private int[] combatPoints = {4, 9, 17, 27, 32}; // Puntos donde ocurren los combates
    private int currentCombatIndex = 0;
    private int combatStartIndex = 0;

    private Dialog pauseDialog, inventoryDialog, statusDialog, equipmentDialog, itemStatsDialog, helpDialog, saveDialog, loadDialog;
    private boolean isPaused = false;
    private boolean isInventoryActive = false;
    private boolean isStatusActive = false;
    private boolean isEquipmentActive = false;
    private boolean isItemStatsActive = false;
    private boolean isHelpActive = false;
    private boolean isOptionsDialogVisible = false;
    private boolean isSaveDialogActive = false;
    private OptionsDialog optionsDialog;
    private Skin skin;
    private boolean isOptionsDialogActive = false;

    public GameplayScreen(MyGdxGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.guiAtlas = new TextureAtlas("images/GUI/GUI.atlas");
        this.bgAtlas = new TextureAtlas("images/Forest/Forest.atlas");
        this.itemAtlas = new TextureAtlas("images/items/items.atlas");
        this.cavernAtlas = new TextureAtlas("images/cavern/cavern_BG.atlas");
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.boxTexture = guiAtlas.findRegion("11 Border 01-0");
        this.slideBarTexture=guiAtlas.findRegion("UI_Flat_Scrollbar");

        // Inicializar el fondo actual basado en el índice del combate actual
        this.currentBackground = bgAtlas.findRegion("bg", currentCombatIndex + 1);

        this.stage = new Stage(new ScreenViewport(), batch);
        this.font = new BitmapFont();
        this.textFont = new BitmapFont();
        textFont.getData().setScale(2);

        Gdx.app.log("GameplayScreen", "Initializing GameplayScreen, playing music: Goblins_Den_(Regular).wav");
    if (AudioManager.getInstance().getCurrentMusic() == null ||
        !AudioManager.getInstance().getCurrentMusicFilePath().equals("audio/music/Golden Serpant Tavern (LOOP).mp3")) {
        AudioManager.getInstance().playMusic("audio/music/Golden Serpant Tavern (LOOP).mp3");
        AudioManager.getInstance().setMusicVolume(2f);
    }
        Gdx.input.setInputProcessor(stage);
        initUI();
        initText();
        initSkin();

        // Listener para detectar clics fuera de los diálogos
        stage.addListener(new ClickListener() {
            @SuppressWarnings("SuspiciousIndentation")
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isPaused) {
                    boolean clickInsideAnyDialog = false;

                    if (isDialogOpen(pauseDialog, x, y)) clickInsideAnyDialog = true;
                    if (isDialogOpen(inventoryDialog, x, y)) clickInsideAnyDialog = true;
                    if (isDialogOpen(statusDialog, x, y)) clickInsideAnyDialog = true;
                    if (isDialogOpen(equipmentDialog, x, y)) clickInsideAnyDialog = true;
                    if (isDialogOpen(itemStatsDialog, x, y)) clickInsideAnyDialog = true;
            if (isDialogOpen(helpDialog, x, y)) clickInsideAnyDialog = true;
                    if (isDialogOpen(optionsDialog, x, y)) clickInsideAnyDialog = true;
                    if (isDialogOpen(saveDialog, x, y)) clickInsideAnyDialog = true;

            if (!clickInsideAnyDialog) {
                        hideAllDialogs();
                        isPaused = false;
                    }
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    private void initSkin() {
        skin = new Skin();
        skin.add("default-font", font);
        skin.addRegions(atlas);

        // Crear estilos
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = new TextureRegionDrawable(borderTexture);
        textButtonStyle.down = new TextureRegionDrawable(borderTexture);
        textButtonStyle.font = skin.getFont("default-font");
        skin.add("default", textButtonStyle);

        // Create the Label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
    labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Create the Window style
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(borderTexture);
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.titleFontColor = Color.WHITE;
        skin.add("default", windowStyle);

    // Crear estilo de Slider
    Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
    sliderStyle.background = new TextureRegionDrawable(slideBarTexture);
    sliderStyle.knob = new TextureRegionDrawable(slideBarTexture);
    skin.add("default-horizontal", sliderStyle);

        // Create the ScrollPane style
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new TextureRegionDrawable(slideBarTexture);
        scrollPaneStyle.hScrollKnob = new TextureRegionDrawable(slideBarTexture);
        skin.add("default", scrollPaneStyle);
    }

    private void initUI() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float borderHeight = screenHeight * 0.30f;
        float borderWidth = screenWidth - 20;
        lowerBorderArea = new Rectangle(10, 10, borderWidth, borderHeight);

        float pauseButtonWidth = 200;
        float pauseButtonHeight = 60;
        pauseButtonArea = new Rectangle(10, screenHeight - pauseButtonHeight - 10, pauseButtonWidth, pauseButtonHeight);

        // Estilo del botón de pausa con dimensiones independientes
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(boxTexture, 0, 0, 65, 65));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(boxTexture, 0, 0, 65, 65));
        buttonStyle.font = font;

        font.getData().setScale(3);

        pauseButton = new TextButton("Pausa", buttonStyle);
        pauseButton.setBounds(pauseButtonArea.x, pauseButtonArea.y, pauseButtonArea.width, pauseButtonArea.height);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("GameplayScreen", "Pause button clicked, playing sound effect.");
                AudioManager.getInstance().playSound("audio/sound effects/confirm_style_5_001.wav");
            //Gdx.app.log("PauseButton", "Clicked");
                if (!isPaused) {
                    isPaused = true;
                    showPauseDialog();
                //Gdx.app.log("GameState", "Game Paused");
                } else {
                    isPaused = false;
                    hideAllDialogs();
                //Gdx.app.log("GameState", "Game Resumed");
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
        TextButton optionsButton = new TextButton("Opciones", skin);
        TextButton helpButton = new TextButton("Ayuda", skin);
            TextButton saveButton = new TextButton("Guardar", skin);
            TextButton exitButton = new TextButton("Salir", skin);

            // Set button sizes
            float buttonWidth = 200;
            float buttonHeight = 50;
            inventoryButton.setSize(buttonWidth, buttonHeight);
            equipmentButton.setSize(buttonWidth, buttonHeight);
            statusButton.setSize(buttonWidth, buttonHeight);
        optionsButton.setSize(buttonWidth, buttonHeight);
        helpButton.setSize(buttonWidth, buttonHeight);
            saveButton.setSize(buttonWidth, buttonHeight);
            exitButton.setSize(buttonWidth, buttonHeight);

            // Add buttons to the dialog
            pauseDialog.getContentTable().add(inventoryButton).width(buttonWidth).height(buttonHeight).pad(5);
            pauseDialog.getContentTable().row();
            pauseDialog.getContentTable().add(equipmentButton).width(buttonWidth).height(buttonHeight).pad(5);
            pauseDialog.getContentTable().row();
            pauseDialog.getContentTable().add(statusButton).width(buttonWidth).height(buttonHeight).pad(5);
            pauseDialog.getContentTable().row();
        pauseDialog.getContentTable().add(optionsButton).width(buttonWidth).height(buttonHeight).pad(5);
        pauseDialog.getContentTable().row();
        pauseDialog.getContentTable().add(helpButton).width(buttonWidth).height(buttonHeight).pad(5);
        pauseDialog.getContentTable().row();
            pauseDialog.getContentTable().add(saveButton).width(buttonWidth).height(buttonHeight).pad(5);
            pauseDialog.getContentTable().row();
            pauseDialog.getContentTable().add(exitButton).width(buttonWidth).height(buttonHeight).pad(5);

            pauseDialog.pack();

            pauseDialog.setPosition(10, Gdx.graphics.getHeight() - pauseDialog.getHeight() - 100);

        ClickListener buttonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().playSound("audio/sound effects/confirm_style_5_001.wav");
            }
        };

        exitButton.addListener(buttonClickListener);
            exitButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                Player.getInstance(game).reset();
                    game.setScreen(new MainMenuScreen(game));
                }
            });

        inventoryButton.addListener(buttonClickListener);
            inventoryButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isInventoryActive) {
                        inventoryDialog.remove();
                        isInventoryActive = false;
                    } else {
                        showInventoryDialog();
                        isInventoryActive = true;
                    }
                }
            });

        equipmentButton.addListener(buttonClickListener);
            equipmentButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isEquipmentActive) {
                        equipmentDialog.remove();
                        isEquipmentActive = false;
                    } else {
                        showEquipmentDialog();
                        isEquipmentActive = true;
                    }
                }
            });

        statusButton.addListener(buttonClickListener);
            statusButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isStatusActive) {
                        statusDialog.remove();
                        isStatusActive = false;
                    } else {
                        showStatusDialog();
                        isStatusActive = true;
                    }
                }
            });

        optionsButton.addListener(buttonClickListener);
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isOptionsDialogActive) {
                    optionsDialog.remove();
                } else {
                    showOptionsDialog();
            }
                isOptionsDialogActive = !isOptionsDialogActive;
            }
        });

        helpButton.addListener(buttonClickListener);
        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isHelpActive) {
                    helpDialog.remove();
                    isHelpActive = false;
                } else {
                showHelpDialog();
                    isHelpActive = true;
                }
            }
        });
            saveButton.addListener(buttonClickListener);
            saveButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isSaveDialogActive) {
                        saveDialog.remove();
                    } else {
                    showSaveDialog();
                }
                    isSaveDialogActive = !isSaveDialogActive;
                }
            });
        }
        stage.addActor(pauseDialog);
    }
    private void showSaveDialog() {
        if (saveDialog == null) {
            saveDialog = new Dialog("Guardar Partida", skin) {
                @Override
                protected void result(Object object) {
                }
            };

            addCloseButton(saveDialog);

            Table contentTable = new Table(skin);
            for (int i = 1; i <= 5; i++) {
            TextButton saveSlotButton = new TextButton("Ranura " + i, skin);
            final int slot = i;
            saveSlotButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    saveGame(slot);
                }
            });
            contentTable.add(saveSlotButton).row();
        }

            ScrollPane scrollPane = new ScrollPane(contentTable, skin);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false);
            saveDialog.getContentTable().add(scrollPane).width(800).height(400).pad(10);
            saveDialog.pack();
            saveDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());
    }
        stage.addActor(saveDialog);
    }

    private void showLoadDialog() {
        if (loadDialog == null) {
            loadDialog = new Dialog("Cargar Partida", skin) {
                @Override
                protected void result(Object object) {
                }
            };

            addCloseButton(loadDialog);

            Table contentTable = new Table(skin);
            List<SavedGame> savedGames = game.getSaveGameService().loadAllSavedGames();
            for (SavedGame savedGame : savedGames) {
            TextButton loadSlotButton = new TextButton("Ranura " + savedGame.getSlotNumber() + " - " + savedGame.getPlayerName() + " - Progreso: " + savedGame.getCurrentTextIndex(), skin);
                loadSlotButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        loadGame(savedGame);
                        loadDialog.hide();
                    }
                });
                contentTable.add(loadSlotButton).row();
            }

            ScrollPane scrollPane = new ScrollPane(contentTable, skin);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false);
            loadDialog.getContentTable().add(scrollPane).width(800).height(400).pad(10);
            loadDialog.pack();
            loadDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());
        }
        stage.addActor(loadDialog);
    }

    private void saveGame(int slot) {
        Player player = Player.getInstance(game);
        String saveData = player.toJson();
    SavedGame savedGame = new SavedGame(player.getPlayerName(), saveData, slot, currentTextIndex);
        game.getSaveGameService().saveGame(savedGame);
    }

    private void loadGame(SavedGame savedGame) {
        Player loadedPlayer = Player.fromJson(savedGame.getSaveData(), game);
        Player.getInstance(game).copyFrom(loadedPlayer);
        currentTextIndex = savedGame.getCurrentTextIndex();
        updateStatusTable();
    }

    public int getCurrentTextIndex() {
        return currentTextIndex;
    }

    public void setCurrentTextIndex(int currentTextIndex) {
        this.currentTextIndex = currentTextIndex;
    }

    private String serializeGameData(Player player) {
        Json json = new Json();
        return json.toJson(player);
    }




    private void showHelpDialog() {
    if (helpDialog == null) {
        helpDialog = new Dialog("Guía de Atributos y Equipamiento", skin) {
            @Override
            protected void result(Object object) {
            }
        };

        addCloseButton(helpDialog);

        Label helpContent = new Label(
                "Atributos:\n\n" +
                "VITALIDAD: Incrementa tus puntos de vida (2 puntos de vida por cada punto de vitalidad).\n" +
                "FUERZA: Aumenta el daño infligido (3 puntos de daño por cada punto de fuerza).\n" +
                "RESISTENCIA: Mejora tu defensa (0.5 puntos de defensa por cada punto de resistencia).\n" +
                "DESTREZA: Incrementa el daño crítico (2% por cada punto de destreza).\n" +
                "SUERTE: Aumenta la probabilidad de golpe crítico (1% por cada punto de suerte).\n\n" +
                "Equipamiento:\n\n" +
                "ARMA: Incrementa las estadísticas de ataque (solo puedes equipar una).\n" +
                "ACCESORIOS (hasta 4): Ofrecen diversas mejoras.\n" +
                "ARMADURA: Incrementa la defensa (solo puedes equipar una).\n\n" +
                "Habilidades en combate:\n\n" +
                "DEFEND: no recibes daño en el siguiente ataque enemigo.\n" +
                "Puntos de Vida:\n\n" +
                "Aumentan con la vitalidad.\n" +
                "Disminuyen con ataques recibidos.\n" +
                "Si llegan a cero, el personaje muere y regresa a la pantalla de inicio sin guardar. Guarda con frecuencia.\n\n" +
                "Daño Crítico:\n\n" +
                "Daño adicional basado en destreza.\n" +
                "Probabilidad de crítico aumentada por suerte.", skin);

        helpContent.setWrap(true);
        Table contentTable = new Table(skin);
        contentTable.add(helpContent).width(1100).pad(10);

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        helpDialog.getContentTable().add(scrollPane).width(1200).height(500).pad(10); // Adjust size as needed
        helpDialog.pack();
        helpDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());
    }
    stage.addActor(helpDialog);
    isHelpActive = true;
    }
    private void showOptionsDialog() {
        if (optionsDialog == null) {
            optionsDialog = new OptionsDialog("Opciones", skin);
            addCloseButton(optionsDialog);
            optionsDialog.pack();
            optionsDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());
        }
        stage.addActor(optionsDialog);
        isOptionsDialogActive = true;
    }
    private void addCloseButton(Dialog dialog) {
        if (dialog != null) {
        TextureRegionDrawable closeTexture = new TextureRegionDrawable(guiAtlas.findRegion("UI_Flat_Cross_Large"));

        ImageButton.ImageButtonStyle closeButtonStyle = new ImageButton.ImageButtonStyle();
        closeButtonStyle.imageUp = closeTexture;
        closeButtonStyle.imageDown = closeTexture;

        ImageButton closeButton = new ImageButton(closeButtonStyle);

            float closeButtonWidth = 150;
            float closeButtonHeight = 150;

            // Set the size of the close button
        closeButton.setSize(closeButtonWidth, closeButtonHeight);
        closeButton.getImage().setFillParent(true);

            closeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    dialog.remove();
                    if (dialog == inventoryDialog) {
                        isInventoryActive = false;
                    } else if (dialog == statusDialog) {
                        isStatusActive = false;
                    } else if (dialog == equipmentDialog) {
                        isEquipmentActive = false;
                    } else if (dialog == itemStatsDialog) {
                        isItemStatsActive = false;
                } else if (dialog == helpDialog) {
                    isHelpActive = false;
            } else if (dialog == optionsDialog) {
                isOptionsDialogActive = false;
                    } else if (dialog == saveDialog) {
                        isSaveDialogActive = false;
                    }
                }
            });

        // Add the close button to the top right corner of the dialog title table
        Table titleTable = dialog.getTitleTable();
        titleTable.add(closeButton).size(closeButtonWidth, closeButtonHeight).expandX().right().top().padBottom(-1000).padRight(10);
        dialog.pack();
        } else {
            System.out.println("Error: Dialog is null when trying to add close button.");
        }
    }


    private void hideAllDialogs() {
        if (pauseDialog != null) {
            pauseDialog.remove();
        }
        if (inventoryDialog != null) {
            inventoryDialog.remove();
            isInventoryActive = false;
        }
        if (statusDialog != null) {
            statusDialog.remove();
            isStatusActive = false;
        }
        if (equipmentDialog != null) {
            equipmentDialog.remove();
            isEquipmentActive = false;
        }
        if (itemStatsDialog != null) {
            itemStatsDialog.remove();
            isItemStatsActive = false;
        }
    if (helpDialog != null) {
        helpDialog.remove();
        isHelpActive = false;
    }
        if (saveDialog != null) {
            saveDialog.remove();
            isSaveDialogActive = false;
        }
        isPaused = false;
    }

    private void showInventoryDialog() {
        if (inventoryDialog == null) {
            inventoryDialog = new Dialog("Inventario", skin) {
                @Override
                protected void result(Object object) {
                }
            };

            addCloseButton(inventoryDialog);
    }

            Table inventoryTable = new Table(skin);
            Inventory playerInventory = Player.getInstance(game).getInventory();

            int itemsPerRow = 9;
            int currentColumn = 0;
            for (Equipment item : playerInventory.getEquipment()) {
                TextureRegion itemTexture = item.getTexture();
                if (itemTexture != null) {
                    TextButton itemButton = new TextButton("", new TextButton.TextButtonStyle(new TextureRegionDrawable(itemTexture), null, null, font));
                    itemButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                    Player.getInstance(game).equip(item);
                            updateInventoryDialog();
                        }
                    });
                    inventoryTable.add(itemButton).size(80).pad(5);

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
                        inventoryTable.add(potionButton).size(80).pad(5);

                        currentColumn++;
                        if (currentColumn >= itemsPerRow) {
                            inventoryTable.row();
                            currentColumn = 0;
                        }
                    }
                }
            }

        ScrollPane scrollPane = new ScrollPane(inventoryTable, skin);
        scrollPane.setFadeScrollBars(true);
            scrollPane.setScrollingDisabled(true, false);
    inventoryDialog.getContentTable().clear();
            inventoryDialog.getContentTable().add(scrollPane).width(900).height(400).pad(10);
            inventoryDialog.pack();
            inventoryDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());

        stage.addActor(inventoryDialog);
    }

    private void updateInventoryDialog() {
        if (inventoryDialog != null) {
            inventoryDialog.remove();
            inventoryDialog = null;
            showInventoryDialog();
        }
    }

    private void showStatusDialog() {
        if (statusDialog == null) {
            statusDialog = new Dialog("Estado del Jugador", skin) {
                @Override
                protected void result(Object object) {
                }
            };

            addCloseButton(statusDialog);

            updateStatusTable();
            statusDialog.pack();
            statusDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY()-90);
        }
        stage.addActor(statusDialog);
    }

    private void updateStatusTable() {
        if (statusDialog != null) {
            statusDialog.getContentTable().clear();
            Player player = Player.getInstance(game);
            Table statusTable = new Table(skin);

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
        }
    }


    private void showEquipmentDialog() {
        if (equipmentDialog == null) {
            equipmentDialog = new Dialog("Equipo", skin) {
                @Override
                protected void result(Object object) {
                }
            };

            addCloseButton(equipmentDialog);
    }

            Table equipmentTable = new Table(skin);
            Inventory playerInventory = Player.getInstance(game).getInventory();
            Player player = Player.getInstance(game);

            int itemsPerRow = 8;
            int currentColumn = 0;
            for (Equipment item : playerInventory.getEquipment()) {
                TextureRegion itemTexture = item.getTexture();
                if (itemTexture != null) {
                    TextButton.TextButtonStyle itemButtonStyle = new TextButton.TextButtonStyle();
                    itemButtonStyle.up = new TextureRegionDrawable(itemTexture);
                    itemButtonStyle.font = font;

                    // Añadir tamaño ampliado si el objeto está equipado
                    if (player.isEquipped(item)) {
                        itemButtonStyle.up.setMinWidth(100);
                        itemButtonStyle.up.setMinHeight(100);
                    } else {
                        itemButtonStyle.up.setMinWidth(70);
                        itemButtonStyle.up.setMinHeight(70);
                    }

                    TextButton itemButton = new TextButton("", itemButtonStyle);
                    itemButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (player.isEquipped(item)) {
                                player.unequip(item);
                            } else {
                                player.equip(item);
                            }
                            updateStatusTable();
                            updateEquipmentDialog();
                            if (isItemStatsActive) {
                                itemStatsDialog.remove();
                                isItemStatsActive = false;
                            } else {
                                showItemStatsDialog(item);
                                isItemStatsActive = true;
                            }
                        }
                    });
                    equipmentTable.add(itemButton).size(itemButtonStyle.up.getMinWidth(), itemButtonStyle.up.getMinHeight()).pad(5);

                    currentColumn++;
                    if (currentColumn >= itemsPerRow) {
                        equipmentTable.row();
                        currentColumn = 0;
                    }
                }
            }

            ScrollPane scrollPane = new ScrollPane(equipmentTable, skin);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false);
    equipmentDialog.getContentTable().clear();
            equipmentDialog.getContentTable().add(scrollPane).width(800).height(400).pad(10);
            equipmentDialog.pack();
            equipmentDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());

        stage.addActor(equipmentDialog);
    }


    private void updateEquipmentDialog() {
        if (equipmentDialog != null) {
            equipmentDialog.remove();
            equipmentDialog = null;
            showEquipmentDialog();
        }
    }

    private void showItemStatsDialog(Equipment item) {
        if (itemStatsDialog != null) {
            itemStatsDialog.remove();
        }

        itemStatsDialog = new Dialog(item.getName(), skin) {
            @Override
            protected void result(Object object) {
            }
        };

        Table statsTable = new Table(skin);

        statsTable.add(new Label("Nombre: " + item.getName(), skin)).pad(5).row();
        statsTable.add(new Label("Tipo: " + item.getType(), skin)).pad(5).row();
        statsTable.add(new Label("Vitalidad: " + item.getVitalityBonus(), skin)).pad(5).row();
        statsTable.add(new Label("Fuerza: " + item.getStrengthBonus(), skin)).pad(5).row();
        statsTable.add(new Label("Resistencia: " + item.getEnduranceBonus(), skin)).pad(5).row();
        statsTable.add(new Label("Destreza: " + item.getDexterityBonus(), skin)).pad(5).row();
        statsTable.add(new Label("Suerte: " + item.getLuckBonus(), skin)).pad(5).row();

        itemStatsDialog.getContentTable().add(statsTable).pad(10);
        itemStatsDialog.pack();
        itemStatsDialog.setPosition(Gdx.graphics.getWidth() - itemStatsDialog.getWidth() - 10, 10);

        addCloseButton(itemStatsDialog);
        stage.addActor(itemStatsDialog);
    }

    private void initText() {
        textFont = new BitmapFont();
        textFont.getData().setScale(2);
        gameTexts = new ArrayList<>();
        gameTexts.add("...");
        gameTexts.add("En una era olvidada, el reino de Eldoria ha caído en la oscuridad. Un antiguo mal ha despertado, envolviendo las tierras en sombras.\n");
        gameTexts.add("Se dice que un héroe, elegido por el destino, puede restaurar la luz perdida. Este héroe debe atravesar Eldenwood, un bosque antiguo lleno de peligros y secretos.\n");
        gameTexts.add("Te adentras en Eldenwood, con solo tu coraje y una espada a tu lado. La misión es clara: encontrar el corazón del mal y destruirlo.\n");
        gameTexts.add("Un rugido feroz rompe el silencio. Desde la espesura surge una bestia con ojos brillantes, lista para atacarte.");
        gameTexts.add("Un lobo gigante, cubierto de cicatrices, con colmillos afilados y una mirada de odio ancestral.\n");
        gameTexts.add("El lobo cae al suelo, su último aliento se mezcla con el viento. Eldenwood parece aceptar tu fuerza, aunque nuevos peligros te aguardan.\n");
        gameTexts.add("Caminas más profundo en el bosque, donde los árboles se hacen más altos y las sombras más largas. Cada paso te acerca a tu destino.");
        gameTexts.add("El sonido de criaturas desconocidas resuena a tu alrededor, y los susurros del bosque te advierten de un peligro inminente.\n");
        gameTexts.add("Llegas a un claro oscuro, donde el suelo está cubierto de hojas muertas. Un olor nauseabundo emana de un estanque cercano, en el cual algo se mueve.\n");
        gameTexts.add("Del estanque surge una figura grotesca, cubierta de lodo y hojas podridas. Sus ojos brillan con malevolencia.\n");
        gameTexts.add("Un gruñido profundo emana de la criatura, prometiendo dolor y sufrimiento.\n");
        gameTexts.add("Un ghoul del pantano, su piel verde y resbaladiza, con zarpas afiladas y una boca llena de dientes irregulares y podridos");
        gameTexts.add("La criatura se desintegra en el lodo, dejando tras de sí un silencio inquietante. El bosque se abre un poco más, permitiéndote continuar.\n");
        gameTexts.add("Sigues avanzando, sintiendo el peso de la misión sobre tus hombros. El aire se vuelve más fresco, indicando la proximidad de una montaña.\n");
        gameTexts.add("A lo lejos, distingues la silueta de una montaña. A su base, una cueva oscura te espera, prometiendo nuevos desafíos.\n");
        gameTexts.add("La entrada de la cueva es vasta y oscura, un abismo que parece devorar la luz del día. Un guardián inmenso se encuentra vigilando la entrada.\n");
        gameTexts.add("El guardián, una estatua viviente de piedra, se activa al sentir tu presencia. Sus ojos brillan con una luz roja y amenazante.");
        gameTexts.add("Un rugido sordo y profundo emana del guardián, preparando su ataque.\n");
        gameTexts.add("Un gólem de piedra, inmenso y robusto, con runas brillantes grabadas en su cuerpo y un mazo de granito que maneja con facilidad.\n");
        gameTexts.add("El gólem se desmorona en una pila de escombros, sus runas apagándose lentamente. La entrada de la cueva está despejada, lista para ser explorada.\n");
        gameTexts.add("Das un paso adelante y entras en la cueva, donde la oscuridad es total y el aire frío. La sensación de peligro aumenta a cada paso.\n");
        gameTexts.add("El interior de la cueva es laberíntico y lleno de ecos de tiempos antiguos. Tus pasos reverberan en las paredes de piedra.\n");
        gameTexts.add("Caminas por pasadizos estrechos y vastas cavernas, cada una más oscura y silenciosa que la anterior.\n");
        gameTexts.add("Al llegar a una cámara amplia, una cascada subterránea cae desde el techo, iluminada por un extraño resplandor azul. La belleza de la escena es inquietante.\n");
        gameTexts.add("De detrás de la cascada emerge una figura espectral, sus ojos brillando con un fulgor sobrenatural.\n");
        gameTexts.add("Un alarido etéreo llena la cámara, mientras la figura avanza hacia ti con una velocidad inhumana.\n");
        gameTexts.add("Un espectro guardián, una figura fantasmal envuelta en sombras, con ojos brillantes y garras afiladas.\n");
        gameTexts.add("El espectro se disipa en una niebla fría, dejando atrás una sensación de vacío. La cueva sigue extendiéndose, llamándote hacia lo desconocido.");
        gameTexts.add("Sigues adelante, atravesando túneles serpenteantes y cámaras ocultas. El silencio es tu único compañero en esta oscuridad abrumadora.\n");
        gameTexts.add("Después de horas de caminata, un rayo de luz natural se filtra desde la distancia, prometiendo una salida de la cueva.\n");
        gameTexts.add("Emerges de la cueva para encontrarte en otro lado del bosque. Ante ti, un vasto valle se despliega, con las ruinas de un antiguo torreón erguido majestuoso en el centro.");
        gameTexts.add("El aire es fresco y limpio, y los restos de estructuras antiguas se dispersan por el paisaje, insinuando una grandeza perdida hace mucho tiempo.\n");
        gameTexts.add("Te diriges hacia el torreón, sintiendo una atracción inexplicable. El camino es empinado y lleno de escombros, pero la determinación te impulsa.\n");
        gameTexts.add("En la entrada del torreón, una figura imponente se alza. Un caballero oscuro, cubierto de una armadura negra, su presencia emana poder y peligro.\n");
        gameTexts.add("El caballero oscuro desenfunda su espada, y un rugido bajo resuena desde su casco, desafiándote a un duelo mortal.\n");
        gameTexts.add("Un caballero oscuro, imponente y ágil, con una espada enorme y armadura negra que refleja la luz con un brillo siniestro.\n");
        gameTexts.add("El caballero oscuro cae, su armadura desmoronándose. Las puertas del torreón se abren lentamente, invitándote a explorar las profundidades de su oscuridad.\n");
        gameTexts.add("Cruzas el umbral del torreón, donde la penumbra y la historia se entrelazan. Corredores interminables y salas abandonadas se extienden ante ti, prometiendo nuevos desafíos y secretos ocultos.");
        gameTexts.add("Final del juego después del último combate.");

    }

    private void handleInput() {
        if (Gdx.input.justTouched() && !isInCombat && !isPaused) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (lowerBorderArea.contains(x, y)) {
                currentTextIndex++;
                if (currentTextIndex >= gameTexts.size()) {
                    currentTextIndex = 0;
                }

                if (currentCombatIndex < combatPoints.length && currentTextIndex == combatPoints[currentCombatIndex]) {
                    isInCombat = true;
                    combatStartIndex = currentTextIndex;
                    startCombat();
                } else {
                    // Actualizar fondo al entrar en la cueva (punto específico en el texto)
                    if (currentTextIndex == 20) {
                        currentBackground = cavernAtlas.findRegion("bg", 1);
                } else {
                    if (currentCombatIndex < combatPoints.length && currentTextIndex > combatPoints[currentCombatIndex]) {
                        currentCombatIndex++;
                            currentBackground = bgAtlas.findRegion("bg", currentCombatIndex + 1); // Actualizar fondo
                        }
                    }
                }
            }
        }
    }

    private void startCombat() {
        if (isInCombat) {
            game.setScreen(new Combat(game, this));
        }
    }

    public void endCombat() {
        isInCombat = false;
        currentTextIndex = combatStartIndex + 1;
        currentCombatIndex++;
        currentBackground = bgAtlas.findRegion("bg", currentCombatIndex + 1); // Actualizar fondo

    }

    @Override
    public void render(float delta) {
        updateStatusTable();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(currentBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(borderTexture, lowerBorderArea.x, lowerBorderArea.y, lowerBorderArea.width, lowerBorderArea.height);
        if (!gameTexts.isEmpty() && !isInCombat) {
            String currentText = gameTexts.get(currentTextIndex);
            textFont.draw(batch, currentText, lowerBorderArea.x + 20, lowerBorderArea.y + lowerBorderArea.height / 2 + 10, lowerBorderArea.width - 40, Align.center, true);
        }
        batch.draw(boxTexture, pauseButtonArea.x, pauseButtonArea.y, pauseButtonArea.width, pauseButtonArea.height);
        batch.end();

        stage.act(delta);
        stage.draw();
        handleInput();
        //stage.setDebugAll(true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

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
    public void hide() {
        Gdx.input.setInputProcessor(null);

    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        bgAtlas.dispose();
        stage.dispose();
    }

    private boolean isDialogOpen(Dialog dialog, float x, float y) {
        if (dialog != null && dialog.isVisible()) {
            float dialogX = dialog.getX();
            float dialogY = dialog.getY();
            float dialogWidth = dialog.getWidth();
            float dialogHeight = dialog.getHeight();
            if (x >= dialogX && x <= dialogX + dialogWidth && y >= dialogY && y <= dialogY + dialogHeight) {
                return true;
            }
        }
        return false;
    }
}