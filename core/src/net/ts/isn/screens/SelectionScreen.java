package net.ts.isn.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.ts.isn.Config;
import net.ts.isn.GameMode;
import net.ts.isn.Main;
import net.ts.isn.Resources;
import net.ts.isn.screens.gui.ArrowSet;
import net.ts.isn.screens.gui.Button;

public class SelectionScreen extends RootScreen implements InputProcessor {
	private static int levelID;
	private static int playerNumber;
	private static int modeParam;
	private static int[] ids = {0, 1, 0, 0};
	private static int customLevelsNumber;
	private static GameMode mode;

	private static Button begin;
	private static Button levelEditor;
	private static Button back;
	private static Button thumbnails[];
	private static Button addPlayer;
	private static Button removePlayer;
	
	private static ArrowSet gameModeArrows;
	private static ArrowSet paramArrows;
	private static ArrayList<ArrowSet> charSelectionArrows;
	
	public SelectionScreen(Main game) {
		super(game);
		
		levelID = 0;
		playerNumber = 2;
		modeParam = 120;
		
		mode = GameMode.TIME;
		mode.setParam(modeParam);
		
		begin = new Button("Start", 1500, 950);
		levelEditor = new Button("Level editor", 1500, 850);
		back = new Button("Back", 1660, 50);
		thumbnails = new Button[3];
		thumbnails[0] = new Button(100, 800, 0, 0, 288, 162);
		thumbnails[1] = new Button(500, 800, 288, 0, 288, 162);
		thumbnails[2] = new Button(900, 800, 288, 0, 288, 162);
		
		gameModeArrows = new ArrowSet(290, 1040, mode.name, 1, 0, 1);
		paramArrows = new ArrowSet(490, 1040, mode.paramName+Integer.toString(mode.getParam())+mode.paramSuffix, mode.paramMax, mode.paramMin, mode.paramStep);
		charSelectionArrows = new ArrayList<ArrowSet>();
		charSelectionArrows.add(new ArrowSet(100, 100, this.getCharacterName(0), 1, 0, 1));
		charSelectionArrows.add(new ArrowSet(500, 100, this.getCharacterName(1), 1, 0, 1, 1));
		
		addPlayer = new Button(964, 150, new TextureRegion(Resources.gui, 50, 0, 24, 24));
		removePlayer = new Button(964, 40, new TextureRegion(Resources.gui, 50, 9, 24, 6));
		
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void show() {
		super.show();
		customLevelsNumber = Config.getCustomLevelsNumber()/2;
	}
	
	public void update(float delta) {
		super.update(delta);
		
		// mise à jour des flèches
		gameModeArrows.update();
		paramArrows.update();
	
		this.arrowsUpdate();
		this.buttonsUpdate();
	}

	public void render(float delta) {
		super.render(delta);
	
		batch.begin();
		
		batch.draw(Resources.backgrounds[0], 0, 0, 1920, 1080);
		
		begin.render(batch);
		levelEditor.render(batch);
		back.render(batch);
		
		if (playerNumber > 2)
			removePlayer.render(batch);
		
		for (int i = 0; i < thumbnails.length; i++) {
			thumbnails[i].render(batch);
		}
		
		Resources.font.draw(batch, "Game mode : ", 100, 1051);
		gameModeArrows.render(batch);
		paramArrows.render(batch);
		
		for (int i = 0; i < 4; i++) {
			if (i <= playerNumber-1) {
				ArrowSet arrowSet = charSelectionArrows.get(i);
				arrowSet.render(batch);
				batch.draw(Resources.heads, arrowSet.getPos().x+(arrowSet.getWidthTotal()/2)-40.5f, arrowSet.getPos().y+20, 81, 90, ids[i]*27, 0, 27, 30, false, false);
			} else
				addPlayer.render(batch);
		}
		
		batch.end();
	}
	
	public void arrowsUpdate() {
		if (gameModeArrows.isUpdated()) {
			mode = this.getGameMode((int)gameModeArrows.getIndex());
			gameModeArrows.setText(mode.name);
			paramArrows.setParamThreshold(mode.paramMax, mode.paramMin);
			mode.setParam((mode.paramMax+mode.paramMin)/2);
			paramArrows.setIndex(mode.getParam());
			paramArrows.setStep(mode.paramStep);
			paramArrows.setText(mode.paramName+Integer.toString(mode.getParam())+mode.paramSuffix);
		}
		
		if (paramArrows.isUpdated()) {
			mode.setParam((int)paramArrows.getIndex());
			paramArrows.setText(mode.paramName+Integer.toString(mode.getParam())+mode.paramSuffix);
		}
		
		for (int i = 0; i < charSelectionArrows.size(); i++) {
			ArrowSet arrowSet = charSelectionArrows.get(i);
			if (arrowSet.isUpdated()) {
				arrowSet.setText(this.getCharacterName((int)arrowSet.getIndex()));
				ids[i] = (int)arrowSet.getIndex();
			}
		}
	}
	
	public void buttonsUpdate() {
		if (begin.isClicked()) {
			((GameScreen)Main.screens.get(2)).create(mode, levelID, playerNumber, ids);
			this.game.setScreen(Main.screens.get(2));
		}
		
		if (levelEditor.isClicked())
			this.game.setScreen(Main.screens.get(5));
		
		if (back.isClicked())
			this.game.setScreen(Main.screens.get(0));
		
		if (removePlayer.isClicked() && playerNumber > 2) {
			charSelectionArrows.remove(charSelectionArrows.size()-1);
			playerNumber--;
			
			switch (playerNumber) {
				case 2: 
					addPlayer.setPosX(964);
					break;
				case 3: 
					addPlayer = new Button(1364, 150, new TextureRegion(Resources.gui, 50, 0, 24, 24));
					break;
			}
		}
		
		// mise à jour des boutons de sélection du niveau
		for (int i = 0; i < thumbnails.length; i++) {
			if (thumbnails[i].isClicked()) { // si le bouton est cliqué, on l'active et on fait prendre la valeur de l'indice à la variable définissant l'id du niveau choisi
				levelID = i;
				thumbnails[i].setSelected(true);
				
//				for (int j = 0; j < 2; j++) { // on "déselectionne" les autres boutons
//					if (j != i)
//						this.thumbnails[i].setSelected(false);
//				}
			}
		}
		
		// mise à jour des flèches de la sélection des personnages
		for (int i = 0; i < charSelectionArrows.size(); i++) {
			charSelectionArrows.get(i).update();
		}
	
		// mise à jour du bouton "+" pour ajouter un joueur
		if (addPlayer != null && addPlayer.isClicked()) {
			switch(playerNumber) {
				case 2:
					addPlayer.setPosX(1364);
					playerNumber++;
					charSelectionArrows.add(new ArrowSet(900, 100, this.getCharacterName(0), 1, 0, 1));
					break;
				case 3:
					addPlayer = null;
					playerNumber++;
					charSelectionArrows.add(new ArrowSet(1300, 100, this.getCharacterName(0), 1, 0, 1));
					break;
			}
		}
	}
	
	public String getCharacterName(int charID) {
		switch(charID) {
			case 0:
				return "Tristan";
			case 1:
				return "Melvin";
			default:
				return "";
		}
	}
	
	public GameMode getGameMode(int index) {
		switch(index) {
			case 0:
				return GameMode.TIME.setParam(120);
			case 1: 
				return GameMode.LIFE.setParam(5);
			default:
				System.err.println("wrong index given for game mode");
				return GameMode.TIME;
		}
	}

	public static int getPlayerNumber() {
		return playerNumber;
	}

	public static int[] getIds() {
		return ids;
	}

	public static GameMode getMode() {
		return mode;
	}
}