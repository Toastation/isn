package net.ts.isn.screens.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.Config;
import net.ts.isn.Resources;
import net.ts.isn.screens.RootScreen;

public class Button {
	private String text; // le texte qu'affiche le bouton (inutile si le bouton affiche une image)
	private Vector2 pos; // la position du bouton
	private Vector2 size; // la taille du bouton (largeur et hauteur)
	private GlyphLayout layout; // le texte et sa géométrie (relie la chaîne de caractère avec la police)
	private Sprite sprite;
	private BitmapFont font;
	
	private boolean isAnImage; // vrai si le bouton utilise une image, faux si le bouton affiche seulement une chaîne de caractère
	private boolean selected; // vrai est "activé", ne concerne que certains boutons
	private boolean canClick;
	
	/**
	 * constructeur du bouton dans le cas où le texte n'affiche que du texte
	 * @param text : le texte
	 * @param x : l'abscisse du bouton
	 * @param y : l'ordonnée du bouton
	 */
	public Button(String text, int x, int y) {
		this(text, x, y, false);
	}
	
	/**
	 * constructeur du bouton dans le cas où le texte n'affiche que du texte
	 * @param text : le texte
	 * @param x : l'abscisse du bouton
	 * @param y : l'ordonnée du bouton
	 * @param centeredHorizontally : vrai si le texte doit être centré par rapport à l'abscisse x rentré en paramètre
	 */
	public Button(String text, int x, int y, boolean centeredHorizontally) {
		this.text = text;
		this.pos = new Vector2(x, y);
		this.layout = new GlyphLayout(Resources.buttonFont, this.text);
		this.size = new Vector2(layout.width, layout.height);
		this.isAnImage = false;
		this.canClick = true;
		this.font = Resources.buttonFont;
		
		if (centeredHorizontally)
			this.pos.x -= this.layout.width / 2;
	}
	
	/**
	 * constructeur du bouton dans le cas où une image est affiché
	 * @param x : l'abscisse du bouton
	 * @param y : l'ordonnée du bouton
	 * @param sourceX : l'abscisse de l'image de l'image la texture source
	 * @param sourceY : l'ordonnée de l'image de l'image la texture source
	 * @param width : la largeur du bouton
	 * @param height : la hauteur du bouton
	 */
	public Button(int x, int y, int sourceX, int sourceY, int width, int height) {
		this.pos = new Vector2(x, y);
		this.size = new Vector2(width, height);
		this.isAnImage = true;
		this.sprite = new Sprite(new TextureRegion(Resources.thumbnails, sourceX, sourceY, width, height));
		this.sprite.setPosition(this.pos.x, this.pos.y);
		this.canClick = true;
		this.font = Resources.buttonFont;
	}
	
	/**
	 * constructeur du bouton dans le cas où une image est affiché
	 * @param x : l'abscisse du bouton
	 * @param y : l'ordonnée du bouton
	 * @param tex : la texture du bouton
	 */
	public Button(float x, float y, TextureRegion tex) {
		this.pos = new Vector2(x, y);
		this.size = new Vector2(tex.getRegionWidth(), tex.getRegionHeight());
		this.isAnImage = true;
		this.sprite = new Sprite(tex);
		this.sprite.setPosition(this.pos.x, this.pos.y);
		this.canClick = true;
		this.font = Resources.buttonFont;
	}
	
	/**
	 * affiche le bouton (soit le texte, soit l'image)
	 * @param batch : le SpriteBatch (voir RootScreen)
	 */
	public void render(SpriteBatch batch) {
		if (this.isHovered()) {
			if (!this.isAnImage)
				this.layout.setText(this.font, this.text, new Color(0.15f, 0.15f, 0.15f, 1f), this.layout.width, 0, false);
		} else {
			if (!this.isAnImage)
				this.layout.setText(this.font, this.text, Color.BLACK, this.layout.width, 0, false);
		}
		
		if (this.selected) {
			if (this.isAnImage) {
			}
		}
		
		if (!this.isAnImage)
			this.font.draw(batch, this.layout, this.pos.x, this.pos.y + this.layout.height / 3 + 3);
		else 
			this.sprite.draw(batch, this.isHovered() ? 0.7f : 1f);
	}

	/**
	 * vérifie si la souris est "au dessus" du bouton 
	 * @return vrai si les coordonnées de la souris sont comprises dans le rectangle représentant les limites du texte
	 */
	public boolean isHovered() {
		float mouseX = (Gdx.input.getX()*1920)/RootScreen.getScreenWidth();
		float mouseY = (Gdx.input.getY()*1080)/RootScreen.getScreenHeight();
		
		if (this.isAnImage)
			return (mouseX >= this.pos.x && mouseX <= this.pos.x + this.size.x) && ((1080-mouseY) >= this.pos.y && 1080-mouseY <= this.pos.y + this.size.y);
		else
			return (mouseX >= this.pos.x && mouseX <= this.pos.x + this.size.x) && ((1080-mouseY) <= this.pos.y && 1080-mouseY >= this.pos.y - this.size.y);
	}
	
	/**
	 * vérifie si la souris est "au dessus" du bouton et que le bouton gauche de la souris est pressé
	 * @return si la souris est "au dessus" du bouton et que le bouton gauche de la souris est pressé
	 */
	public boolean isClicked() {
		if (Gdx.input.isButtonPressed(Buttons.LEFT) && this.isHovered() && !this.canClick) {
			this.canClick = true;
			Resources.selectionClick.play(Config.getSoundEffectVolume());
			return true;
		} else if (!Gdx.input.isButtonPressed(Buttons.LEFT) && this.isHovered() && this.canClick) {
			this.canClick = false;
			return false;
		}
		
		return false;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void setPosX(float x) {
		this.pos.set(x, this.pos.y);
		this.sprite.setPosition(this.pos.x, this.pos.y);
	}
	
	public void setFont(BitmapFont font) {
		this.font = font;
		this.layout.setText(this.font, this.text);
	}
	
	public void centerRelativeToScreen() {
		this.centerRelativeToCoord(960);
	}
	
	public void centerRelativeToCoord(int x) {
		this.pos.x = x-layout.width/2;
	}
	
	public Vector2 getPos() {
		return pos;
	}

	public Vector2 getSize() {
		return size;
	}
	
	public boolean isSelected() {
		return this.selected;
	}
}