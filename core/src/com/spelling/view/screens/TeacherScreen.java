package com.spelling.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.spelling.model.DataManager;
import com.spelling.view.AssetManager;
import com.spelling.view.GdxGame;
import com.spelling.viewmodel.ScreenManager;

/**
 * Screen for the teacher to use.
 */
public class TeacherScreen implements Screen {
    private GdxGame game;
    public static Stage stage;
    private Table mainTable;
    private Sound clickSound;

    public TeacherScreen(GdxGame gdxGame) {
        this.game = gdxGame;
        stage = new Stage(gdxGame.viewport, gdxGame.batch);
        Gdx.input.setInputProcessor(stage);

        setStage();
    }

    private void setStage() {
        mainTable = new Table();
        mainTable.top().left().setBounds(0, 0, GdxGame.virtualWidth, GdxGame.virtualHeight);
        mainTable.setBackground(new TextureRegionDrawable(AssetManager.getTextureRegion("background")));
        stage.addActor(mainTable);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/switch2.ogg"));

        selectTeachers();
    }

    /**
     * Shows all of the teachers.
     */
    private void selectTeachers() {
        String titleText = "Select a teacher to see their students.";
        if (DataManager.getTeachers().size() == 0) {
            titleText = "Add a teacher to see their students.";
        }
        ChangeListener doOnBackButton = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                ScreenManager.setScreen(new StartScreen(TeacherScreen.this.game));
            }
        };
        ChangeListener doAfterSelectItem = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                TeacherScreen.this.selectStudents();
            }
        };

        String addItemInfoText = "<Teacher name>";
        ChangeListener doAfterAddRemove = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TeacherScreen.this.selectTeachers();
            }
        };

        mainTable.clearChildren();
        mainTable.addActor(ScreenManager.screenFactory(ScreenManager.ScreenType.TEACHERS, titleText, doOnBackButton, doAfterSelectItem, addItemInfoText, doAfterAddRemove));
    }

    /**
     * Shows the students under the selected teacher.
     */
    private void selectStudents() {
        String titleText = "Select a student to see their history.";
        if (ScreenManager.getSelectedTeacher().getStudents().size() == 0) {
            titleText = "No students. Add a student.";
        }
        ChangeListener doOnBackButton = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                TeacherScreen.this.selectTeachers();
            }
        };
        ChangeListener doAfterSelectItem = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                displayHistories();
            }
        };

        String addItemInfoText = "<Student name>";
        ChangeListener doAfterAddRemove = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                TeacherScreen.this.selectStudents();
            }
        };

        mainTable.clearChildren();
        mainTable.addActor(ScreenManager.screenFactory(ScreenManager.ScreenType.STUDENTS, titleText, doOnBackButton, doAfterSelectItem, addItemInfoText, doAfterAddRemove));
    }

    /**
     * Shows histories of the selected student.
     */
    private void displayHistories() {
        String titleText = "Student's game history:";
        ChangeListener doOnBackButton = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                TeacherScreen.this.selectStudents();
            }
        };

        mainTable.clearChildren();
        mainTable.addActor(ScreenManager.screenFactory(ScreenManager.ScreenType.HISTORIES, titleText, doOnBackButton, null, null, null));
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
        clickSound.dispose();
        stage.dispose();
    }
}
