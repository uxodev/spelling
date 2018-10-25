package com.spelling.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.spelling.view.GdxGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Spelling Game";
        config.width = 1280;
        config.height = 720;
        config.x = 0;
        config.y = 0;
        new LwjglApplication(new GdxGame(), config);
    }
}
