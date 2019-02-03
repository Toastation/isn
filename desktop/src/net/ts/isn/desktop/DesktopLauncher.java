package net.ts.isn.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.ts.isn.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = true;
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		config.vSyncEnabled = true;
		config.resizable = true;
		config.title = "Cassin All-Star Battle";
		config.useGL30 = true;
		config.addIcon("gfx/icon.png", FileType.Internal);
		new LwjglApplication(new Main(), config);
	}
}