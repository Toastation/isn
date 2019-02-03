package net.ts.isn.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.ts.isn.Config;

public class Animation {

	private int xo, yo ;// 
	private int x; //
	private int h ; //
	private int w ; //
	private int wmax ; //
	private int duration; //
	private Timer t; //
	private Texture src; //
	
	
	public Animation(int xo, int yo, int h, int w, int wmax, int duration, Texture src) {
		this.xo = xo;
		this.yo = yo;
		this.x = xo;
		this.h = h;
		this.wmax = wmax;
		this.w = w;
		this.duration = duration;
		this.t = new Timer(this.duration);
		this.t.start();
		this.src = src;
	}
	
	
	public void update() {
		if (x != wmax-w) {
			if (this.t.isComplete()) {
				x = x + w;
				this.t.reset();
			}
		}else if (x == wmax-w) {
			x = xo;
		}
	}
	
	public void render(SpriteBatch batch, float xx, float yy, boolean flip) {
		batch.draw(src, xx, yy, w*Config.SCALE, h*Config.SCALE, x, yo, w, h, flip, false);
	}
	
	public void render(SpriteBatch batch, float xx, float yy, boolean flip, float scale) {
		batch.draw(src, xx, yy, w*Config.SCALE*scale, h*Config.SCALE*scale, x, yo, w, h, flip, false);
	}
}
