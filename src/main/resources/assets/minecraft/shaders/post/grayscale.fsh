#version 330

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform GrayscaleConfig {
    float GrayscaleStrength;
    float LightCancel;
    float Gamma;
};

out vec4 fragColor;

// Standard luminance coefficients for grayscale
const vec3 Gray = vec3(0.299, 0.587, 0.114);

void main() {
    vec2 sizeRatio = OutSize / InSize;

    vec4 color = texture(InSampler, texCoord);

    // --- 1. Extended mask for orange-yellow shades ---
    bool warmColor =
    (color.r > 0.5) &&          // high red
    (color.g > 0.25) &&         // medium/high green
    (color.b < 0.35) &&         // low blue
    (abs(color.r - color.g) > 0.1 || abs(color.g - color.b) > 0.1); // not gray

    if (warmColor) {
        fragColor = color;
        return;
    }

    // --- 2. Gamma correction (brightens the image) ---
    vec3 brighter = pow(color.rgb, vec3(1.0 / Gamma));

    // --- 3. Pixel brightness ---
    float brightness = max(max(brighter.r, brighter.g), brighter.b);

    // --- 4. Reduce grayscale based on brightness ---
    float grayFactor = clamp(GrayscaleStrength - brightness * LightCancel, 0.0, 1.0);

    // --- 5. Pure grayscale ---
    float luma = dot(brighter, Gray);
    vec3 grayColor = vec3(luma);

    // --- 6. Mix color and grayscale ---
    vec3 mixed = mix(brighter, grayColor, grayFactor);

    // --- 7. Final - black and white with halftones ---
    float finalLuma = dot(mixed, Gray);
    fragColor = vec4(vec3(finalLuma), color.a);
}
