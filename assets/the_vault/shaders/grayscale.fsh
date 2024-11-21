#version 330

in vec2 TexCoord;
uniform sampler2D tex;
out vec4 fragColor;

uniform float Grayscale;
uniform float Brightness;

void main() {
    vec4 color = texture(tex, TexCoord);
    if (color.a == 0.0) {
        discard;
    }

    fragColor = vec4(mix(color.rgb, vec3(0.2 * color.r + 0.7 * color.g + 0.7 * color.b), Grayscale) * Brightness, color.w);
}