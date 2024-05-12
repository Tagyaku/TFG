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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
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

    private TextureRegion backgroundTexture;
    private GlyphLayout layout;
    private float stateTime;
    private Animation<TextureRegion> attackAnimation; // Animación del ataque
    private boolean isAttacking = false; // Estado de ataque

    public Combat(MyGdxGame game, TextureRegion background) {
    // Inicialización en el constructor
    this.layout = new GlyphLayout();
        this.game = game;
        this.backgroundTexture = background; // Guarda el fondo pasado desde GameplayScreen
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.player = Player.getInstance();
        this.enemy = new Enemies();

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

    // Configuración del estilo de los botones
    font = new BitmapFont();  // Se podría utilizar una fuente más grande o personalizada
    font.getData().setScale(3);  // Aumentar el tamaño de la fuente
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = font;

        attackButton = new TextButton("Attack", buttonStyle);
        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attackEnemy();
            }
        });

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


    private void attackEnemy() {
        if (attackAnimation != null) {
        int damage = player.calculateDamage();
        enemy.receiveDamage(damage);
            isAttacking = true;
            stateTime = 0;
        } else {
            System.out.println("Animation not initialized.");
        }
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



    @Override
    public void show() {}

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
        float rotation = 270;
        if (!isAttacking) {
            batch.draw(playerImage, imageX + originX * playerScale, imageY + originY * playerScale, originX, originY, playerImage.getRegionWidth(), playerImage.getRegionHeight(), playerScale, playerScale, rotation);
        }
        animateAttack(delta); // Manejar la animación de ataque si está activa

        // Configuración y dibujo del enemigo asegurando que no toque al jugador
        TextureRegion enemyImage = enemy.getEnemyTexture();
        float enemyScale = (Gdx.graphics.getHeight() - 20) / enemyImage.getRegionHeight();
        float enemyX = imageX + playerImage.getRegionWidth() * playerScale + 50; // Margen de 50 píxeles entre jugador y enemigo
        float enemyY = 10;
        batch.draw(enemyImage, enemyX, enemyY, 0, 0, enemyImage.getRegionWidth(), enemyImage.getRegionHeight(), enemyScale, enemyScale, 0);

        // Dibuja el borde
        batch.draw(borderTexture, lowerBorderArea.x, lowerBorderArea.y, lowerBorderArea.width, lowerBorderArea.height);

        // Muestra información del jugador
        String playerStats = player.getPlayerName() + " HP= " + player.getHitPoints() + "/" + (10 + player.getVitality() * 2);
        font.draw(batch, playerStats, lowerBorderArea.x + 10, lowerBorderArea.y + lowerBorderArea.height - 10);

        // Muestra información del enemigo
        String enemyStats = enemy.getTextureName() + " HP= " + enemy.getHealth() + "/" + Enemies.DEFAULT_HEALTH;
        layout.setText(font, enemyStats); // Usar GlyphLayout para medir el ancho del texto
        font.draw(batch, enemyStats, lowerBorderArea.x + lowerBorderArea.width - layout.width - 10, lowerBorderArea.y + lowerBorderArea.height - 10);

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
