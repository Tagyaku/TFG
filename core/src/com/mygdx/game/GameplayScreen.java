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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class GameplayScreen implements Screen {
    private MyGdxGame game;
    private SpriteBatch batch;
    private TextureAtlas atlas, guiAtlas, bgAtlas, itemAtlas;
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
    private int[] combatPoints = {2, 5}; // Puntos donde ocurren los combates
    private int currentCombatIndex = 0;
    private int combatStartIndex = 0;

    private Dialog pauseDialog, inventoryDialog, statusDialog, equipmentDialog, itemStatsDialog, helpDialog;
    private boolean isPaused = false;
    private boolean isInventoryActive = false;
    private boolean isStatusActive = false;
    private boolean isEquipmentActive = false;
    private boolean isItemStatsActive = false;
    private boolean isHelpActive = false;
    private boolean isOptionsDialogVisible = false;
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
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.boxTexture = guiAtlas.findRegion("11 Border 01-0");
        this.slideBarTexture=guiAtlas.findRegion("UI_Flat_Scrollbar");

        // Inicializar el fondo actual basado en el índice del combate actual
        this.currentBackground = bgAtlas.findRegion("bg_f", currentCombatIndex + 1);

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
            if (isDialogOpen(optionsDialog, x, y)) clickInsideAnyDialog = true; // Añadir esta línea

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
        }
        stage.addActor(pauseDialog);
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
        isPaused = false;
    }

    private void showInventoryDialog() {
        Player player = Player.getInstance(game);

        if (inventoryDialog == null) {
            inventoryDialog = new Dialog("Inventario", skin) {
                @Override
                protected void result(Object object) {
                }
            };

            addCloseButton(inventoryDialog);

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
                            player.equip(item);
                            updateInventoryDialog();
                        }
                    });
                inventoryTable.add(itemButton).size(80).pad(5); // Adjust button size

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
                    inventoryTable.add(potionButton).size(80).pad(5); // Adjust button size

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
        scrollPane.setScrollingDisabled(true, false); // Disable horizontal scrolling
        inventoryDialog.getContentTable().add(scrollPane).width(900).height(400).pad(10); // Adjust size as needed
            inventoryDialog.pack();

            // Set the position of the inventory dialog
            inventoryDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());
        }
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
        scrollPane.setScrollingDisabled(true, false); // Disable horizontal scrolling
        equipmentDialog.getContentTable().add(scrollPane).width(800).height(400).pad(10); // Adjust size as needed
            equipmentDialog.pack();
            equipmentDialog.setPosition(pauseDialog.getX() + pauseDialog.getWidth() + 10, pauseDialog.getY());
        }
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
        gameTexts.add("Primer mensaje que aparecerá en el juego.");
        gameTexts.add("Segundo mensaje, continúa la historia.");
        gameTexts.add("Preparando para el primer combate.");
        gameTexts.add("Mensaje después del primer combate.");
        gameTexts.add("Mensaje previo al segundo combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
        gameTexts.add("Final del juego después del último combate.");
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
                    if (currentCombatIndex < combatPoints.length && currentTextIndex > combatPoints[currentCombatIndex]) {
                        currentCombatIndex++;
                        currentBackground = bgAtlas.findRegion("bg_f", currentCombatIndex + 1); // Actualizar fondo
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
        currentBackground = bgAtlas.findRegion("bg_f", currentCombatIndex + 1); // Actualizar fondo

    }

    @Override
    public void render(float delta) {
        //stage.setDebugAll(true);

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