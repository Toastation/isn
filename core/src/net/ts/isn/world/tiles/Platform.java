package net.ts.isn.world.tiles;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.world.Level;

public class Platform extends Tile {
	public Platform(float x, float y) {
		this.id = 2;
		this.pos = new Vector2(x, y);
		this.size = new Vector2(Level.TILE_WIDTH, 28);
		this.sourceXOrigin = this.sourceX = 160; // 96
		this.sourceYOrigin = this.sourceY = 64; // 64
		this.hitbox = new Rectangle(this.pos.x, this.pos.y, this.size.x, this.size.y);
		this.hasCollision = true;
		this.hasOrientedTexture = true;
		this.noBottomCollision = true;
		this.letPassIfCrouch = true;
	}
	
	public void setCorrectTexture(int[] neighbors) {
		if (neighbors[1] < 0) 
			this.sourceX = this.sourceXOrigin + 32;
		else if (neighbors[3] < 0)
			this.sourceX = this.sourceXOrigin - 32;
		else 
			this.sourceX = this.sourceXOrigin;
	}
	
	public Tile getClone() {
		return new Platform(this.pos.x, this.pos.y);
	}
}
