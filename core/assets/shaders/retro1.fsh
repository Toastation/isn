#version 120

varying vec2 v_texCoord0;
varying vec4 v_color;

uniform sampler2D u_sampler2D;
uniform vec2 u_resolution;

void main() {
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;

	
	int y = int(mod(gl_FragCoord.y, 2));
	int x = int(mod(gl_FragCoord.y, 1));
	
	if (y == 0)
		color.rgb /= 2;
	if (x == 0)
		color.rgb /= 1.5;
		
	color.rgb *= 1.5;
		

	gl_FragColor = color;
}