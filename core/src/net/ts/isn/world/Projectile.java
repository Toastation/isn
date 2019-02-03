package net.ts.isn.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.ts.isn.Resources;
import net.ts.isn.util.Timer;

public class Projectile extends Entity implements Poolable {
	private int sourceX; // abscisse de la texture du projectile dans la "texture mère"
	private int sourceY; // ordonnée de la texture du projectile dans la "texture mère"
	private int sourceWidth; // largeur de la texture du projectile dans la "texture mère"
	private int sourceHeight; // hauteur de la texture du projectile dans la "texture mère"
	private float speedX; // la vitesse horizontale du projectile
	private boolean direction; // la direction du proejectile    false: gauche  true: droite
	private boolean alive;
	private boolean kill;

	private Timer duration;
	
	public Projectile() {
		this.type = "Projectile";
		this.pos = new Vector2();
		this.size = new Vector2();
		this.hitbox = new Rectangle();
		this.duration = new Timer(0);
	}
	
	/**
	 * initialise les paramètres physiques du projectile, doit être appelé après avoir obtenu le projectile de la Pool
	 */
	public void initPhysics(int id, float x, float y, int sx, int sy, int sw, int sh, float spd, boolean dir) {
		this.id = id;
		this.pos.set(x, y);
		this.size.set(sw*3, sh*3);
		this.hitbox.set(this.pos.x, this.pos.y, this.size.x, this.size.y);
		this.sourceX = sx;
		this.sourceY = sy;
		this.sourceWidth = sw;
		this.sourceHeight = sh;
		this.speedX = spd;
		this.direction = dir;
		this.alive = true;
	}
	
	/**
	 * initialise les paramètres physiques du projectile, doit être appelé après avoir obtenu le projectile de la Pool
	 */
	public void initCharacteristics(int duration, boolean kill) {
		this.duration.setDuration(duration);
		this.duration.start();
		this.kill = kill;
	}
	
	@Override
	public void reset() {
		this.pos.set(0, 0);
		this.size.set(0, 0);
		this.hitbox.set(0, 0, 0, 0);
		this.sourceX = 0;
		this.sourceY = 0;
		this.sourceWidth = 0;
		this.sourceHeight = 0;
		this.speedX = 0;
		this.direction = false;
		this.alive = false;
		this.kill = false;
		this.duration.setDuration(0);
	}
	
	/**
	 * met à jour le projectile
	 */
	public void update() {
		if (this.duration.isComplete() && this.duration.getDuration() != 0)
			this.alive = false;
		
		// déplacement du projectile
		if (this.direction) 
			this.pos.x += this.speedX;
		else
			this.pos.x -= this.speedX;
		
		// vérification que le projectile ne sort pas de l'écran
		if (this.pos.x+this.size.x < 0)
			this.pos.x = 1920;
		else if (this.pos.x > 1920)
			this.pos.x = -this.size.x;
		
		this.hitbox.setPosition(this.pos);
	}
	
	/**
	 * affiche le projectile à l'écran
	 * @param batch : voir RootScreen
	 */
	public void render(SpriteBatch batch) {
		batch.draw(Resources.projectiles, this.pos.x, this.pos.y, this.size.x, this.size.y, this.sourceX, this.sourceY, this.sourceWidth, this.sourceHeight, this.direction, false);
	}
	
	public void die() {
		this.alive = false;
	}
	
	public boolean isAlive() {
		return this.alive;
	}
	
	public boolean doKill() {
		return this.kill;
	}
}
