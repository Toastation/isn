package net.ts.isn.util;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Util {
	private static Random rand = new Random();
	
	public static int getRandomInteger(int min, int max) {
		return min + rand.nextInt(max-min+1); // nextInt(int a) renvoie un nombre pseudo-aléatoire de l'intervalle [0;a[
	}
	
	public static float linearInterpolation(float a, float b, float t) {
		return a+(b-a)*t;
	}
	
	public static Color linearInterpolation(Color color, Color target, float t) {
		float r = linearInterpolation(color.r, target.r, t);
		float g = linearInterpolation(color.g, target.g, t);
		float b = linearInterpolation(color.b, target.b, t);
		return new Color(r, g, b, 1);
	}
	
	public static float getStringLength (String quote, BitmapFont font) {
		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, quote);
		return layout.width;
	}
}
