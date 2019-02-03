package net.ts.isn.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.ts.isn.Config;
import net.ts.isn.Main;
import net.ts.isn.Resources;
import net.ts.isn.util.Timer;
import net.ts.isn.util.Util;

public class RootScreen implements Screen, InputProcessor {
	protected static int screenWidth = Gdx.graphics.getWidth();
	protected static int screenHeight = Gdx.graphics.getHeight();
	protected static int halfWidth = screenWidth / 2;
	protected static int halfHeight = screenHeight / 2;
	
	protected Game game; // l'unique instance de la classe Main, elle gère les écrans
	protected static SpriteBatch batch; // permet de synchroniser toutes les textures envoyés au GPU pour chaque frame
	protected static OrthographicCamera cam;
	protected Viewport viewPort;

	protected static boolean playShader; // vrai si le shader actuel est en train d'être rendu
	protected static int currentShaderID;
	
	protected static Timer shaderDuration;
	
	public RootScreen(Main game) {
		this.game = game;
		batch = new SpriteBatch();
		batch.enableBlending();
		batch.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		
		cam = new OrthographicCamera(1920, 1080);
		cam.position.set(960, 540, 0);
	    cam.update();
	    this.viewPort = new FitViewport(1920, 1080, cam);
	    
		playShader = false;
		currentShaderID = Config.getDefaultShaderID();
		batch.setShader(Resources.shaders[currentShaderID]);
		bindInputToShader();
		
		shaderDuration = new Timer(200);
	}
	
	public void show() {
		Gdx.input.setInputProcessor(this);
	}
	
	public void update(float delta) {
		this.input();
		
		if (playShader) { // vrai si les shaders sont activés
			bindInputToShader();
			
			if (shaderDuration.isStarted() && shaderDuration.isComplete()) {
				playShader = false;
				currentShaderID = Config.getDefaultShaderID();
				batch.setShader(Resources.shaders[currentShaderID]); // on remet le shader par défaut
				shaderDuration.stop();
			}
		}
	}

	public void render(float delta) {
		this.update(delta);
		
		// nettoyage de l'écran et on le rempli avec un fond noir
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 
        Gdx.gl.glClearColor(0, 0, 0, 1);
        
        // mise à jour de la caméra et de la matrice de projection du spritebatch
        cam.update();
        batch.setProjectionMatrix(cam.combined);
		
		batch.setShader(Resources.shaders[currentShaderID]);
	}
	
	/**
	 * gère les entrées clavier universelles dans le programme (accessibles dans n'importe quel écran)
	 */
	public void input() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Keys.R)) 
			cam.rotate(new Vector3(0, 0, 1), 180);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_0)) 
			cam.zoom -= 0.1f;
		if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) 
			cam.zoom += 0.1f;
		if (Gdx.input.isKeyJustPressed(Keys.NUM_8)) {
			if (Config.getDefaultShaderID() == 3)
				Config.setDefaultShaderID(0);
			else
				Config.setDefaultShaderID(Config.getDefaultShaderID()+1);
			
			if (!playShader) {
				currentShaderID = Config.getDefaultShaderID();
				batch.setShader(Resources.shaders[Config.getDefaultShaderID()]);
			}	
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.F1))
			Config.setDebugOverlay(!Config.getDebugOverlay());
		if (Gdx.input.isKeyJustPressed(Keys.F2))
			Config.setFartMode(!Config.getFartMode());
		if (Gdx.input.isKeyPressed(Keys.F)) // plein écran
			Config.setDesktopMode(!Config.getDesktopMode());
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
			Gdx.app.exit();
	}
	
	public static void bindInputToShader() {
		batch.getShader().begin();
		switch(currentShaderID) {
			case 1:
				batch.getShader().setUniformf("u_resolution", screenWidth, screenHeight);
				break;	
			case 2:
				batch.getShader().setUniformf("u_resolution", screenWidth, screenHeight);
				break;	
			case 3:
				batch.getShader().setUniformf("u_resolution", screenWidth, screenHeight);
				break;	
			case 4:
				batch.getShader().setUniformMatrix("u_projTrans", cam.combined);
				batch.getShader().setUniformf("u_distort", Util.getRandomInteger(-5, 5), Util.getRandomInteger(-5, 5), 0);
				break;	
		}
		batch.getShader().end();
	}
	
	public void resize(int width, int height) {
		this.viewPort.update(width, height);
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		halfWidth = screenWidth/2;
		halfHeight = screenHeight/2;
	}

	public void pause() {
		
	}

	public void resume() {
		
	}

	public void hide() {
		
	}

	public void dispose() {
		batch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (amount >= 1)
			cam.rotate(new Vector3(0, 0, 1), 1f);
		else if (amount <= -1)
			cam.rotate(new Vector3(0, 0, 1), -1f);
		return false;
	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}

	public static int getHalfWidth() {
		return halfWidth;
	}

	public static int getHalfHeight() {
		return halfHeight;
	}
}
