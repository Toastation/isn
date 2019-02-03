package net.ts.isn.world.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.ts.isn.Resources;
import net.ts.isn.world.Entity;
import net.ts.isn.world.Level;
import net.ts.isn.world.Player;

public abstract class Tile extends Entity {
	protected int sourceX, sourceY, sourceXOrigin, sourceYOrigin; // les coordonn�es de la texure dans le spritesheet
	protected boolean hasOrientedTexture; // vrai si le bloc va avoir diff�rentes apparence en fonction des blocs autour de lui
	protected boolean responsive; // vrai si le bloc va avoir une int�raction sp�ciale avec le joueur
	protected boolean toUpdate; // vrai si le bloc doit �tre mis � jour � chaque frame
	protected int version;
	
	public void update(float delta) {
		
	}
	
	public void render(SpriteBatch batch) {
		batch.draw(Resources.tiles, this.pos.x, this.pos.y, Level.TILE_WIDTH, Level.TILE_HEIGHT, this.sourceX, this.sourceY, 32, 32, false, false);
	}

	public void syncHitbox() {
		this.hitbox.setPosition(this.pos);
	}
	
	public void reactToPlayer(Player player, int direction) {
	}
	
	/**
	 * v�rifie si le bloc est entour� d'autre bloc, si c'est le cas il est inutile de le mettre � jour (pour les collisions, etc)
	 * @param neighbors : un tableau contenant les identifiants des blocs voisins
	 */
	public void checkNeedToUpdate(int[] neighbors) {
		int numberOfNeighbors = 0;
		
		for (int i = 0; i < 4; i++) {
			if (neighbors[i] != -1)
				numberOfNeighbors++;
		}
		
		this.toUpdate = numberOfNeighbors < 4;
	}
	
	public void setCorrectTexture(int[] neighbors) {
		int neighborsNbr = 0;
		
		for (int i = 0; i < 4; i++) {
			if (neighbors[i] != -1)
				neighborsNbr++;
		}
		
		switch (neighborsNbr) {
			case 1:
				if (neighbors[0] >= 0) {
					this.sourceX -= 32;
					this.sourceY -= 32;
				} else if (neighbors[2] >= 0) {
					this.sourceX -= 32;
				} else if (neighbors[1] >= 0) {
					this.sourceY -= 32;
				} else if (neighbors[3] >= 0) {
					this.sourceX += 32;
					this.sourceY -= 32;
				}
				break;
			case 2:
				if (neighbors[3] >= 0 && neighbors[2] >= 0) {
					this.sourceX -= 64;
				} else if (neighbors[0] >= 0 && neighbors[3] >= 0) {
					this.sourceX -= 64;
					this.sourceY -= 32;
				} else if (neighbors[0] >= 0 && neighbors[1] >= 0) {
					this.sourceX -= 96;
					this.sourceY -= 32;
				} else if (neighbors[2] >= 0 && neighbors[1] >= 0) {
					this.sourceX -= 96;
				}
				break;
		}
	}
	
	public void setPosition(int x, int y) {
		this.pos.x = x;
		this.pos.y = y;
		this.hitbox.setPosition(this.pos);
	}

	public void setToUpdate(boolean toUpdate) {
		this.toUpdate = toUpdate;
	}
	
	public boolean toUpdate() {
		return this.toUpdate;
	}
	
	public boolean hasOrientedTexture() {
		return this.hasOrientedTexture;
	}
	
	public boolean isResponsive() {
		return this.responsive;
	}
	
	public int getSourceX() {
		return sourceX;
	}

	public int getSourceY() {
		return sourceY;
	}
	
	public int getVersion() {
		return this.getVersion();
	}
	
	public abstract Tile getClone();
}
