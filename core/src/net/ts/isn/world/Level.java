package net.ts.isn.world;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import net.ts.isn.Config;
import net.ts.isn.Resources;
import net.ts.isn.screens.RootScreen;
import net.ts.isn.screens.gui.Text;
import net.ts.isn.util.Util;
import net.ts.isn.world.tiles.Platform;
import net.ts.isn.world.tiles.Spikes;
import net.ts.isn.world.tiles.Tile;
import net.ts.isn.world.tiles.Trampoline;
import net.ts.isn.world.tiles.Wall;
import net.ts.isn.world.tiles.Warp;

public class Level {
	public static final int WIDTH = 30;
	public static final int HEIGHT = 16;
	public static final int TILE_WIDTH = 32*Config.SCALE;
	public static final int TILE_HEIGHT = 32*Config.SCALE;
	
	private static int id; // l'identifiant du niveau, permet de charger le fond et le niveau correspondant
	private static int hudGap; // l'espace en pixel entre chaque paneau d'information du joueur, dàpend du nombre de joueur
	private static int lastSpawn; // l'identifiant du dernier endroit oà un joueur est apparu, permet de faire en sorte que les joueurs ne ràapparaissent pas tous au màme endroit
	
	private static boolean editMode; // vrai si le niveau est en mode édition, pour l'éditeur de niveau
	
	private static Tile[][] tiles;
	private static int[][] backgroundTiles;
	
	private static ArrayList<Player> players;
	private static ArrayList<Item> items;
	private static ArrayList<Projectile> projectiles;
	private static ArrayList<PooledEffect> effects;
	private static ArrayList<Text> texts;
	private static ArrayList<Vector2> spawnList;
	
	private static Pool<Projectile> projectilePool;
	
	private Level() {
		
	}
	
	public static void init() {
		spawnList = new ArrayList<Vector2>();
		lastSpawn = 0;
		
		tiles = new Tile[WIDTH][HEIGHT];
		backgroundTiles = new int[WIDTH][HEIGHT];
		
		players = new ArrayList<Player>();
		items = new ArrayList<Item>();
		//items.add(Item.SPEEDBONUS.setPosition(1000, 266));
		//items.add(Item.HADOKEN.setPosition(500, 266));
		effects = new ArrayList<PooledEffect>();
		projectiles = new ArrayList<Projectile>();
		
		projectilePool = new Pool<Projectile>() {
			@Override
			protected Projectile newObject() {
				return new Projectile();
			}
		};
		
		texts = new ArrayList<Text>();
	}
	
	public static void createHUD() {
		hudGap = RootScreen.getScreenWidth() / (players.size() + 1) - 50;
		
		for (int i = 0; i < players.size(); i++) {
			texts.add(new Text("Player "+(i+1)+System.lineSeparator()+"Score : "+players.get(i).getScore(), hudGap*(players.get(i).getId() + 1)+60, 1067, true, 2));
			
			if (i == 0)
				texts.get(i).setColor(Color.BLUE);
			if (i == 1)
				texts.get(i).setColor(Color.RED);
			if (i == 2)
				texts.get(i).setColor(Color.GREEN);
			if (i == 3)
				texts.get(i).setColor(Color.YELLOW);
		}
	}
	
	/**
	 * créer les personnages définis dans l'écran de sàlection
	 * @param playerNbr : le nombre de joueurs
	 * @param ids : l'identifiant des personnages (l'indice est l'identifiant du joueur, la valeur est l'identifiant du personnage choisi)
	 */
	public static void loadPlayers(int playerNbr, int[] ids) {
		lastSpawn = Util.getRandomInteger(0, spawnList.size()-1);
		for (int i = 0; i < playerNbr; i++) {
			players.add(i, new Player(i, ids[i], spawnList.get(lastSpawn)));
			newSpawn();
		}
		
		createHUD();
	}
	
	/** 
	 * charge tous les éléments du niveau passé en paramètre
	 * @param _id l'identifiant du niveau
	 */
	public static void loadLevel(int _id) {
		id = _id; // on stock la valeur de l'identifiant du niveau que l'on souhaite charger
		
		loadActiveTile();
		loadBackgroundTiles();
		
		checkTiles(); // cette fonction va déterminer quels blocs sont à mettre à jour en fonction de leur environnement
	}
	
	/**
	 * parcours une image pixel par pixel et ajoute un bloc ou une entité au niveau, aux coordonnées du pixel, et en fonction de sa couleur
	 * @param id : l'identifiant du niveau
	 */
	public static void loadActiveTile() {
		Resources.levels.getTextureData().prepare(); // on signale à l'objet Texture, qui représente l'image du niveau, que l'on souhaite accéder à ses données (couleur des pixels)
		Pixmap pm = Resources.levels.getTextureData().consumePixmap(); // on stocke la couleur de chaque pixel dans un tableau (l'objet Pixmap) contenant la valeur de chaque composante (RGBA)
		
		Color color = new Color(); // cet objet va stocker alternativement la couleur de chaque pixel au fur et à mesure du balayage de l'image par la double boule "for"
		int xx = 0; // abscisse ramenée à l'origine 

		// on balaye le tableau (Pixmap) et on stock la couleur dans l'objet Color pour une manipulation plus facile
		for (int x = WIDTH*id; x < WIDTH+WIDTH*id; x++) { // pour x allant de l'abscisse d'origine du niveau dans l'image, à l'abscisse de son côté droit
			for (int y = 0; y < HEIGHT; y++) { // pour x allant de 0 (ordonnée d'origine) à la hauteur du niveau (côté supérieur de l'image)
				Color.rgba8888ToColor(color, pm.getPixel(x, HEIGHT-1-y)); // on stock la couleur et on ajuste l'ordonnée car on commence la boucle à la valeur 0
				
				xx = x-WIDTH*id; // on décale l'abscisse vers la gauche pour la recentrée à 0
				
				// selon la couleur du pixel actuel, on créer et stock différents blocs dans le tableau aux coordonnées (xx,y)
				if (color.equals(Color.RED)) 
					tiles[xx][y] = new Wall(xx*TILE_WIDTH, y*TILE_HEIGHT, 0);
				else if (color.equals(new Color(1, 0.8f, 0, 1))) 
					tiles[xx][y] = new Wall(xx*TILE_WIDTH, y*TILE_HEIGHT, 1);
				else if  (color.equals(Color.BLACK)) 
					tiles[xx][y] = new Spikes(xx*TILE_WIDTH, y*TILE_HEIGHT);
				else if  (color.equals(Color.GREEN))
					tiles[xx][y] = new Platform(xx*TILE_WIDTH, y*TILE_HEIGHT);
				else if  (color.equals(Color.WHITE))
					tiles[xx][y] = new Trampoline(xx*TILE_WIDTH, y*TILE_HEIGHT, 0);
				else if  (color.equals(new Color(1, 0.8f, 1, 1)))
					tiles[xx][y] = new Trampoline(xx*TILE_WIDTH, y*TILE_HEIGHT, 1);
				else if  (color.equals(new Color(1, 0, 1, 1)))
					tiles[xx][y] = new Warp(xx*TILE_WIDTH, y*TILE_HEIGHT);
	
				else if  (color.equals(Color.BLUE)) {
					spawnList.add(new Vector2(xx*TILE_WIDTH, y*TILE_HEIGHT)); // si c'est un spawn (endroit ou doit appaitre le joueur) on ajoute la position en pixel dans une liste
					newSpawn(); // on indique qu'on a ajouté un spawn 
				}
			}
		}
		
		pm.dispose(); // on supprime le tableau contenant les données de l'image
	}
	
	/**
	 * parcours une image pixel par pixel et ajoute une certaine texture, aux coordonnées du pixel, en fonction de sa couleur
	 * @param id : l'identifiant du niveau
	 */
	public static void loadBackgroundTiles() {
		Resources.levelsBackground.getTextureData().prepare();
		Pixmap pm = Resources.levelsBackground.getTextureData().consumePixmap();
		Color color = new Color();
		int xx = 0;
		
		for (int x = WIDTH*id; x < WIDTH+WIDTH*id; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				Color.rgba8888ToColor(color, pm.getPixel(x, HEIGHT-1-y));
				xx = x-WIDTH*id;
				
				if (color.equals(Color.BLACK))
					backgroundTiles[xx][y] = 0;
				else if (color.equals(new Color(1, 0.8f, 0, 1)))
					backgroundTiles[xx][y] = 1;
				else
					backgroundTiles[xx][y] = -1;
			}
		}
		
		pm.dispose();
	}
	
	/**
	 * vérifie si des blocs sont mis à jour inutilement (s'ils sont entouràs d'autres blocs)
	 */
	public static void checkTiles() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				if (tiles[x][y] != null) {
					tiles[x][y].checkNeedToUpdate(getNeighbors(x, y));
					
					if (tiles[x][y].hasOrientedTexture())
						tiles[x][y].setCorrectTexture(getNeighbors(x, y));
				}
			}
		}
	}
	
	/**
	 * vide les tableaux contenant les blocs, les joueurs, et les autres entitàs
	 */
	public static void clearLevel() {
		clearLevelButPlayers();
		players.clear();
	}
	
	public static void clearBackgroundTiles() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				backgroundTiles[x][y] = -1;
			}
		}
	}
	
	/**
	 * vide les tableaux contenant les blocs et les autres entitàs mais garde le tableau des joueurs
	 */
	public static void clearLevelButPlayers() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				tiles[x][y] = null;
			}
		}
		
		items.clear();
		projectiles.clear();
		projectilePool.clear();
	}
	
	/**
	 * mise à jour principale du niveau, appelle les fonctions mettant à jour toutes les entitàs du nivea pràsente dans le niveau
	 * @param delta : durée en milliseconde entre chaque frame (si 60fps -> delta = 0.016s)
	 */
	public static void update(float delta) {
		// mise à jour des joueurs
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).canPlay())
				playersUpdate(players.get(i), i, delta);
		}
		
		// mise à jour des blocs
		tilesUpdate(delta);
		
		for (Item item : items) {
			item.fluttering();
		}
		
		for (int i = 0; i < effects.size(); i++) {
			effects.get(i).update(delta);
			
			if (effects.get(i).isComplete()) {
				effects.get(i).free();
				effects.remove(i);
			}
		}
		
		// mise à jour des textes
		for (int i = 0; i < texts.size(); i++) {
			texts.get(i).update(delta);
		
			if (texts.get(i).mustBeCleared())
				texts.remove(i);
		}
		
		// mise à jour des projectiles
		projectileUpdate();
	}
	
	/**
	 * met à jour les joueurs : appelle leur fonction "update", gàre la mort des personnages et leur respawn ainsi que la dàtection pràliminaire des collisions avec une entità
	  * @param delta : duràe en milliseconde entre chaque frame (si 60fps -> delta = 0.016s)
	 */
	public static void playersUpdate(Player player, int i, float delta) {
		if (!player.isDead()) { // si le joueur est en vie
			player.update(delta);
				
			// vàrifie si le joueur est en collision avec un ou plusieurs joueurs
			for (int j = 0; j < players.size(); j++) {
				checkPlayerCollisionWithPlayer(i, j, player, players.get(j));
			}
			
			// vàrifie si le joueur est en collision avec un item
			for (int j = 0; j < items.size(); j++) {
				if (player.getHitbox().overlaps(items.get(j).getHitbox())) {
					ItemBehavior.pickItem(player, items.get(j));
					items.remove(j);
				}
			}
				
			// vérifie si le joueur est en collision avec un projectile
			for (int j = 0; j < projectiles.size(); j++) {
				checkCollisionWithProjectile(projectiles.get(j), player);
			}
		
			// rafraichissement du boolàen outsideBoders apràs que le joueur ait àtà tàlàportà
			if (player.isOutsideBorders())
				player.setOutsideBorders(false);
		} 
		
		else if (!player.isRespawned()) { // le joueur ràapparait au spawn et est invincible pendant 1,5 secondes
			spawnPlayer(player);
		}
		
		else if (player.isRespawnInvicibilityTimerComplete() || player.anyKeyPressed()) { // quand les 1,5 secondes sont terminàes ou si le joueur appuie sur une touche, il est ramenà à la vie
			player.setDead(false);
			player.setSpdX(0);
			player.setSpdY(0);
			player.setDefaultCharacteristics();
		} 
			
		// on parcourt tous les blocs du niveau et on vàrifie s'il sont en collision avec le joueur
		for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
				checkPlayerCollisionWithTile(tiles[x][y], player);
			}
		}
	}
	
	/**
	 * met à jour les blocs : appelle leur fonction "update"
	 * @param delta : duràe en milliseconde entre chaque frame (si 60fps -> delta = 0.016s)
	 */
	public static void tilesUpdate(float delta) {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				if (tiles[x][y] != null && tiles[x][y].toUpdate())
					tiles[x][y].update(delta);
			}
		}
	}
	
	public static void projectileUpdate() {
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile projectile = projectiles.get(i);
			if (projectile.isAlive()) {
				projectile.update();
			
				for (int x = 0; x < WIDTH; x++) {
					for (int y = 0; y < HEIGHT; y++) {
						if (tiles[x][y] != null && tiles[x][y].toUpdate()) {
							if (tiles[x][y].getHitbox().overlaps(projectile.getHitbox()))
								projectile.die();
						}
					}
				}
			} else {
				projectiles.remove(projectile);
				projectilePool.free(projectile);
			}
		}
	}
	
	/**
	 * effectue les rendus de toutes les entitàs en jeu et du HUD
	 * @param batch : le SpriteBatch de l'àcran de jeu
	 */
	public static void render(SpriteBatch batch) {
		batch.draw(Resources.backgrounds[0], 0, 0, 1920, 1080); 
		
		// affiche des tiles de fond
		Sprite sprite;
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				if (backgroundTiles[x][y] >= 0) {
					sprite = new Sprite(Resources.tiles, (int)Resources.backgroundTileSourceCoord[backgroundTiles[x][y]].x, (int)Resources.backgroundTileSourceCoord[backgroundTiles[x][y]].y, 32, 32);
					sprite.setOrigin(0, 0);
					sprite.setPosition(x*TILE_WIDTH, y*TILE_HEIGHT);
					sprite.setScale(2);
					sprite.draw(batch);
				}
			}
		}
		
		// affichage des tiles
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				if (tiles[x][y] != null)
					tiles[x][y].render(batch);
			}
		}
		
		// affichage des effets de particule
		for (int i = 0; i < effects.size(); i++) {
			effects.get(i).draw(batch);
		}
		
		// afichage des joueurs
		Player player;
		for (int i = 0; i < players.size(); i++) {
			player = players.get(i);
			
			if (player.canPlay()) {
				player.render(batch);
				
				if (Gdx.input.isKeyPressed(Keys.TAB)) {
					Resources.font.draw(batch, "P"+(i+1), player.getPos().x+64-Util.getStringLength("P"+(i+1), Resources.font)/2, player.getPos().y+240);
					batch.draw(Resources.gui, player.getPos().x+47, player.getPos().y+184, 34, 24, 74, 0, 17, 12, false, false);
				}
			}
		}
		
		// affichage des items
		for (Item item : items) {
			item.render(batch);
		}
		
		// affichage des projectiles
		for (int i = 0; i< projectiles.size(); i++) {
			projectiles.get(i).render(batch);
		}
		
		// affichage des textes 
		for (int i = 0; i < texts.size(); i++) {
			texts.get(i).render(batch);
		}
	
		// affichage du HUD 
		for (int i = 0; i < players.size(); i++) {
			batch.draw(Resources.heads, hudGap*(i + 1), 1015, 54, 60, 27*players.get(i).getCharID(), 0, 27, 30, false, false);
			
			if (players.get(i).getCurrentItem() != Item.NONE)
				batch.draw(Resources.items, hudGap*(i + 1)+20, 990, 24, 24, 24*players.get(i).getCurrentItem().getID(), 0, 24, 24, false, false);
		}
	}
	
	/**
	 * vàrifie si le joueur est en collision avec un ou plusieurs blocs et appelle la fonction collisionWithEntity qui va faire ràagir le joueur
	 */
	public static void checkPlayerCollisionWithTile(Tile tile, Player player) {
		if (tile != null && tile.toUpdate() && tile.hasCollision()) {
			
			boolean letPassIfCrouch = tile.letPassIfCrouch(player.isCrouch());
					
			if (tile.getHitbox().overlaps(player.getHitbox()))
				player.collisionWithEntity(tile);
			// cette ligne ràgle le problàme de l'oscillation de l'ordonnée du joueur en faisant en sorte que le joueur touche le bloc du bas s'il est à un pixel au dessus
			if (((player.getHitbox().x >= tile.getPos().x && player.getHitbox().x <= tile.getPos().x+tile.getSize().x) || (player.getHitbox().x >= tile.getPos().x && player.getHitbox().x <= tile.getPos().x+tile.getSize().x)) && player.getHitbox().y == tile.getPos().y+tile.getSize().y && !letPassIfCrouch) {
				player.addTileTouchedOnTop();
				player.reactToTile(2, tile);
			}
		}
	}
	
	/**
	 * vérifie si le joueur est en collision avec un ou plusieurs joueurs
	 */
	public static void checkPlayerCollisionWithPlayer(int i, int j, Player player, Player otherPlayer) {
		if (j != i && player.getHitbox().overlaps(otherPlayer.getHitbox()) && otherPlayer.canPlay())  { // si le joueur est en collision avec un autre joueur
			if (player.isOutsideBorders()) { // dans le cas où la collision se fait quand il y a une téléportation d'un côté à l'autre de l'écran 
				if (player.getPHPos().x+player.getHitbox().width < 1) 
					otherPlayer.correctHitboxPosition(player.getHitbox().x-1, otherPlayer.getHitbox().y);
				else if (player.getPHPos().x > 1919) 
					otherPlayer.correctHitboxPosition(player.getHitbox().x+player.hitbox.width+1, otherPlayer.getHitbox().y);
				
				player.setOutsideBorders(false);
			} else // sinon c'est une simple collision entre deux joueurs
				player.collisionWithEntity(otherPlayer);
		}
	}
	
	/**
	 * vérifie si le joueur est en collision avec un projectile
	 */
	public static void checkCollisionWithProjectile(Projectile projectile, Player player) {
		if (player.getHitbox().overlaps(projectile.getHitbox())) {
			if (projectile.getId() != player.getId()) {
				if (projectile.doKill()) {
					player.die();
					players.get(projectile.getId()).addKill();
				}
			
				projectile.die();
			}
		}
	}
	
	/**
	 * met à jour les textures des blocs qui ont une texture qui dàpend de l'environnement
	 */
	public static void refreshTileTexture() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				if (tiles[x][y] != null && tiles[x][y].hasOrientedTexture())
					tiles[x][y].setCorrectTexture(getNeighbors(x, y));
			}
		}
	}
	
	/**
	 * détermine la position de spawn du joueur passé en paramètre
	 * @param player : le joueur à faire respawn
	 */
	public static void spawnPlayer(Player player) {
		Vector2 spawnCoord = new Vector2();
		int collisionCounter = 0;
		
		for (int i = Util.getRandomInteger(0, spawnList.size()-2); i < spawnList.size(); i++) {
			collisionCounter = 0;
			player.getHitbox().setPosition(spawnList.get(i));
			
			for (int j = 0; j < players.size(); j++) {
				if (j != player.getId() && player.getHitbox().overlaps(players.get(j).getHitbox())) 
					collisionCounter++;
			}
			
			if (collisionCounter == 0) {
				spawnCoord.set(spawnList.get(i));
				break;
			}
			
			if (i == spawnList.size()-1)
				i = 0;
		}
		
		player.respawn(spawnCoord);
	}
	
	/**
	 * met à jour l'indice du dernier spawn
	 */
	public static void newSpawn() {
		if (lastSpawn < spawnList.size()-1)
			lastSpawn++;
		else 
			lastSpawn = 0;
	}
	
	/**
	 * retourne un tableau contenant les identifiants des blocs adjacents au bloc de position (x, y)
	 * en cas d'absence de bloc l'id envoyà est -1
	 * l'ordre des blocs dans le tableau est le suivant : bloc du haut (àlàment 0), droite (àlàment 1), bas (àlàment 2), gauche (àlàment 3)
	 * @param x la position x du bloc (dans le tableau "tiles")
	 * @param y la position y du bloc (dans le tableau "tiles")
	 * @return un tableau contenant les identifiants blocs adjacents au bloc de position (x, y)
	 */
	public static int[] getNeighbors(int x, int y) {
		int[] neighbors = new int[4];
		
		if (y > 0 && tiles[x][y-1] != null)
			neighbors[0] = tiles[x][y-1].getId();
		else
			neighbors[0] = -1;
		
		if (x < WIDTH-1 && tiles[x+1][y] != null)
			neighbors[1] = tiles[x+1][y].getId();
		else
			neighbors[1] = -1;
		
		if (y < HEIGHT-1 && tiles[x][y+1] != null)
			neighbors[2] = tiles[x][y+1].getId();
		else
			neighbors[2] = -1;
			
		if (x > 0 && tiles[x-1][y] != null)
			neighbors[3] = tiles[x-1][y].getId();
		else
			neighbors[3] = -1;
		
		return neighbors;
	}
	
	/**
	 * @param x l'abscisse de l'emplacement à vérifier
	 * @param y l'ordonnée de l'emplacement à vérifier
	 * @return vrai si l'emplacement du bloc est libre
	 */
	public static boolean isTileFree(int x, int y) {
		return tiles[x][y] == null;
	}
	
	/**
	 * cette fonction n'est appelà qu'à la fin d'une partie, elle n'est pas utilisà pour afficher les stats dans le HUD pendant la partie
	 * @return un tableau contenant le nombre de personne tuà, le nombre de mort et le score pour chaque joueur
	 */
	public static int[][] getScore() {
		int[][] scores = new int[4][players.size()];
		Player player;
		
		for (int i = 0; i <players.size(); i++) {
			player = players.get(i);
			scores[0][player.getId()] = player.getKills();
			scores[1][player.getId()] = player.getDeaths();
			scores[2][player.getId()] = scores[0][player.getId()] - scores[1][player.getId()];
			scores[3][player.getId()] = player.getCharID();
		}
		
		return scores;
	}
	
	/**
	* ajoute un projectile
	 */
	public static void addProjectile(int id, float x, float y, int sx, int sy, int sw, int sh, float speedX, boolean direction, int duration, boolean kill) {
		Projectile projectile = projectilePool.obtain();
		projectile.initPhysics(id, x, y, sx, sy, sw, sh, speedX, direction);
		projectile.initCharacteristics(duration, kill);
		projectiles.add(projectile);
	}
	
	/**
	 * ajoute un effet de particule à liste des particules
	 */
	public static void addEffect(PooledEffect effect) {
		effects.add(effect);
	}
	
	public static void addTile(Tile tile, int x, int y) {
		tiles[x][y] = tile;
		tiles[x][y].setPosition(x*TILE_WIDTH, y*TILE_HEIGHT);
		
		if (tiles[x][y].hasOrientedTexture())
			tiles[x][y].setCorrectTexture(getNeighbors(x, y));
	}
	
	public static void addBackgroundTile(int id, int x, int y) {
		backgroundTiles[x][y] = id;
	}
	
	public static void deleteBackgroundTile(int x, int y) {
		backgroundTiles[x][y] = -1;
	}
	
	public static void deleteTile(int x, int y) {
		tiles[x][y] = null;
	}
	
	public static void setEditMode(boolean _editMode) {
		editMode = _editMode;
	}
	
	public static int getHudGap() {
		return hudGap;
	}

	public static boolean getEditMode() {
		return editMode;
	}
	
	public static Tile[][] getTiles() {
		return tiles;
	}
	
	public static int[][] getBackgroundTiles() {
		return backgroundTiles;
	}
	
	public static int getTileID(int x, int y) {
		if (tiles[x][y] != null)
			return tiles[x][y].getId();
		
		return -1;
	}
	
	public static ArrayList<Vector2> getSpawnList() {
		return spawnList;
	}

	public static ArrayList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * @return la liste de tous les textes à l'àcran
	 */
	public static ArrayList<Text> getTexts() {
		return texts;
	}
	
	/**
	 * @return la "piscine" de projectiles (voir la déclaration de l'object pour plus d'informations sur la "piscine")
	 */
	public static Pool<Projectile> getProjectilePool() {
		return projectilePool;
	}
}
