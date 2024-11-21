#version 150

uniform sampler2D Sampler0;

uniform vec3 Colorize;
uniform float Grayscale;
uniform float Brightness;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 renderer = texture(Sampler0, texCoord0);
    if (renderer.a == 0.0) {
        discard;
    }
    vec4 grayscale = vec4(mix(renderer.rgb, vec3(0.2 * renderer.r + 0.7 * renderer.g + 0.7 * renderer.b), Grayscale) * Brightness, renderer.w);
    vec4 colorize = vec4(Colorize.rgb, 1.0);

    fragColor = grayscale * colorize;
}
