package net.ts.isn;

import java.util.HashMap;

import com.badlogic.gdx.Game;

import net.ts.isn.screens.GameScreen;
import net.ts.isn.screens.LevelEditor;
import net.ts.isn.screens.MainScreen;
import net.ts.isn.screens.OptionScreen;
import net.ts.isn.screens.RootScreen;
import net.ts.isn.screens.ScoreScreen;
import net.ts.isn.screens.SelectionScreen;

public class Main extends Game {
	public static HashMap<Integer, RootScreen> screens;

	public void create () {
		Resources.init();
		screens = new HashMap<Integer, RootScreen>();
		screens.put(0, new MainScreen(this));
		screens.put(1, new SelectionScreen(this));
		screens.put(2, new GameScreen(this));
		screens.put(3, new ScoreScreen(this));
		screens.put(4, new OptionScreen(this));
		screens.put(5, new LevelEditor(this));
		setScreen(screens.get(0));
	}
	
	public void dispose() {
		super.dispose();
		Resources.dispose();
	}
}