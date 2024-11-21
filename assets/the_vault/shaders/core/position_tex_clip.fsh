#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec4 Clip;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 renderer = texture(Sampler0, texCoord0);
    if (renderer.a == 0.0) { discard; }
    if (gl_FragCoord.x < Clip[0]) { discard; }
    if (gl_FragCoord.y > Clip[1]) { discard; }
    if (gl_FragCoord.x > Clip[0] + Clip[2]) { discard; }
    if (gl_FragCoord.y < Clip[1] + Clip[3]) { discard; }
    fragColor = renderer * ColorModulator;
}
