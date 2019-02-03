package net.ts.isn.screens;

import com.badlogic.gdx.Gdx;

import net.ts.isn.Main;
import net.ts.isn.Resources;
import net.ts.isn.screens.gui.Button;
import net.ts.isn.screens.gui.Text;

public class MainScreen extends RootScreen {
	
	private static Text title;
	
	private static Button begin;
	private static Button option;
	private static Button exit;

	public MainScreen(Main game) {
		super(game);
		
		title = new Text("Cassin All-Star Battle", 0, 900, true, 10);
		title.setFont(Resources.titleFont);
		
		begin = new Button("Play", 0, 600, true);
		option = new Button("Options", 0, 500, true);
		exit = new Button("Exit", 0, 400, true);
		
		title.centerRelativeToScreen();
		begin.centerRelativeToScreen();
		option.centerRelativeToScreen();
		exit.centerRelativeToScreen();
	}
	
	public void update(float delta) {
		super.update(delta);
		
		title.update(delta);
		
		if (begin.isClicked())
			this.game.setScreen(Main.screens.get(1));
		else if (option.isClicked())
			this.game.setScreen(Main.screens.get(4));
		else if (exit.isClicked()) 
			Gdx.app.exit();
	}
	
	public void render(float delta) {
		super.render(delta);
		
		batch.begin();
		
		batch.draw(Resources.backgrounds[0], 0, 0);
		
		title.render(batch);
		
		begin.render(batch);
		option.render(batch);
		exit.render(batch);
		
		batch.end();
	}
}
