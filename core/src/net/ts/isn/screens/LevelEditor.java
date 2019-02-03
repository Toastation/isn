package net.ts.isn.screens;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.Config;
import net.ts.isn.Main;
import net.ts.isn.Resources;
import net.ts.isn.screens.gui.Button;
import net.ts.isn.util.Animation;
import net.ts.isn.world.Level;
import net.ts.isn.world.tiles.Platform;
import net.ts.isn.world.tiles.Spikes;
import net.ts.isn.world.tiles.Tile;
import net.ts.isn.world.tiles.Trampoline;
import net.ts.isn.world.tiles.Wall;

public class LevelEditor extends RootScreen {

	private static Button block; // bouton permettant d'acc馘er aux blocs
	private static Button item; // bouton permettant d'acc馘er aux items
	private static Button spawn; // bouton permettant d'acc馘er au "spawn", ou lieu d'apparition des personnages lorsqu'ils "meurent"
	private static Button returnSelect;  // bouton permettant de retourner ・l'馗ran de s駘ection des niveaux
	private static Button start; // bouton permettant de commencer la partie
	private static Button save;
	
	private static int entityId; // identifiant du bloc ou item
	private static int entityIdMax;
	private static Tile[] id; // tableau qui associe chaque identifiant ・bloc d馭ini
	
	private static boolean leftClick; // vrai si le clic gauche de la souris est enfonc・
	private static boolean rightClick; // vrai si le clic droit de la souris est enfonc・
	private static boolean firstkey; // vrai si le joueur appuie sur l'une des deux touches d馗lar馥s (gauche ou droite)

	private static boolean editVisible; // vrai si la barre d'édition est visible
	
	private static int xo, yo, xm, ym; //
	private static String editMode;
	
	private static Animation key; //
	private static Sprite currentTileImage;
	
	public LevelEditor(Main game) {
		super(game);
		
		entityId = 0;
		entityIdMax = 3;
		id = new Tile[4];
		id[0] = new Wall(0, 0, 0);
		id[1] = new Platform(0, 0);
		id[2] = new Spikes(0, 0);
		id[3] = new Trampoline(0, 0, 0);
		editVisible = true;
		firstkey = false;
		
		key = new Animation(0, 0, 46, 51, 204, 400, Resources.arrowkeys);
		currentTileImage = new Sprite(Resources.tiles, id[entityId].getSourceX(), id[entityId].getSourceY(), 32, 32);
		currentTileImage.setSize(64, 64);
		
		block = new Button("Block", 1540, 290);
		item = new Button("Background block", 1540, 215);
		spawn = new Button ("Spawn", 1540, 140);
		start = new Button ("Start", 1540, 75);
		save = new Button("Save", 1680, 75);
		returnSelect = new Button ("Back", 1835, 75);
		block.setFont(Resources.font);
		item.setFont(Resources.font);
		spawn.setFont(Resources.font);
		returnSelect.setFont(Resources.font);
		start.setFont(Resources.font);
		save.setFont(Resources.font);
		editMode = "Block";
	}
	
	public void show() {
		super.show();
		Level.init();
		Level.clearBackgroundTiles();
	}
	
	public void update(float delta) {
		super.update(delta);
		
		this.updateInputsData();
		
		key.update();
		
		if (xm < 0 || xm > 1919 || ym < 0 || ym > 1024) {
			leftClick = false;
			rightClick = false;
		}
		
		if (rightClick) {
			if (editVisible && (xm < 1520 || (xm > 1520 && ym > 400)))
				this.rightClickBehaviour();
			else if (!editVisible)
				this.rightClickBehaviour();
		}
		
		if (leftClick) {
			if (editVisible && (xm < 1520 || (xm > 1520 && ym > 400)))
				this.leftClickBehaviour();
			else if (!editVisible) 
				this.leftClickBehaviour();
		}
		
		currentTileImage.setPosition(xo*64, yo*64);
		
		this.updateKeys();
		
		if (editVisible)
			this.updateButtons();
		
	}
	
	private void updateInputsData() {
		xm = Gdx.input.getX()*1920/RootScreen.screenWidth;
		ym = 1080-(Gdx.input.getY()*1080/RootScreen.screenHeight);
		xo = (int) Math.ceil((xm*30)/1920);
		yo = (int) Math.ceil((ym*16)/1024);
		leftClick = Gdx.input.isButtonPressed(Buttons.LEFT);
		rightClick = Gdx.input.isButtonPressed(Buttons.RIGHT);
	}
	
	private void updateKeys() {
		if (Gdx.input.isKeyJustPressed(Keys.X))
			editVisible = !editVisible;
		else if (Gdx.input.isKeyJustPressed(Keys.S)) {
			Level.getSpawnList().add(new Vector2(xo*Level.TILE_WIDTH, yo*Level.TILE_HEIGHT));
			Level.newSpawn();
		} else if (Gdx.input.isKeyJustPressed(Keys.P))
			this.saveLevel();
		
		else if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			firstkey = true;
			
			if (entityId == entityIdMax)
				entityId = 0;
			else
				entityId++;
			
			if (editMode == "Block")
				currentTileImage.setRegion(id[entityId].getSourceX(), id[entityId].getSourceY(), 32, 32);
			else if (editMode == "Background block")
				currentTileImage.setRegion(Resources.backgroundTileSourceCoord[entityId].x, Resources.backgroundTileSourceCoord[entityId].y, 32, 32);
		}
		
		else if (Gdx.input.isKeyJustPressed(Keys.LEFT))	{
			firstkey = true;
			
			if (entityId==0)
				entityId = entityIdMax;
			else
				entityId--;
			
			if (editMode == "Block")
				currentTileImage.setRegion(id[entityId].getSourceX(), id[entityId].getSourceY(), 32, 32);
			else if (editMode == "Background block")
				currentTileImage.setRegion(Resources.backgroundTileSourceCoord[entityId].x, Resources.backgroundTileSourceCoord[entityId].y, 32, 32);
		}
	}

	private void updateButtons() {
		if (start.isClicked()) {
			Level.checkTiles();
			((GameScreen)Main.screens.get(2)).create(SelectionScreen.getMode(), SelectionScreen.getPlayerNumber(), SelectionScreen.getIds());
			this.game.setScreen(Main.screens.get(2));
		} else if (returnSelect.isClicked()) 
			this.game.setScreen(Main.screens.get(1));
		else if (block.isClicked()) {
			editMode = "Block";
			entityIdMax = id.length-1;
			entityId = 0;
			currentTileImage.setRegion(id[entityId].getSourceX(), id[entityId].getSourceY(), 32, 32);
		} else if (spawn.isClicked()) {
			editMode = "Spawn";
			entityIdMax = 0;
			entityId = 0;
		} else if (item.isClicked()) {
			editMode = "Background block";
			entityIdMax = 1;
			entityId = 0;
			currentTileImage.setRegion(Resources.backgroundTileSourceCoord[entityId].x, Resources.backgroundTileSourceCoord[entityId].y, 32, 32);
		} else if (save.isClicked())
			this.saveLevel();
	}
	
	private void leftClickBehaviour() {
		if (editMode == "Block" && Level.isTileFree(xo, yo)) {
			Level.addTile(id[entityId].getClone(), xo, yo);
			Level.refreshTileTexture();
			Resources.putBlock.play(Config.getSoundEffectVolume());
		} else if (editMode == "Spawn") {
			Level.getSpawnList().add(new Vector2(xo*Level.TILE_WIDTH, yo*Level.TILE_HEIGHT));
			Level.newSpawn();
		} else if (editMode == "Background block")
			Level.addBackgroundTile(entityId, xo, yo);
	}
	
	private void rightClickBehaviour() {
		if (editMode == "Block" && !Level.isTileFree(xo, yo)) {
			Level.deleteTile(xo, yo);
			Resources.deleteBlock.play(Config.getSoundEffectVolume());
		} else if (editMode == "Spawn") {
			
		} else if (editMode == "Background Block")
			Level.deleteBackgroundTile(xo, yo);
	}
	
	private void saveLevel() {
		BufferedImage level = new BufferedImage(30, 16,BufferedImage.TYPE_INT_RGB);
		BufferedImage levelBackground = new BufferedImage(30, 16,BufferedImage.TYPE_INT_RGB);
		File folder = new File(System.getProperty("user.home") + "\\Local Settings\\Application Data\\Cassin All-Star Battle");
		
		if (!folder.exists())
			folder.mkdir();
		
		File outputLevel = new File(System.getProperty("user.home") + "\\Local Settings\\Application Data\\Cassin All-Star Battle\\level.png");
		File outputLevelBackground = new File(System.getProperty("user.home") + "\\Local Settings\\Application Data\\Cassin All-Star Battle\\level_background.png");
		
		for (int x = 0; x < Level.WIDTH; x++) {
			for (int y = 0; y < Level.HEIGHT; y++) {
				switch (Level.getTileID(x, y)) {
					case 0:
						level.setRGB(x, 15-y, 0xFF0000);
						break;
					case 1:
						level.setRGB(x, 15-y, 0x000000);
						break;
					case 2:
						level.setRGB(x, 15-y, 0x00FF00);
						break;
					case 3:
						level.setRGB(x, 15-y, 0xFFCCFF);
						break;
					default:
						level.setRGB(x, 15-y, 0xFF00DC);
						break;
				}
				
				switch (Level.getBackgroundTiles()[x][y]) {
					case 0:
						levelBackground.setRGB(x, 15-y, 0xFF0000);
						break;
					case 1:
						levelBackground.setRGB(x, 15-y, 0xFFCC00);
						break;
					default:
						levelBackground.setRGB(x, 15-y, 0xFF00DC);
						break;
				}
			}
		}

		Vector2 spawnCoord;
		for (int i = 0; i < Level.getSpawnList().size(); i++) {
			spawnCoord = Level.getSpawnList().get(i);
			level.setRGB((int)spawnCoord.x / Level.TILE_WIDTH, 15-(int)spawnCoord.y / Level.TILE_HEIGHT, 0x0000FF);
		}
		
		try {
			ImageIO.write(level, "png", outputLevel);
			ImageIO.write(levelBackground, "png", outputLevelBackground);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void render(float delta) {
		super.render(delta);
		batch.begin();
		
		batch.draw(Resources.backgrounds[0],0, 0);
		Level.render(batch);
		if (editMode == "Block")
			currentTileImage.draw(batch, 0.25f);
		else if (editMode == "Spawn" )
			batch.draw(Resources.spawn, xo*64, yo*64);
		
		if (editVisible) {
			batch.draw(Resources.editBackground, 1520, 0);
			block.render(batch);
			item.render(batch);
			spawn.render(batch);
			returnSelect.render(batch);
			start.render(batch);
			save.render(batch);
		}
		
		if (!firstkey) {
			key.render(batch, xm-70, ym-20, false, 0.5f);
			key.render(batch, xm+20, ym-20, true, 0.5f);	
		}
		
		for (int i=0 ; i<Level.getSpawnList().size() ; i++) {
			 batch.draw(Resources.spawn, Level.getSpawnList().get(i).x, Level.getSpawnList().get(i).y);
		}
		batch.end();
	}
}
