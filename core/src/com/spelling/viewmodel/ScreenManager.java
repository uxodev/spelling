package com.spelling.viewmodel;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.spelling.model.DataManager;
import com.spelling.model.History;
import com.spelling.model.Student;
import com.spelling.model.Teacher;
import com.spelling.view.AssetManager;
import com.spelling.view.GdxGame;
import com.spelling.view.screens.TeacherScreen;

import java.util.ArrayList;

/**
 * Creates screens and manages the global state variables. Stores a previous screen when appropriate.
 */
public class ScreenManager {
    public static Language selectedLanguage;
    public static int selectedTeacherIndex;
    public static String selectedTeacherName;
    public static int selectedStudentIndex;
    public static String selectedStudentName;

    private static GdxGame game;
    private static Screen previousScreen;

    public static void start(GdxGame gdxGame, Screen firstScreen) {
        ScreenManager.game = gdxGame;
        selectedLanguage = Language.HMONG;
        setScreen(firstScreen);
    }

    public static void setScreen(Screen next) {
        game.setScreen(next);
    }

    public static void setPreviousScreen(Screen previousScreen) {
        ScreenManager.previousScreen = previousScreen;
    }

    public static Screen getPreviousScreen() {
        return previousScreen;
    }

    public static Teacher getSelectedTeacher() {
        return DataManager.getTeachers().get(selectedTeacherIndex);
    }

    public static Student getSelectedStudent() {
        return DataManager.getStudents(ScreenManager.selectedTeacherIndex).get(ScreenManager.selectedStudentIndex);
    }

    /**
     * Creates a table that is all parts of the non-game screens.
     * The name of each button is the list index for that button's function.
     *
     * @param screenType        What kind of screen.
     * @param titleText         Text for the title of the screen.
     * @param doOnBackButton    Do this after back button is pressed.
     * @param doAfterSelectItem Do this after an item is selected.
     * @param addItemInfoText   Info text for the default name in the add item field.
     * @param doAfterAddRemove  Do this after an item has been added or removed.
     * @return A table to be used as a screen.
     */
    public static Table screenFactory(ScreenType screenType, String titleText, ChangeListener doOnBackButton, ChangeListener doAfterSelectItem, String addItemInfoText, ChangeListener doAfterAddRemove) {
        int backButtonSize = 150;

        Table mainTable = new Table();
        mainTable.setBounds(0, 0, GdxGame.virtualWidth, GdxGame.virtualHeight);
        Label titleLabel = new Label(titleText, AssetManager.labelStyle64Clear);
        titleLabel.setBounds(300, mainTable.getHeight() - 220, mainTable.getWidth() - 600, 200);
        mainTable.addActor(titleLabel);
        Table bodyTable = new Table();
        bodyTable.setBounds(300, mainTable.getHeight() - 750 - 250, mainTable.getWidth() - 600, 750);
        mainTable.addActor(bodyTable);
        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.align(Align.topLeft);
        ScrollPane scrollPane = new ScrollPane(verticalGroup, AssetManager.defaultSkin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        ImageButton backButton = new ImageButton(AssetManager.backButtonStyle);
        backButton.setBounds(50, 50, backButtonSize, backButtonSize);
        backButton.setSize(backButtonSize, backButtonSize);
        backButton.addListener(doOnBackButton);
        mainTable.addActor(backButton);

        int rowHeight = 100;
        int rowSeparator = 25;
        Table aDataRow = new Table();

        switch (screenType) {
            case TEACHERS:
            case STUDENTS:
                boolean allowChanges = !(addItemInfoText == null);
                int nameWidth = 800;
                int columnSeparator = 75;
                int buttonWidth = 300;

                // If the screen allows changes to entries, have first row be the add entry row.
                if (allowChanges) {
                    aDataRow.background(AssetManager.backPlate);
                    aDataRow.pad(10);

                    final TextField addItemField = new TextField(addItemInfoText, AssetManager.textFieldStyle64);
                    addItemField.addListener(new ClickListener() {
                        boolean firstClick = true;

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            if (firstClick) addItemField.selectAll();
                            firstClick = false;
                        }
                    });

                    // Button will add a new teacher or student with the string in the text field as the name.
                    TextButton addButton = new TextButton("Add", AssetManager.textButtonStyle64);
                    switch (screenType) {
                        case TEACHERS:
                            addButton.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    DataManager.addTeacher(new Teacher(addItemField.getText()));
                                }
                            });
                            break;
                        case STUDENTS:
                            addButton.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    DataManager.addStudent(selectedTeacherIndex, new Student(addItemField.getText()));
                                }
                            });
                            break;
                    }
                    addButton.addListener(doAfterAddRemove);

                    aDataRow.add(addItemField).width(nameWidth).height(rowHeight).left();
                    aDataRow.add().width(columnSeparator);
                    aDataRow.add(addButton).width(buttonWidth).height(rowHeight);
                    bodyTable.add(aDataRow).left();
                    bodyTable.row();
                }

                // Make a row for each entry of either teachers or students.
                switch (screenType) {
                    case TEACHERS:
                        final ArrayList<Teacher> teachers = DataManager.getTeachers();
                        for (int i = 0; i < teachers.size(); i++) { // Make a data row for each teacher.
                            aDataRow = new Table();

                            // Button for selecting a teacher.
                            TextButton nameButton = new TextButton(teachers.get(i).getName(), AssetManager.textButtonStyle64);
                            nameButton.setName(String.valueOf(i));
                            nameButton.getLabel().setAlignment(Align.left);
                            nameButton.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    selectedTeacherIndex = Integer.parseInt(actor.getName());
                                    selectedTeacherName = DataManager.getTeachers().get(selectedTeacherIndex).getName();
                                }
                            });
                            nameButton.addListener(doAfterSelectItem);
                            aDataRow.add(nameButton).width(nameWidth).height(rowHeight);

                            // Button for removing a teacher.
                            if (allowChanges) {
                                TextButton deleteButton = new TextButton("Delete", AssetManager.textButtonStyle64);
                                deleteButton.setName(String.valueOf(i));
                                deleteButton.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        DataManager.removeTeacher(Integer.parseInt(actor.getName()));
                                    }
                                });
                                deleteButton.addListener(doAfterAddRemove);
                                aDataRow.add().width(columnSeparator);
                                aDataRow.add(deleteButton).width(buttonWidth).height(rowHeight);
                            }

                            aDataRow.row();
                            aDataRow.add().height(rowSeparator);
                            verticalGroup.addActor(aDataRow);
                        }
                        break;
                    case STUDENTS:
                        final ArrayList<Student> students = DataManager.getStudents(selectedTeacherIndex);
                        for (int i = 0; i < students.size(); i++) { // Make a data row for each student.
                            aDataRow = new Table();

                            // Button for selecting a student.
                            TextButton nameButton = new TextButton(students.get(i).getName(), AssetManager.textButtonStyle64);
                            nameButton.setName(String.valueOf(i));
                            nameButton.getLabel().setAlignment(Align.left);
                            nameButton.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    selectedStudentIndex = Integer.parseInt(actor.getName());
                                    selectedStudentName = DataManager.getStudents(selectedTeacherIndex).get(selectedStudentIndex).getName();
                                }
                            });
                            nameButton.addListener(doAfterSelectItem);
                            aDataRow.add(nameButton).width(nameWidth).height(rowHeight);

                            // Button for removing a student.
                            if (allowChanges) {
                                TextButton deleteButton = new TextButton("Delete", AssetManager.textButtonStyle64);
                                deleteButton.setName(String.valueOf(i));
                                deleteButton.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        DataManager.removeStudent(selectedTeacherIndex, Integer.parseInt(actor.getName()));
                                    }
                                });
                                deleteButton.addListener(doAfterAddRemove);
                                aDataRow.add().width(columnSeparator);
                                aDataRow.add(deleteButton).width(buttonWidth).height(rowHeight);
                            }

                            aDataRow.row();
                            aDataRow.add().height(rowSeparator);
                            verticalGroup.addActor(aDataRow);
                        }
                        break;
                }

                Table backgroundTable = new Table();
                backgroundTable.background(AssetManager.backPlate);
                backgroundTable.pad(10);
                bodyTable.add(backgroundTable);
                if (allowChanges) {
                    backgroundTable.add(scrollPane).width(nameWidth + columnSeparator + buttonWidth + 20).height((rowHeight + rowSeparator) * 5);
                } else {
                    backgroundTable.add(scrollPane).width(nameWidth + 20).height((rowHeight + rowSeparator) * 6);
                }
                break;
            case HISTORIES:
                int dateWidth = 400;
                int gameNameWidth = 500;
                int numberWidth = 150;
                columnSeparator = 50;

                final ArrayList<History> histories = DataManager.getHistory(DataManager.getStudents(selectedTeacherIndex).get(selectedStudentIndex));
                for (int i = 0; i < histories.size(); i++) { // Make a data row for each history.
                    aDataRow = new Table();
                    Label dateLabel = new Label(String.valueOf(histories.get(i).getDateString()), AssetManager.labelStyle64Clear);
                    dateLabel.setAlignment(Align.center);
                    Label gameNameLabel = new Label(histories.get(i).getGamePlayed(), AssetManager.labelStyle64Clear);
                    gameNameLabel.setAlignment(Align.center);

                    // Button that shows a dialog of what words were spelled.
                    TextButton numberOfWordsButton = new TextButton(String.valueOf(histories.get(i).getWordsSpelled().size()), AssetManager.textButtonStyle64);
                    numberOfWordsButton.setName(String.valueOf(i));
                    numberOfWordsButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            StringBuilder wordList = new StringBuilder();
                            wordList.append("Words Spelled:\n");
                            ArrayList<String> wordsSpelled = histories.get(Integer.parseInt(actor.getName())).getWordsSpelled();
                            for (int j = 1; j <= wordsSpelled.size(); j++) {
                                wordList.append(wordsSpelled.get(j - 1));
                                if (j != wordsSpelled.size()) wordList.append(", "); // last item, do not add a comma
                                if (j % 3 == 0) wordList.append("\n"); // every third, add a new line
                            }
                            Dialog dialog = new Dialog("", AssetManager.defaultSkin);
                            dialog.getBackground().setMinWidth(500);
                            dialog.getBackground().setMinHeight(300);
                            Label label = new Label(wordList.toString(), AssetManager.labelStyle64Solid);
                            label.setAlignment(Align.center);
                            dialog.text(label);
                            dialog.button(new TextButton("OK", AssetManager.textButtonStyle64));
                            dialog.show(TeacherScreen.stage);
                        }
                    });

                    aDataRow.add(dateLabel).width(dateWidth).height(rowHeight);
                    aDataRow.add().width(columnSeparator);
                    aDataRow.add(gameNameLabel).width(gameNameWidth).height(rowHeight);
                    aDataRow.add().width(columnSeparator);
                    aDataRow.add(numberOfWordsButton).width(numberWidth).height(rowHeight);
                    aDataRow.row();
                    aDataRow.add().height(rowSeparator);
                    verticalGroup.addActor(aDataRow);
                }

                backgroundTable = new Table();
                backgroundTable.background(AssetManager.backPlate);
                backgroundTable.pad(10);
                bodyTable.add(backgroundTable);
                backgroundTable.add(scrollPane).width(dateWidth + gameNameWidth + numberWidth + columnSeparator * 2).height((rowHeight + rowSeparator) * 6);
                break;
            case GAMES:
                gameNameWidth = 500;
                int gameNameHeight = 200;
                columnSeparator = 100;

                bodyTable.background(AssetManager.backPlate);
                Table gamesList = new Table();

                TextButton spellingGameButton = new TextButton("Spelling Game", AssetManager.textButtonStyle64);
                spellingGameButton.addListener(doAfterSelectItem);
                gamesList.add(spellingGameButton).width(gameNameWidth).height(gameNameHeight);

                Table languageSelectList = new Table();

                bodyTable.add(languageSelectList);
                bodyTable.add().width(columnSeparator);
                bodyTable.add(gamesList);

                // Button to select English as the language for the game.
                TextButton englishButton = new TextButton("English", AssetManager.textButtonStyle64Checked);
                englishButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        ScreenManager.selectedLanguage = Language.ENGLISH;
                    }
                });
                languageSelectList.add(englishButton).width(gameNameWidth).height(gameNameHeight);
                languageSelectList.row();

                // Button to select Hmong as the language for the game.
                TextButton hmongButton = new TextButton("Hmong", AssetManager.textButtonStyle64Checked);
                hmongButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        ScreenManager.selectedLanguage = Language.HMONG;
                    }
                });
                languageSelectList.add(hmongButton).width(gameNameWidth).height(gameNameHeight);

                switch (selectedLanguage) {
                    case ENGLISH:
                        englishButton.setChecked(true);
                        break;
                    case HMONG:
                        break;
                }
                ButtonGroup<TextButton> buttonGroup = new ButtonGroup<TextButton>();
                buttonGroup.setMaxCheckCount(1);
                buttonGroup.setMinCheckCount(1);
                buttonGroup.add(englishButton);
                buttonGroup.add(hmongButton);
                break;
        }
        return mainTable;
    }

    /**
     * Screen types that can be created by screenFactory.
     */
    public enum ScreenType {
        TEACHERS, STUDENTS, HISTORIES, GAMES
    }

    /**
     * Supported languages.
     */
    public enum Language {
        ENGLISH("English"), HMONG("Hmong");
        public final String fileName;

        Language(String fileName) {
            this.fileName = fileName;
        }
    }
}
