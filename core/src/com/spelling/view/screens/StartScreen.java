package com.spelling.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.spelling.view.AssetManager;
import com.spelling.view.GdxGame;
import com.spelling.viewmodel.ScreenManager;

/**
 * Main screen displays Teacher and Student buttons.
 */
public class StartScreen implements Screen {
    private GdxGame game;
    private Stage stage;
    private Sound clickSound;

    public StartScreen(GdxGame gdxGame) {
        this.game = gdxGame;
        stage = new Stage(gdxGame.viewport, gdxGame.batch);
        Gdx.input.setInputProcessor(stage);

        setStage();
    }

    private void setStage() {
        Table mainTable = new Table();
        mainTable.setBounds(0, 0, GdxGame.virtualWidth, GdxGame.virtualHeight);
        stage.addActor(mainTable);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/switch2.ogg"));

        Image background = new Image(AssetManager.getTextureRegion("StartScreenBackground"));
        mainTable.setBackground(background.getDrawable());

        Drawable drawable = new TextureRegionDrawable(AssetManager.getTextureRegion("TeacherButtonSkin"));
        ImageButton teacherButton = new ImageButton(drawable);
        teacherButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                ScreenManager.setScreen(new TeacherScreen(StartScreen.this.game));
            }
        });

        drawable = new TextureRegionDrawable(AssetManager.getTextureRegion("StudentButtonSkin"));
        ImageButton studentButton = new ImageButton(drawable);
        studentButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                ScreenManager.setScreen(new StudentScreen(StartScreen.this.game));
            }
        });

        mainTable.add().height(425);
        mainTable.row();
        mainTable.add(teacherButton).width(500).height(300);
        mainTable.add().width(350);
        mainTable.add(studentButton).width(500).height(300);
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
