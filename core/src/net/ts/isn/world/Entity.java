package net.ts.isn.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	protected int id;
	protected String type;
	protected Vector2 pos; // position de l'entité
	protected Vector2 size; // largeur et hateur de l'entité
	protected Rectangle hitbox; // zone de collision de l'entité
	protected boolean hasCollision; // vrai si l'entité ne peut pas être traversé
	protected boolean noBottomCollision; // vrai si l'entité peut être traversée par le bas
	protected boolean letPassIfCrouch; // vrai si le bloc laisse passer les joueurs quand ils s'accroupissent
	
	public boolean letPassIfCrouch(boolean crouch) {
		return this.letPassIfCrouch && crouch;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Vector2 getPos() {
		return this.pos;
	}
	
	public Vector2 getSize() {
		return this.size;
	}
	
	public Rectangle getHitbox() {
		return this.hitbox;
	}
	
	public boolean hasCollision() {
		return this.hasCollision;
	}
	
	public boolean getNoBottomCollision() {
		return this.noBottomCollision;
	}
}
