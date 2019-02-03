package net.ts.isn.world.tiles;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.util.Util;
import net.ts.isn.world.Level;
import net.ts.isn.world.Player;

public class Warp extends Tile {
	public Warp(float x, float y) {
		this.id = 4;
		this.pos = new Vector2(x, y);
		this.size = new Vector2(Level.TILE_WIDTH, Level.TILE_HEIGHT);
		this.sourceX = 0; 
		this.sourceY = 96; 
		this.hitbox = new Rectangle(this.pos.x, this.pos.y, this.size.x, this.size.y);
		this.hasCollision = true;
		this.hasOrientedTexture = false;
		this.responsive = true;
	}

	public void reactToPlayer(Player player, int direction) {
//		ArrayList<Warp> warp; 
//		warp = new ArrayList<Warp>();
//		
//		if (direction == 2){
//			player.setIsOnWarp(true);
//			System.out.println("test");
//			for (int x = 0; x < Level.WIDTH; x++) {
//				for (int y = 0; y < Level.HEIGHT; y++) {
//					if (Level.getTiles()[x][y]!= null && Level.getTiles()[x][y].getId() == 4 && !Level.getTiles()[x][y].getPos().equals(this.pos)){
//						warp.add((Warp)Level.getTiles()[x][y]);
//						} 
//				}
//				}
//				int size = warp.size()-1;
//				int randomIndex = Util.getRandomInteger(0, size );
//				Warp warpOut = warp.get(randomIndex);
//				player.setPosWarp(warpOut.getPos());
//		}
	}
	
	public Tile getClone() {
		return new Warp(this.pos.x, this.pos.y);
	}
}