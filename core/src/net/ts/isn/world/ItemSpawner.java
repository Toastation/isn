package net.ts.isn.world;

import net.ts.isn.world.tiles.Tile;

public class ItemSpawner extends Tile {
	public ItemSpawner() {
		
	}

	public Tile getClone() {
		return new ItemSpawner();
	}
}
