package com.spelling.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.spelling.view.AssetManager;
import com.spelling.view.GdxGame;
import com.spelling.viewmodel.ScreenManager;

/**
 * Credits shown for libGDX and music.
 */
public class LibGDXSplashScreen implements Screen {
    private GdxGame game;
    private Stage stage;

    public LibGDXSplashScreen(GdxGame gdxGame) {
        this.game = gdxGame;
        stage = new Stage(gdxGame.viewport, gdxGame.batch);
        Gdx.input.setInputProcessor(stage);

        setStage();
    }

    private void setStage() {
        Image image = new Image(AssetManager.getTextureRegion("libGDXSplash"));
        image.setSize(GdxGame.virtualWidth, GdxGame.virtualHeight);
        stage.addActor(image);

        image.addAction(Actions.sequence(
                Actions.delay(2f),
                Actions.run(new Runnable() {
                    public void run() {
                        ScreenManager.start(game, new StartScreen(game));
                    }
                })
        ));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

