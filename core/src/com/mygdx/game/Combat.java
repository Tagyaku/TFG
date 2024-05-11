package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

    public Combat(MyGdxGame game, TextureRegion background) {
        this.game = game;
        this.backgroundTexture = background; // Guarda el fondo pasado desde GameplayScreen
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        this.atlas = new TextureAtlas("images/TFG_Atlas_1.atlas");
        this.borderTexture = atlas.findRegion("MenuBox2");
        this.player = Player.getInstance();
        this.enemy = new Enemies();

        initUI();
        Gdx.input.setInputProcessor(stage);
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

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Obtén la imagen del jugador y ajusta su escala y rotación
        TextureRegion playerImage = player.getPlayerTexture("Idle-3"); // Asegúrate de que 'Idle-3' es el estado correcto
        // Escala la imagen para que ocupe toda la altura de la pantalla menos un pequeño margen
        float playerScale = (Gdx.graphics.getHeight() - 20) / playerImage.getRegionWidth();
        // Ajusta la posición x para centrar la imagen dentro del margen izquierdo y el borde de la pantalla
        float imageX = 10;
        // El margen superior ajusta la imagen para que esté centrada verticalmente dentro del área disponible
        float imageY = 10;

        // Punto de origen para la rotación (centro de la imagen original)
        float originX = playerImage.getRegionWidth() / 2;
        float originY = playerImage.getRegionHeight() / 2;

        // Rota la imagen 90 grados para ponerla de pie (270 grados si se necesita al revés)
        float rotation = 270;

        // Dibuja el fondo y el borde
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Ajusta la posición de dibujo y la escala para que la imagen quede bien posicionada
        batch.draw(playerImage, imageX + originX * playerScale, imageY + originY * playerScale, originX, originY, playerImage.getRegionWidth(), playerImage.getRegionHeight(), playerScale, playerScale, rotation);
        batch.draw(borderTexture, lowerBorderArea.x, lowerBorderArea.y, lowerBorderArea.width, lowerBorderArea.height);
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
