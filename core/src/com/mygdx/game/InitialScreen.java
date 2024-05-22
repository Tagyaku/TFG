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
    private final MyGdxGame game; // Referencia al juego principal
    private OrthographicCamera camera; // Referencia a la cámara del juego
    private SpriteBatch batch; // Lote de sprites para dibujar
    private BitmapFont font= new BitmapFont(); // Fuente para el texto
    private List<String> gameTexts; // Lista de textos para mostrar en el juego
    private int currentTextIndex = 0; // Índice del texto actual
    private boolean backgroundVisible = true; // Indica si la imagen de fondo debe ser visible
    private Rectangle textBox; // Cuadro de texto donde se mostrarán los textos
    private Texture frameTexture; // Textura del marco del cuadro de texto

    private TextureRegion background;
    private TextField nameField;
    private TextButton submitButton;
    private String playerName;
    private boolean nameEntered = false;
    private Label nameLabel;
    private float touchCooldown = 200; // Tiempo en milisegundos entre toques válidos
    private long lastTouchTime = 0; // Tiempo desde el último toque

    private List<TextureRegion> backgrounds; // Lista para almacenar las texturas de los fondos
    private TextureRegion currentBackground; // La textura de fondo actual mostrada
    private TextureAtlas backgroundsAtlas;

    private boolean firstClickHandled = false;  // Nueva variable para controlar el primer clic


    public InitialScreen(MyGdxGame game) {
        this.game = game;
        this.camera = game.getCamera(); // Obtener la cámara del juego desde MyGdxGame
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport(camera), batch);
        SkinManager.loadResources();  // Make sure to load resources
        Gdx.input.setInputProcessor(stage);

        backgroundsAtlas = new TextureAtlas(Gdx.files.internal("images/intro_elden/Intro_BG.atlas"));



        initNameFieldUI(); // Llamamos a la inicialización del UI del nombre
        //initTextsAndBackgrounds();
        playerName = Player.getInstance().getPlayerName();

    }

    private void initNameFieldUI() {
        // Usando nombres de regiones del atlas
        BitmapFont nameFieldFont= new BitmapFont();
        nameFieldFont.getData().setScale(3f);  // Aumenta la escala para hacer el texto más grande

        //background = new TextureRegion(SkinManager.getAtlas().findRegion("MainTitleCavesBackground"));
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = nameFieldFont;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(SkinManager.getAtlas().findRegion("MenuBox2")));

        // Campo de texto

        textFieldStyle.font = nameFieldFont;
        nameField = new TextField("", textFieldStyle);
        nameField.setPosition(Gdx.graphics.getWidth() / 2f - 200, Gdx.graphics.getHeight() - 150); // Ajusta para centrar y colocar en la parte superior
        nameField.setSize(400, 100);
        stage.addActor(nameField);

        // Estilo para el label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        font.getData().setScale(2.5f);
        // Label para el mensaje
        nameLabel = new Label("Introduzca su nombre", new Label.LabelStyle(font, Color.WHITE));
        nameLabel.setPosition(nameField.getX(), nameField.getY() - nameLabel.getHeight() - 5);
        stage.addActor(nameLabel);
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(SkinManager.getAtlas().findRegion("Check-up")));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(SkinManager.getAtlas().findRegion("Check-down")));
        buttonStyle.font = new BitmapFont(); // Considerar el uso de un BitmapFont cargado correctamente

        submitButton = new TextButton("", buttonStyle);
        submitButton.setPosition(nameField.getX() + nameField.getWidth() + 10, nameField.getY()); // A la derecha del campo de texto
        submitButton.setSize(70, 70);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playerName = nameField.getText();
        if (!playerName.isEmpty()) {  // Asegúrate de que el nombre no esté vacío
                Player.getInstance().setPlayerName(playerName);  // Guarda el nombre del jugador en el GameManager
                nameEntered = true; // Cambiamos el estado a verdadero
                initGameUI(); // Inicializamos el UI del juego
            }
    }
        });
        stage.addActor(submitButton);
    }
    private void initGameUI() {
        // Limpiar UI de nombre
        nameField.remove();
        submitButton.remove();
        nameLabel.remove();

        // Cargar elementos de la interfaz del juego
        initTextsAndBackgrounds(); // Inicializa los textos del juego
    }

    private void renderGameTexts() {
        if (textBox == null) {
            return; // Early exit if textBox is not initialized
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
    if (Gdx.input.justTouched() && nameEntered) {  // Añadimos la verificación de nameEntered aquí
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTouchTime > touchCooldown) {
                int x = Gdx.input.getX();
                int y = Gdx.graphics.getHeight() - Gdx.input.getY();

                if (textBox.contains(x, y)) {
                    lastTouchTime = currentTime;
                    currentTextIndex++; // Incrementamos el índice para mostrar el próximo texto
                    if (currentTextIndex >= gameTexts.size()) {
                    // Cambiamos a la pantalla de juego cuando hemos mostrado todos los textos
                    game.setScreen(new GameplayScreen(game));  // Suponiendo que tienes un constructor adecuado en GameplayScreen
                    return;  // Salimos del método para evitar cambios adicionales
                    }

                    if (!firstClickHandled) {
                        // Marcamos el primer clic como manejado
                        firstClickHandled = true;
                    } else {
                        // A partir del segundo clic, cambiamos el fondo
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
        initNameFieldUI(); // Initialize the name input UI

        // Asegúrate de que la textura del marco del cuadro de texto se cargue correctamente
        frameTexture = new Texture(Gdx.files.internal("backgrounds/MenuBox2.png"));

        // Initialize the textBox Rectangle at the bottom of the screen
        float textBoxWidth = Gdx.graphics.getWidth();
        float textBoxHeight = Gdx.graphics.getHeight() * 0.3f; // Set to 30% of screen height
        float textBoxX = 0;
        float textBoxY = 0; // Align to the bottom of the screen
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
            // Dibuja el marco del cuadro de texto
            if (frameTexture != null) {
                batch.draw(frameTexture, textBox.x, textBox.y, textBox.width, textBox.height);
            }

            // Asegúrate de que el texto se dibuja dentro del cuadro
            if (!gameTexts.isEmpty()) {
                font.draw(batch, gameTexts.get(currentTextIndex), textBox.x + 20, textBox.y + textBox.height / 2 + 10, textBox.width - 40, Align.center, true);
            }
            batch.end();
        }

        //stage.act(delta);
        //stage.draw();
        handleInput();
    }



    private void initTextsAndBackgrounds() {
        gameTexts = new ArrayList<>();
        backgrounds = new ArrayList<>();

        // Suponiendo que tienes un atlas llamado "backgrounds.atlas" que contiene las regiones de las texturas
        gameTexts.add("El susurro de las hojas caídas narra una historia de gloria perdida.");
        gameTexts.add("En otro tiempo, el poderoso Anillo de Elden se alzaba entero e invicto, una luz en los Territorios Intermedios. Pero ahora, yace hecho añicos, roto por las manos del destino.");

        backgrounds.add(backgroundsAtlas.findRegion("marika_shatered_EldenRing"));
        gameTexts.add("En la estela de este cataclismo, reina el caos supremo. La Reina Marika, soberana eterna, ha desaparecido en las sombras, dejando atrás un reino tambaleante al borde de la perdición.");
        backgrounds.add(backgroundsAtlas.findRegion("marika"));
        gameTexts.add("La Noche de los Cuchillos Negros se cobró al valiente Godwyn el Dorado, un presagio de días aún más oscuros por venir." );
        backgrounds.add(backgroundsAtlas.findRegion("godwyn"));
        gameTexts.add("En el caos posterior, los vástagos de Marika, semidioses nacidos de la divinidad y la mortalidad, aferran los fragmentos del anillo roto. Pero su codicia por el poder solo acelera la caída hacia la locura, desencadenando una guerra que no deja vencedores ni señores para reclamar dominio.");
        backgrounds.add(backgroundsAtlas.findRegion("mogh"));
        gameTexts.add("Mientras el velo del destino se deshilacha, un llamado resuena a través de las tierras envueltas en la niebla, convocando a los Sinluz, aquellos descartados y olvidados, pero aún atados por el hilo del destino.");
        backgrounds.add(backgroundsAtlas.findRegion("Horah_loux"));
        gameTexts.add("Entre las brumas de los Territorios Intermedios, la oscuridad se cierne como un manto, un recordatorio constante de los peligros que acechan en cada sombra. Criaturas de pesadilla acechan en las profundidades de antiguas mazmorras, susurrando de tiempos olvidados y secretos oscuros.");

        backgrounds.add(backgroundsAtlas.findRegion("lands_in_between"));
        gameTexts.add("En este mundo  donde la luz apenas se atreve a penetrar las sombras, los corazones de los valientes aún arden con la esperanza de restaurar la gloria perdida y enfrentar el destino que les aguarda.");
        gameTexts.add("En el vasto lienzo de los Territorios Intermedios, un enigma se materializa entre las sombras. "+playerName+" un joven cuyo nombre apenas susurra el viento, emerge como un reflejo de la desgracia que yace en el corazón de estas tierras fracturadas.");
        gameTexts.add("Cuando el eco de la mazmorra resuena en su alma, "+playerName+" ve en ello una llamada, un destino entrelazado con las cicatrices del Anillo de Elden. La búsqueda de redención y significado lo impulsa hacia lo desconocido, hacia las profundidades donde los fragmentos del pasado yacen ocultos entre las sombras");

        // Set the initial background if any
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
    public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundsAtlas.dispose();
        SkinManager.getAtlas().dispose();
    }
}