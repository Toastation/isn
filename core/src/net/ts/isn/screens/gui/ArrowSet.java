package net.ts.isn.screens.gui;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.ts.isn.Resources;

public class ArrowSet {
	private Vector2 pos;
	private float index;
	private int indexMax;
	private int indexMin;
	private float step;
	private float widthTotal;
	private boolean updated;
	private boolean isBoolean;

	private GlyphLayout layout;
	
	private Button arrowDecrease;
	private Button arrowIncrease;
	
	public ArrowSet(int x, int y, String text, int indexMax, int indexMin, float step) {
		this(x, y, text, indexMax, indexMin, indexMin, step);
	}
	
	public ArrowSet(int x, int y, String text, int indexMax, int indexMin, float index, float step) {
		this.pos = new Vector2(x, y);
		this.index = index;
		this.indexMax = indexMax;
		this.indexMin = indexMin;
		this.step = step;
		this.updated = false;
		
		this.layout = new GlyphLayout(Resources.font, text);
		
		this.arrowDecrease = new Button(this.pos.x, this.pos.y-10, new TextureRegion(Resources.gui, 0, 0, 25, 18));
		this.arrowIncrease = new Button(this.pos.x+this.layout.width+35, this.pos.y-10, new TextureRegion(Resources.gui, 25, 0, 25, 18));
		
		this.widthTotal = this.layout.width+60;
	}
	
	public void update() {
		if (this.arrowDecrease.isClicked() && !this.isBoolean) {
			if (this.index <= this.indexMin)
				this.index = this.indexMax;
			else
				this.index -= this.step;
			
			this.updated = true;
		} else if (this.arrowIncrease.isClicked()) {
			if (this.index >= this.indexMax)
				this.index = this.indexMin;
			else
				this.index += this.step;
			
			this.updated = true;
		}
		
		if (this.isBoolean && !this.updated) {
			if (this.index == 0)
				this.setText("off");
			else
				this.setText("on");
		}
	}
	
	public void render(SpriteBatch batch) {
		if (!this.isBoolean)
			this.arrowDecrease.render(batch);
		
		this.arrowIncrease.render(batch);
	
		Resources.font.draw(batch, this.layout, this.pos.x+30, this.pos.y + this.layout.height / 3 + 3);
	}
	
	public void setText(String text) {
		this.layout.setText(Resources.font, text);
		this.arrowIncrease.setPosX(this.pos.x+this.layout.width+35);
		this.updated = false;
		this.widthTotal = this.layout.width+60;
	}
	
	public void setParamThreshold(int max, int min) {
		this.indexMax = max;
		this.indexMin = min;
	}
	
	public void setPosX(int x) {
		this.arrowDecrease.setPosX(x);
		this.arrowIncrease.setPosX(x+this.layout.width+35);
		this.pos.x = x;
	}
	
	public void centerRelativeToCoord(int x) {
		this.pos.x = x-this.widthTotal/2;
	}
	
	public void setIndex(float index) {
		this.index = index;
	}
	
	public void setStep(float step) {
		this.step = step;
	}
	
	public void setBoolean(boolean isBoolean) {
		this.isBoolean = isBoolean;
	}
	
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
	public float getIndex() {
		return this.index;
	}
	
	public float getWidthTotal() {
		return this.widthTotal;
	}
	
	public Vector2 getPos() {
		return this.pos;
	}
	
	public boolean isUpdated() {
		return this.updated;
	}
}