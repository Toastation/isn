#define distortion 0.2

varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;

vec2 radialDistortion(vec2 coord) {
  vec2 cc = coord - vec2(0.5);
  float dist = dot(cc, cc) * distortion;
  return coord + cc * (1.0 - dist) * dist;
}

void main() {
  vec2 texCoord = vec2(v_texCoord0);
  vec4 rgba = texture2D(u_sampler2D, radialDistortion(texCoord));
  vec4 intensity;
  if(fract(gl_FragCoord.y * (0.5 * 4.0 / 3.0)) > 0.5) {
    intensity = vec4(0);
  } else {
    intensity = smoothstep(0.2, 0.8, rgba) + normalize(rgba);
  }
  gl_FragColor = intensity * -0.25 + rgba * 1.4;
} 