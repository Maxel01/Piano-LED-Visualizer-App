package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;

public abstract class StageScreen implements Screen {

    protected Core core;
    protected Connection connection;
    protected Stage stage;
    protected Skin skin;

    public StageScreen(Core core) {
        this.core = core;
        this.connection = core.connection;
        createStage();
        skin = VisUI.getSkin();
    }

    public void createStage() {
        stage = new Stage(new FitViewport(480, 800));
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
