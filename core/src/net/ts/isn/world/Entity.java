package net.ts.isn.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	protected int id;
	protected String type;
	protected Vector2 pos; // position de l'entit�
	protected Vector2 size; // largeur et hateur de l'entit�
	protected Rectangle hitbox; // zone de collision de l'entit�
	protected boolean hasCollision; // vrai si l'entit� ne peut pas �tre travers�
	protected boolean noBottomCollision; // vrai si l'entit� peut �tre travers�e par le bas
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
