package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
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
    private MyGdxGame game;
    private GameplayScreen gameplayScreen; // Añadir esto

    private Stage stage;
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private TextureAtlas itemAtlas;
    private TextureRegion borderTexture;
    private Rectangle lowerBorderArea;
    private Rectangle enemyArea;
    private Rectangle potionMenuArea;
    private Rectangle victoryMenuArea;
    private Player player;
    private Enemies enemy;
    private Button attackButton, defendButton, useItemButton, escapeButton;
    private BitmapFont font;
    private boolean isPotionMenuVisible = false;
    private boolean isVictoryMenuVisible = false;
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
    private List<TextureRegion> rewardTextures; // Almacenar recompensas
    private ImageButton potion30Button;
    private ImageButton potion100Button;
    private TextureRegion protectTexture;
    private boolean isDefending = false;
    private Animation<TextureRegion> hurtAnimation;
    private boolean isHurt = false;
    private Label criticalHitLabel;
    private Label damageLabel;
    private BitmapFont specialFont;
    public Combat(MyGdxGame game, GameplayScreen gameplayScreen) {
        this.layout = new GlyphLayout();
        this.game = game;
        this.gameplayScreen = gameplayScreen; // Guardar la instancia de GameplayScreen

        this.backgroundTexture = gameplayScreen.currentBackground; // Usar el fondo actual
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.itemAtlas = new TextureAtlas("images/items/items.atlas");
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.potion30Texture = itemAtlas.findRegion("potion30");
        this.potion100Texture = itemAtlas.findRegion("potion100");
        this.player = Player.getInstance();
        this.enemy = new Enemies(game, this); // Pasa la instancia de Combat al constructor de Enemies
        this.equipableItems = new EquipableItems();
        // Cargar la fuente desde el archivo
        specialFont = new BitmapFont(Gdx.files.internal("skin/fonts/default.fnt"));
        specialFont.getData().setScale(2); // Ajusta el tamaño de la fuente

        // Inicializa la fuente general para otros textos
        font = new BitmapFont();
        setupAnimations();
        initUI();
        Gdx.input.setInputProcessor(stage);
    }
    private void playerReceiveDamage(int damage) {
    if (!isDefending) { // Verifica si el jugador no está defendiendo
        player.receiveDamage(damage);
        isHurt = true;
        buttonsEnabled = false; // Deshabilitar botones
        stateTime = 0; // Reset animation state time

        // Schedule to stop the hurt animation after it completes
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isHurt = false;
                buttonsEnabled = true; // Habilitar botones después de la animación
            }
        }, hurtAnimation.getAnimationDuration());
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

        // Configure button styles
        font.getData().setScale(3); // Increase font size
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        attackButton = new TextButton("Attack", buttonStyle);
        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonsEnabled) {
                    buttonsEnabled = false; // Disable buttons
                    attackEnemy();
                }
            }
        });

        defendButton = new TextButton("Defend", buttonStyle);
        defendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonsEnabled) {
                    buttonsEnabled = false; // Disable buttons
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

        escapeButton = new TextButton("Escape", buttonStyle);
        escapeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonsEnabled) {
                    buttonsEnabled = false; // Disable buttons
                    attemptEscape();
                }
            }
        });

        // Button width and spacing
        float buttonWidth = (lowerBorderArea.width - 80) / 4; // Distribute area width among buttons, leaving space
        float buttonHeight = 50; // Fixed height for all buttons
        float spacing = 13; // Space between buttons

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
        }, 0.8f); // Display for 0.5 seconds
    }

    private void showDamage(int damage) {
    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();

        damageLabel.setText(damage +" DMG!");
    // Posiciona el mensaje en el centro arriba de la pantalla
    damageLabel.setPosition((screenWidth - damageLabel.getWidth()) / 2, screenHeight - damageLabel.getHeight() - 50);

        damageLabel.setVisible(true);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                damageLabel.setVisible(false);
            }
        }, 0.8f); // Display for 0.5 seconds
    }

    private void triggerEnemyAttack() {
        enemyAttacking = true; // Enable enemy attack animation
        enemyAttack();
    }

    private void enemyAttack() {
        // Asegurarse de que todos los botones están desactivados durante el ataque
        buttonsEnabled = false;
        enemyMoving = true;
        float originalX = enemyX;
        float attackMovement = 500;  // Distance to move left for the animation

        // Step 1: Move enemy to the left (simulating attack)
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                enemyX -= attackMovement;
            }
        }, 0.3f);

        // Step 2: Deal damage and reset position after a short delay
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

    // Trigger enemy attack after a delay to allow defend animation to show
    Timer.schedule(new Timer.Task() {
        @Override
        public void run() {
            //triggerEnemyAttack();
        }
    }, 1.0f); // Adjust the delay as needed
    }

    private void attackEnemy() {
        if (attackAnimation != null) {
            isAttacking = true; // Iniciar animación de ataque
            stateTime = 0; // Reiniciar el tiempo de animación

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
                    updateEnemyStatsDisplay();  // Actualiza la información de la vida del enemigo en la UI.

                    if (!enemy.isAlive()) {
                        showVictoryDialog();
                    } else {
                        triggerEnemyAttack();
                    }
                    isAttacking = false; // Detener la animación del ataque.
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
        // Este método actualiza el texto que muestra la salud actual del enemigo en la interfaz de usuario.
        String enemyStats = enemy.getTextureName() + " HP= " + enemy.getHealth() + "/" + enemy.getMaxHealth();
        layout.setText(font, enemyStats);  // Pre-calcula el layout del texto para su correcta visualización.
        // Asumiendo que tienes una forma de dibujar este texto en tu método render(), solo necesitas actualizar el texto aquí.
    }

    private void animateAttack(float delta) {
        if (isAttacking) {
            stateTime += delta; // Update the state time for the animation
            TextureRegion currentFrame = attackAnimation.getKeyFrame(stateTime, false);
            int frameIndex = attackAnimation.getKeyFrameIndex(stateTime);

            if (attackAnimation.isAnimationFinished(stateTime)) {
                isAttacking = false; // Stop the animation once it finishes
            } else {
                // Player's position and scale setup
                TextureRegion playerImage = player.getPlayerTexture("Idle-3");
                float playerScale = (Gdx.graphics.getHeight() - 20) / playerImage.getRegionWidth();
                float imageX = 10;
                float imageY = 10;
                float originX = playerImage.getRegionWidth() / 2;
                float originY = playerImage.getRegionHeight() / 2;

            // Adjust position for frame 4
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
        float enemyScale = 2.0f; // Set a fixed scale factor for the enemy
        enemyX = Gdx.graphics.getWidth() - enemyImage.getRegionWidth() * enemyScale - 50;  // Position enemy on the right side with some padding
        enemyY = (Gdx.graphics.getHeight() - enemyImage.getRegionHeight() * enemyScale) / 2;  // Center vertically
        }

    private void attemptEscape() {
        double escapeChance = Math.random();
        if (escapeChance <= 0.3) {
            game.setScreen(new GameplayScreen(game)); // Successful escape
        } else {
            triggerEnemyAttack(); // Failed escape, enemy attacks
        }
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
    int healAmount = 0;
    switch (potionType) {
        case HEAL_30:
            healAmount = 30;
            break;
        case HEAL_100:
            healAmount = 100;
            break;
    }

    if (player.getInventory().usePotion(potionType)) {
        player.heal(healAmount);
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
        buttonsEnabled = true;
    }


 @SuppressWarnings("SuspiciousIndentation")
 void showVictoryDialog() {
    if (rewardTextures == null) {
        // Incrementar experiencia del jugador
        int experienceGained = 40;
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
                    endCombat(); // Finalizar el combate
                }
            }
        });
    }

    private void renderVictoryMenu() {

        // Background for victory menu
        batch.draw(borderTexture, victoryMenuArea.x, victoryMenuArea.y, victoryMenuArea.width, victoryMenuArea.height);

        // Render victory text
        font.draw(batch, "You've defeated the enemy!", victoryMenuArea.x + 20, victoryMenuArea.y + victoryMenuArea.height - 40);
        font.draw(batch, "Experience gained: 40", victoryMenuArea.x + 20, victoryMenuArea.y + victoryMenuArea.height - 80);

        // Render rewards
        float itemX = victoryMenuArea.x + 20;
        float itemY = victoryMenuArea.y + victoryMenuArea.height - 220;
        for (TextureRegion texture : rewardTextures) {
            batch.draw(texture, itemX, itemY, 90, 90);
            itemX += 130;
        }
    }

    private List<TextureRegion> generateRewards() {
        Inventory playerInventory = player.getInventory();
        List<TextureRegion> rewardTextures = new ArrayList<>();
        Random random = new Random();

    if (random.nextFloat() < 1) { // Cambiado a 1 para asegurar que se genera siempre un ítem
            Equipment equipment = equipableItems.createRandomItem();
        if (equipment != null) { // Comprobar si se creó correctamente el equipo
                playerInventory.addEquipment(equipment);
                rewardTextures.add(equipment.getTexture());
        } else {
            System.out.println("No se generó ningún ítem equipable.");
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
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Dibuja el fondo
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Configuración y dibujo del jugador
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

    if (isHurt && !isDefending) { // Renderizar animación de daño solo si no se está defendiendo
        animateHurt(delta);

    }

        animateAttack(delta); // Manejar la animación de ataque si está activa

        // Configuración y dibujo del enemigo asegurando que se muestre en el centro de la pantalla
        TextureRegion enemyImage = enemy.getEnemyTexture();
        float enemyScale = 2.0f; // Set a fixed scale factor for the enemy
        float enemyY = (Gdx.graphics.getHeight() - enemyImage.getRegionHeight() * enemyScale) / 2;  // Center vertically
        float enemyRotation = enemy.shouldRotate() ? 270 : 0;
        batch.draw(enemyImage, enemyX, enemyY, originX, originY, enemyImage.getRegionWidth(), enemyImage.getRegionHeight(),
                enemyScale, enemyScale, enemyRotation);

        // Dibuja el borde
        batch.draw(borderTexture, lowerBorderArea.x, lowerBorderArea.y, lowerBorderArea.width, lowerBorderArea.height);

        // Muestra información del jugador y enemigo
        displayStats();

        if (isPotionMenuVisible) {
            renderPotionMenu();
        }

        if (isVictoryMenuVisible) {
            renderVictoryMenu();
        }

        batch.end();

        // Handle click outside potion menu to close it
        if (isPotionMenuVisible && Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Adjust for coordinate system
            if (!potionMenuArea.contains(touchX, touchY)) {
            closePotionMenu();
            }
        }

        stage.act(delta);
        stage.draw();

        // Update enemy position during attack animation
        if (enemyMoving) {
            float deltaX = 500 * delta;
            enemyX -= deltaX;
        }

    // Reset isDefending after showing the protection for a brief period
    if (isDefending) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isDefending = false;
            }
        }, 1.0f); // Adjust the duration as needed
    }
}
    private void endCombat() {
        gameplayScreen.endCombat(); // Llamar al método para finalizar el combate en GameplayScreen
        game.setScreen(gameplayScreen); // Volver a GameplayScreen
    }


    private void displayStats() {
        if (font != null) {
        String playerStats = player.getPlayerName() + " HP= " + player.getHitPoints() + "/"+ player.getMaxHitPoints();
        String enemyStats = enemy.getTextureName() + " HP= " + enemy.getHealth() + "/" + enemy.getMaxHealth();

        // Calculate the y-coordinate to align with the top of the border
        float statsY = lowerBorderArea.y + lowerBorderArea.height -20; // Adjust this value based on your font size and desired padding

        // Display the player stats on the left
        font.draw(batch, playerStats, lowerBorderArea.x + 30, statsY);

        // Calculate the width of the enemy stats to right-align them
        layout.setText(font, enemyStats);
        float enemyStatsX = lowerBorderArea.x + lowerBorderArea.width - layout.width - 30; // Right-align text
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
        itemAtlas.dispose(); // Dispose itemAtlas as well
        if (font != null) font.dispose();
    }
}