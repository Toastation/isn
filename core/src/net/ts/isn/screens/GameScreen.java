package net.ts.isn.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.ts.isn.Config;
import net.ts.isn.GameMode;
import net.ts.isn.Main;
import net.ts.isn.Resources;
import net.ts.isn.util.Timer;
import net.ts.isn.world.Level;
import net.ts.isn.world.Player;

public class GameScreen extends RootScreen {
	private static int playerNumber; // le nombre de joueur
	private static int levelID; // l'identifiant du niveau actuel
	private static int[] ids; // liste des identifiants des personnages de chaque joueur
	private static GameMode mode; // le mode de jeu
	private static int timeOrigin;
	
	private static boolean isGameFinished;
	private static int playersEliminated;
	private static Timer timeLeftDelay; // d駘ai entre chaque d馗r駑entation du temps restant (de base 1 seconde)
	
	private static ShapeRenderer renderer;
	
	public GameScreen(Main game) {
		super(game);
		
		isGameFinished = false;
		timeLeftDelay = new Timer(1000);
		
		renderer = new ShapeRenderer();
		renderer.setAutoShapeType(true);
	}
	
	public void create(GameMode _mode, int _levelID, int _playerNumber, int[] _ids) {
		mode = _mode;
		timeOrigin = _mode.getParam();
		playerNumber = _playerNumber;
		levelID = _levelID;
		ids = _ids;
	
	//	Config.initControllers(playerNumber);
		
		this.start();
		
		Level.init();
		Level.loadLevel(levelID);
		Level.loadPlayers(playerNumber, ids);
	}
	
	public void create(GameMode _mode, int _playerNumber, int[] _ids) {
		mode = _mode;
		timeOrigin = _mode.getParam();
		playerNumber = _playerNumber;
		ids = _ids;
	
		Config.initControllers(playerNumber);
		
		this.start();
		
		Level.loadPlayers(playerNumber, ids);
	}
	
	/**
	 * charge le niveau, les joueurs et d饕ute la musique
	 */
	public void start() {
		playersEliminated = 0;
		
		Resources.battletheme.play();
		Resources.battletheme.setVolume(Config.getMusicVolume());
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Config.setGamePaused(!Config.isGamePaused());
		
		isGameFinished = this.isGameFinished();
		
		if (isGameFinished)
			this.finishGame();
		
		if (!Config.isGamePaused())
			Level.update(delta);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		batch.begin();
		
		Level.render(batch);
		
		if (!isGameFinished)
			this.renderGameMode(batch);
		
		Resources.font.draw(batch, "FPS : "+Gdx.graphics.getFramesPerSecond(), 5, 25);
		
		batch.end();
		
		if (Config.getDebugOverlay()) {
			renderer.begin();
			for (int i = 0; i < Level.getPlayers().size(); i++) {
				Player player = Level.getPlayers().get(i);
				renderer.rect(player.getHitbox().x, player.getHitbox().y, player.getHitbox().width, player.getHitbox().height);
			}
			
			for (int x = 0; x < Level.WIDTH; x++) {
				for (int y = 0; y < Level.HEIGHT; y++) {
					if (Level.getTiles()[x][y] != null)
						renderer.rect(Level.getTiles()[x][y].getHitbox().x, Level.getTiles()[x][y].getHitbox().y, Level.getTiles()[x][y].getHitbox().width, Level.getTiles()[x][y].getHitbox().height);
				}
			}
			renderer.end();
		}
	}
	
	/**
	 * v駻ifie les conditions pour qu'une partie soit termin�e en fonction du mode de jeu
	 * @return si oui ou non la partie est terminn�e en fonction du mode de jeu actuel
	 */
	public boolean isGameFinished() {
		switch (mode) {
			case TIME:
				if (!timeLeftDelay.isStarted())
					timeLeftDelay.start();
				
				if (!timeLeftDelay.isPaused() && timeLeftDelay.isComplete()) { // mise � jour du d馗ompte
					mode.setParam(mode.getParam()-1);
					timeLeftDelay.reset();
				}
				
				return (mode.getParam() <= 0);
			
			case LIFE:
				int life = 0;
				
				for (int i = 0; i < playerNumber; i++) {
					life = mode.getParam() - Level.getPlayers().get(i).getDeaths();
					
					if (life <= 0 && Level.getPlayers().get(i).canPlay()) { // on v駻ifie si il reste encore des vies au joueur
						Level.getPlayers().get(i).setCanPlay(false);
						playersEliminated++;
					}	
				}
				
				return playersEliminated == playerNumber-1; // retourne vrai si il ne reste plus qu'un seul joueur qui n'est pas 駘imin�
			
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * affiche les 駘駑ents relatif au mode de jeu
	 * @param batch : voir RootScreen
	 */
	public void renderGameMode(SpriteBatch batch) {
		switch(mode) {
			case TIME:
				Resources.font.draw(batch, "Time left : "+mode.getParam(), 1730, 25);
				break;
			case LIFE:
				int life = 0;
				String text = "";
				for (int i = 0; i < playerNumber; i++) {
					life = mode.getParam() - Level.getPlayers().get(i).getDeaths();
					text = life == 1 ? "Vie : 1" : "Vies : "+life;
					Level.getTexts().get(i).setText("Joueur "+(i+1)+System.lineSeparator()+"Score : "+Level.getPlayers().get(i).getScore()+System.lineSeparator()+text);
				}
				break;
			default:
				break;
		}
	}
	
	/**
	 * transition jusqu'� l'�cran des scores et "nettoyage" du niveau
	 */
	public void finishGame() {
		((ScoreScreen)Main.screens.get(3)).getAndOrganizeScore(Level.getScore(), playerNumber); // on envoie les donn馥s des joueurs � l'馗ran des scores
		
		Level.clearLevel(); // on vide le niveau
		
		this.game.setScreen(Main.screens.get(3)); // changement de l'馗ran
	}

	/**
	 * affiche un shader pour une dur馥 donn馥 
	 * @param shader : le shader devant 黎re affich�
	 * @param duration : dur馥 du shader
	 */
	public static void playShader(int shaderID, int duration) {
		playShader = true;
		currentShaderID = shaderID;
		shaderDuration.setDuration(duration);
		shaderDuration.start();
		batch.setShader(Resources.shaders[currentShaderID]);
	}
	
	public static int getTimeOrigin() {
		return timeOrigin;
	}
	
	public static GameMode getMode() {
		return mode;
	}
	
	public static int getLevelID() {
		return levelID;
	}

	public static int[] getIds() {
		return ids;
	}
}