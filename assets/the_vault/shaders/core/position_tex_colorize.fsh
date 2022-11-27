#version 150

uniform sampler2D Sampler0;

uniform vec3 Colorize;
uniform float Grayscale;
uniform float Brightness;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a == 0.0) {
        discard;
    }
    vec4 grayscale = vec4(mix(color.rgb, vec3(0.2 * color.r + 0.7 * color.g + 0.7 * color.b), Grayscale) * Brightness, color.w);
    vec4 colorize = vec4(Colorize.rgb, 1.0);

    fragColor = grayscale * colorize;
}