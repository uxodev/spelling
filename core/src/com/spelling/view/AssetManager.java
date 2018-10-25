package com.spelling.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Stores all of the assets for use with libGDX.
 */
public class AssetManager {
    public static TextureAtlas atlas;
    public static Skin defaultSkin;
    public static ButtonStyle defaultStyle;
    public static Drawable backPlate;
    public static BitmapFont font64;
    public static BitmapFont font32;
    public static LabelStyle labelStyle64Clear;
    public static LabelStyle labelStyle64Solid;
    public static TextFieldStyle textFieldStyle64;
    public static TextButtonStyle textButtonStyle64Checked;
    public static TextButtonStyle textButtonStyle64;
    public static TextButtonStyle textButtonStyle32;
    public static ImageButtonStyle imageButtonStyle;
    public static ImageButtonStyle backButtonStyle;

    /**
     * Initialize all of the asset styles for libGDX.
     */
    public static void init() {
        atlas = new TextureAtlas(Gdx.files.internal("packed-images/pack.atlas"));
        defaultSkin = new Skin(Gdx.files.internal("skins/clean-crispy/clean-crispy-ui.json"));
        defaultStyle = new ButtonStyle();
        defaultStyle.up = AssetManager.defaultSkin.getDrawable("button-c");
        defaultStyle.down = AssetManager.defaultSkin.getDrawable("button-pressed-over-c");
        defaultStyle.checked = AssetManager.defaultSkin.getDrawable("button-pressed-over-c");
        defaultStyle.over = AssetManager.defaultSkin.getDrawable("button-over-c");
        backPlate = AssetManager.defaultSkin.getDrawable("button-c-clear");
        // Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/open-sans/OpenSans-Semibold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.hinting = FreeTypeFontGenerator.Hinting.Full;
        parameter.color = Color.BLACK;
        parameter.size = 64;
        font64 = generator.generateFont(parameter);
        parameter.size = 32;
        font32 = generator.generateFont(parameter);
        // Labels
        labelStyle64Solid = new LabelStyle(font64, Color.BLACK);
        labelStyle64Clear = new LabelStyle(font64, Color.BLACK);
        labelStyle64Clear.background = AssetManager.backPlate;
        // Text Fields
        textFieldStyle64 = new TextFieldStyle(font64, Color.BLACK, defaultStyle.down, defaultStyle.down, defaultStyle.up);
        // Text Buttons
        textButtonStyle64Checked = new TextButtonStyle(defaultStyle.up, defaultStyle.down, defaultStyle.checked, font64);
        textButtonStyle64 = new TextButtonStyle(defaultStyle.up, defaultStyle.down, defaultStyle.over, font64);
        textButtonStyle32 = new TextButtonStyle(defaultStyle.up, defaultStyle.down, defaultStyle.over, font32);
        // Image Buttons
        imageButtonStyle = new ImageButtonStyle(defaultStyle);
        backButtonStyle = new ImageButtonStyle(defaultStyle);
        backButtonStyle.imageUp = new TextureRegionDrawable(getTextureRegion("BackButton"));
    }

    /**
     * Retrieve a texture region from the texture atlas by the file name.
     */
    public static TextureRegion getTextureRegion(String fileName) {
        return new TextureRegion(AssetManager.atlas.findRegion(fileName));
    }

    /**
     * Retrieve an mp3 by the file name.
     */
    public static Music getMusic(String fileName) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("sounds/" + fileName + ".mp3"));
        music.setLooping(true);
        music.setVolume(0.0f);
        return music;
    }
}
