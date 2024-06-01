package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class InitialScreen implements Screen {
    private Stage stage;
    private final MyGdxGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font = new BitmapFont();
    private List<String> gameTexts;
    private int currentTextIndex = 0;
    private boolean backgroundVisible = true;
    private Rectangle textBox;
    private Texture frameTexture;

    private TextureRegion background;
    private TextField nameField;
    private TextButton submitButton;
    private String playerName;
    private boolean nameEntered = false;
    private Label nameLabel;
    private float touchCooldown = 200;
    private long lastTouchTime = 0;

    private List<TextureRegion> backgrounds;
    private TextureRegion currentBackground;
    private TextureAtlas backgroundsAtlas;
    private TextureAtlas atlas;

    private boolean firstClickHandled = false;

    public InitialScreen(MyGdxGame game) {
        this.game = game;
        this.camera = game.getCamera();
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport(camera), batch);
        loadResources();
        Gdx.input.setInputProcessor(stage);

        backgroundsAtlas = new TextureAtlas(Gdx.files.internal("images/intro_elden/Intro_BG.atlas"));

        initNameFieldUI();
        playerName = Player.getInstance(game).getPlayerName();
        if (AudioManager.getInstance().getCurrentMusic() == null) {
            AudioManager.getInstance().playMusic("audio/music/intro/Battle-Dawn_loop.m4a");
        }
    }

    private void loadResources() {
        atlas = new TextureAtlas(Gdx.files.internal("images/TFG_Atlas_1.atlas"));
    }

    private void initNameFieldUI() {
        BitmapFont nameFieldFont= new BitmapFont();
        nameFieldFont.getData().setScale(3f);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = nameFieldFont;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("MenuBox2")));

        // Campo de texto

        textFieldStyle.font = nameFieldFont;
        nameField = new TextField("", textFieldStyle);
        nameField.setPosition(Gdx.graphics.getWidth() / 2f - 200, Gdx.graphics.getHeight() - 150);
        nameField.setSize(400, 100);
        stage.addActor(nameField);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        font.getData().setScale(2.5f);

        nameLabel = new Label("Introduzca su nombre", new Label.LabelStyle(font, Color.WHITE));
        nameLabel.setPosition(nameField.getX(), nameField.getY() - nameLabel.getHeight() - 5);
        stage.addActor(nameLabel);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("Check-up")));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("Check-down")));
        buttonStyle.font = new BitmapFont();

        submitButton = new TextButton("", buttonStyle);
        submitButton.setPosition(nameField.getX() + nameField.getWidth() + 10, nameField.getY());
        submitButton.setSize(70, 70);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playerName = nameField.getText();
                if (!playerName.isEmpty()) {
                    Player.getInstance(game).setPlayerName(playerName);
                    nameEntered = true;
                    initGameUI();
            }
    }
        });
        stage.addActor(submitButton);
    }
    private void initGameUI() {
        nameField.remove();
        submitButton.remove();
        nameLabel.remove();
        initTextsAndBackgrounds();
    }

    private void renderGameTexts() {
        if (textBox == null) {
            return;
        }

        batch.begin();
        batch.draw(frameTexture, textBox.x, textBox.y, textBox.width, textBox.height);

        if (gameTexts != null && !gameTexts.isEmpty()) {
            font.setColor(Color.WHITE);
            font.getData().setScale(3);
            font.draw(batch, gameTexts.get(currentTextIndex), textBox.x + 20, textBox.y + (textBox.height / 2) + 30 + font.getLineHeight() / 2, textBox.width - 40, Align.center, true);
        }
        batch.end();

        handleInput();
    }
    private void handleInput() {
        if (Gdx.input.justTouched() && nameEntered) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTouchTime > touchCooldown) {
                int x = Gdx.input.getX();
                int y = Gdx.graphics.getHeight() - Gdx.input.getY();

                if (textBox.contains(x, y)) {
                    lastTouchTime = currentTime;
                    currentTextIndex++;
                    if (currentTextIndex >= gameTexts.size()) {
                        game.setScreen(new GameplayScreen(game, 0, 0));
                        return;
                    }

                    if (!firstClickHandled) {
                        firstClickHandled = true;
                    } else {
                    if (currentTextIndex - 1 < backgrounds.size()) {
                        currentBackground = backgrounds.get(currentTextIndex - 1);
                        }
                    }
                }
            }
        }
    }



    @Override
    public void show() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport(camera), batch);
        Gdx.input.setInputProcessor(stage);
        initNameFieldUI();

        frameTexture = new Texture(Gdx.files.internal("backgrounds/MenuBox2.png"));

        float textBoxWidth = Gdx.graphics.getWidth();
        float textBoxHeight = Gdx.graphics.getHeight() * 0.3f;
        float textBoxX = 0;
        float textBoxY = 0;
        textBox = new Rectangle(textBoxX, textBoxY, textBoxWidth, textBoxHeight);
    }


    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (nameEntered) {
            if (backgroundVisible) {
                batch.begin();
                batch.draw(currentBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                batch.end();
            }

            batch.begin();
            if (frameTexture != null) {
                batch.draw(frameTexture, textBox.x, textBox.y, textBox.width, textBox.height);
            }

            if (!gameTexts.isEmpty()) {
                font.draw(batch, gameTexts.get(currentTextIndex), textBox.x + 20, textBox.y + textBox.height / 2 + 10, textBox.width - 40, Align.center, true);
            }
            batch.end();
        }

        handleInput();
    }



    private void initTextsAndBackgrounds() {
        gameTexts = new ArrayList<>();
        backgrounds = new ArrayList<>();

        gameTexts.add("El susurro de las hojas caídas narra una historia de gloria perdida.");
        gameTexts.add("En otro tiempo, el poderoso Anillo de Elden se alzaba entero e invicto, una luz en los Territorios Intermedios. Pero ahora, yace hecho añicos, roto por las manos del destino.");

        backgrounds.add(backgroundsAtlas.findRegion("marika_shatered_EldenRing"));
        gameTexts.add("En la estela de este cataclismo, reina el caos supremo. La Reina Amigdala, soberana eterna, ha desaparecido en las sombras, dejando atrás un reino tambaleante al borde de la perdición.");
        backgrounds.add(backgroundsAtlas.findRegion("marika"));
        gameTexts.add("En el caos posterior, los vástagos de Marika, semidioses nacidos de la divinidad y la mortalidad, aferran los fragmentos del anillo roto. Pero su codicia por el poder solo acelera la caída hacia la locura, desencadenando una guerra que no deja vencedores ni señores para reclamar dominio.");
        backgrounds.add(backgroundsAtlas.findRegion("mogh"));
        gameTexts.add("La Noche de los Cuchillos Negros se cobró al valiente Judeau el Dorado, vástago de la reina, un presagio de días aún más oscuros por venir." );
        backgrounds.add(backgroundsAtlas.findRegion("godwyn"));

        gameTexts.add("Mientras el velo del destino se deshilacha, un llamado resuena a través de las tierras envueltas en la niebla, los no muertos fuisteis convocados , aquellos descartados y olvidados, pero aún atados por el hilo del destino.");
        backgrounds.add(backgroundsAtlas.findRegion("Horah_loux"));
        gameTexts.add("Entre las brumas de los Territorios Intermedios, la oscuridad se cierne como un manto, un recordatorio constante de los peligros que acechan en cada sombra. Criaturas de pesadilla acechan en las profundidades de antiguas mazmorras, susurrando de tiempos olvidados y secretos oscuros.");

        backgrounds.add(backgroundsAtlas.findRegion("lands_in_between"));
        gameTexts.add("En este mundo  donde la luz apenas se atreve a penetrar las sombras, los corazones de los valientes aún arden con la esperanza de restaurar la gloria perdida y enfrentar el destino que les aguarda.");
        gameTexts.add("En el vasto lienzo de los Territorios Intermedios, un enigma se materializa entre las sombras. "+playerName+" un no muerto cuyo nombre apenas susurra el viento, emerge como un reflejo de la desgracia que yace en el corazón de estas tierras fracturadas.");
        gameTexts.add("Cuando el eco de la mazmorra resuena en su alma, "+playerName+" ve en ello una llamada, un destino entrelazado con las cicatrices del Anillo de Elden. La búsqueda de redención y significado lo impulsa hacia lo desconocido, hacia las profundidades donde los fragmentos del pasado yacen ocultos entre las sombras");

        if (!backgrounds.isEmpty()) {
            currentBackground = backgrounds.get(0);
        }
    }
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {
        if (!(game.getScreen() instanceof GameplayScreen)) {
            //AudioManager.getInstance().stopMusic();
        }
    }
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundsAtlas.dispose();
        atlas.dispose();
    }
}