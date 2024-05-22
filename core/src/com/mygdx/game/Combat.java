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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Combat implements Screen {
    private MyGdxGame game;
    private Stage stage;
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private TextureAtlas itemAtlas;
    private TextureRegion borderTexture;
    private Rectangle lowerBorderArea;
    private Rectangle enemyArea;
    private Rectangle potionMenuArea;
    private Player player;
    private Enemies enemy;
    private Button attackButton, defendButton, useItemButton, escapeButton;
    private BitmapFont font;
    private boolean isPotionMenuVisible = false;
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

    public Combat(MyGdxGame game, TextureRegion background) {
        this.layout = new GlyphLayout();
        this.game = game;
        this.backgroundTexture = background; // Guarda el fondo pasado desde GameplayScreen
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.itemAtlas = new TextureAtlas("images/items/items.atlas");
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.potion30Texture = itemAtlas.findRegion("potion30");
        this.potion100Texture = itemAtlas.findRegion("potion100");
        this.player = Player.getInstance();
        this.enemy = new Enemies(game);
        this.equipableItems = new EquipableItems();

        setupAnimations();
        initUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void setupAnimations() {
        TextureAtlas playerAtlas = new TextureAtlas(Gdx.files.internal("images/Main_Character/main_Character.atlas"));

        // Manually add each frame to the animation
        Array<TextureRegion> frames = new Array<>();
        frames.add(playerAtlas.findRegion("Attack 1-0"));
        frames.add(playerAtlas.findRegion("Attack 1-1"));
        frames.add(playerAtlas.findRegion("Attack 1-2"));
        frames.add(playerAtlas.findRegion("Attack 1-3"));
        frames.add(playerAtlas.findRegion("Attack 1-4"));

        // Check if any frames are missing to avoid NullPointerException
        for (TextureRegion frame : frames) {
            if (frame == null) {
                System.out.println("One or more animation frames are missing!");
                return;
            }
        }

        // Create the animation with the frames array
        attackAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);
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

        // Configure button styles
        font = new BitmapFont(); // Use a larger or customized font
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
                player.receiveDamage(enemy.getDamage());
                enemyX = originalX;  // Reset position
                enemyAttacking = false; // Disable enemy attack animation
                enemyMoving = false; // Disable enemy moving animation
                buttonsEnabled = true;  // Re-enable buttons after the attack
            }
        }, 0.4f);  // Delay to simulate the attack movement
    }

    private void defend() {
        player.defend();
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

        // Render potion 30
        batch.draw(potion30Texture, potionMenuArea.x + 30, potionMenuArea.y + potionMenuArea.height - 70, 50, 50);
        font.draw(batch, "Potion 30: " + player.getInventory().getPotionQuantity(Potion.PotionType.HEAL_30), potionMenuArea.x + 100, potionMenuArea.y + potionMenuArea.height - 50);

        // Render potion 100
        batch.draw(potion100Texture, potionMenuArea.x + 30, potionMenuArea.y + potionMenuArea.height - 150, 50, 50);
        font.draw(batch, "Potion 100: " + player.getInventory().getPotionQuantity(Potion.PotionType.HEAL_100), potionMenuArea.x + 100, potionMenuArea.y + potionMenuArea.height - 130);
    }

    private void showVictoryDialog() {
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameplayScreen(game));
            }
        });

        batch.begin();
        float dialogX = (Gdx.graphics.getWidth() - borderTexture.getRegionWidth()) / 2;
        float dialogY = (Gdx.graphics.getHeight() - borderTexture.getRegionHeight()) / 2;
        batch.draw(borderTexture, dialogX, dialogY, borderTexture.getRegionWidth(), borderTexture.getRegionHeight());

        List<TextureRegion> rewardTextures = generateRewards();
        float itemX = dialogX + 50;
        float itemY = dialogY + borderTexture.getRegionHeight() - 100;

        for (TextureRegion texture : rewardTextures) {
            batch.draw(texture, itemX, itemY, 50, 50);
            itemX += 60;
        }

        batch.end();
    }

    private List<TextureRegion> generateRewards() {
        Inventory playerInventory = player.getInventory();
        List<TextureRegion> rewardTextures = new ArrayList<>();
        Random random = new Random();

        if (random.nextFloat() < 4) { // 50% probability to generate an equipable item
            Equipment equipment = equipableItems.createRandomItem();
            if (equipment != null) { // Check if equipment was successfully created
            playerInventory.addEquipment(equipment);
            rewardTextures.add(equipment.getTexture());
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
        TextureRegion playerImage = player.getPlayerTexture("Idle-3");
        float playerScale = (Gdx.graphics.getHeight() - 20) / playerImage.getRegionWidth();
        float imageX = 10;
        float imageY = 10;
        float originX = playerImage.getRegionWidth() / 2;
        float originY = playerImage.getRegionHeight() / 2;
        float rotation = player.shouldRotate() ? 270 : 0;
        if (!isAttacking) {
            batch.draw(playerImage, imageX + originX * playerScale - originX, imageY + originY * playerScale - originY,
                    originX, originY, playerImage.getRegionWidth(), playerImage.getRegionHeight(),
                    playerScale, playerScale, rotation);
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

        batch.end();

        // Handle click outside potion menu to close it
        if (isPotionMenuVisible && Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Adjust for coordinate system
            if (!potionMenuArea.contains(touchX, touchY)) {
                isPotionMenuVisible = false;
                buttonsEnabled = true;
            }
        }

        stage.act(delta);
        stage.draw();

        // Update enemy position during attack animation
        if (enemyMoving) {
            float deltaX = 500 * delta;
            enemyX -= deltaX;
        }
    }

    private void displayStats() {
        String playerStats = player.getPlayerName() + " HP= " + player.getHitPoints();
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
    }
}
