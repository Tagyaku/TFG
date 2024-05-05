package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MyGdxGame extends Game {


	/*IFirebase firebaseinterface;
	public MyGdxGame(IFirebase firebaseinterface) {
		this.firebaseinterface = firebaseinterface;
	}*/
	public MyGdxGame() {
	}
	private OrthographicCamera camera; // Referencia a la cámara del juego

	@Override
	public void create () {
		// Inicializar la cámara del juego
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Crear una instancia de MainMenuScreen y establecerla como la pantalla actual del juego
		MainMenuScreen mainMenuScreen = new MainMenuScreen(this);
		setScreen(mainMenuScreen);
	}

	// Getter para la cámara
	public OrthographicCamera getCamera() {
		return camera;
	}
}
