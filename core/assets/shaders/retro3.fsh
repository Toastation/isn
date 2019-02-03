#version 120

varying vec2 v_texCoord0;
varying vec4 v_color;

uniform sampler2D u_sampler2D;
uniform vec2 u_resolution;

void main() {
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;
	
	int x = int(mod(gl_FragCoord.x, 2));
	int y = int(mod(gl_FragCoord.y, 2));
	
	float dark = 0.5;
	float alpha = 0.1;
	
	if (x == 0) {
		color.r = color.r < 0.5 ? (2.0 * color.r * dark) : (1.0 - 2.0 * (1.0 - color.r) * (1.0 - dark));
		color.g = color.g < 0.5 ? (2.0 * color.g * dark) : (1.0 - 2.0 * (1.0 - color.g) * (1.0 - dark));
		color.b = color.b < 0.5 ? (2.0 * color.b * dark) : (1.0 - 2.0 * (1.0 - color.b) * (1.0 - dark));
		color.a = color.a < 0.5 ? (2.0 * color.a * alpha) : (1.0 - 2.0 * (1.0 - color.a) * (1.0 - alpha));
	}
	
	if (y == 0) {
		color.r = color.r < 0.5 ? (2.0 * color.r * dark) : (1.0 - 2.0 * (1.0 - color.r) * (1.0 - dark));
		color.g = color.g < 0.5 ? (2.0 * color.g * dark) : (1.0 - 2.0 * (1.0 - color.g) * (1.0 - dark));
		color.b = color.b < 0.5 ? (2.0 * color.b * dark) : (1.0 - 2.0 * (1.0 - color.b) * (1.0 - dark));
		color.a = color.a < 0.5 ? (2.0 * color.a * alpha) : (1.0 - 2.0 * (1.0 - color.a) * (1.0 - alpha));
	}
		
	gl_FragColor = color;
}