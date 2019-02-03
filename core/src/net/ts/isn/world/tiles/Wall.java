package net.ts.isn.world.tiles;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.world.Level;

public class Wall extends Tile {
	public Wall(float x, float y, int version) {
		this.id = 0;
		this.pos = new Vector2(x, y);
		this.size = new Vector2(Level.TILE_WIDTH, Level.TILE_HEIGHT);
		this.hitbox = new Rectangle(this.pos.x, this.pos.y, this.size.x, this.size.y);
		this.hasCollision = true;
		this.hasOrientedTexture = false;
		this.version = version;
		
		if (this.version == 0) {
			this.sourceX = 96; 
			this.sourceY = 64; 
		} else {
			this.sourceX = 64; 
			this.sourceY = 0; 
		}
	}
	
	public Tile getClone() {
		return new Wall(this.pos.x, this.pos.y, this.version);
	}
}