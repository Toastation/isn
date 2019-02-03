package net.ts.isn.screens;

import com.badlogic.gdx.Gdx;

import net.ts.isn.Config;
import net.ts.isn.Main;
import net.ts.isn.Resources;
import net.ts.isn.screens.gui.ArrowSet;
import net.ts.isn.screens.gui.Button;

public class OptionScreen extends RootScreen {
	
	private static Button back;
	
	private static ArrowSet vSync;
	private static ArrowSet debugOverlay;
	private static ArrowSet loop;
	private static ArrowSet secret;
	private static ArrowSet soundEffectVolume;
	private static ArrowSet musicVolume;
	
	public OptionScreen(Main game) {
		super(game);
		
		back = new Button("Back", 0, 50, true);
		vSync = new ArrowSet(180, 800, "on", 1, 0, 1, 1);		
		vSync.setBoolean(true);
		debugOverlay = new ArrowSet(255, 750, "off", 1, 0, 1);		
		debugOverlay.setBoolean(true);
		loop = new ArrowSet(1726, 800, "off", 1, 0, 1);
		loop.setBoolean(true);
		secret = new ArrowSet(1578, 750, "off", 1, 0, 1);
		secret.setBoolean(true);
		soundEffectVolume = new ArrowSet(1210, 800, ""+(int)(Config.getSoundEffectVolume()*10), 1, 0, 1, 0.1f);
		musicVolume = new ArrowSet(1100, 750, ""+(int)(Config.getMusicVolume()*10), 1, 0, 1, 0.1f);
		
		back.centerRelativeToScreen();
	}

	public void update(float delta) {
		super.update(delta);
		
		if (back.isClicked())
			this.game.setScreen(Main.screens.get(0));
		
		// mise à jour des boutons
		vSync.update();
		debugOverlay.update();
		loop.update();
		secret.update();
		soundEffectVolume.update();
		musicVolume.update();
		
		// comportement des boutons
		if (vSync.isUpdated()) {
			Gdx.graphics.setVSync(vSync.getIndex() == 0 ? false : true);
			vSync.setUpdated(false);
		}
		else if (debugOverlay.isUpdated()) {
			Config.setDebugOverlay(debugOverlay.getIndex() == 0 ? false : true);
			debugOverlay.setUpdated(false);
		}
		else if (loop.isUpdated()) {
			Config.setLoop(loop.getIndex() == 0 ? true : false);
			loop.setUpdated(false);
		}
		else if (secret.isUpdated()){
			Config.setFartMode(secret.getIndex() == 0 ? false : true);
			secret.setUpdated(false);
		}
		else if (soundEffectVolume.isUpdated()){
			Config.setSoundEffectVolume(soundEffectVolume.getIndex());
			soundEffectVolume.setUpdated(false);
			soundEffectVolume.setText(""+((int)(soundEffectVolume.getIndex()*10)));
			
		}
		else if (musicVolume.isUpdated()){
			Config.setMusicVolume(musicVolume.getIndex());
			musicVolume.setUpdated(false);
			musicVolume.setText(""+((int)(musicVolume.getIndex()*10)));
		}
	}

	public void render(float delta) {
		super.render(delta);

		batch.begin();
		
		batch.draw(Resources.backgrounds[0], 0, 0);
		
		vSync.render(batch);
		debugOverlay.render(batch);
		loop.render(batch);
		secret.render(batch);
		soundEffectVolume.render(batch);
		musicVolume.render(batch);
		
		Resources.font.draw(batch, "Video", 200, 900);
		Resources.font.draw(batch, "VSync : ", 100, 810);
		Resources.font.draw(batch, "Debug mode : ", 100, 760);

		Resources.font.draw(batch, "Audio", 900, 900);
		Resources.font.draw(batch, "Sound effects volume : ", 900, 810);
		Resources.font.draw(batch, "Music volume : ", 900, 760);
		
		Resources.font.draw(batch, "Game", 1600, 900);
		Resources.font.draw(batch, "Off-screen death :", 1500, 810);
		Resources.font.draw(batch, "Bonus :",1500, 760);
		
		back.render(batch);

		batch.end();
	}
}
