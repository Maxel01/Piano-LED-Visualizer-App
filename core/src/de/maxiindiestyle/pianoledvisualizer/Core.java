package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.kotcrab.vis.ui.VisUI;

public class Core extends Game {

	public Connection connection;
	public Actions actions;
	
	@Override
	public void create () {
		VisUI.load();
		connection = new Connection();
		actions = new Actions(this);
		setScreen(new LoginScreen(this));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		connection.dispose();
		VisUI.dispose();
	}
}
