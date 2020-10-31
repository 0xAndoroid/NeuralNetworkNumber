package ua.andoroid.firstneuralnetwork.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ua.andoroid.firstneuralnetwork.Main;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 276;
		config.height = 406;
		config.resizable = false;
		config.x = 0;
		config.y = 0;
		config.addIcon("icon32.png", Files.FileType.Internal);
		config.addIcon("icon64.png", Files.FileType.Internal);
		config.addIcon("icon128.png", Files.FileType.Internal);
		config.title = "NeuralNetworkNumber";
		config.foregroundFPS = 120;
		new LwjglApplication(new Main(), config);
	}
}
