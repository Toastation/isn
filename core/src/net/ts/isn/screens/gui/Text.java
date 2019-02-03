package net.ts.isn.screens.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.Resources;
import net.ts.isn.util.Timer;
import net.ts.isn.util.Util;

public class Text {
	private String text; 
	private Vector2 pos;
	private Color color;
	private float spdX, spdY, offsetY, amplitude, sin;
	private boolean mustBeCleared; // vrai si le texte doit être supprimé 
	private boolean infinite;
	private Timer duration;
	private BitmapFont font;
	private GlyphLayout layout;
	
	// les booléens suivant décrivent les différents effets visuels que peuvent avoir le texte
	private boolean fluttering; // vrai si le texte va "flotter" (oscillation de la position y du texte)
	private boolean gravity; // vrai si le texte va monter comme un ballon
	
	public Text(String text, float x, float y, boolean gravity, int duration, Color color, BitmapFont font) {
		this.text = text;
		this.pos = new Vector2(x, y);
		this.gravity = gravity;
		this.offsetY = 0;
		this.sin = -0x800;
		
		this.duration = new Timer(duration);
		this.duration.start();
		
		this.spdY = Util.getRandomInteger(300, 400);
		this.spdX = Util.getRandomInteger(-200, 200);	
		
		this.color = color;
		this.font = font;
		this.layout = new GlyphLayout(this.font, this.text);
	}
	
	public Text(String text, float x, float y, boolean fluttering, int amplitude) {
		this.text = text;
		this.pos = new Vector2(x, y);
		this.fluttering = fluttering;
		this.offsetY = 0;
		this.sin = -0x800;
		this.infinite = true;
		this.spdY = 0.05f;
		this.color = Color.BLACK;
		this.amplitude = amplitude;
		this.font = Resources.font;
		this.layout = new GlyphLayout(this.font, this.text);
	}
	
	public void update(float delta) {
		if (!this.infinite && this.duration.isComplete())
			this.mustBeCleared = true;
		
		if (this.gravity) {
			// gravité
			if (this.spdY > -400)
				this.spdY -= 20;
			if (this.spdY < -400)
				this.spdY = -400;
			
			this.move(delta);
		} else if (this.fluttering) {
			this.fluttering(delta);
		}
	}
	
	public void render(SpriteBatch batch) {
		this.font.setColor(color);
		this.font.draw(batch, this.text, this.pos.x, this.pos.y+this.offsetY);
		this.font.setColor(Color.BLACK);
	}
	
	public void move(float delta) {
		this.pos.x += this.spdX * delta;
		this.pos.y += this.spdY * delta;
	}
	
	public void fluttering(float delta) {
		this.sin++;
		this.offsetY = (float)Math.sin(this.sin*this.spdY)*this.amplitude;

		this.move(delta);
	}
	
	public void centerRelativeToScreen() {
		this.centerRelativeToCoord(960);
	}
	
	public void centerRelativeToCoord(int x) {
		this.pos.x = x-layout.width/2;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setText(String text) {
		this.text = text;
		this.layout.setText(this.font, this.text);
	}
	
	public void setFont(BitmapFont font) {
		this.font = font;
		this.layout.setText(this.font, this.text);
	}
	
	public String getString() {
		return this.getString();
	}
	
	public boolean mustBeCleared() {
		return this.mustBeCleared;
	}
}
