package net.ts.isn.world.tiles;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.world.Level;
import net.ts.isn.world.Player;

public class Spikes extends Tile {

	public Spikes(float x, float y) {
		this.id = 1;
		this.pos = new Vector2(x, y);
		this.size = new Vector2(Level.TILE_WIDTH, 50);
		this.sourceX = 0;
		this.sourceY = 0;
		this.hitbox = new Rectangle(this.pos.x, this.pos.y, this.size.x, this.size.y);
		this.toUpdate = true;
		this.hasCollision = true;
		this.responsive = true;
	}
	
	public void reactToPlayer(Player player, int direction) {
		if (direction == 2) 
			player.die();
	}
	
	public Tile getClone() {
		return new Spikes(this.pos.x, this.pos.y);
	}
}
