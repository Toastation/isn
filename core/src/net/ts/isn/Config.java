package net.ts.isn;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controllers;

public class Config {
	public final static int SCALE = 2;
	public final static int[][] keys = {{Keys.UP, Keys.LEFT, Keys.DOWN, Keys.RIGHT, Keys.CONTROL_RIGHT, Keys.SHIFT_RIGHT}, {Keys.Z, Keys.Q, Keys.S, Keys.D, Keys.A, Keys.E}, {Keys.I, Keys.J, Keys.K, Keys.L, Keys.U, Keys.O}, {Keys.NUMPAD_5, Keys.NUMPAD_1, Keys.NUMPAD_2, Keys.NUMPAD_3, Keys.NUMPAD_4, Keys.NUMPAD_6}};

	private static boolean[] useController = {false, false, false, false}; // défini quel joueur utilise une manette (controller) : la position du booléen correspond à l'id du joueur
	private static boolean desktopMode = false; // vrai si le jeu est en fenêtré, faux si le jeu est en plein écran
	private static boolean debugOverlay = false; // vrai si l'affichage des données débogages est activé
	private static boolean loop = true; // si vrai les joueurs qui sortent de l'écran par le bas réapparaissent en haut de l'écran, si faux ils meurent
	private static boolean fartMode = false;
	private static boolean gamePaused = false;
	private static float soundEffectVolume = 1f; // volume des effets sonores
	private static float musicVolume = 1f; // volume de la musique
	private static int defaultShaderID = 0;
	
	/**
	 * TODO : retravailler la méthode lors de l'ajout de l'écran de sélection
	 * parcours les manettes connectées et les attributs aux joueurs disponibles
	 * @param playersNumber
	 */
	public static void initControllers(int playersNumber) {
		for (int i = 0; i < Controllers.getControllers().size; i++) {
			if (Controllers.getControllers().get(i) != null)
				useController[i] = true;
		}
	}
	
	public static int getCustomLevelsNumber() {
		File folder = new File(System.getProperty("user.home") + "\\Local Settings\\Application Data\\Cassin All-Star Battle");
		
		if (!folder.exists())
			folder.mkdir();
		
		return folder.list().length;
	}

	/**
	 * change la résolution du jeu (plein écran ou fenêtré 960x540)
	 * @param _desktopMode : vrai si le jeu est en fenêtré, faux si le jeu est en plein écran
	 */
	public static void setDesktopMode(boolean _desktopMode) {
		desktopMode = _desktopMode;
		
		if (desktopMode) 
			Gdx.graphics.setDisplayMode(960, 540, false);
		else
			Gdx.graphics.setDisplayMode(1920, 1080, true);
	}
	
	public static void setDebugOverlay(boolean _debugOverlay) {
		debugOverlay = _debugOverlay;
	}
	
	public static void setLoop(boolean _loop) {
		loop = _loop;
	}
	
	public static void setFartMode(boolean _fart) {
		fartMode = _fart;
	}
	
	public static void setGamePaused(boolean _gamePaused) {
		gamePaused = _gamePaused;
	}
	
	public static void setDefaultShaderID(int id) {
		defaultShaderID = id;
	}
	
	public static void setSoundEffectVolume(float _soundEffectVolume) {
		soundEffectVolume = _soundEffectVolume;
	}
	
	public static void setMusicVolume(float _musicVolume) {
		musicVolume = _musicVolume;
	}
	
	public static boolean getUseController(int index) {
		return useController[index];
	}
	
	public static boolean getDesktopMode() {
		return desktopMode;
	}
	
	public static boolean getDebugOverlay() {
		return debugOverlay;
	}
	
	public static boolean getLoop() {
		return loop;
	}
	
	public static boolean getFartMode() {
		return fartMode;
	}
	
	public static boolean isGamePaused() {
		return gamePaused;
	}
	
	public static int getDefaultShaderID() {
		return defaultShaderID;
	}
	
	public static float getSoundEffectVolume() {
		return soundEffectVolume;
	}
	
	public static float getMusicVolume() {
		return musicVolume;
	}
}
