package net.ts.isn.world.tiles;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.world.Level;
import net.ts.isn.world.Player;

public class Trampoline extends Tile{

	public Trampoline(float x, float y, int version) {
		this.id = 3;
		this.pos = new Vector2(x, y);
		this.size = new Vector2(Level.TILE_WIDTH, Level.TILE_HEIGHT);
		
		this.hitbox = new Rectangle(this.pos.x, this.pos.y, this.size.x, this.size.y);
		this.hasCollision = true;
		this.hasOrientedTexture = false;
		this.responsive = true;
		this.version = version;

		if (this.version == 0) {
			this.sourceX = 0; 
			this.sourceY = 96; 
		} else {
			this.sourceX = 32; 
			this.sourceY = 96; 
		}
	}

	@Override
	public void reactToPlayer(Player player, int direction) {
		if (direction == 2 && player.getSpdY() < -1)
			player.bounce(direction, player.isKeyJump() ? 37 : 30);
	}

	public Tile getClone() {
		return new Trampoline(this.pos.x, this.pos.y, this.version);
	}
}
