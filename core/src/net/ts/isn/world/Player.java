package net.ts.isn.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.Config;
import net.ts.isn.Resources;
import net.ts.isn.screens.GameScreen;
import net.ts.isn.screens.RootScreen;
import net.ts.isn.screens.gui.Text;
import net.ts.isn.util.Animation;
import net.ts.isn.util.Timer;
import net.ts.isn.util.Xbox360Pad;
import net.ts.isn.world.tiles.Tile;

public class Player extends Entity {
	private int charID; // identifiant du personnage
	private int sourceWidth, sourceHeight; // la hauteur et la largeur de la texture du joueur dans le spritesheet
	private float px, py; // position du joueur à la frame précédente
	private float phx, phy; // position de la hitbox à la frame précédente
	private float speedX, speedY; // vélocité horizontale et verticale du joueur (en pixel par seconde)
	private float maxSpdX, maxSpdY; // vélocité horizontale et verticale maximales (en pixel par seconde)
	private float jumpHeight; // hauteur d'un saut
	private float acceleration, deceleration, gravityAcceleration; // vitesse à laquelle le joueur accélère et décélère
	private int direction; // direction du joueur (pour l'orientation horizontale du sprite) 0=droite  1=gauche
	private int kills; // nombre de personne que ce joueur a tué
	private int deaths; // nombre de fois que ce joueur est mort
	private int score; // différence entre kills et deaths
	private int tileTouchedOnTop; // nombre de fois que le joueur a touché le haut d'un bloc pendant une frame
	
	private boolean onGround; // vrai si le joueur touche le sol
	private boolean moving; // vrai si le joueur est en mouvement (si faux et que le joueur a une certaine vitesse, alors il dé¦—é§˜é‘½era)
	private boolean dead; // vrai si le joueur est mort
	private boolean invincible; // vrai si le joueur ne peut pas être tué
	private boolean respawned; // vrai si le joueur est réapparu en jeu
	private boolean hDash; // vrai si le joueur est en train de faire un dash horizontal
	private boolean vDash; // vrai si le joueur est en train de faire un dash vertical
	private boolean crouch; // vrai si le joueur est accroupie
	private boolean blockInput; // vrai si les touches du joueur doivent é»Žre bloqué¦¥s
	private boolean canAccelerate; // vrai si le joueur peut augmenter sa vitesse horizontale
	private boolean canJump; // vrai si le joueur saute
	private boolean affectedByGravity; // vrai si le joueur est affecté par la gravité
	private boolean outsideBorders; // vrai si le joueur est en dehors des bords du niveau
	private boolean canPlay; // vrai si le joueur peut jouer, si faux, le joueur n'est plus mis à jour ni rendu, cependant ses données sont conservés.
	
	private boolean keyJump;
	private boolean keyLeft;
	private boolean keyRight;
	private boolean keyCrouch;
	private boolean keyDash;
	private boolean keyUseItem;
	
	private Timer respawnInvicibilityTimer; // duré¦¥ de l'invincibilité du joueur lors du respawn
	private Timer notAffectedByGravityTimer; // duré¦¥ pendant laquelle le joueur n'est pas affectï¿½ par la gravitï¿½
	private Timer inputBlockedTimer; // duré¦¥ du blocage des entré¦¥s pé§»iphé§»iques
	private Timer dashDelay; // duré¦¥ entre les dashs
	private Timer itemUseDelay; // duré¦¥ entre chaque utilisation de l'item actuel
	private Timer physicsChangeDelay;
	
	private Item currentItem; // l'item possédé actuellement par le joueur
	private int currentItemUse; 

	private Animation run; 
	private Animation idle;
	private Animation crouching; 
	private Animation current;
	
	public Player(int id, int charID, Vector2 pos) {
		this.type = "Player";
		this.id = id;
		this.charID = charID;
		this.sourceWidth = Resources.playersSize[this.charID][0];
		this.sourceHeight = Resources.playersSize[this.charID][1];
		this.pos = new Vector2(pos.x, pos.y);
		this.size = new Vector2(this.sourceWidth*Config.SCALE, this.sourceHeight*Config.SCALE);
		this.px = this.pos.x;
		this.py = this.pos.y;
		this.phx = this.pos.x+Resources.playerOffsetX[0];
		this.phy = this.pos.y;
		this.hitbox = new Rectangle(this.pos.x+Resources.playerOffsetX[0], this.pos.y, this.size.x, this.size.y);
		
		this.respawnInvicibilityTimer = new Timer(1500);
		this.notAffectedByGravityTimer = new Timer(0);
		this.inputBlockedTimer = new Timer(0);
		this.dashDelay = new Timer(1500);
		this.itemUseDelay = new Timer(0);
		this.physicsChangeDelay = new Timer(0);

		this.setDefaultCharacteristics();
		this.canPlay = true;
		
		this.run = new Animation(0, 89+89*charID, 89, 64, 448, 100, Resources.charactersSheet);
		this.idle = new Animation(64*charID, 0, 89, 64, 64+64*charID, 100 , Resources.charactersSheet);
		this.crouching = new Animation(448, 89+89*charID, 89, 64, 512, 100, Resources.charactersSheet);
		this.current = this.idle;
	}
	
	public void update(float delta) {
		// on applique une "gravité" sur le joueur s'il y est soumis, sinon on vérifie pour encore combien de temps il n'y est pas soumis
		if (this.affectedByGravity)
			this.gravity();
		else if (this.notAffectedByGravityTimer.isComplete())
				this.affectedByGravity = true;
		
		// on vérifie si les changements temporaires des valeurs physiques sont terminées
		if (this.physicsChangeDelay.isComplete()) {
			this.setDefaultPhysicsValues();
			this.physicsChangeDelay.stop();
		}
		
		// récupération des entrées des périphériques (clavier, manettes)
		this.getInput();
		
		// traitement des entrées récupérés par getInput(), sauf si le traitement de celles-ci est bloqué
		if (!this.blockInput)
			this.processInput();
		else if (this.inputBlockedTimer.isComplete()) {
			this.inputBlockedTimer.stop();
			this.blockInput = false;
		}

		// mise à jour des dashs (si le joueur est en train d'en faire un ou non)
		this.dashUpdate();
		
		// décélération du joueur s'il n'appuie plus sur aucune touche de déplacement
		if (!this.moving)
			this.decelerate();
		
		
		// vérifie si le joueur est sur un "sol", et si oui, on annule sa vitesse verticale et on change la valeur du booléen "onGround" à vrai
		if (this.tileTouchedOnTop > 0 && this.speedY <= 0) {
			this.speedY = 0;
			this.onGround = true;
			this.tileTouchedOnTop = 0;
		} else if (this.tileTouchedOnTop == 0)
			this.onGround = false;
		else if (this.speedY > 0)
			this.tileTouchedOnTop = 0;
		
		// mise à jour de la position du joueur, et vé§»ification de sa position par rapport aux dimensions du niveau
		this.move(delta);
		this.checkPosition();
		
		// mise à jour des animations, on choisit la bonne animation à afficher et on la met à jour
		this.setCorrectAnimation();
		this.current.update();
	}
	
	/**
	 * affiche l'image du joueur à l'écran
	 * @param batch : voir RootScreen
	 */
	public void render(SpriteBatch batch) {
		this.current.render(batch, this.pos.x, this.pos.y, this.direction == 1);
		
		// affichage pour le débogage
//		if (id == 0) 
//			Resources.font.draw(batch, "acc: "+gravityAcceleration, 5, 100);
//			Resources.font.draw(batch, "hDash: "+hDash, 5, 150);
//			Resources.font.draw(batch, "vDash: "+vDash, 5, 200);
//			Resources.font.draw(batch, "speedY: "+speedY, 5, 250);
//		}
	}
	
	// met à jour la bonne animation en fonction de la situation
	public void setCorrectAnimation() {
		if (this.speedX != 0)
			this.current = this.run;
		else
			this.current = this.idle;
		
		if (this.crouch || this.vDash || this.hDash)
			this.current = this.crouching;
	}
	
	/**
	 * capte les entrées des périphériques
	 */
	public void getInput() {
		if (Config.getUseController(this.id)) {
			this.keyJump = Controllers.getControllers().get(this.id).getButton(Xbox360Pad.BUTTON_A);
			this.keyLeft = Controllers.getControllers().get(this.id).getAxis(Xbox360Pad.AXIS_LEFT_X) < -0.2f;
			this.keyRight = Controllers.getControllers().get(this.id).getAxis(Xbox360Pad.AXIS_LEFT_X) > 0.2f;
			this.keyCrouch = Controllers.getControllers().get(this.id).getAxis(Xbox360Pad.AXIS_LEFT_Y) > 0.2f;
			this.keyDash = Controllers.getControllers().get(this.id).getButton(Xbox360Pad.BUTTON_B);
			this.keyUseItem = Controllers.getControllers().get(this.id).getButton(Xbox360Pad.BUTTON_X);
		} else {
			this.keyJump = Gdx.input.isKeyPressed(Config.keys[this.id][0]);
			this.keyLeft = Gdx.input.isKeyPressed(Config.keys[this.id][1]);
			this.keyRight = Gdx.input.isKeyPressed(Config.keys[this.id][3]);
			this.keyCrouch = Gdx.input.isKeyPressed(Config.keys[this.id][2]);
			this.keyDash = Gdx.input.isKeyPressed(Config.keys[this.id][4]);
			this.keyUseItem = Gdx.input.isKeyPressed(Config.keys[this.id][5]);
		}
	}
	
	/**
	 * interprète les entrées des périphériques
	 */
	public void processInput() {
		// TODO revoir cette partie
		if (Gdx.input.isKeyJustPressed(Config.keys[this.id][1]) || Gdx.input.isKeyJustPressed(Config.keys[this.id][3]) && this.onGround) {
			PooledEffect effect = Resources.dustParticlePool.obtain();
			effect.setPosition(hitbox.x+(hitbox.width/2), hitbox.y);
			Level.addEffect(effect);
		} 
		
		// sauts
		if (this.keyJump && this.onGround && this.canJump) {
			this.speedY = this.jumpHeight;
			this.onGround = false;
			this.canJump = false;
			Resources.jump.play(Config.getSoundEffectVolume());
		} else if (!this.keyJump)
			this.canJump = true;
			
		// accroupissage
		this.crouch = this.keyCrouch;
		
		if (this.crouch && !this.onGround) {
			if (this.gravityAcceleration < 2.0f)
				this.gravityAcceleration += 0.05f;
			
			this.maxSpdY = 28f;
		} else {
			this.gravityAcceleration = 1f;
			this.maxSpdY = 24f;
		}
		
//		if (this.crouch && this.isOnWarp) {
//			correctHitboxPosition(posWarp.x, posWarp.y+64);
//			this.isOnWarp = false;
//			
//			for (int j = 0; j < Level.getPlayers().size(); j++) {
//				if (this.collisionWithEntity(Level.getPlayers().get(j))) {
//					//faire que l'autre joueur se téléporte IMPORTANT
//				}
//			}
//		}
		
		// accélération gauche/droite
		if (this.keyRight) {
			this.direction = 0;
					
			if (this.canAccelerate)
				this.accelerate();
		} else if (this.keyLeft) {
			this.direction = 1;
				
			if (this.canAccelerate)
				this.accelerate();
		} else if (this.keyRight && this.keyLeft)
			this.moving = false;
		else
			this.moving = false;
		
		// utilisation des items
		if (this.keyUseItem && (this.itemUseDelay.isComplete() || !this.itemUseDelay.isStarted())) {
			ItemBehavior.useItem(this, this.currentItem);
			this.itemUseDelay.start();
		}
		
		// dash
		if (this.keyDash && (!this.hDash && !this.vDash) && (this.dashDelay.isComplete() || !this.dashDelay.isStarted())) {
			if (this.keyCrouch)
				this.activateDash(false);
			else if (this.keyLeft || this.keyRight)
				this.activateDash(true);
		}
	}
	
	/**
	 * @return vrai si le joueur appuie sur au moins une de ses touche
	 */
	public boolean anyKeyPressed() {
		this.getInput(); // on appelle getInput() car la mé¨�hode anyKeyPressed peut é»Žre appelé¦¥ alors que le joueur est mort (et donc que update() ne soit pas appelï¿½)
		return (keyCrouch || keyDash || keyJump || keyLeft || keyRight || keyUseItem);
	}
	
	/**
	 * actualise l'abscisse et l'ordonnée du joueur en fonction de sa vitesse horizontale et de sa vitesse verticale
	 * les vitesses sont exprimées en pixel par image (le jeu tourne à 60 images par seconde, bien que ce nombre puisse varier en fonction de la puissance de la machine)
	 * @param delta : durée en milliseconde entre chaque frame (si 60fps -> delta = 0.016s)
	 */
	public void move(float delta) {
		if (this.speedX != 0) {
			this.px = this.pos.x;
			this.phx = this.hitbox.x;
			this.pos.x += this.speedX;
			this.hitbox.setPosition(this.pos.x+Resources.playerOffsetX[this.charID], this.pos.y);

			if (Config.getFartMode()) {
				PooledEffect effect = Resources.dustParticlePool.obtain();
				effect.setPosition(hitbox.x+(hitbox.width/2), hitbox.y+54);
				Level.addEffect(effect);
			}
		}
			
		if (this.speedY != 0) {
			this.py = this.pos.y;
			this.phy = this.hitbox.y;
			this.pos.y += this.speedY;
			this.hitbox.setPosition(this.pos.x+Resources.playerOffsetX[this.charID], this.pos.y);
		}
	}
	
	/**
	 * augmente progressivement la vitesse horizontale du joueur en fonction de sa direction
	 */
	public void accelerate() {
		if (!this.crouch || !this.onGround) {
			this.moving = true;
			
			if (this.direction == 0) {
				if (this.speedX + acceleration > this.maxSpdX)
					this.speedX = this.maxSpdX;
				else
					this.speedX += this.acceleration;
			} else {
				if (this.speedX - acceleration < -this.maxSpdX)
					this.speedX = -this.maxSpdX;
				else
					this.speedX -= this.acceleration;
			}
		}
	}
	
	/**
	 * réduit progressivement la vitesse horizontale du joueur
	 */
	public void decelerate() {
		if (this.speedX > 0) {
			if (this.speedX - this.deceleration < 0)
				this.speedX = 0;
			else
				this.speedX -= this.deceleration;
		} else if (this.speedX < 0) {
			if (this.speedX + this.deceleration > 0)
				this.speedX = 0;
			else
				this.speedX += this.deceleration;
		}
	}
	
	/**
	 * accélère verticalement le joueur tant qu'il ne touche pas le sol
	 */
	public void gravity() {
		if (!this.onGround) {
			if (this.speedY > -this.maxSpdY)
				this.speedY -= this.gravityAcceleration;
			if (this.speedY < -this.maxSpdY && !this.vDash)
				this.speedY = -this.maxSpdY;
			
			if (!this.hDash && !this.vDash) // décélération faible si le joueur est dans les airs
				this.deceleration = 0.0015f; 
		} else {
			if (this.speedY < 0)
				this.speedY = 0;
			
			this.deceleration = 0.5f; // décélération normale sur le sol
		}
	}
	
	/**
	 * le joueur va se déplacer très rapidement pendant une courte période, dans la direction vers laquelle il fait face ou vers le bas
	 * @param horizontal : vrai si le dash va se faire sur l'abscisse du joueur, si faux, le dash va se faire sur son ordonné¦¥ (et 
	 * seulement vers le bas)
	 */
	public void activateDash(boolean horizontal) {
		this.moving = false;
		this.canAccelerate = false;
		Resources.dash.play(Config.getSoundEffectVolume());
		
		if (horizontal) { // si le dash est horizontal
			if (this.direction == 0)
				this.speedX = 60;
			else
				this.speedX = -60;
			
			this.hDash = true;
			this.deceleration = 5;
		} else { // sinon, il est vertical
			if (this.keyCrouch)
				this.speedY = -120;
			//else
			//	this.speedY = 80;
			
			this.vDash = true;
			this.deceleration = 5;
		}
	}
	
	/**
	 * met à jour les paramètres du dash du joueur
	 */
	public void dashUpdate() {
		if ((this.hDash && this.speedX == 0) || (this.vDash && this.speedY >= -5)) {
			this.hDash = false;
			this.vDash = false;
			this.canAccelerate = true;
			this.dashDelay.start();
		} else if (this.hDash) {
			this.deceleration = 5;
			this.speedY = 0;
			PooledEffect effect = Resources.dustDashParticlePool.obtain();
			effect.setPosition(hitbox.x+(hitbox.width/2), hitbox.y+(hitbox.height/2));
			Level.addEffect(effect);
		} else if (this.vDash) {
			this.speedX = 0;
			this.deceleration = 5;
			PooledEffect effect = Resources.dustDashParticlePool.obtain();
			effect.setPosition(hitbox.x+(hitbox.width/2), hitbox.y+(hitbox.height/2));
			Level.addEffect(effect);
		}
	}
	
	/**
	 * vé§»ifie si le joueur est toujours dans l'é¦—ran : compare sa position avec les dimensions du niveau
	 * ensuite le té§˜é§±orte de l'autre cîŒ¤ï¿½ de l'é¦—ran si le joueur sort de l'é¦—arn et que le boolé¦¥n "loop" est vrai, sinon, le joueur meurt
	 */
	public void checkPosition() {
		// quand le joueur sort de l'é¦—ran (au bas de l'é¦—ran)
		if (this.pos.y+this.size.y < 0 && !this.dead) {
			if (!Config.getLoop())
				this.die();
			else
				this.correctHitboxPosition(this.pos.x, RootScreen.getScreenHeight());
		}
		
		// vérifie si le joueur est toujours dans les bords du niveau (le côté gauche et droit)
		if (this.hitbox.x+this.hitbox.width < 1 ) {
			this.px = this.pos.x;
			this.phx = this.pos.x+Resources.playerOffsetX[this.charID];
			this.correctHitboxPosition(1919, this.phy);
			this.outsideBorders = true;
		} else if (this.hitbox.x > 1919) {
			this.px = this.pos.x;
			this.phx = this.pos.x+Resources.playerOffsetX[this.charID];
			this.correctHitboxPosition(1-this.hitbox.width, this.phy);
			this.outsideBorders = true;
		}
	}
	
	/**
	 * gère les collisions entre le joueur et une autre entité (voir la classe abstraite Entity)
	 * cette fonction détecte de quelle côté la collision se passe et corrige la position du joueur pour ne pas qu'il entre ï¿½ l'inté§»ieur de l'entitï¿½
	 * @param ent : l'entité en collision avec le joueur
	 * @return vrai si le joueur est en collision avec un joueur (seulement ce type d'entité)
	 */
	public boolean collisionWithEntity(Entity ent) {
		int direction = 0;
		
		if ((this.phx+this.hitbox.width) <= ent.getHitbox().x && this.hitbox.x+this.hitbox.width > ent.getHitbox().x && !ent.getNoBottomCollision()) { // le joueur est en collision avec la gauche de l'entitï¿½
			this.correctHitboxPosition(ent.getHitbox().x-this.hitbox.width, this.hitbox.y);
			direction = 0;
		} else if (this.phx >= (ent.getHitbox().x+ent.getHitbox().width) && this.hitbox.x < ent.getHitbox().x+ent.getHitbox().width && !ent.getNoBottomCollision()) { // le joueur est en collision avec la droite de l'entitï¿½
			this.correctHitboxPosition(ent.getHitbox().x+ent.getHitbox().width, this.hitbox.y);
			direction = 1;
		}
		
		if (this.phy >= ent.getHitbox().y+ent.getHitbox().height && this.hitbox.y < ent.getHitbox().y+ent.getHitbox().height && !ent.letPassIfCrouch(this.crouch)) {
				//&& ((this.pos.x+14 >= entPos.x && this.pos.x+14 <= entPos.x+entSize.x) || ((this.pos.x+62 >= entPos.x && this.pos.x+62 <= entPos.x+entSize.x)))) { // le joueur est en collision avec le haut de l'entitï¿½
			this.correctHitboxPosition(this.hitbox.x, ent.getHitbox().y+ent.getHitbox().height);
			direction = 2;
		} else if ((this.phy+this.hitbox.height <= ent.getHitbox().y && this.hitbox.y+this.hitbox.height > ent.getHitbox().y) && !ent.getNoBottomCollision()) { // le joueur est en collision avec le bas de l'entitï¿½
			this.correctHitboxPosition(this.hitbox.x, ent.getHitbox().y-this.getHitbox().height);
			direction = 3;
		}
		
		if (ent instanceof Tile)
			this.reactToTile(direction, (Tile)ent);
		else if (ent instanceof Player) {
			this.reactToPlayer(direction, (Player)ent);
			return true;
		}
		
		return false;
	}
	
	/**
	 * gère les ré¥Œctions des collisions entre le joueur et un bloc
	 * @param direction : direction dans laquelle la collision se passe (0->gauche du bloc  1->droite du bloc  2->haut du bloc  3->bas du bloc)
	 * @param tileID : l'identifiant du bloc
	 */
	public void reactToTile(int direction, Tile tile) { 
		switch (direction) {
			case 0: 
				if (!tile.noBottomCollision)
					this.speedX = 0;
				break;
			case 1: 
				if (!tile.noBottomCollision)
					this.speedX = 0;
				break;
			case 2: 
				if (tile.isResponsive())
					tile.reactToPlayer(this, 2);
				
				this.addTileTouchedOnTop();
				break;
			case 3: 
				this.speedY = 0;
				break;
		}
	}
	
	/**
	 * gé‘½e les ré¥Œctions des collisions entre deux joueurs
	 * @param direction : direction dans laquelle la collision se passe (0->gauche du bloc  1->droite du bloc  2->haut du bloc  3->bas du bloc)
	 * @param player : le joueur avec en collision avec cette instance de classe
	 */
	public void reactToPlayer(int direction, Player player) {
		float playerBounceIntensity = Math.abs(player.getSpdX()) + 2;  // intensité du rebond de cette instance de classe
		float otherPlayerBounceIntensity = Math.abs(this.speedX) + 2; // intensité du rebond du joueur avec lequel le joueur actuel est en collision
		
		if (otherPlayerBounceIntensity > 15) { // si la vitesse du joueur est assez élevée (en cas de dash), on fait sauter l'autre joueur plus haut et plus loin
			player.bounce(2, 25);
			player.hit(500);
			otherPlayerBounceIntensity = 25;
		}
		
		if (playerBounceIntensity == 2) // si la vitesse de l'autre joueur est nulle
			playerBounceIntensity = 4.5f;
		
		switch (direction) {
			case 0:
				this.bounce(1, playerBounceIntensity);
				player.bounce(0, otherPlayerBounceIntensity);
				
				if (otherPlayerBounceIntensity == 11)
					player.bounce(2, 8);
				break;
			case 1:
				this.bounce(0, playerBounceIntensity);
				player.bounce(1, otherPlayerBounceIntensity);
				
				if (otherPlayerBounceIntensity == 11)
					player.bounce(2, 8);
				break;
			case 2: 
				if (!player.isDead()) {
					this.addKill();
					player.die();
				}
					
				this.bounce(2, 12);
				break;
			case 3:
				this.speedY = 0;
				
				if (player.getSpdY() >= 0)
					player.bounce(2, this.jumpHeight);
				
				break;
		}
	}
	
	/**
	 * intialise la période pendant laquelle le joueur ne va pas être affecté par la gravité
	 */
	public void notAffectedByGravity(int duration) {
		this.speedY = 0;
		this.affectedByGravity = false;
		this.notAffectedByGravityTimer.start(duration);
	}
	
	/**
	 * bloque les entré¦¥s claviers du joueur pendant x millisecondes et change son sprite
	 * @param duration : duré¦¥ de l'é¨�at "touchï¿½" en milliseconde
	 */
	public void hit(int duration) {
		if (!this.blockInput) {
			this.blockInput = true;
			this.inputBlockedTimer.start(duration);
		}
	}
	
	/**
	 * fait "rebondir" le joueur dans la direction et avec l'intensitï¿½ donné¦¥s en paramé‘¼re
	 * @param direction : direction du rebondissement
	 * @param bounceSpeed : la vitesse du rebondissement
	 */
	public void bounce(int direction, float bounceSpeed) {
		if (bounceSpeed != 0)
			this.moving = false;
		
		switch (direction) {
			case 0: 
				this.speedX = bounceSpeed;
				break;
			case 1: 
				this.speedX = -bounceSpeed;
				break;
			case 2: 
				this.speedY = bounceSpeed;
				this.onGround = false;
				break;
			case 3: 
				this.speedY = -bounceSpeed;
				break;
		}
	}
	
	/**
	 * fait "rebondir" le joueur dans la direction donné¦¥ en paramé‘¼re et avec une intensitï¿½ é¦®ale ï¿½ la vitesse maximale horizontale
	 * @param direction : direction du rebondissement
	 */
	public void bounce(int direction) {
		this.bounce(direction, this.maxSpdX);
	}
 	
	/**
	 * corrige la position du joueur et de la hitbox, le joueur n'est pas dé§±lacï¿½ "fluidement" mais est "té§˜é§±ortï¿½"
	 * fonction utilisï¿½ dans pour les collisions mais pas pour les dé§±lacements basiques (cf. la fonction move())
	 * @param x : la nouvelle coordonné¦¥ x du joueur
	 * @param y : la nouvelle coordonné¦¥ y du joueur
	 */
	public void correctHitboxPosition(float x, float y) {
		this.hitbox.setPosition(x, y);
		this.pos.x = this.hitbox.x-Resources.playerOffsetX[this.charID];
		this.pos.y = this.hitbox.y;
	}
	
	/**
	 * ré§Ÿnitialise les boolé¦¥ns dé¦­inissant les caracté§»istiques du joueur 
	 */
	public void setDefaultCharacteristics() {
		// paramètres physiques et autres valeurs numé§»iques
		this.speedX = this.speedY = 0;
		this.setDefaultPhysicsValues();
		this.direction = 0;
		this.tileTouchedOnTop = 0;
		
		// booléen "d'état"
		this.canJump = true;
		this.affectedByGravity = true;
		this.blockInput = false;
		this.moving = false;
		this.respawned = true;
		this.blockInput = false;
		this.hDash = false;
		this.vDash = false;
		this.crouch = false;
		this.canAccelerate = true;
		this.outsideBorders = false;
		
		this.currentItem = Item.NONE;
	}
	
	public void setDefaultPhysicsValues() {
		this.maxSpdX = 9;
		this.maxSpdY = 24;
		this.jumpHeight = 23;
		this.acceleration = 1f;
		this.deceleration = 0.5f;
		this.gravityAcceleration = 1f;
	}
	
	/**
	 * fait "mourir" le joueur : change ses booléens d'état et ses caractéristiques et met à jour les scores
	 * cette fonction ne fait rien si le joueur est dans l'état invincible
	 */
	public void die() {
		if (!this.invincible) {
			this.dead = true;
			this.speedX = 0;
			this.speedY = 0;
			
			this.setDefaultCharacteristics();
			this.current = this.idle;

			this.respawned = false;
			this.deaths++;
			this.refreshScore();
			
			Resources.death.play(Config.getSoundEffectVolume());
			GameScreen.playShader(4, 400); // lancement du shader "vibration de l'é¦—ran" pour 200ms
		}
	}
	
	/**
	 * rafraîchit le score du joueur
	 * on appelle cette fonction seulement lors d'un changement de la variable "kills" ou "deaths"
	 */
	public void refreshScore() {
		this.score = this.kills - this.deaths;
		Level.getTexts().get(this.id).setText("Joueur "+(this.id+1)+System.lineSeparator()+"Score : "+this.score);
	}
	
	/**
	 * fait réapparaitre le joueur, après sa mort à une nouvelle position et le rend invincible et immobile pendant 1.5 secondes
	 * @param pos : la nouvelle position du joueur lors de sa réapparition
	 */
	public void respawn(Vector2 pos) {
		this.px = pos.x;
		this.py = pos.y;
		this.phx = pos.x+Resources.playerOffsetX[this.charID];
		this.phy = pos.y;
		this.correctHitboxPosition(pos.x, pos.y);
		this.respawned = true;
		this.respawnInvicibilityTimer.start();
	}
	
	/**
	 * incrémente le nombre de personne que ce joueur a tué et le fait rebondir
	 */
	public void addKill() {
		this.kills++;
		this.refreshScore();
		Level.getTexts().add(new Text("+1", this.pos.x+(this.size.x/2), this.pos.y+this.size.y+20, true, 700, new Color(190, 0, 0, 255), Resources.font));
	}
	
	/**
	 * ajoute une utilisation à l'item actuel
	 */
	public void addUse() {
		this.currentItemUse++;
		
		if (this.currentItemUse == this.currentItem.useCount)
			this.setItem(Item.NONE);
	}

	/**
	 * incrémente le nombre de fois que le joueur ï¿½ touchï¿½ le haut d'un bloc pendant une frame
	 */
	public void addTileTouchedOnTop() {
		this.tileTouchedOnTop++;
	}
	
	/**
	 *  accesseurs et mutateurs
	 */
	
	public void setSpdX(float spdX) {
		this.speedX = spdX;
	}
	
	public void setSpdY(float spdY) {
		this.speedY = spdY;
	}
	
	public void setMaxSpeedX(float maxSpeedX) {
		this.maxSpdX = maxSpeedX;
		this.physicsChangeDelay.start(5000);
	}
	
	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public void setOutsideBorders(boolean outsideBorders) {
		this.outsideBorders = outsideBorders;
	}
	
	public void setItem(Item item) {
		this.currentItem = item;
		this.itemUseDelay.setDuration(item.delayDuration);
		this.currentItemUse = 0;
	}
	
	public void setCanPlay(boolean canPlay) {
		this.canPlay = canPlay;
	}
	
	public Vector2 getPPos() {
		return new Vector2(this.px, this.py);
	}
	
	public Vector2 getPHPos() {
		return new Vector2(this.phx, this.phy);
	}
	
	public int getCharID() {
		return charID;
	}

	public float getPx() {
		return px;
	}

	public float getPy() {
		return py;
	}

	public float getSpdX() {
		return speedX;
	}

	public float getSpdY() {
		return speedY;
	}
	
	public int getDirection() {
		return this.direction;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public boolean canJump() {
		return canJump;
	}

	public boolean isMoving() {
		return moving;
	}

	public boolean isDead() {
		return dead;
	}
	
	public boolean isRespawned() {
		return respawned;
	}
	
	public boolean isInvicible() {
		return invincible;
	}
	
	public boolean isCrouch() {
		return this.crouch;
	}

	public boolean isKeyJump() {
		return this.keyJump;
	}
	
	public boolean canPlay() {
		return this.canPlay;
	}
	
	public boolean isRespawnInvicibilityTimerComplete() {
		return this.respawnInvicibilityTimer.isComplete();
	}
	
	public int getKills() {
		return this.kills;
	}
	
	public int getDeaths() {
		return this.deaths;
	}

	public int getScore() {
		return this.score;
	}
	
	public boolean isOutsideBorders() {
		return this.outsideBorders;
	}
	
	public int getCurrentItemUse() {
		return this.currentItemUse;
	}
	
	public Item getCurrentItem() {
		return this.currentItem;
	}
}