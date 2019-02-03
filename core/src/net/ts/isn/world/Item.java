package net.ts.isn.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import net.ts.isn.Resources;

public enum Item {
	NONE(-1, "No item", 0, 0, 0, 0, false),
	SPEEDBONUS(0, "Speed bonus", 24, 24, 0, 0, false),
	HADOKEN(1, "Hadoken", 24, 24, 1000, 3, true);
	
	public final String name; // le nom de l'item
	public final int id; // l'identifiant de l'item
	public final int width, height; // la largeur et la hauteur de l'item
	public final int delayDuration; // durée entre chaque utilisation de l'item
	public final int useCount; // nombre d'utilisation de l'item
	public final boolean pickable; // vrai si l'item est ramassable
	
	private float x; // l'abscisse de l'item
	private float y; // l'ordonnée de l'item
	private float originY; // l'ordonnée d'origine de l'item
	private float speedY; // la vitesse verticale de l'item (utilisé pour les "flottements")
	private Rectangle hitbox; // la zone de collision de l'item
	
	private Item(int id, String name, int width, int height, int duration, int useCount, boolean pickable) {
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.delayDuration = duration;
		this.useCount = useCount;
		this.pickable = pickable;
	}
	
	/**
	 * change les coordonnées de l'item (lorsqu'il est dans le niveau)
	 * @param x l'abscisse de l'item
	 * @param y l'ordonnée de l'item
	 * @return la nouvelle instance
	 */
	public Item setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		this.originY = this.y;
		this.speedY = 0.1f;
		this.hitbox = new Rectangle(this.x, this.y, 48, 48);
		return this;
	}
	
	/**
	 * affiche l'item à l'écran
	 * @param batch : voir RootScreen
	 */
	public void render(SpriteBatch batch) {
		batch.draw(Resources.items, this.x, this.y, 48, 48, 24*this.id, 0, this.width, this.height, false, false);
	}
	
	public void fluttering() {
		if (this.speedY > 0) {
			if (this.y >= this.originY + 5)
				this.speedY = -0.1f;
		} else if (this.speedY < 0) {
			if (this.y <= this.originY - 5)
				this.speedY = 0.1f;
		}

		this.move();
	}	
	
	public void move() {
		this.y += this.speedY;
	}
	
	public int getID() {
		return this.id;
	}
	
	public Rectangle getHitbox() {
		return this.hitbox;
	}
}
