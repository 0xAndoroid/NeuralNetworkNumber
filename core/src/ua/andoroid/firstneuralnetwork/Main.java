package ua.andoroid.firstneuralnetwork;

import com.badlogic.gdx.Game;
import ua.andoroid.firstneuralnetwork.screens.AskScreen;


public class Main extends Game {
	@Override
	public void create() {
	    Statements st = new Statements(this);
		setScreen(new AskScreen(st));
	}
}
