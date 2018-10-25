package com.spelling.view.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.spelling.model.Word;
import com.spelling.view.AssetManager;
import com.spelling.view.GdxGame;
import com.spelling.view.actors.Letter;
import com.spelling.viewmodel.ScreenManager;
import com.spelling.viewmodel.SpellingGameManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * The game screen.
 */
public class SpellingGameScreen implements Screen {
    private GdxGame game;
    private Stage stage;
    private DragAndDrop dragAndDrop;
    private Random random;

    // Actors added to the screen are drawn in the order they were added. Actors drawn later are drawn on top of everything before.
    // These groups are used to add actors to the screen in the right order. All actors added to groups are drawn when the group is drawn.
    private Group backgroundGroup;
    private Group actorsGroup;
    private Group animationsGroup;

    private Table letterTable;
    private Table pictureTable;
    private Table spaceTable;
    private Container<Image> pictureContainer;
    private ArrayList<Container<Letter>> letterSpaces;
    public ImageButton backButton;
    public Label hintPopup;
    private TextButton skipButton;
    private TextButton hintButton;
    private SpellingGameManager spellingGameManager;
    private Music backgroundMusic;
    private Sound clickSound;

    private int pictureSize = 400;
    private int letterSpaceWidth = 150;
    private int letterSpaceHeight = 195;
    private int letterSize = 140;
    private int buttonWidth = 300;
    private int buttonHeight = 150;

    public SpellingGameScreen(GdxGame gdxGame) {
        this.game = gdxGame;
        stage = new Stage(gdxGame.viewport, gdxGame.batch);
        Gdx.input.setInputProcessor(stage);
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setDragTime(0);
        dragAndDrop.setDragActorPosition(letterSize / 2, -letterSize / 2);
        random = new Random(System.currentTimeMillis());

        setStage();
    }

    /**
     * Screen size in virtual pixels: virtualWidth = 1920 virtualHeight = 1080;
     */
    private void setStage() {
        backgroundMusic = AssetManager.getMusic("GameMusic");
        backgroundMusic.setVolume(0.00f);
        backgroundMusic.play();
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/switch2.ogg"));

        stage.addActor(backgroundGroup = new Group());
        stage.addActor(actorsGroup = new Group());
        stage.addActor(animationsGroup = new Group());

        Image backgroundImage = new Image(AssetManager.getTextureRegion("background"));
        backgroundImage.setSize(GdxGame.virtualWidth, GdxGame.virtualHeight);
        backgroundGroup.addActor(backgroundImage);

        Table mainTable = new Table();
        mainTable.setBounds(0, 0, GdxGame.virtualWidth, GdxGame.virtualHeight);
        actorsGroup.addActor(mainTable);

        // Move picture table to fit the Hmong alphabet
        spaceTable = new Table();
        spaceTable.setBounds(mainTable.getWidth() / 2, 50, 0, letterSpaceHeight);
        mainTable.addActor(spaceTable);
        pictureTable = new Table();
        pictureTable.setBounds(mainTable.getWidth() / 2, 50 + letterSpaceHeight + 20, 0, pictureSize);
        mainTable.addActor(pictureTable);
        letterTable = new Table();
        mainTable.add(letterTable).expand().top().padTop(20);

        pictureTable.add(pictureContainer = new Container<Image>().size(pictureSize));
        letterSpaces = new ArrayList<Container<Letter>>();

        // Back button
        backButton = new ImageButton(AssetManager.backButtonStyle);
        backButton.setBounds(50, 50, buttonHeight, buttonHeight);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                backgroundMusic.stop();
                ScreenManager.setScreen(ScreenManager.getPreviousScreen());
            }
        });
        backButton.setSize(buttonHeight, buttonHeight);
        mainTable.addActor(backButton);

        // Skip button
        skipButton = new TextButton("Skip", AssetManager.textButtonStyle64);
        skipButton.setBounds(mainTable.getWidth() - buttonWidth - 50, 50, buttonWidth, buttonHeight);
        skipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpellingGameScreen.this.spellingGameManager.changeToNextWord();
            }
        });
        mainTable.addActor(skipButton);

        // Hint button
        hintButton = new TextButton("Hint", AssetManager.textButtonStyle64);
        hintButton.setBounds(mainTable.getWidth() - buttonWidth - 50, buttonHeight + 50, buttonWidth, buttonHeight);
        hintButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hintPopup.clearActions();
                hintPopup.addAction(Actions.sequence(
                        Actions.fadeIn(1),
                        Actions.delay(1),
                        Actions.fadeOut(1)
                ));
            }
        });
        mainTable.addActor(hintButton);

        hintPopup = new Label("", AssetManager.labelStyle64Clear);
        hintPopup.setBounds(mainTable.getWidth() - buttonWidth - 50, buttonHeight * 2 + 50, buttonWidth, buttonHeight);
        hintPopup.getColor().a = 0;
        hintPopup.setAlignment(Align.center);
        mainTable.addActor(hintPopup);

        // complementary manager
        spellingGameManager = new SpellingGameManager(this);
    }

    public void setDisplayLanguage(ScreenManager.Language language) {
        setAlphabet(language);
    }

    /**
     * Sets up game screen with indicated language and associated alphabet
     */
    private void setAlphabet(ScreenManager.Language language) {
        int numRows = 3;
        int numCol = 5;
        int letterSelectSize = 89;
        if (GdxGame.isResolution43) {
            letterSelectSize += 20;
            numRows += 2;
            numCol -= 5;
        }
        letterTable.clearChildren();
        switch (language) {
            default:
            case ENGLISH:
                numRows = 2;
                letterSelectSize += 31;
                String[] alphabet = {
                        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
                for (int i = 0; i < numRows; i++) { // row
                    for (int j = 0; j < 13; j++) { // column
                        Letter letter = new Letter(alphabet[(i * 13) + j], letterSelectSize);
                        letterTable.add(new Container<Letter>(letter).size(letterSelectSize));
                        setLetterAsDraggable(letter);
                    }
                    letterTable.row();
                }
                return;
            case HMONG:
                numRows += 1;
                String[] consonants = {"c", "ch", "d", "dh", "dl", "f", "h", "hl", "hm", "hml", "hn", "hny",
                        "k", "kh", "l", "m", "ml", "n", "nc", "nch", "ndl", "nk", "nkh", "np", "nph", "npl", "nplh", "nq",
                        "nqh", "nr", "nrh", "nt", "nth", "nts", "ntsh", "ntx", "ntxh", "ny", "p", "ph", "pl", "plh", "q",
                        "qh", "r", "rh", "s", "t", "th", "ts", "tsh", "tx", "txh", "v", "x", "xy", "y", "z"};
                String[] vowels = {"a", "aa", "ai", "au", "aw", "e", "ee", "i", "ia", "o", "oo", "u", "ua", "w"};
                String[] tones = {"koJ", "muS", "kuV", "niaM", "neeG", "siaB", "zoO", "toD"};

                Table consonantsTable = new Table();
                consonantsTable.setBackground(AssetManager.backPlate);
                letterTable.add(consonantsTable);
                Table vowelsTable = new Table();
                vowelsTable.setBackground(AssetManager.backPlate);
                letterTable.add(vowelsTable);
                Table tonesTable = new Table();
                tonesTable.setBackground(AssetManager.backPlate);
                letterTable.add(tonesTable);

                for (int i = 0; i < numRows; i++) { // row
                    for (int j = 0; j < 10 + numCol; j++) { // column
                        if ((i * (10 + numCol)) + j < consonants.length) { // leaves empty spaces
                            Letter letter = new Letter(consonants[(i * (10 + numCol)) + j], letterSelectSize - 10);
                            consonantsTable.add(new Container<Letter>(letter).size(letterSelectSize));
                            setLetterAsDraggable(letter);
                        }
                    }
                    consonantsTable.row();
                    for (int j = 0; j < 4; j++) { // column
                        if ((i * 4) + j < vowels.length) { // leaves empty spaces
                            Letter letter = new Letter(vowels[(i * 4) + j], letterSelectSize - 10);
                            vowelsTable.add(new Container<Letter>(letter).size(letterSelectSize));
                            setLetterAsDraggable(letter);
                        }
                    }
                    vowelsTable.row();
                    for (int j = 0; j < 2; j++) { // column
                        if ((i * 2) + j < tones.length) { // leaves empty spaces
                            Letter letter = new Letter(tones[(i * 2) + j], letterSelectSize - 10);
                            letter.setIsTone();
                            tonesTable.add(new Container<Letter>(letter).size(letterSelectSize));
                            setLetterAsDraggable(letter);
                        }
                    }
                    tonesTable.row();
                }
                return;
        }
    }

    /**
     * Creates a copy when letter is dragged from the alphabet.
     */
    private void setLetterAsDraggable(Letter letter) {
        dragAndDrop.addSource(new DragAndDrop.Source(letter) {
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Letter letterCopy = new Letter((Letter) getActor(), letterSize);
                payload.setDragActor(letterCopy);

                dragAndDrop.addSource(new DragAndDrop.Source(letterCopy) {
                    @Override
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
                        payload.setDragActor(getActor());
                        return payload;
                    }
                });
                return payload;
            }
        });
    }

    public void setPictureAndSpaceLength(String pictureFileName, int spaceLength) {
        pictureContainer.setActor(new Image(AssetManager.getTextureRegion(pictureFileName)));
        setSpaces(spaceLength);
    }

    private void setSpaces(int spaceLength) {
        spaceTable.clearChildren();
        letterSpaces.clear();
        for (int i = 0; i < spaceLength; i++) {
            Container<Letter> letterContainer = new Container<Letter>();
            letterContainer.setTouchable(Touchable.enabled);
            letterContainer.setBackground(new TextureRegionDrawable(AssetManager.getTextureRegion("underline")));
            spaceTable.add(letterContainer.size(letterSize, letterSize)).size(letterSpaceWidth, letterSpaceHeight);
            letterSpaces.add(letterContainer);

            dragAndDrop.addTarget(new DragAndDrop.Target(letterContainer) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    return true;
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    for (Container<Letter> letterContainer : letterSpaces) {
                        if (!letterContainer.hasChildren()) {
                            letterContainer.setActor(null);
                        }
                    }
                    Container<Letter> newParent = (Container<Letter>) getActor();
                    newParent.setActor((Letter) payload.getDragActor());
                    SpellingGameScreen.this.spellingGameManager.droppedLetter(payload.getDragActor());
                }
            });
        }
    }

    /**
     * Gets the string formed by the letters dropped into the spaces
     */
    public String getWordInSpaces() {
        String currentString = "";
        for (Container<Letter> letterContainer : letterSpaces) {
            if (letterContainer.hasChildren()) {
                currentString += letterContainer.getActor().getSpelling();
            } else {
                currentString += " ";
            }
        }
        return currentString;
    }

    /**
     * Decides whether the spaces are filled with letter
     */
    public boolean spacesFull() {
        for (Container<Letter> letterContainer : letterSpaces) {
            if (letterContainer.hasChildren()) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public void winConfetti(String fileName) {
        for (Container<Letter> letterContainer : letterSpaces) {
            if (letterContainer.hasChildren()) {
                confettiEffect(letterContainer.getActor(), fileName);
            }
        }
    }

    /**
     * Confetti animation from the center of the subject actor.
     */
    public void confettiEffect(Actor subject, String fileName) {
        int size = 100;
        float duration = 2f;
        int distance = 400;
        Vector2 vector2 = subject.localToStageCoordinates(new Vector2(subject.getX(), subject.getY()));
        for (int i = 0; i < 5; i++) {
            Actor explosion = new Image(AssetManager.getTextureRegion(fileName));
            explosion.setTouchable(Touchable.disabled);
            animationsGroup.addActor(explosion);

            explosion.setBounds(vector2.x, vector2.y, size, size);
            explosion.setOrigin(size / 2, size / 2);
            explosion.addAction(Actions.moveTo(vector2.x + (random.nextBoolean() ? random.nextInt(distance) : -random.nextInt(distance)) + random.nextInt(distance), vector2.y + random.nextInt(distance),
                    duration, Interpolation.smooth));
            explosion.addAction(Actions.rotateBy(random.nextBoolean() ? random.nextInt(270) : -random.nextInt(270), duration));
            explosion.addAction(Actions.fadeOut(duration));
        }
    }

    public void playLetter(String language, Letter letter) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + language + "Alphabet/" + letter.getName() + ".mp3"));
        sound.setVolume(sound.play(), 0);
        sound.dispose();
    }

    public void playWord(String language, Word currentWord) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + language + "Words/" + currentWord.getWordId() + ".mp3"));
        sound.play();
        sound.dispose();
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
        clickSound.dispose();
    }
}
