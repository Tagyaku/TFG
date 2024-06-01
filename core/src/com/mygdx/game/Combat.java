package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Combat implements Screen {
    private final MyGdxGame game;
    private final GameplayScreen gameplayScreen;

    private final Stage stage;
    private final SpriteBatch batch;
    private final TextureAtlas atlas;
    private final TextureAtlas itemAtlas;
    private final TextureRegion borderTexture;
    private Rectangle lowerBorderArea;
    private Rectangle enemyArea;
    private Rectangle potionMenuArea;
    private Rectangle victoryMenuArea;
    private final Player player;
    private Enemies enemy;
    private Button attackButton, defendButton, useItemButton;
    private BitmapFont font;
    private boolean isPotionMenuVisible = false;
    private boolean isVictoryMenuVisible = false;
    private boolean isPlayerDead = false;
    private TextureRegion potion30Texture;
    private TextureRegion potion100Texture;
    private TextureRegion backgroundTexture;
    private GlyphLayout layout;
    private float stateTime;
    private Animation<TextureRegion> attackAnimation;
    private boolean isAttacking = false;
    private boolean buttonsEnabled = true;
    private float enemyX, enemyY;
    private boolean enemyAttacking = false;
    private boolean enemyMoving = false;
    private EquipableItems equipableItems;
    private Skin skin;
    private List<TextureRegion> rewardTextures;
    private ImageButton potion30Button;
    private ImageButton potion100Button;
    private TextureRegion protectTexture;
    private boolean isDefending = false;
    private Animation<TextureRegion> hurtAnimation;
    private boolean isHurt = false;
    private Label criticalHitLabel;
    private Label damageLabel;
    private BitmapFont specialFont;
    public Combat(MyGdxGame game, GameplayScreen gameplayScreen, int contCombat) {
        this.layout = new GlyphLayout();
        this.game = game;
        this.gameplayScreen = gameplayScreen;

        this.backgroundTexture = gameplayScreen.currentBackground;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.itemAtlas = new TextureAtlas("images/items/items.atlas");
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.potion30Texture = itemAtlas.findRegion("potion30");
        this.potion100Texture = itemAtlas.findRegion("potion100");
        this.player = Player.getInstance(game);
        this.enemy = new Enemies(game, this, contCombat);
        this.equipableItems = new EquipableItems();
        specialFont = new BitmapFont(Gdx.files.internal("skin/fonts/default.fnt"));
        specialFont.getData().setScale(2);

        // Precargar los sonidos necesarios
        AudioManager.getInstance().loadSound("audio/sound effects/SFX_Whoosh_Sword_01.mp3");
        AudioManager.getInstance().loadSound("audio/sound effects/Giant_Grunt3.wav");

        if (AudioManager.getInstance().getCurrentMusic() == null ||
                !AudioManager.getInstance().getCurrentMusicFilePath().equals("audio/music/battle/Goblins_Dance_(Battle).wav")) {
            AudioManager.getInstance().playMusic("audio/music/battle/Goblins_Dance_(Battle).wav");
            AudioManager.getInstance().setMusicVolume(0.5f);
            AudioManager.getInstance().setSoundVolume(2f);

        }
        font = new BitmapFont();
        setupAnimations();
        initUI();
        Gdx.input.setInputProcessor(stage);
   }
    private void playerReceiveDamage(int damage) {
        if (!isDefending && !isPlayerDead) {
            player.receiveDamage(damage);
            isHurt = true;
            buttonsEnabled = false;
            stateTime = 0;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    isHurt = false;
                    if (!isPlayerDead) {
                        buttonsEnabled = true;
                    }
                }
            }, hurtAnimation.getAnimationDuration());
        }
        if (player.getHitPoints() <= 0) {
            isPlayerDead = true;
            buttonsEnabled=false;
            showDeathMessageAndReturnToMenu();
        }

    }

    private void animateHurt(float delta) {
        if (isHurt) {
            buttonsEnabled=false;
            stateTime += delta; // Update the state time for the animation
            TextureRegion currentFrame = hurtAnimation.getKeyFrame(stateTime, false);

            // Player's position and scale setup
            TextureRegion playerImage = player.getPlayerTexture("Idle-3");
            float playerScale = (Gdx.graphics.getHeight() - 20) / playerImage.getRegionWidth();
            float imageX = 10;
            float imageY = 10;
            float originX = playerImage.getRegionWidth() / 2;
            float originY = playerImage.getRegionHeight() / 2;

            // Draw the current frame of the hurt animation at the same position and size as the player
            batch.draw(currentFrame, imageX + originX * playerScale - originX, imageY + originY * playerScale - originY,
                    originX, originY, currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
                    playerScale, playerScale, 270);
        }
    }

    private void setupAnimations() {
        TextureAtlas playerAtlas = new TextureAtlas(Gdx.files.internal("images/Main_Character/main_Character.atlas"));

        // Manually add each frame to the attack animation
        Array<TextureRegion> attackFrames = new Array<>();
        attackFrames.add(playerAtlas.findRegion("Attack 1-0"));
        attackFrames.add(playerAtlas.findRegion("Attack 1-1"));
        attackFrames.add(playerAtlas.findRegion("Attack 1-2"));
        attackFrames.add(playerAtlas.findRegion("Attack 1-3"));
        attackFrames.add(playerAtlas.findRegion("Attack 1-4"));

        // Check if any frames are missing to avoid NullPointerException
        for (TextureRegion frame : attackFrames) {
            if (frame == null) {
                System.out.println("One or more attack animation frames are missing!");
                return;
            }
        }

        // Load the protect texture
        protectTexture = playerAtlas.findRegion("Protect");

        // Create the attack animation with the frames array
        attackAnimation = new Animation<>(0.1f, attackFrames, Animation.PlayMode.NORMAL);

        // Manually add each frame to the hurt animation
        Array<TextureRegion> hurtFrames = new Array<>();
        hurtFrames.add(playerAtlas.findRegion("Hurt-0"));
        hurtFrames.add(playerAtlas.findRegion("Hurt-1"));

        // Check if any frames are missing to avoid NullPointerException
        for (TextureRegion frame : hurtFrames) {
            if (frame == null) {
                System.out.println("One or more hurt animation frames are missing!");
                return;
            }
        }

        // Create the hurt animation with the frames array
        hurtAnimation = new Animation<>(0.2f, hurtFrames, Animation.PlayMode.NORMAL);
    }

    private void initUI() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float borderHeight = screenHeight * 0.30f;
        lowerBorderArea = new Rectangle(10, 10, screenWidth - 20, borderHeight);

        // Define the enemy area
        enemyArea = new Rectangle(screenWidth - 500, 200, 280, screenHeight - 300);

        // Define the potion menu area
        potionMenuArea = new Rectangle(10, 600, screenWidth - 1500, screenHeight - 600);

        // Define the victory menu area
        victoryMenuArea = new Rectangle(screenWidth / 4, screenHeight / 4, screenWidth / 2, screenHeight / 2);

        font.getData().setScale(3);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        attackButton = new TextButton("Attack", buttonStyle);
        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonsEnabled) {
                    buttonsEnabled = false;
                    attackEnemy();
                }
            }
        });

        defendButton = new TextButton("Defend", buttonStyle);
        defendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonsEnabled) {
                    buttonsEnabled = false;
                    isHurt=false;

                    defend();
                    triggerEnemyAttack();
                }
            }
        });

        useItemButton = new TextButton("Use Item", buttonStyle);
        useItemButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonsEnabled) {
                    isPotionMenuVisible = !isPotionMenuVisible; // Toggle visibility
                }
            }
        });

/*        escapeButton = new TextButton("Escape", buttonStyle);
        escapeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonsEnabled) {
                    buttonsEnabled = false; // Disable buttons
                    attemptEscape();
                }
            }
        });*/

        float buttonWidth = (lowerBorderArea.width - 80) / 4;
        float buttonHeight = 50;
        float spacing = 13;

        // Posición de los botones en horizontal
        attackButton.setSize(buttonWidth, buttonHeight);
        defendButton.setSize(buttonWidth, buttonHeight);
        useItemButton.setSize(buttonWidth, buttonHeight);
        //.setSize(buttonWidth, buttonHeight);

        attackButton.setPosition(lowerBorderArea.x + 10, lowerBorderArea.y + (lowerBorderArea.height - buttonHeight) / 2);
        defendButton.setPosition(attackButton.getX() + buttonWidth + spacing, lowerBorderArea.y + (lowerBorderArea.height - buttonHeight) / 2);
        useItemButton.setPosition(defendButton.getX() + buttonWidth + spacing, lowerBorderArea.y + (lowerBorderArea.height - buttonHeight) / 2);
        //escapeButton.setPosition(useItemButton.getX() + buttonWidth + spacing, lowerBorderArea.y + (lowerBorderArea.height - buttonHeight) / 2);

        // Agregar botones al escenario
        stage.addActor(attackButton);
        stage.addActor(defendButton);
        stage.addActor(useItemButton);
        //stage.addActor(escapeButton);

        // Configurar LabelStyle para criticalHitLabel y damageLabel
        LabelStyle specialLabelStyle = new LabelStyle();
        specialLabelStyle.font = specialFont;

        criticalHitLabel = new Label("CRITICAL HIT!", specialLabelStyle);
        criticalHitLabel.setVisible(false);
        stage.addActor(criticalHitLabel);

        damageLabel = new Label("", specialLabelStyle);
        damageLabel.setVisible(false);
        stage.addActor(damageLabel);
    }
    private void showCriticalHit() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Posiciona el mensaje en el centro de la pantalla
        criticalHitLabel.setPosition((screenWidth - criticalHitLabel.getWidth()) / 2, (screenHeight - criticalHitLabel.getHeight()) / 2);
        criticalHitLabel.setVisible(true);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                criticalHitLabel.setVisible(false);
            }
        }, 0.8f);
    }
    public void showDeathMessageAndReturnToMenu() {
        buttonsEnabled=false;
        isPlayerDead = true;

        Label deathLabel = new Label("Has muerto", new Label.LabelStyle(specialFont, Color.RED));
        deathLabel.setFontScale(7);
        deathLabel.setPosition(Gdx.graphics.getWidth() / 2f - deathLabel.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        stage.addActor(deathLabel);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new MainMenuScreen(game));
            }
        }, 3);
    }


    private void showDamage(int damage) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        damageLabel.setText(damage +" DMG!");
        damageLabel.setPosition((screenWidth - damageLabel.getWidth()) / 2, screenHeight - damageLabel.getHeight() - 50);

        damageLabel.setVisible(true);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                damageLabel.setVisible(false);
            }
        }, 0.8f);
    }

    private void triggerEnemyAttack() {
        if (!isPlayerDead) {
            enemyAttacking = true;
            enemyAttack();
        }
    }

    private void enemyAttack() {
        buttonsEnabled = false;
        enemyMoving = true;
        float originalX = enemyX;
        float attackMovement = 500;

        AudioManager.getInstance().playSound("audio/sound effects/Giant_Grunt3.wav");

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                enemyX -= attackMovement;
            }
        }, 0.3f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                playerReceiveDamage(enemy.getDamage());
                enemyX = originalX;  // Reset position
                enemyAttacking = false; // Disable enemy attack animation
                enemyMoving = false; // Disable enemy moving animation
                buttonsEnabled = true;  // Re-enable buttons after the attack
            }
        }, 0.4f);  // Delay to simulate the attack movement
    }

    private void defend() {
        player.defend();
        isDefending = true;
        isHurt=false;
        buttonsEnabled=false;
        // Programar la duración de la defensa
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isDefending = false; // Restablecer isDefending después de un breve período
                buttonsEnabled = true;
            }
        }, 1.0f); // Ajusta la duración según sea necesario
    }

    private void attackEnemy() {
        if (attackAnimation != null) {
            isAttacking = true;
            stateTime = 0;

            // Reproducir sonido de ataque del personaje
            AudioManager.getInstance().playSound("audio/sound effects/SFX_Whoosh_Sword_01.mp3");

            // Realizar el ataque después de la duración de la animación
            Runnable attackRunnable = new Runnable() {
                @Override
                public void run() {
                    int damage = player.calculateDamage();
                    if (player.isCriticalHit()) {
                        showCriticalHit();
                    }
                    showDamage(damage);
                    enemy.receiveDamage(damage);
                    updateEnemyStatsDisplay();

                    if (!enemy.isAlive()) {
                        showVictoryDialog();
                    } else {
                        triggerEnemyAttack();
                    }
                    isAttacking = false;
                }
            };

            // Programar el Runnable después de la duración de la animación
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.postRunnable(attackRunnable);
                }
            }, attackAnimation.getAnimationDuration());
        } else {
            System.out.println("Animation not initialized.");
        }
    }

    private void updateEnemyStatsDisplay() {
        String enemyStats = enemy.getTextureName() + " HP= " + enemy.getHealth() + "/" + enemy.getMaxHealth();
        layout.setText(font, enemyStats);
    }

    private void animateAttack(float delta) {
        if (isAttacking) {
            stateTime += delta;
            TextureRegion currentFrame = attackAnimation.getKeyFrame(stateTime, false);
            int frameIndex = attackAnimation.getKeyFrameIndex(stateTime);

            if (attackAnimation.isAnimationFinished(stateTime)) {
                isAttacking = false;
            } else {
                TextureRegion playerImage = player.getPlayerTexture("Idle-3");
                float playerScale = (Gdx.graphics.getHeight() - 20) / playerImage.getRegionWidth();
                float imageX = 10;
                float imageY = 10;
                float originX = playerImage.getRegionWidth() / 2;
                float originY = playerImage.getRegionHeight() / 2;

                if (frameIndex == 2) {
                    imageY += -100;
                }
                if (frameIndex == 3) {
                    imageX += 200; // Adjust the value to move frame 4 to the right
                    imageY += -100;
                }
                if (frameIndex == 4) {
                    imageX += 200; // Adjust the value to move frame 4 to the right
                    imageY += -100;
                }

                // Conditionally rotate based on the frame index
                float rotation = (frameIndex == 0 || frameIndex == 1) ? 270 : 0;  // Rotate only the first two frames

                // Draw the current frame of the attack animation at the same position and size as the player
                batch.draw(currentFrame, imageX + originX * playerScale - originX, imageY + originY * playerScale - originY,
                        originX, originY, currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
                        playerScale, playerScale, rotation);
            }
        }
    }

    private void positionEnemy() {
        TextureRegion enemyImage = enemy.getEnemyTexture();
        float enemyScale = 2.0f;
        enemyX = Gdx.graphics.getWidth() - enemyImage.getRegionWidth() * enemyScale - 50;
        enemyY = (Gdx.graphics.getHeight() - enemyImage.getRegionHeight() * enemyScale) / 2;
    }

    private void renderPotionMenu() {
        // Background for potion menu
        batch.draw(borderTexture, potionMenuArea.x, potionMenuArea.y, potionMenuArea.width, potionMenuArea.height);

        if (potion30Button == null) {
            // Crear y configurar el botón de la poción 30
            ImageButton.ImageButtonStyle potion30ButtonStyle = new ImageButton.ImageButtonStyle();
            potion30ButtonStyle.imageUp = new TextureRegionDrawable(potion30Texture);

            potion30Button = new ImageButton(potion30ButtonStyle);
            potion30Button.setPosition(potionMenuArea.x , potionMenuArea.y + potionMenuArea.height - 300);
            potion30Button.setSize(200, 200);

            potion30Button.getImage().setFillParent(true);
            potion30Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    usePotion(Potion.PotionType.HEAL_30);
                }
            });

            stage.addActor(potion30Button);
        }

        if (potion100Button == null) {
            // Crear y configurar el botón de la poción 100
            ImageButton.ImageButtonStyle potion100ButtonStyle = new ImageButton.ImageButtonStyle();
            potion100ButtonStyle.imageUp = new TextureRegionDrawable(potion100Texture);

            potion100Button = new ImageButton(potion100ButtonStyle);
            potion100Button.setPosition(potionMenuArea.x , potionMenuArea.y + potionMenuArea.height - 500);
            potion100Button.setSize(200, 200);

            potion100Button.getImage().setFillParent(true);
            potion100Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    usePotion(Potion.PotionType.HEAL_100);
                }
            });

            stage.addActor(potion100Button);
        }

        // Renderizar botones y cantidad de pociones
        font.draw(batch, "Potion 30: " + player.getInventory().getPotionQuantity(Potion.PotionType.HEAL_30), potionMenuArea.x + 250, potionMenuArea.y + potionMenuArea.height - 80);
        font.draw(batch, "Potion 100: " + player.getInventory().getPotionQuantity(Potion.PotionType.HEAL_100), potionMenuArea.x + 300, potionMenuArea.y + potionMenuArea.height - 300);
    }

    private void usePotion(Potion.PotionType potionType) {
        float healAmount = 0;
        switch (potionType) {
            case HEAL_30:
                healAmount = player.getMaxHitPoints() * 0.3f; // Usa 'f' para asegurar que sea tratado como float
                break;
            case HEAL_100:
                healAmount = player.getMaxHitPoints(); // Mantén el tipo de dato consistente
                break;
        }

        if (player.getInventory().usePotion(potionType)) {
            player.heal((int)healAmount);
            closePotionMenu();
            triggerEnemyAttack();
        }
    }

    private void closePotionMenu() {
        isPotionMenuVisible = false;
        if (potion30Button != null) {
            potion30Button.remove();
            potion30Button = null;
        }
        if (potion100Button != null) {
            potion100Button.remove();
            potion100Button = null;
        }
        if (!isPlayerDead) {
            buttonsEnabled = true;
        }
    }

    @SuppressWarnings("SuspiciousIndentation")
    void showVictoryDialog() {
        if (rewardTextures == null) {
            // Incrementar experiencia del jugador
            int experienceGained = 100;
            player.gainExperience(experienceGained);

            // Generar recompensas una sola vez
            rewardTextures = generateRewards();

        }
        isVictoryMenuVisible = true;

        // Añadir listener para detectar clics fuera del menú de victoria y cerrar
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isVictoryMenuVisible && !victoryMenuArea.contains(x, Gdx.graphics.getHeight() - y)) {
                    isVictoryMenuVisible = false;
                    endCombat();
                }
            }
        });
    }

    private void renderVictoryMenu() {

        // Background for victory menu
        batch.draw(borderTexture, victoryMenuArea.x, victoryMenuArea.y, victoryMenuArea.width, victoryMenuArea.height);

        // Render victory text
        font.draw(batch, "Has derrotado al enemigo!", victoryMenuArea.x + 20, victoryMenuArea.y + victoryMenuArea.height - 40);
        font.draw(batch, "Experiencia obtenida: 100", victoryMenuArea.x + 20, victoryMenuArea.y + victoryMenuArea.height - 80);

        // Render rewards
        float itemX = victoryMenuArea.x + 20;
        float itemY = victoryMenuArea.y + victoryMenuArea.height - 220;
        for (TextureRegion texture : rewardTextures) {
            batch.draw(texture, itemX, itemY, 90, 90);
        itemX += 110; // Ajusta la separación horizontal
        if (itemX > victoryMenuArea.x + victoryMenuArea.width - 110) { // Ajusta el límite para el cambio de fila
            itemX = victoryMenuArea.x + 20;
            itemY -= 110; // Ajusta la separación vertical
        }
    }
}

    private List<TextureRegion> generateRewards() {
        Inventory playerInventory = player.getInventory();
        List<TextureRegion> rewardTextures = new ArrayList<>();
        Random random = new Random();

    // Generar equipamientos con un 50% de probabilidad
    for (int i = 0; i < 6; i++) { // Ajusta el número de intentos para generar equipamientos
        if (random.nextFloat() < 0.5) {
            Equipment equipment = equipableItems.createRandomItem();
            if (equipment != null) {
                playerInventory.addEquipment(equipment);
                rewardTextures.add(equipment.getTexture());
            } else {
                System.out.println("No se generó ningún ítem equipable.");
            }
        }
    }

        Potion.PotionType[] potionTypes = Potion.PotionType.values();
        for (Potion.PotionType potionType : potionTypes) {
            playerInventory.addPotion(potionType, 1);
            switch (potionType) {
                case HEAL_30:
                    rewardTextures.add(potion30Texture);
                    break;
                case HEAL_100:
                    rewardTextures.add(potion100Texture);
                    break;
            }
        }

        return rewardTextures;
    }

    @Override
    public void show() {
        positionEnemy();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        TextureRegion playerImage = isDefending ? protectTexture : player.getPlayerTexture("Idle-3");
        float playerScale = (Gdx.graphics.getHeight() - 20) / playerImage.getRegionWidth();
        float imageX = 10;
        float imageY = 10;
        float originX = playerImage.getRegionWidth() / 2;
        float originY = playerImage.getRegionHeight() / 2;
        float rotation = player.shouldRotate() ? 270 : 0;
        if (!isAttacking && !isHurt) {
            batch.draw(playerImage, imageX + originX * playerScale - originX, imageY + originY * playerScale - originY,
                    originX, originY, playerImage.getRegionWidth(), playerImage.getRegionHeight(),
                    playerScale, playerScale, rotation);
        }

        if (isHurt && !isDefending) {
            animateHurt(delta);

        }

        animateAttack(delta); // Manejar la animación de ataque si está activa

        TextureRegion enemyImage = enemy.getEnemyTexture();
        float enemyScale = 2.0f;
        float enemyY = (Gdx.graphics.getHeight() - enemyImage.getRegionHeight() * enemyScale) / 2;
        float enemyRotation = enemy.shouldRotate() ? 270 : 0;
        batch.draw(enemyImage, enemyX, enemyY, originX, originY, enemyImage.getRegionWidth(), enemyImage.getRegionHeight(),
                enemyScale, enemyScale, enemyRotation);

        batch.draw(borderTexture, lowerBorderArea.x, lowerBorderArea.y, lowerBorderArea.width, lowerBorderArea.height);
        displayStats();

        if (isPotionMenuVisible) {
            renderPotionMenu();
        }

        if (isVictoryMenuVisible) {
            renderVictoryMenu();
        }

        batch.end();

        if (isPotionMenuVisible && Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (!potionMenuArea.contains(touchX, touchY)) {
                closePotionMenu();
            }
        }

        stage.act(delta);
        stage.draw();

        if (enemyMoving) {
            float deltaX = 500 * delta;
            enemyX -= deltaX;
        }


    }
    private void endCombat() {
        gameplayScreen.endCombat();

        if (AudioManager.getInstance().getCurrentMusic() == null ||
                !AudioManager.getInstance().getCurrentMusicFilePath().equals("audio/music/Golden Serpant Tavern (LOOP).mp3")) {
            AudioManager.getInstance().playMusic("audio/music/Golden Serpant Tavern (LOOP).mp3");
        }

        game.setScreen(gameplayScreen);
    }


    private void displayStats() {
        if (font != null) {
            String playerStats = player.getPlayerName() + " HP= " + player.getHitPoints() + "/"+ player.getMaxHitPoints();
            String enemyStats = enemy.getTextureName() + " HP= " + enemy.getHealth() + "/" + enemy.getMaxHealth();
            float statsY = lowerBorderArea.y + lowerBorderArea.height - 20;
            font.draw(batch, playerStats, lowerBorderArea.x + 30, statsY);
            layout.setText(font, enemyStats);
            float enemyStatsX = lowerBorderArea.x + lowerBorderArea.width - layout.width - 30;
            font.draw(batch, enemyStats, enemyStatsX, statsY);
        }
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
        itemAtlas.dispose();
        if (font != null) font.dispose();
    }
}